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

## Manual Connectivity Check

### Send MCP JSON-RPC message (stateless)
```bash
curl -X POST "http://localhost:8080/mcp" \
  -H "Content-Type: application/json" \
  -H "X-MCP-API-KEY: $MCP_SERVER_API_KEY" \
  -d '{
    "jsonrpc": "2.0",
    "id": "1",
    "method": "tools/list"
  }'
```

## Python Example (Raw HTTP)
```python
import os
import requests

base_url = "http://localhost:8080"
headers = {
    "Content-Type": "application/json",
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
- Requests to `/mcp` without valid `X-MCP-API-KEY` return `401`.
- Keep API keys in environment variables only; never commit keys.
