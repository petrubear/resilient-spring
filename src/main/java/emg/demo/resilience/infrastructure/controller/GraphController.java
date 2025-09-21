package emg.demo.resilience.infrastructure.controller;

import emg.demo.resilience.application.services.QuoteService;
import emg.demo.resilience.infrastructure.mapper.QuoteResponseMapper;
import emg.demo.resilience.infrastructure.model.dto.QuoteResponse;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class GraphController {
    private final QuoteService quoteService;

    public GraphController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @QueryMapping(name = "quote")
//    @PreAuthorize("hasRole('USER')")
    public QuoteResponse getQuoteQuery() {
        return quoteService.getQuote()
                .map(QuoteResponseMapper::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Quote unavailable"));
    }
}
