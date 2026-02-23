# MCP Agent Connection Guide (Stateless HTTP)

## Transport Endpoint
Configured in `application.properties`:
- Stateless MCP endpoint: `/mcp`

Default local base URL:
- `http://localhost:8080`

## Required Authentication
MCP transport is protected by API key header:
- Header: `X-MCP-API-KEY`
- Value: `${MCP_SERVER_API_KEY}`

Environment variables required at startup:
- `TFL_API_KEY`
- `MCP_SERVER_API_KEY`

## Important Request Headers
For stateless MCP requests in this setup, include:
- `Content-Type: application/json`
- `Accept: application/json, text/event-stream`
- `X-MCP-API-KEY: <value>`

If `Accept` is missing, the server can return `HTTP 400` with empty body.

## Working Manual Calls

### 1. List available tools
```bash
curl -i -sS -X POST "http://localhost:8080/mcp" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "X-MCP-API-KEY: $MCP_SERVER_API_KEY" \
  -d '{"jsonrpc":"2.0","id":"1","method":"tools/list"}'
```

### 2. Call `planJourney`
```bash
curl -i -sS -X POST "http://localhost:8080/mcp" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "X-MCP-API-KEY: $MCP_SERVER_API_KEY" \
  -d '{
    "jsonrpc":"2.0",
    "id":"2",
    "method":"tools/call",
    "params":{
      "name":"planJourney",
      "arguments":{
        "request":{
          "from":"Waterloo",
          "to":"Victoria",
          "needAccessibleRoute":false
        }
      }
    }
  }'
```

## Runtime Troubleshooting
- `401 Unauthorized`: `X-MCP-API-KEY` missing or wrong.
- `400` with empty body: missing/incorrect `Accept` header.
- Tool returns `VALIDATION_ERROR` with text like `Not enough variable values available to expand 'TFL_API_KEY'`:
  - App process started without `TFL_API_KEY`.
  - Fix by setting env vars in the same process that starts the app, then restart.

Example:
```bash
export TFL_API_KEY='your-real-tfl-key'
export MCP_SERVER_API_KEY='your-mcp-key'
mvn spring-boot:run
```

## Python Example (Raw HTTP)
```python
import os
import requests

base_url = "http://localhost:8080"
headers = {
    "Content-Type": "application/json",
    "Accept": "application/json, text/event-stream",
    "X-MCP-API-KEY": os.environ["MCP_SERVER_API_KEY"],
}

payload = {
    "jsonrpc": "2.0",
    "id": "1",
    "method": "tools/list"
}

resp = requests.post(f"{base_url}/mcp", headers=headers, json=payload)
print(resp.status_code, resp.text)
```

## Notes
- Keep API keys in environment variables only; never commit keys.
- Use `tools/list` first to verify expected input schema before calling tools.
