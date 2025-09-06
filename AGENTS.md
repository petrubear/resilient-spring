Project Guide for Agents

Scope
- Spring Boot 3, Java 21 demo app implementing Hexagonal Architecture with Resilience4j and Zipkin tracing.
- Primary endpoint: `GET /api/v1/quote` (returns DTO), backed by an outbound HTTP call wrapped with CircuitBreaker and RateLimiter.

Repo Map
- `domain/`: model `Quote`, ports `inbound.GetQuotePort`, `outbound.RetrieveQuotePort`.
- `application/`: use case `GetQuoteUseCase` (implements inbound port), optional service `QuoteService` delegating to the port.
- `infrastructure/`
  - `controller/`: `QuoteController` (inbound HTTP adapter) returns `QuoteResponse` DTO.
  - `adapters/`: `ChuckQuoteAdapter` (outbound adapter) calling external API with Resilience4j annotations.
  - `model/`: `ChuckQuote` external response mapping; `model/dto/QuoteResponse` transport DTO.
  - `configuration/`: bean wiring, `RestTemplate` via `RestTemplateBuilder` (tracing).
- `application.yml`: Resilience4j + Micrometer Tracing + Zipkin settings.
- `pom.xml`: Spring Boot, Resilience4j, Micrometer Tracing, Zipkin reporter, AOP.

Common Tasks
- Build: `mvn -q clean package`
- Run: `mvn -q spring-boot:run`
- Test: `mvn -q test`
- Call API: `curl http://localhost:8080/api/v1/quote`
- Run Zipkin: `docker run -d -p 9411:9411 openzipkin/zipkin`

Resilience4j Notes
- Annotations: `@CircuitBreaker(name="quoteApi", fallbackMethod="getDefaultQuote")`, `@RateLimiter(name="quoteApi")` on outbound adapter method.
- AOP required: ensure `spring-boot-starter-aop` exists; otherwise annotations won’t be applied.
- Durations: use `2s`/`5s` strings, not nested maps.
- Fallback signatures: parameter should be `Throwable`/`Exception` compatible with the method; return type must match method’s return type.
- If methods return `Optional`: empty does NOT trigger fallback. Throw to invoke fallback, or configure a `recordResult` predicate to count empties as failures for breaker state only.

Tracing Notes
- Sampling: `management.tracing.sampling.probability=1.0` (adjust in prod).
- Zipkin endpoint: `management.zipkin.tracing.endpoint` (default `http://localhost:9411/api/v2/spans`).
- `RestTemplateBuilder` builds a tracing-instrumented client automatically.

Hexagonal Alignment Tips
- Controllers should depend on inbound port (`GetQuotePort`) directly; keep transport mapping (DTO) at the boundary.
- Outbound adapters implement outbound ports and translate between transport and domain.
- Keep domain free of framework annotations and transport concerns.

Validation Steps for Changes
- After modifying resilience config or annotations, run tests and hit `/actuator/metrics` to confirm counters change.
- To verify breaker: simulate 500s via `MockRestServiceServer` or by pointing to an invalid URL; confirm fallback path and state transitions in logs.
- For tracing: generate a few requests, then verify traces in Zipkin UI under service `resilience-spring`.

Pitfalls
- Missing AOP → annotations silently no-op.
- Misbound durations → defaults used; behavior differs from expectations.
- Leaking exception messages in fallback → avoid exposing internal details in responses.
- Integration test drift if port signatures change (e.g., to `Optional`). Update tests accordingly.

