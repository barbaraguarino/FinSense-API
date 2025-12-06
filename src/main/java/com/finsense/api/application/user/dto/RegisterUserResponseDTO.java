package com.finsense.api.application.user.dto;

import com.finsense.api.domain.user.enums.UserStatus;

import java.util.UUID;

public record RegisterUserResponseDTO(
        UUID idUser,
        String name,
        String email,
        UserStatus status
) {}
