# Future Improvements

## Centralized Token Bucket Rate Limiting (Redis + Bucket4j)

### Why
Current rate limiting is in-memory per app instance. In a multi-instance deployment, each node enforces limits independently.

### Goal
Enforce consistent TPS limits across all instances using a shared Redis-backed token bucket.

### Proposed Design
- Use `Bucket4j` with Redis as distributed state store.
- Keep rate-limit check in service layer (`Tool -> Service -> Client`) for per-tool control.
- Apply token check at start of `planJourney` execution.

### Key Strategy
Option A (recommended): per-client + per-tool bucket
- Key format: `tool:planJourney:client:<clientId>`
- Ensures fairness across clients.

Option B (simplest): single global bucket
- Key format: `tool:planJourney:global`
- Only one bucket; easier but no client-level fairness.

### What Each Redis Key Stores
Library-managed bucket state (not user-edited), conceptually:
- current token count
- refill timing state
- bucket configuration metadata (implementation-dependent)

### Memory Characteristics
- A single bucket does not grow unbounded; tokens are capped by capacity.
- Memory growth comes from number of distinct bucket keys.

### Inactive Bucket Cleanup
- Add TTL on bucket keys.
- Refresh TTL on access.
- Let Redis evict inactive buckets automatically.

### Suggested Initial Limits
- `capacity=5`
- `refill=5 tokens / 1 second`
- request cost = `1 token`

### Error Mapping
When permit is unavailable, return:
- `RATE_LIMIT_EXCEEDED`
- human message such as `Rate limit exceeded for planJourney.`

### Rollout Plan
1. Introduce `RateLimitService` abstraction.
2. Keep current in-memory limiter as default fallback.
3. Add Redis-backed Bucket4j implementation.
4. Switch service to distributed limiter via config flag.
5. Add metrics: allowed/denied counts, per-tool and optionally per-client.
