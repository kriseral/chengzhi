package dev.chengzhi.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DocumentRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull List<GrantRequest> grants) {

    public record GrantRequest(@NotBlank String subjectType, @NotBlank String subjectId) {
    }
}
