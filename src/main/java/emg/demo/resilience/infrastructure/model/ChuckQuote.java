package emg.demo.resilience.infrastructure.model;

import emg.demo.resilience.domain.model.Quote;

public record ChuckQuote(
        String iconUrl,
        String id,
        String url,
        String value
) {
    public static ChuckQuote of(String iconUrl, String id, String url, String value) {
        return new ChuckQuote(iconUrl, id, url, value);
    }

    public Quote toDomainQuote() {
        return Quote.of(value);
    }
}
