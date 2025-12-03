package com.finsense.api.infrastructure.config.i18n;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LocaleConfigTest {

    private final LocaleConfig localeConfig = new LocaleConfig();

    @Nested
    @DisplayName("Testes da Configuração do Resolver")
    class LocaleResolverConfig { // <--- Removido o 'extends'

        @Test
        @DisplayName("Deve retornar AcceptHeaderLocaleResolver com padrão US")
        void shouldReturnAcceptHeaderLocaleResolverWithUSDefault() {

            LocaleResolver resolver = localeConfig.localeResolver();

            assertInstanceOf(AcceptHeaderLocaleResolver.class, resolver, "O resolver deve ser uma instância de AcceptHeaderLocaleResolver");

            MockHttpServletRequest request = new MockHttpServletRequest();
            Locale resolvedLocale = resolver.resolveLocale(request);

            assertEquals(Locale.US, resolvedLocale,
                    "Quando não há header, deve retornar o Locale padrão (US)");
        }
    }
}