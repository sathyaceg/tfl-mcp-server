# Java MCP + Agentic Service Architecture Summary

## Goal

Build a **serious always-on personal backend** that: - exposes
tools/APIs - enforces **strict TPS limits** - connects to external
APIs - can later serve agentic/MCP workloads - follows production-grade
practices

------------------------------------------------------------------------

## Language Decision

### Recommended Stack

Backend / MCP Server → Java\
Agent Layer (optional) → Python or Java

**Reasoning** - Java excels at long-running, controlled, rate-limited
services. - Strong operational guardrails. - Mature ecosystem (Spring
Boot). - MCP is language-agnostic → agent and tools can differ.

------------------------------------------------------------------------

## Recommended Architecture

Agent / Client\
↓\
MCP Server (Java)\
↓\
Spring Boot Webservice\
↓\
Service Layer\
↓\
External APIs / DB

Your idea is correct: - Build a Java webservice - Expose APIs as tools -
Internal APIs call external providers

------------------------------------------------------------------------

## Guardrails for Always-On Services

Production guardrails mean operational safety.

### Required Controls

-   Authentication & authorization
-   Rate limiting (TPS)
-   Concurrency limits
-   Timeouts
-   Circuit breakers
-   Input/output validation
-   Observability (logs + metrics)
-   Audit logging

Goal: Prevent runaway agents, retries, or cost explosions.

------------------------------------------------------------------------

## TPS Limiting Strategy

Use **two layers**:

### 1. Edge Limiting

(API Gateway / Spring Cloud Gateway) - global TPS - per-client TPS -
burst limits

### 2. Service-Level Limiting

(per tool/API) - token bucket limiter - concurrency caps - circuit
breakers

Typical libraries: - Resilience4j - Bucket4j

------------------------------------------------------------------------

## Java Web Framework Choice

### Use: Spring Boot

Reasons: - Industry standard - Huge ecosystem - Built-in DI - Security +
metrics - Excellent for APIs & MCP tooling - Long-term maintainability

Alternatives: - Quarkus → performance-focused - Micronaut → lightweight
cloud-native

Default choice = Spring Boot.

------------------------------------------------------------------------

## Dependency Injection (DI)

### Use Spring's built-in DI container

Do NOT add Guice/Dagger/etc.

Spring already provides: IoC Container + DI

------------------------------------------------------------------------

## Modern Spring DI Rule

### Constructor Injection ONLY

### No @Autowired on fields

Preferred pattern:

``` java
@Service
public class PaymentService {

    private final ExternalClient client;

    public PaymentService(ExternalClient client) {
        this.client = client;
    }
}
```

Benefits: - explicit dependencies - immutable objects - easy unit
testing - compile-time safety - predictable lifecycle

------------------------------------------------------------------------

## Clean Dependency Flow

Controller\
↓\
Service\
↓\
Client / Repository\
↓\
External System

Tool (MCP)\
↓\
Service\
↓\
Client / Repository\
↓\
External System

Spring = wiring only\
Business logic = plain Java

------------------------------------------------------------------------

## External API Keys --- Local Development

Best local setup:

### Environment Variables

SOME_API_KEY=xxx

Referenced in Spring: external.api.key=\${SOME_API_KEY}

Rules: - never commit secrets - `.env.local` in `.gitignore` - separate
dev/prod keys - fail startup if missing

Later upgrade to: - Cloud Secret Manager - Vault - Kubernetes Secrets

------------------------------------------------------------------------

## Recommended Stack

Spring Boot - Spring Web - Spring Security - Resilience4j -
RateLimiter - CircuitBreaker - Retry - WebClient - Actuator +
Micrometer - Redis/Postgres (optional)

------------------------------------------------------------------------

## Key Philosophy

Spring assembles objects.\
Your code stays plain Java.

Avoid framework magic.\
Prefer explicit wiring and controlled execution.

------------------------------------------------------------------------

## Final Recommendation

-   Build MCP/tool backend in Spring Boot
-   Use constructor injection
-   Add rate limiting early
-   Treat APIs as tools
-   Keep agent separate from infrastructure
