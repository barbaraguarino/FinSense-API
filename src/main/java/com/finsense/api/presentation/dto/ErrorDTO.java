package com.finsense.api.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDTO(
       LocalDateTime timestamp,
       Integer status,
       String error,
       String message,
       String path,
       List<ErrorDetailDTO> details
){}
