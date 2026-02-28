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

## Tool: `stationToilets`

Looks up toilet facilities for a station using local TfL station data.

## Request Model
`StationToiletLookupRequest`

Fields:
- `stationName` (`String`, required): user-facing station name (for example `Abbey Wood`).

## Response Model
`StationToiletToolResponse`

Fields:
- `success` (`boolean`): indicates whether tool execution succeeded.
- `code` (`String`): machine-readable result/error code.
- `message` (`String`): human-readable result/error summary.
- `stationName` (`String | null`): resolved station name.
- `stationUniqueId` (`String | null`): resolved station unique id from TfL station data.
- `toilets` (`StationToiletDetail[] | null`): toilet rows for the station.
- `stationDisambiguation` (`StationDisambiguationSuggestion[] | null`): station alternatives when input is ambiguous.

`StationToiletDetail` fields:
- `location` (`String | null`): location text from source data.
- `platformNumbers` (`int[]`): extracted platform numbers from location text when present.
- `accessible` (`boolean`)
- `babyChanging` (`boolean`)
- `insideGateLine` (`boolean`)
- `feeCharged` (`boolean`)
- `type` (`String`)

### Success Semantics
- `success=true`
- `code=OK`
- `toilets` may be empty if no toilet facilities are listed for the resolved station.

### Error Semantics
- `success=false`
- `code` is one of:
  - `VALIDATION_ERROR`: invalid request input.
  - `STATION_NOT_FOUND`: no matching station found.
  - `DISAMBIGUATION_REQUIRED`: multiple stations matched input.
  - `RATE_LIMIT_EXCEEDED`: TPS limit exceeded for `stationToilets`.
  - `DATA_NOT_AVAILABLE`: station data file load/read failure.
  - `INTERNAL_ERROR`: unexpected internal failure.

## Local Data Notes
- `tfl.station-data.detailed-dir` points to the local detailed station dataset directory.
- Default path: `${user.home}/Downloads/TfL station data detailed`.
