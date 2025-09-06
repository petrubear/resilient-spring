package emg.demo.resilience.infrastructure.adapters;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.domain.ports.outbound.RetrieveQuotePort;
import emg.demo.resilience.infrastructure.model.ChuckQuote;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class ChuckQuoteAdapter implements RetrieveQuotePort {
    private final RestTemplate restTemplate;

    public ChuckQuoteAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @RateLimiter(name = "quoteApi", fallbackMethod = "getDefaultQuote")
    @CircuitBreaker(name = "quoteApi", fallbackMethod = "getDefaultQuote")
    public Quote getQuote() {
        final var quoteUrl = "https://api.chucknorris.io/jokes/random";
        var response = restTemplate.getForEntity(quoteUrl, ChuckQuote.class);
        var chuckQuote = Optional.ofNullable(response.getBody());
        return chuckQuote.map(ChuckQuote::toDomainQuote).orElse(null);
    }

    public Quote getDefaultQuote(Exception ex) {
        return Quote.of("Chuck is busy solving " + ex.getMessage());
    }
}
