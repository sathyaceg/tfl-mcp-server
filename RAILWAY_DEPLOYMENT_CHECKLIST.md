# Railway Deployment Checklist (tfl-mcp-server)

## 1. Make App Listen on Railway `PORT`
Add this to `src/main/resources/application.properties`:

```properties
server.port=${PORT:8080}
```

Reason: Railway routes traffic and health checks through the `PORT` environment variable.

## 2. Add a Real `200` Health Endpoint
Use Spring Boot Actuator and expose health:

- Add actuator dependency in `pom.xml`:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

- Add properties:

```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.probes.enabled=true
```

Then set Railway healthcheck path to:

- `/actuator/health`

## 3. Configure Required Environment Variables in Railway
Set these in Railway service variables:

- `TFL_API_KEY`
- `MCP_SERVER_API_KEY`
- `TFL_STATION_DATA_DETAILED_DIR`

Notes:
- `TFL_API_KEY` is required by `tfl.api.key=${TFL_API_KEY}`
- `MCP_SERVER_API_KEY` is required by MCP transport auth
- `TFL_STATION_DATA_DETAILED_DIR` is required for station/toilet CSV lookup path

## 4. Provide Station Data Files
The app expects:

- `Stations.csv`
- `Toilets.csv`

at `${TFL_STATION_DATA_DETAILED_DIR}`.

Recommended on Railway:

- Mount a Volume and place files there
- Point `TFL_STATION_DATA_DETAILED_DIR` to the mounted path

Alternative:

- Package files into the container/repo and set `TFL_STATION_DATA_DETAILED_DIR` accordingly

## 5. Create Railway Service from GitHub Repo
- Connect repository to Railway
- Let Railway build Java app (Maven/Nixpacks autodetection)
- Ensure startup runs Spring Boot app successfully

## 6. Enable Public Networking and Verify Endpoints
After deploy, validate:

- Health endpoint returns `200` (`/actuator/health`)
- MCP endpoint works: `/mcp`
- Send header: `X-MCP-API-KEY: <MCP_SERVER_API_KEY>`

## 7. Optional Hardening
- Tune rate limiter values per environment
- Restrict public access if needed
- Keep secrets only in Railway variables
- Add a dedicated non-protected health endpoint if you don't want actuator

## References
- https://docs.railway.com/deployments/healthchecks
- https://docs.railway.com/public-networking
- https://docs.railway.com/deploy/volumes
- https://docs.railway.com/volumes/reference
