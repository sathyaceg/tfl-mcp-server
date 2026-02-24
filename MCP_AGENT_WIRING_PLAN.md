# MCP Agent Wiring Plan

## Objective
Wire `tfl-mcp-server` to agent clients while preserving architecture constraints from `AGENTS.md` and `ARCHITECTURE_SUMMARY.md`.

## Status Legend
- `[x]` Done
- `[~]` Partially done
- `[ ]` Not started

## Phase 1: MCP Server Contract Hardening
- `[x]` Enforce layering: `Tool -> Service -> Client -> External`
- `[x]` Introduce request model for tool input (`JourneyPlanRequest`)
- `[x]` Startup fail-fast for missing `TFL_API_KEY`
- `[x]` Introduce explicit output model (replace raw `String` payloads)
- `[x]` Standardize tool error mapping (validation vs upstream vs timeout)
- `[x]` Document contract-level error semantics for agent consumers

Completion note:
- Added `JourneyPlanToolResponse` and standardized error codes in `JourneyPlannerErrorCode`.
- Added contract documentation in `MCP_TOOL_CONTRACTS.md`.

## Phase 2: Agent Connectivity (MCP Transport)
- `[x]` Confirm MCP endpoint(s) and connection details for stateless HTTP
- `[x]` Add and document transport authentication (API key or bearer token)
- `[x]` Add connection examples for an agent client

Completion note:
- Migrated to stateless MCP protocol with `/mcp` endpoint.
- Added API-key protection for MCP transport using `X-MCP-API-KEY` backed by `${MCP_SERVER_API_KEY}`.
- Added stateless connection examples in `MCP_CONNECTION_GUIDE.md`.

## Phase 3: Guardrails and Operations
- `[x]` Add per-tool rate limiting (TPS)
- `[ ]` Add concurrency caps
- `[ ]` Add bounded retries and circuit breaker policies for TfL API
- `[ ]` Enforce response-size and input safety limits
- `[ ]` Add structured logs and metrics for tool invocations

Completion note for 3.1:
- Added Resilience4j rate limiter for `planJourney`.
- Added rate limiter properties in `application.properties`.
- Added `RATE_LIMIT_EXCEEDED` tool error mapping when no permit is available.

## Phase 4: Agent Connector and Runbook
- `[ ]` Implement a minimal reference agent connector (Java or Python)
- `[ ]` Validate end-to-end tool invocation from agent to MCP server
- `[ ]` Add `RUNBOOK.md` with startup, config, and verification steps

## Current Overall Status
- `Phase 1`: **Complete**
- `Phase 2`: **Complete**
- `Phase 3`: **Partially complete (3.1 done)**
- `Phase 4`: **Not started**

## Update Policy
As each task is completed, update this file in the same commit by changing status markers and (if needed) adding a short completion note under that task.
