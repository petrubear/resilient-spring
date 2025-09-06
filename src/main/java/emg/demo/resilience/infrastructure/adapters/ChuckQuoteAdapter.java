package emg.demo.resilience.infrastructure.adapters;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.domain.ports.outbound.RetrieveQuotePort;
import emg.demo.resilience.infrastructure.model.ChuckQuote;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
public class ChuckQuoteAdapter implements RetrieveQuotePort {
    private final RestTemplate restTemplate;

    public ChuckQuoteAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @RateLimiter(name = "quoteApi", fallbackMethod = "getDefaultQuote")
    @CircuitBreaker(name = "quoteApi", fallbackMethod = "getDefaultQuote")
    public Optional<Quote> getQuote() {
        final var quoteUrl = "https://api.chucknorris.io/jokes/random";
        var response = restTemplate.getForEntity(quoteUrl, ChuckQuote.class);
        return Optional.ofNullable(response.getBody()).map(q ->
                Optional.of(Quote.of(q.value())).orElseThrow(()
                        -> new IllegalStateException("Empty body from quote API"))
        );
    }

    public Optional<Quote> getDefaultQuote(Throwable throwable) {
        log.error("quoteApi Fallback called. reason: {}", throwable.getMessage());
        return Optional.of(Quote.of("Chuck is busy, try again later "));
    }
}
