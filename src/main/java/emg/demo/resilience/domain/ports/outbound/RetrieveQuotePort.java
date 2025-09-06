package emg.demo.resilience.domain.ports.outbound;

import emg.demo.resilience.domain.model.Quote;

@FunctionalInterface
public interface RetrieveQuotePort {
    Quote getQuote();
}
