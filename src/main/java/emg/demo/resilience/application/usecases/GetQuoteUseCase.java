package emg.demo.resilience.application.usecases;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.domain.ports.inbound.GetQuotePort;
import emg.demo.resilience.domain.ports.outbound.RetrieveQuotePort;

public class GetQuoteUseCase implements GetQuotePort {
    private final RetrieveQuotePort retrieveQuotePort;

    public GetQuoteUseCase(RetrieveQuotePort retrieveQuotePort) {
        this.retrieveQuotePort = retrieveQuotePort;
    }

    @Override
    public Quote getQuote() {
        return retrieveQuotePort.getQuote();
    }
}
