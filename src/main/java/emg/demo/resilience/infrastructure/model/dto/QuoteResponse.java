package emg.demo.resilience.infrastructure.model.dto;

import emg.demo.resilience.domain.model.Quote;

public record QuoteResponse(
        String quote,
        int length
) {
    public static QuoteResponse from(Quote quote) {
        return new QuoteResponse(quote.value(), quote.value().length());
    }
}
