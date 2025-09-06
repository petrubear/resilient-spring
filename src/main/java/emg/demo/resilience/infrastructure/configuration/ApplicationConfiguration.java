package emg.demo.resilience.infrastructure.configuration;

import emg.demo.resilience.application.services.QuoteService;
import emg.demo.resilience.application.usecases.GetQuoteUseCase;
import emg.demo.resilience.domain.ports.inbound.GetQuotePort;
import emg.demo.resilience.domain.ports.outbound.RetrieveQuotePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public QuoteService quoteService(GetQuotePort getQuotePort) {
        return new QuoteService(getQuotePort);
    }

    @Bean
    public GetQuotePort getQuotePort(RetrieveQuotePort retrieveQuotePort) {
        return new GetQuoteUseCase(retrieveQuotePort);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
