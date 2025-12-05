package com.finsense.api.presentation.advice;

import com.finsense.api.application.exception.BusinessRuleException;
import com.finsense.api.infrastructure.config.i18n.LocaleConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@Import({LocaleConfig.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/business-exception")
        public void throwBusinessException() {
            throw new BusinessRuleException("error.business-rule.title", "error.business-rule.account.insufficient-funds");
        }

        @PostMapping("/validation-exception")
        public void throwValidationException(@Valid @RequestBody TestDTO dto) {}

        record TestDTO(@NotNull(message = "{jakarta.validation.constraints.NotNull.message}") String name) {}

        @GetMapping("/generic-exception")
        public void throwGenericException() {
            throw new RuntimeException("Erro inesperado forçado para teste!");
        }

        @GetMapping("/type-mismatch/{id}")
        public void typeMismatch(@PathVariable Long id) {
        }

        @PostMapping("/data-integrity")
        public void throwDataIntegrity() {
            throw new org.springframework.dao.DataIntegrityViolationException("Duplicate key");
        }
    }


    @Nested
    @DisplayName("Tratamento de Regra de Negócio (BusinessRuleException)")
    class BusinessRule{

        @Test
        @DisplayName("Deve retornar 422 Unprocessable Entity com mensagem traduzida")
        void shouldReturn422WithTranslatedMessage() throws Exception {
            mockMvc.perform(get("/test/business-exception")
                            .header("Accept-Language", "en-US"))
                    .andExpect(status().is(HttpStatus.UNPROCESSABLE_CONTENT.value()))
                    .andExpect(jsonPath("$.status").value(422))
                    .andExpect(jsonPath("$.error").value("Business Rule Violation"))
                    .andExpect(jsonPath("$.message").value("Unable to complete operation due to insufficient balance."))
                    .andExpect(jsonPath("$.path").value("/test/business-exception"));
        }

    }

    @Nested
    @DisplayName("Tratamento de Validação (MethodArgumentNotValidException)")
    class Validation{

        @Test
        @DisplayName("Deve retornar 400 Bada Request com um array details contendo 1 elemento")
        void shouldReturn400WithArrayDetails() throws Exception {
            mockMvc.perform(post("/test/validation-exception")
                            .header("Accept-Language", "en-US")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation Error"))
                    .andExpect(jsonPath("$.message").value("One or more fields have invalid values. Please check the details."))
                    .andExpect(jsonPath("$.path").value("/test/validation-exception"))
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details", hasSize(1)))
                    .andExpect(jsonPath("$.details[0].field").value("name"));

        }

    }

    @Nested
    @DisplayName("Tratamento de Erros Genéricos (GenericException)")
    class Generic{

        @Test
        @DisplayName("Deve retorna 500 Internal Server Error para exeções não tratadas")
        void shouldReturn500ForUnhandledException() throws Exception {
            mockMvc.perform(get("/test/generic-exception")
                            .header("Accept-Language", "en-US"))
                    .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please contact support."))
                    .andExpect(jsonPath("$.path").value("/test/generic-exception"))
                    .andExpect(jsonPath("$.details").doesNotExist());
        }
    }

    @Nested
    @DisplayName("Tratamento de JSON Inválido (HttpMessageNotReadableException)")
    class HttpMessageNotReadable{

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando o JSON está malformado")
        void shouldReturn400ForMalformedJson() throws Exception {
            String jsonQuebrado = "{ \"name\": \"John\"";
            mockMvc.perform(post("/test/validation-exception")
                            .header("Accept-Language", "en-US")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonQuebrado))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Invalid Format"))
                    .andExpect(jsonPath("$.message").value("The request body is malformed or invalid."))
                    .andExpect(jsonPath("$.path").value("/test/validation-exception"))
                    .andExpect(jsonPath("$.details").doesNotExist());
        }

        @Test
        @DisplayName("Deve retornar 400 quando o corpo da requisição é obrigatório mas não foi enviado")
        void shouldReturn400ForMissingBody() throws Exception {
            mockMvc.perform(post("/test/validation-exception")
                            .header("Accept-Language", "en-US")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid Format"))
                    .andExpect(jsonPath("$.message").value("The request body is malformed or invalid."));
        }

    }

    @Nested
    @DisplayName("Tratamento de Tipo de Parâmetro Inválido (MethodArgumentTypeMismatch)")
    class MethodArgumentTypeMismatch {

        @Test
        @DisplayName("Deve retornar 400 quando passar uma String para um parâmetro Long")
        void shouldReturn400ForTypeMismatch() throws Exception {
            mockMvc.perform(get("/test/type-mismatch/abc")
                            .header("Accept-Language", "en-US"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Invalid Parameter Type"))
                    .andExpect(jsonPath("$.message").value("The parameter 'id' received an invalid value 'abc'."));
        }
    }

    @Nested
    @DisplayName("Tratamento de Método HTTP Não Suportado (MethodNotSupported)")
    class MethodNotSupported {

        @Test
        @DisplayName("Deve retornar 405 quando usar DELETE em um endpoint GET")
        void shouldReturn405ForInvalidMethod() throws Exception {
            mockMvc.perform(delete("/test/business-exception")
                            .header("Accept-Language", "en-US"))
                    .andExpect(status().isMethodNotAllowed()) // Espera 405
                    .andExpect(jsonPath("$.status").value(405))
                    .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                    .andExpect(jsonPath("$.message").value("The method 'DELETE' is not supported for this request."));
        }
    }

    @Nested
    @DisplayName("Tratamento de Violação de Integridade de Dados (DataIntegrityViolation)")
    class DataIntegrityViolation {

        @Test
        @DisplayName("Deve retornar 409 Conflict para violação de dados")
        void shouldReturn409ForDataIntegrityViolation() throws Exception {

            mockMvc.perform(post("/test/data-integrity")
                            .header("Accept-Language", "en-US"))
                    .andExpect(status().isConflict()) // Espera 409
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Data Conflict"))
                    .andExpect(jsonPath("$.message").value("The operation cannot be completed due to a data conflict."));
        }
    }
}