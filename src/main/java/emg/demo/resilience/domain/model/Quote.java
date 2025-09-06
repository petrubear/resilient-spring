package emg.demo.resilience.domain.model;

public record Quote(
        String value
) {
    public static Quote of(String value) {
        return new Quote(value);
    }
}
