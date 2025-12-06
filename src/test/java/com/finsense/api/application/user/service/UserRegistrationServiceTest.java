package com.finsense.api.application.user.service;

import com.finsense.api.application.user.dto.RegisterUserRequestDTO;
import com.finsense.api.application.user.dto.RegisterUserResponseDTO;
import com.finsense.api.application.user.exception.EmailDuplicateException;
import com.finsense.api.application.user.mapper.UserRegistrationMapper;
import com.finsense.api.domain.user.enums.UserStatus;
import com.finsense.api.domain.user.model.User;
import com.finsense.api.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserRegistrationMapper userRegistrationMapper;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Nested
    @DisplayName("Cadastro de Usuário")
    class  CreateUser{

        @Test
        @DisplayName("Deve criar com sucesso um usuário.")
        void shouldWithSuccessCreateUser(){

            String rawPassword = "123456";
            String encodedPassword = "encodedPassword";

            RegisterUserRequestDTO requestDTO = new RegisterUserRequestDTO(
                    "Testador 01",
                    "test@gmail.com",
                    rawPassword
            );

            User newUser = new User();
            newUser.setEmail(requestDTO.email().toLowerCase());
            newUser.setName(requestDTO.name());
            newUser.setPassword(encodedPassword);
            newUser.setStatus(UserStatus.PENDING);

            User savedUser = newUser;
            savedUser.setIdUser(UUID.randomUUID());

            RegisterUserResponseDTO responseDTO = new RegisterUserResponseDTO(
                    savedUser.getIdUser(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getStatus()
            );

            when(userRepository.existsByEmail(requestDTO.email().toLowerCase())).thenReturn(false);
            when(userRegistrationMapper.toEntity(requestDTO)).thenReturn(newUser);
            when(encoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(userRepository.save(newUser)).thenReturn(savedUser);
            when(userRegistrationMapper.toDTO(savedUser)).thenReturn(responseDTO);

            RegisterUserResponseDTO response = userRegistrationService.registerUser(requestDTO);

            assertNotNull(response);
            assertEquals(savedUser.getIdUser(), response.idUser());
            assertEquals(savedUser.getName(), response.name());
            assertEquals(savedUser.getEmail(), response.email());

            verify(userRepository).existsByEmail(requestDTO.email().toLowerCase());
            verify(encoder).encode(rawPassword);
            verify(userRepository).save(newUser);

        }

        @Test
        @DisplayName("Deve lançar execeção quando o e-mail já estiver cadastrado.")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            RegisterUserRequestDTO requestDTO = new RegisterUserRequestDTO(
                    "Testador Duplicado",
                    "duplicate@gmail.com",
                    "123456"
            );

            when(userRepository.existsByEmail(requestDTO.email().toLowerCase())).thenReturn(true);

            assertThrows(EmailDuplicateException.class, () -> {
                userRegistrationService.registerUser(requestDTO);
            });

            verify(userRepository).existsByEmail(requestDTO.email().toLowerCase());
            verifyNoInteractions(encoder);
            verifyNoInteractions(userRegistrationMapper);
            verify(userRepository, never()).save(any());
        }

    }

}