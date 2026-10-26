package dev.chengzhi.qa;

import jakarta.validation.constraints.NotBlank;

public record AskRequest(@NotBlank String question) {
}
