package dev.chengzhi.web;

import dev.chengzhi.auth.AuthService;
import dev.chengzhi.auth.CurrentUser;
import dev.chengzhi.auth.LoginRequest;
import dev.chengzhi.auth.LoginResponse;
import dev.chengzhi.document.DocumentRequest;
import dev.chengzhi.document.DocumentResponse;
import dev.chengzhi.document.DocumentService;
import dev.chengzhi.qa.AskRequest;
import dev.chengzhi.qa.AskResponse;
import dev.chengzhi.qa.QuestionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AuthService auth;
    private final CurrentUser currentUser;
    private final DocumentService documents;
    private final QuestionService questions;

    public ApiController(
            AuthService auth,
            CurrentUser currentUser,
            DocumentService documents,
            QuestionService questions) {
        this.auth = auth;
        this.currentUser = currentUser;
        this.documents = documents;
        this.questions = questions;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request.username(), request.password());
    }

    @PostMapping("/documents")
    public DocumentResponse createDocument(@Valid @RequestBody DocumentRequest request) {
        return documents.create(currentUser.require(), request);
    }

    @PutMapping("/documents/{documentId}")
    public DocumentResponse updateDocument(
            @PathVariable String documentId,
            @Valid @RequestBody DocumentRequest request) {
        return documents.update(currentUser.require(), documentId, request);
    }

    @GetMapping("/documents")
    public List<DocumentResponse> listDocuments() {
        return documents.list(currentUser.require());
    }

    @PostMapping("/questions")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return questions.ask(currentUser.require(), request.question());
    }
}
