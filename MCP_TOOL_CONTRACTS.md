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
- `journeyJson` (`String | null`): raw TfL response payload when successful.

### Success Semantics
- `success=true`
- `code=OK`
- `journeyJson` contains the TfL journey payload.

### Error Semantics
- `success=false`
- `journeyJson=null`
- `code` is one of:
  - `VALIDATION_ERROR`: invalid request input.
  - `UPSTREAM_TIMEOUT`: timeout while calling TfL API.
  - `UPSTREAM_ERROR`: non-timeout upstream failure (HTTP or request error).
  - `INTERNAL_ERROR`: unexpected internal failure.

## Notes
- `TFL_API_KEY` is required at startup and must be provided via environment variable.
- Secrets are never returned in tool response payloads.
