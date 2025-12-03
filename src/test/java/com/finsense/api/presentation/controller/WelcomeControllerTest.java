package com.finsense.api.presentation.controller;

import com.finsense.api.infrastructure.config.i18n.LocaleConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WelcomeController.class)
@Import(LocaleConfig.class)
class WelcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Cenários de Bem-Vindo para testar o i18n.")
    class Welcome{

        @Test
        @DisplayName("Deve retornar mensagem em Inglês (Default) quando não enviar Header")
        void shouldReturnDefaultMessageWhenNoHeaderProvided() throws Exception {
            mockMvc.perform(get("/api/v1/welcome"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Welcome to FinSense API!"));
        }

        @Test
        @DisplayName("Deve retornar mensagem em Português quando enviar Header pt-BR")
        void shouldReturnPortugueseMessageWhenHeaderProvided() throws Exception {
            mockMvc.perform(get("/api/v1/welcome")
                            .header("Accept-Language", "pt-BR"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Bem-vindo à API FinSense!"));
        }

    }

}