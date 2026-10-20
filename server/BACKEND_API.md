# SoccerApp backend API contract

This document describes the new APIs on the `feature/backend-auth-predictions` branch. It does not mean they are deployed to OCI.

## Server setup

The existing PostgreSQL `users` table must exist. Apply SQL migrations in order:

1. `server/sql/001_sessions.sql`
2. `server/sql/002_predictions.sql`

Set environment variables on the Ktor process:

- `SOCCER_DB_URL` (optional; existing JDBC configuration)
- `SOCCER_DB_PASSWORD` (required)
- `SOCCER_FOOTBALL_DATA_TOKEN` (required for prediction submissions and reconciliation)

Never place tokens or database passwords into Git. Use HTTPS for remotely connected Android builds.

## Authentication

`POST /auth/register` remains unchanged.

`POST /auth/login` accepts:

```json
{"email":"demo@example.com","password":"the-account-password"}
```

On success, HTTP 200:

```json
{"accessToken":"<opaque 43-character token>","expiresAt":"2026-11-19T15:00:00Z","userId":42}
```

Invalid credentials return HTTP 401. The 30-day session token is returned only on login and stored as a SHA-256 digest in PostgreSQL. Client apps should secure the access token and remove it after logout.

Protected endpoints use `Authorization: Bearer <accessToken>`.

- `GET /auth/me`: HTTP 200, `{"userId":42}`
- `POST /auth/logout`: HTTP 204, revokes the current session
- `POST /matches/{matchId}/comments`: authentication required by the production Ktor module; existing request/response shapes unchanged

## Score predictions

`PUT /matches/{matchId}/prediction` requires authentication. Body:

```json
{"homeScore":2,"awayScore":1}
```

HTTP 200:

```json
{"matchId":1234,"homeScore":2,"awayScore":1,"predictedAt":"2026-10-20T06:00:00Z","points":null}
```

Scores must be within 0..20. One prediction is stored per user and match. Repeated PUTs replace it until the kickoff deadline. The backend verifies kickoff and status against football-data.org and additionally enforces cutoff using the PostgreSQL clock. HTTP 409 indicates an expired deadline; HTTP 503 indicates a fixture-provider failure.

`GET /matches/{matchId}/prediction` returns the current user's stored prediction, including `points` when it has been scored.

## Rankings

`GET /rankings?league=PL&season=2026`

HTTP 200:

```json
[
  {"rank":1,"userId":42,"displayName":"Player #42","points":9},
  {"rank":2,"userId":51,"displayName":"Player #51","points":6}
]
```

The endpoint lists up to 100 users ranked by points for the requested league and season. Tied scores share the same dense rank. Real email addresses are not exposed in the ranking.

## Reconciliation

While the production Ktor process is running, a worker checks up to eight recent matches about every 15 minutes. For finished matches with final scores, it overwrites each prediction's points using:

- Exact score: 3
- Same match outcome (home win, draw, away win): 1
- Otherwise: 0

The operation recomputes points instead of incrementing a counter, preventing duplicate points on repeated reconciliation. Recent finished matches are eligible for correction during the seven-day reconciliation window.

## Current limitations

- Database migrations and OCI deployment are **not yet executed**.
- GitHub Actions runs route/unit tests; the new database queries are not yet covered by PostgreSQL integration tests.
- The reconciliation worker has a bounded recent-match scan rather than a production-grade queue; it should be extended for larger leagues or volumes.
- Authentication rate limiting, monitoring, backups, and privacy-friendly public aliases need hardening before general availability.
