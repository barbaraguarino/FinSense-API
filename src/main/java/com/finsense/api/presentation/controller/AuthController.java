package com.finsense.api.presentation.controller;

import com.finsense.api.application.user.dto.RegisterUserRequestDTO;
import com.finsense.api.application.user.dto.RegisterUserResponseDTO;
import com.finsense.api.application.user.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/signup")
    public ResponseEntity<@NonNull RegisterUserResponseDTO> signup(@Valid @RequestBody RegisterUserRequestDTO registerUserDTO){
        RegisterUserResponseDTO userResponseDTO = userRegistrationService.registerUser(registerUserDTO);
        log.info("Usuário criado [ID {}]. Disparando e-mail de confirmação...", userResponseDTO.idUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

}
