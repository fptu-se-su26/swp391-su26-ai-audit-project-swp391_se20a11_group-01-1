# Backend API error contract

All REST and Spring Security errors use the same JSON structure. Clients should branch on `code`; `message` is intended for display and may be refined without changing the contract.

```json
{
  "timestamp": "2026-07-22T00:00:00+07:00",
  "status": 404,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User not found",
  "path": "/api/users/999",
  "correlationId": "4b3ffdf6-1aa1-4e6b-8e68-d8dbd476fcab",
  "fieldErrors": []
}
```

The server accepts an `X-Correlation-ID` request header containing 1–100 letters, digits, `.`, `_`, or `-`. Invalid or missing values are replaced with a generated UUID. The selected value is returned in the response header and body and is included in server logs.

## Error codes

| HTTP | Code | Meaning |
|---:|---|---|
| 400 | `VALIDATION_ERROR` | Bean validation or request-field failure; see `fieldErrors` |
| 400 | `MALFORMED_REQUEST` | Invalid JSON or unreadable request body |
| 400 | `BUSINESS_RULE_VIOLATION` | Request violates a domain rule |
| 401 | `AUTHENTICATION_REQUIRED` | Authentication is missing or invalid |
| 401 | `INVALID_CREDENTIALS` | Email/password authentication failed |
| 401 | `INVALID_TOKEN` | Access token is malformed, expired, or invalid |
| 401 | `SESSION_REJECTED` | Refresh session is missing, expired, or revoked |
| 403 | `ACCESS_DENIED` | Authenticated principal lacks permission |
| 404 | `RESOURCE_NOT_FOUND` | Requested resource does not exist |
| 405 | `METHOD_NOT_ALLOWED` | HTTP method is unsupported for the path |
| 409 | `RESOURCE_CONFLICT` | Duplicate or conflicting resource state |
| 409 | `CONCURRENT_UPDATE` | Concurrent database update conflict |
| 409 | `PAYMENT_ALREADY_PROCESSED` | Payment has already been completed |
| 409 | `INVALID_ORDER_STATE` | Order state does not allow the operation |
| 429 | `RATE_LIMIT_EXCEEDED` | Request limit was exceeded |
| 503 | `EXTERNAL_SERVICE_UNAVAILABLE` | Required external provider is unavailable |
| 500 | `INTERNAL_ERROR` | Unexpected server failure |

Unexpected exceptions are logged with their stack trace and correlation ID. Their internal exception messages are never returned to clients.

## Adding a domain error

Prefer an existing typed exception from `com.rms.restaurant_management_system.error`. Add a new `ErrorCode` only when clients need to distinguish the condition programmatically. Do not throw a generic `RuntimeException` for expected client or domain errors, and do not expose raw third-party exception messages.
