package emg.demo.resilience.domain.ports.inbound;

import emg.demo.resilience.domain.model.Quote;

@FunctionalInterface
public interface GetQuotePort {
    Quote getQuote();
}
