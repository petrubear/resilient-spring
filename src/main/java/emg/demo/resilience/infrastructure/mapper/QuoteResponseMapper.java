package emg.demo.resilience.infrastructure.mapper;

import emg.demo.resilience.domain.model.Quote;
import emg.demo.resilience.infrastructure.model.dto.QuoteResponse;

public class QuoteResponseMapper {
    private QuoteResponseMapper() {
        // utility class
    }

    public static QuoteResponse from(Quote quote) {
        return new QuoteResponse(quote.value(), quote.value().length());
    }
}
