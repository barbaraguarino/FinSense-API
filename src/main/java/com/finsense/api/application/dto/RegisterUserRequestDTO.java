package com.finsense.api.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDTO(
        @NotEmpty
        @Size(max = 200)
        String name,

        @Email
        @NotEmpty
        @Size(max = 150)
        String email,

        @NotEmpty
        @Size(max = 100)
        @Pattern(
                // Explicação da Regex:
                // (?=.*[0-9]) → Pelo menos um número
                // (?=.*[a-z]) → Pelo menos uma letra minúscula
                // (?=.*[A-Z]) → Pelo menos uma letra maiúscula
                // (?=.*[@#$!%*?&._-]) → Pelo menos um especial (exemplo expandido)
                // (?=\S+$) → Sem espaços em branco
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!%*?&._-])[A-Za-z\\d@#$!%*?&._-]{10,}$",
                message = "{validation.password.weak}"
        )
        String password
) {}
