package dev.chengzhi.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class KnowledgeAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper json;

    @Test
    void studentCannotRetrieveRestrictedPayrollEvidence() throws Exception {
        String hr = login("hr", "hr123456");
        String student = login("student", "student123");

        postJson(hr, "/api/documents", Map.of(
                "title", "\u8bf7\u5047\u5236\u5ea6",
                "content", "\u5b66\u751f\u8bf7\u5047\u8d85\u8fc7\u4e09\u5929\u9700\u8981\u5bfc\u5e08\u5ba1\u6279",
                "grants", List.of(Map.of("subjectType", "ROLE", "subjectId", "STUDENT"))));
        postJson(hr, "/api/documents", Map.of(
                "title", "\u85aa\u8d44\u5236\u5ea6",
                "content", "\u5b9e\u4e60\u85aa\u8d44\u4e8e\u6bcf\u6708\u5341\u4e94\u65e5\u53d1\u653e\u6807\u51c6\u4e3a\u6bcf\u5929\u4e8c\u767e\u5143",
                "grants", List.of(Map.of("subjectType", "ROLE", "subjectId", "HR"))));

        mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("question", "\u5b9e\u4e60\u85aa\u8d44\u4ec0\u4e48\u65f6\u5019\u53d1\u653e"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refused").value(true))
                .andExpect(jsonPath("$.citationIds").isEmpty())
                .andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("\u4e8c\u767e\u5143"))));
    }

    @Test
    void studentReceivesCitedAnswerForVisiblePolicy() throws Exception {
        String hr = login("hr", "hr123456");
        String student = login("student", "student123");
        postJson(hr, "/api/documents", Map.of(
                "title", "\u5b9e\u9a8c\u5ba4\u5236\u5ea6",
                "content", "\u8fdb\u5165\u5b9e\u9a8c\u5ba4\u524d\u5fc5\u987b\u5b8c\u6210\u5b89\u5168\u57f9\u8bad\u5e76\u767b\u8bb0\u8bbe\u5907",
                "grants", List.of(Map.of("subjectType", "DEPARTMENT", "subjectId", "dept-student"))));

        mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("question", "\u8fdb\u5165\u5b9e\u9a8c\u5ba4\u524d\u5fc5\u987b"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refused").value(false))
                .andExpect(jsonPath("$.citationIds").isNotEmpty())
                .andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.containsString("\u5b89\u5168\u57f9\u8bad")))
                .andExpect(result -> {
                    com.fasterxml.jackson.databind.JsonNode body = json.readTree(result.getResponse().getContentAsByteArray());
                    assertThat(body.get("evidence").get(0).get("citationId").asText())
                            .isEqualTo(body.get("citationIds").get(0).asText());
                });
    }

    @Test
    void naturalLanguageQuestionRetrievesCurrentAuthorizedEvidence() throws Exception {
        String hr = login("hr", "hr123456");
        String student = login("student", "student123");
        String documentId = createDocument(hr, Map.of(
                "title", "\u5b9e\u9a8c\u5ba4\u5165\u573a\u89c4\u8303",
                "content", "\u65e7\u7248\u89c4\u5b9a\u5141\u8bb8\u672a\u57f9\u8bad\u4eba\u5458\u76f4\u63a5\u8fdb\u5165\u5b9e\u9a8c\u5ba4",
                "grants", List.of(Map.of("subjectType", "DEPARTMENT", "subjectId", "dept-student"))));
        putJson(hr, "/api/documents/" + documentId, Map.of(
                "title", "\u5b9e\u9a8c\u5ba4\u5165\u573a\u89c4\u8303",
                "content", "\u8fdb\u5165\u5b9e\u9a8c\u5ba4\u524d\u5fc5\u987b\u5b8c\u6210\u5b89\u5168\u57f9\u8bad\u5e76\u767b\u8bb0\u8bbe\u5907",
                "grants", List.of(Map.of("subjectType", "DEPARTMENT", "subjectId", "dept-student"))));

        mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("question", "\u8fdb\u5b9e\u9a8c\u5ba4\u524d\u8981\u505a\u54ea\u4e9b\u51c6\u5907"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refused").value(false))
                .andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.containsString("\u5b89\u5168\u57f9\u8bad")))
                .andExpect(jsonPath("$.answer").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("\u672a\u57f9\u8bad"))));
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("username", username, "password", password))))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsByteArray()).get("token").asText();
    }

    private String createDocument(String token, Object body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/documents")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsByteArray()).get("id").asText();
    }

    private void putJson(String token, String path, Object body) throws Exception {
        mockMvc.perform(put(path)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    private void postJson(String token, String path, Object body) throws Exception {
        mockMvc.perform(post(path)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isOk());
    }
}
