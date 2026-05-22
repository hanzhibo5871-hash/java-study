package com.example.taskmanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class TaskManagerApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerRejectsDuplicateUsername() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        register(username, "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"password123"}
                                """.formatted(username)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginRejectsWrongPassword() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        register(username, "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"wrong-password"}
                                """.formatted(username)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void tasksRequireLogin() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCanCreateSearchUpdateAndDeleteOwnTask() throws Exception {
        String token = registerRandomUser();

        long taskId = createTask(token, "Learn SQL", "Write SELECT queries", "TODO");

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", bearer(token))
                        .param("status", "TODO")
                        .param("keyword", "SQL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title").value("Learn SQL"));

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"DONE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DONE"));

        mockMvc.perform(delete("/api/tasks/{id}", taskId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotAccessAnotherUsersTask() throws Exception {
        String ownerToken = registerRandomUser();
        String otherToken = registerRandomUser();
        long taskId = createTask(ownerToken, "Private task", "Only owner can see it", "TODO");

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());
    }

    private String registerRandomUser() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return register(username, "password123");
    }

    private String register(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        return root.path("data").path("token").asText();
    }

    private long createTask(String token, String title, String description, String status) throws Exception {
        String body = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","description":"%s","status":"%s"}
                                """.formatted(title, description, status)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        return root.path("data").path("id").asLong();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
