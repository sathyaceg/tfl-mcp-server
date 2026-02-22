# AGENTS.md — Project Operating Instructions

## Project Purpose

This repository implements a **production-style always-on backend service**.

Primary goals:

* Stable long-running service
* Strict TPS and concurrency control
* External API integrations
* Tool-style APIs usable by agents or MCP clients
* Production-grade architecture even for personal use

---

## Core Architecture

Agent / Client
→ MCP Interface
→ Spring Boot Web Service
→ Service Layer
→ External APIs / Database

The Java service is the **source of truth** for business logic and guardrails.

Agents must never bypass service APIs.

---

## Technology Stack

* Java
* Spring Boot
* Spring DI (constructor injection only)
* Resilience4j
* WebClient
* Actuator + Micrometer

Optional:

* Redis
* PostgreSQL

---

## Dependency Injection Rules

MANDATORY:

* Use constructor injection only
* Do NOT use `@Autowired` on fields
* Dependencies must be `final`
* Beans created only via:

    * `@Component`
    * `@Service`
    * `@Repository`
    * `@Configuration`
    * `@Bean`

Spring is responsible only for wiring.

Business logic must remain plain Java.

---

## Layering Rules

Allowed dependency direction:

Controller → Service → Client → External System
Tool → Service → Client → External System

Forbidden:

* Controller calling external APIs directly
* Tool classes calling clients/external APIs directly
* Circular dependencies
* Business logic inside controllers
* Business logic inside tools

---

## Guardrails (CRITICAL)

All implementations MUST enforce:

### Rate Limiting

* TPS limits per API/tool
* Burst protection
* Per-client limits

### Reliability

* Timeouts required for all external calls
* Circuit breakers for remote services
* Retries must be bounded

### Safety

* Validate all inputs
* Limit response size
* Avoid uncontrolled loops

---

## External API Access

Rules:

* API keys MUST come from environment variables
* Never hardcode secrets
* Never log secrets
* Fail startup if secrets missing

Example:
external.api.key=${SOME_API_KEY}

---

## Coding Philosophy

Prefer:

* Explicitness over magic
* Small services
* Immutable objects
* Fail-fast startup
* Observable systems

Avoid:

* hidden framework behavior
* reflection-heavy patterns
* global mutable state

---

## Agent Behavior Expectations

When modifying code:

1. Preserve architecture layers.
2. Do not introduce field injection.
3. Respect rate limiting patterns.
4. Prefer extending services over bypassing them.
5. Maintain operational guardrails.

If unsure:
→ Always, ask.

### Agent Developer Guidelines
1. Always make sure you're not editing in **main** branch. Agents are forbidden to do any edits in main branch.
---

## Non-Goals

* Experimental framework usage
* Over-abstraction
* Premature microservices
* Direct agent → external API access

---

## Definition of Done

Changes must:

* compile
* follow DI rules
* respect layering
* maintain guardrails
* not expose secrets
