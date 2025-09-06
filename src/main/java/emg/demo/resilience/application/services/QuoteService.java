package emg.demo.resilience.application.services;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.domain.ports.inbound.GetQuotePort;

import java.util.Optional;

public class QuoteService {
    private final GetQuotePort getQuotePort;

    public QuoteService(GetQuotePort getQuotePort) {
        this.getQuotePort = getQuotePort;
    }

    public Optional<Quote> getQuote() {
        return getQuotePort.getQuote();
    }
}
