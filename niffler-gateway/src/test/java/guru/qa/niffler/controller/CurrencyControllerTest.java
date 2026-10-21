package guru.qa.niffler.controller;

import guru.qa.niffler.jupiter.annotation.WiremockStubs;
import guru.qa.niffler.jupiter.extension.WiremockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(WiremockExtension.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void grpcProperties(DynamicPropertyRegistry registry) {
        registry.add("grpc.client.grpcCurrencyClient.address",
                WiremockExtension::getGrpcAddress);
    }

    @Test
    @WiremockStubs({"getAllCurrencies.json"})
    void allCurrenciesShouldBeReturned() throws Exception {
        mockMvc.perform(get("/api/currencies/all")
                        .with(jwt().jwt(c -> c.claim("sub", "bee"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[?(@.currency=='RUB')].currencyRate").value(0.015))
                .andExpect(jsonPath("$[?(@.currency=='USD')].currencyRate").value(1.0))
                .andExpect(jsonPath("$[?(@.currency=='EUR')].currencyRate").value(1.08))
                .andExpect(jsonPath("$[?(@.currency=='KZT')].currencyRate").value(0.0021));
    }
}