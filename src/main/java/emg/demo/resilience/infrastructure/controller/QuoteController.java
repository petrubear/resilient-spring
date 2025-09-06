package emg.demo.resilience.infrastructure.controller;

import emg.demo.resilience.application.services.QuoteService;
import emg.demo.resilience.infrastructure.model.dto.QuoteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/quote")
    public ResponseEntity<QuoteResponse> getQuote() {
        return quoteService.getQuote().map(q ->
                ResponseEntity.ok(QuoteResponse.from(q))
        ).orElse(
                ResponseEntity.internalServerError().build()
        );
    }
}
