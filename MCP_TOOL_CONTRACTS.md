# MCP Tool Contracts

## Tool: `planJourney`

Plans a London journey using TfL Journey Planner.

## Request Model
`JourneyPlanRequest`

Fields:
- `from` (`String`, required): start location or stop code.
- `to` (`String`, required): destination location or stop code.
- `needAccessibleRoute` (`Boolean`, optional): accessibility preference flag for future routing extensions.

## Response Model
`JourneyPlanToolResponse`

Fields:
- `success` (`boolean`): indicates whether tool execution succeeded.
- `code` (`String`): machine-readable result/error code.
- `message` (`String`): human-readable result/error summary.
- `topJourneys` (`JourneyOptionSummary[] | null`): top 5 fastest journey options.
- `fromLocationDisambiguation` (`JourneyDisambiguationSuggestion[] | null`): top disambiguation suggestions for `from`.
- `toLocationDisambiguation` (`JourneyDisambiguationSuggestion[] | null`): top disambiguation suggestions for `to`.

### Success Semantics
- `success=true`
- `code=OK`
- `topJourneys` contains up to 5 journey summaries sorted by fastest duration.

### Error Semantics
- `success=false`
- `topJourneys=null`
- `fromLocationDisambiguation` / `toLocationDisambiguation` are populated when `code=DISAMBIGUATION_REQUIRED`.
- `code` is one of:
  - `VALIDATION_ERROR`: invalid request input.
  - `DISAMBIGUATION_REQUIRED`: TfL could not uniquely resolve `from` or `to`; use one of returned `parameterValue` values and retry.
  - `RATE_LIMIT_EXCEEDED`: TPS limit exceeded for `planJourney`.
  - `UPSTREAM_TIMEOUT`: timeout while calling TfL API.
  - `UPSTREAM_ERROR`: non-timeout upstream failure (HTTP or request error).
  - `INTERNAL_ERROR`: unexpected internal failure.

## Notes
- `TFL_API_KEY` is required at startup and must be provided via environment variable.
- Secrets are never returned in tool response payloads.
- `JourneyDisambiguationSuggestion` includes `parameterValue` (retry token) and `matchQuality`.
- Disambiguation suggestions are sorted by `matchQuality` descending when provided by TfL.
