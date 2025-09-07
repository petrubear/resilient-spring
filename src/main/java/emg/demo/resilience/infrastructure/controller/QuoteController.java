package emg.demo.resilience.infrastructure.controller;

import emg.demo.resilience.application.services.QuoteService;
import emg.demo.resilience.infrastructure.model.dto.QuoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
@Tag(name = "Quotes", description = "Quotes API")
@SecurityRequirement(name = "resilience-api")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping(value = "/quote", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get a random quote", description = "Returns a random quote from Chuck Norris")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a quote",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QuoteResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<QuoteResponse> getQuote() {
        return quoteService.getQuote().map(q ->
                ResponseEntity.ok(QuoteResponse.from(q))
        ).orElse(
                ResponseEntity.internalServerError().build()
        );
    }
}
