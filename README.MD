# TfL MCP Server

Spring Boot MCP server that exposes Transport for London journey-planning and station-facility tools.

This project is under active development and is not ready for unrestricted public use.

## Requirements

- Java 17
- Maven 3.9+
- A TfL API key
- TfL detailed station data containing `Stations.csv` and `Toilets.csv`

## Configuration

The application requires these environment variables:

| Variable | Purpose |
| --- | --- |
| `TFL_API_KEY` | Authenticates outbound requests to the TfL API. |
| `MCP_SERVER_API_KEY` | Temporary bootstrap key accepted from MCP clients. |
| `MCP_API_KEY_PEPPER` | Secret used to hash per-client API keys in the planned database-backed authentication flow. |
| `TFL_STATION_DATA_DETAILED_DIR` | Directory containing the TfL station CSV files. |

Generate development secrets with:

```bash
export MCP_SERVER_API_KEY="$(openssl rand -hex 32)"
export MCP_API_KEY_PEPPER="$(openssl rand -hex 32)"
```

Set the remaining variables without committing their values:

```bash
export TFL_API_KEY="your-tfl-api-key"
export TFL_STATION_DATA_DETAILED_DIR="/absolute/path/to/TfL station data detailed"
```

## Run Locally

```bash
mvn spring-boot:run
```

The MCP endpoint is available at `http://localhost:8080/mcp` and requires the configured API key in the
`X-MCP-API-KEY` header.

List the available tools:

```bash
curl --fail-with-body http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H "X-MCP-API-KEY: ${MCP_SERVER_API_KEY}" \
  --data '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}'
```

Requests with a missing or incorrect key receive an HTTP `401` response.

## Authentication Status

The current implementation validates one bootstrap key loaded from `MCP_SERVER_API_KEY`. The next authentication
milestone will issue separate client keys, store only their hashes in PostgreSQL, and use `MCP_API_KEY_PEPPER` when
validating them. The bootstrap key will remain temporary during that migration.

Never commit API keys, peppers, database credentials, or local environment files.

## Verify

```bash
mvn verify
```

Tool request and response contracts are documented in [MCP_TOOL_CONTRACTS.md](MCP_TOOL_CONTRACTS.md). The current
Railway preparation checklist is in [RAILWAY_DEPLOYMENT_CHECKLIST.md](RAILWAY_DEPLOYMENT_CHECKLIST.md).
