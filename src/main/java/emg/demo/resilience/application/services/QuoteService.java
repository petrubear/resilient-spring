package emg.demo.resilience.application.services;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.domain.ports.inbound.GetQuotePort;

public class QuoteService {
    private final GetQuotePort getQuotePort;

    public QuoteService(GetQuotePort getQuotePort) {
        this.getQuotePort = getQuotePort;
    }

    public Quote getQuote() {
        return getQuotePort.getQuote();
    }
}
