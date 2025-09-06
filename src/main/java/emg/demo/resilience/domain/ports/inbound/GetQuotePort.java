package emg.demo.resilience.domain.ports.inbound;

import emg.demo.resilience.domain.model.Quote;

import java.util.Optional;

@FunctionalInterface
public interface GetQuotePort {

    Optional<Quote> getQuote();
}
