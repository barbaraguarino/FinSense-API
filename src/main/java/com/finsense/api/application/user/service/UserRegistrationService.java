package com.finsense.api.application.user.service;

import com.finsense.api.application.user.dto.RegisterUserRequestDTO;
import com.finsense.api.application.user.dto.RegisterUserResponseDTO;
import com.finsense.api.application.user.exception.EmailDuplicateException;
import com.finsense.api.application.user.mapper.UserMapper;
import com.finsense.api.domain.user.model.User;
import com.finsense.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegistrationService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public RegisterUserResponseDTO registerUser(RegisterUserRequestDTO userDTO){
        if(userRepository.existsByEmail(userDTO.email().toLowerCase()))
            throw new EmailDuplicateException("error.conflict.title", "error.business-rule.user.email.duplicate");

        User newUser = userMapper.toEntity(userDTO);
        newUser.setPassword(encoder.encode(userDTO.password()));

        User saveUser = userRepository.save(newUser);

        return userMapper.toDTO(saveUser);
    }

}
