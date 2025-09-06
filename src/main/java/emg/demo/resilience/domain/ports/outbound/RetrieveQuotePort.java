package emg.demo.resilience.domain.ports.outbound;

import emg.demo.resilience.domain.model.Quote;

import java.util.Optional;

@FunctionalInterface
public interface RetrieveQuotePort {
    Optional<Quote> getQuote();
}
