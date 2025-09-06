package emg.demo.resilience.infrastructure.adapters;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.domain.ports.outbound.RetrieveQuotePort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
class ChuckQuoteAdapterCircuitBreakerTest {

    private static final String QUOTE_URL = "https://api.chucknorris.io/jokes/random";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RetrieveQuotePort retrieveQuotePort;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
        // Simulate consistent server error responses
        server.expect(ExpectedCount.manyTimes(), requestTo(QUOTE_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        // Ensure breaker starts CLOSED for the instance under test
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("quoteApi");
        cb.reset();
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void circuitBreaker_opens_and_fallback_is_used() {
        // Given minimum-number-of-calls: 3 and failure-rate-threshold: 50
        // After 3 failures, state should transition to OPEN
        Quote q1 = retrieveQuotePort.getQuote();
        Quote q2 = retrieveQuotePort.getQuote();
        Quote q3 = retrieveQuotePort.getQuote();

        assertThat(q1.value()).isEqualTo("Chuck is busy, try again later");
        assertThat(q2.value()).isEqualTo("Chuck is busy, try again later");
        assertThat(q3.value()).isEqualTo("Chuck is busy, try again later");

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("quoteApi");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // Additional call while OPEN should short-circuit immediately to fallback
        Quote q4 = retrieveQuotePort.getQuote();
        assertThat(q4.value()).isEqualTo("Chuck is busy, try again later");
    }
}

