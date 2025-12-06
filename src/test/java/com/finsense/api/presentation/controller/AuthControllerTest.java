package com.finsense.api.presentation.controller;

import com.finsense.api.application.user.dto.RegisterUserRequestDTO;
import com.finsense.api.application.user.dto.RegisterUserResponseDTO;
import com.finsense.api.application.user.exception.EmailDuplicateException;
import com.finsense.api.application.user.service.UserRegistrationService;
import com.finsense.api.domain.user.enums.UserStatus;
import com.finsense.api.infrastructure.config.i18n.LocaleConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(LocaleConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRegistrationService userRegistrationService;

    @Nested
    @DisplayName("Cenários de Cadastro (Signup)")
    class SignUp {

        @Test
        @DisplayName("Deve retorno 201 Created quando os dados forem válidos.")
        void shouldReturn201WhenDataIsValid() throws Exception {

            RegisterUserRequestDTO requestDTO = new RegisterUserRequestDTO(
                    "Testador 02",
                    "test@finsense.com",
                    "Strong@123"
            );

            RegisterUserResponseDTO responseDTO = new RegisterUserResponseDTO(
                    UUID.randomUUID(),
                    "Testador 02",
                    "test@finsense.com",
                    UserStatus.PENDING
            );

            when(userRegistrationService.registerUser(any(RegisterUserRequestDTO.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(post("/api/v1/auth/signup")
                            .header("Accept-Language", "en-US")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idUser").exists())
                    .andExpect(jsonPath("$.email").value("test@finsense.com"))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando a senha for fraca")
        void shouldReturn400WhenPasswordIsWeak() throws Exception {
            RegisterUserRequestDTO request = new RegisterUserRequestDTO(
                    "User Weak",
                    "weak@email.com",
                    "123"
            );

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Accept-Language", "en-US"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation Error"))
                    .andExpect(jsonPath("$.details[0].field").value("password"));
        }

        @Test
        @DisplayName("Deve retornar 409 Conflict quando o e-mail já existir")
        void shouldReturn409WhenEmailIsDuplicated() throws Exception {
            RegisterUserRequestDTO request = new RegisterUserRequestDTO(
                    "User Duplicado",
                    "duplicate@email.com",
                    "Strong@123"
            );
            doThrow(new EmailDuplicateException("error.conflict.title", "error.business-rule.user.email.duplicate"))
                    .when(userRegistrationService).registerUser(any());

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header("Accept-Language", "en-US"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Data Conflict"))
                    .andExpect(jsonPath("$.message").value("Email is already registered in the system."));
        }
    }

}