package emg.demo.resilience.infrastructure.controller;

import emg.demo.resilience.application.services.QuoteService;
import emg.demo.resilience.domain.model.Quote;
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
    public ResponseEntity<Quote> getQuote() {
        return ResponseEntity.ok(quoteService.getQuote());
    }
}
