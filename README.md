Resilience Spring

Overview
- Purpose: Demo Spring Boot 3 (Java 21) app showcasing Hexagonal Architecture with Resilience4j (CircuitBreaker, RateLimiter) and Micrometer tracing to Zipkin.
- Main flow: `/api/v1/quote` → use case → outbound adapter calls Chuck Norris API → resilient annotations wrap the call and provide fallback.

Architecture
- Domain: Pure model and ports (`domain.model.Quote`, `ports.inbound.GetQuotePort`, `ports.outbound.RetrieveQuotePort`). No framework dependencies.
- Application: Use case (`application.usecases.GetQuoteUseCase`) implements inbound port. Thin `application.services.QuoteService` delegates to the port.
- Infrastructure (Inbound): REST controller (`infrastructure.controller.QuoteController`) returns a transport DTO (`infrastructure.model.dto.QuoteResponse`).
- Infrastructure (Outbound): HTTP adapter (`infrastructure.adapters.ChuckQuoteAdapter`) uses `RestTemplate` with `@CircuitBreaker` and `@RateLimiter`.
- Configuration: `infrastructure.configuration.ApplicationConfiguration` wires beans and provides a `RestTemplate` via `RestTemplateBuilder` for tracing.
- Dependency direction: Infrastructure → Application → Domain. Domain depends on nothing.

Architecture Diagram
```mermaid
flowchart LR
  subgraph Inbound Adapter (HTTP)
    C[QuoteController]
  end
  subgraph Application
    UC[GetQuoteUseCase\n(implements GetQuotePort)]
  end
  subgraph Domain
    Q[(Quote)]
    InP[[GetQuotePort]]
    OutP[[RetrieveQuotePort]]
  end
  subgraph Outbound Adapter (External)
    A[ChuckQuoteAdapter\n(@CircuitBreaker,@RateLimiter)]
    EXT[(Chuck Norris API)]
  end

  C -->|calls| UC
  UC -->|depends on| OutP
  A -->|implements| OutP
  UC -->|invokes| A
  A -->|HTTP| EXT
  UC --> Q
```

Endpoints
- `GET /api/v1/quote`: Returns a QuoteResponse `{ "value": string, "length": number }`.
- Actuator: `/actuator/health`, `/actuator/info`, `/actuator/metrics` (Resilience4j metrics included).

Resilience
- Annotations: `@CircuitBreaker(name = "quoteApi", fallbackMethod = "getDefaultQuote")`, `@RateLimiter(name = "quoteApi")` on the outbound adapter.
- Config: `src/main/resources/application.yml`
  - Circuit breaker: failure-rate-threshold 50, minimum-number-of-calls 3, wait-in-open 5s, half-open 3, sliding window size 10.
  - Rate limiter: 5 calls / 10s.
  - Time limiter sample present; disabled in code.
- Important: Spring AOP is required (`spring-boot-starter-aop`). Durations use `2s`, `5s`, etc.
- Optional return types: If an adapter returns `Optional`, empty results do not trigger fallback unless an exception is thrown. Use either exceptions (to invoke fallback) or a `recordResult` predicate to mark empties as failures for metrics/state.

Tracing (Zipkin)
- Libraries: Micrometer Tracing (Brave) + Zipkin reporter.
- Config: `management.tracing.enabled=true`, `management.tracing.sampling.probability=1.0`.
- Zipkin endpoint: `management.zipkin.tracing.endpoint=${ZIPKIN_ENDPOINT:http://localhost:9411/api/v2/spans}`.
- RestTemplate is built via `RestTemplateBuilder` so tracing interceptors are applied.

Run Locally
- Prerequisites: Java 21, Maven 3.9+.
- Build: `mvn clean package`
- Run: `mvn spring-boot:run`
- Call: `curl http://localhost:8080/api/v1/quote`

Enable Zipkin
- Start Zipkin: `docker run -d -p 9411:9411 openzipkin/zipkin`
- Export: Requests will appear under service name `resilience-spring` in the Zipkin UI.
- Override endpoint: `export ZIPKIN_ENDPOINT=http://zipkin:9411/api/v2/spans`

Testing
- Unit/Integration: `mvn test`
- Includes an adapter-level integration test that demonstrates the circuit breaker opening and using a fallback. If ports return `Optional`, update assertions accordingly.

Configuration Notes
- application.yml contains Resilience4j and tracing settings.
- For DEBUG logging of Resilience4j, set `logging.level.io.github.resilience4j=DEBUG`.

Troubleshooting
- Annotations not firing: ensure `spring-boot-starter-aop` is on the classpath.
- Durations not applied: use `2s`/`5s` style, not nested `seconds:`.
- Fallback not called with Optionals: throw on empty or configure `recordResult` predicate if you only want to affect breaker state.
