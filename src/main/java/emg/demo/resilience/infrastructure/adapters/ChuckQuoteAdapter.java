package emg.demo.resilience.infrastructure.adapters;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.domain.ports.outbound.RetrieveQuotePort;
import emg.demo.resilience.infrastructure.model.ChuckQuote;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ChuckQuoteAdapter implements RetrieveQuotePort {
    private final RestTemplate restTemplate;

    public ChuckQuoteAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Quote getQuote() {
        final var quoteUrl = "https://api.chucknorris.io/jokes/random";
        var response = restTemplate.getForEntity(quoteUrl, ChuckQuote.class);
        var chuckQuote = response.getBody();
        return chuckQuote.toDomainQuote();
    }

    private Quote getDefaultQuote() {
        return Quote.of("Chuck is busy, try again later");
    }
}
