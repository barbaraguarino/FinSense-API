package com.finsense.api.application.user.mapper;

import com.finsense.api.application.user.dto.RegisterUserRequestDTO;
import com.finsense.api.application.user.dto.RegisterUserResponseDTO;
import com.finsense.api.domain.user.enums.UserStatus;
import com.finsense.api.domain.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

    public User toEntity(RegisterUserRequestDTO dto){
        if(dto == null) return null;
        return User.builder()
                .name(dto.name())
                .email(dto.email().toLowerCase())
                .password(dto.password())
                .status(UserStatus.PENDING)
                .build();
    }

    public RegisterUserResponseDTO toDTO(User user){
        if(user == null) return null;
        return new RegisterUserResponseDTO(
                user.getIdUser(),
                user.getName(),
                user.getEmail(),
                user.getStatus()
        );
    }
}
