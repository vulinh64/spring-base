# Backend Modification Steps

> Session UUID: `f704f5e0-81b3-4490-8537-fb614607b50e`

Changes made to the Spring Boot backend to support the frontend application.

## API Context Path
- Added `server.servlet.context-path: /api` in `application.yaml`
- All backend endpoints now served under `/api` prefix (e.g., `/api/post`, `/api/category`)

## Authentication - BFF Pattern

Moved from Keycloak JS redirect flow to a Backend-For-Frontend (BFF) pattern where the backend proxies authentication and manages tokens via HTTP-only cookies.

### New Files
- **`service/keycloak/KeycloakAuthExchange.java`** — HTTP exchange interface (moved from test code) with two methods:
  - `getToken()` — exchanges username/password for tokens via Keycloak's `grant_type=password`
  - `refreshToken()` — exchanges refresh token for new tokens via `grant_type=refresh_token`
- **`data/dto/response/KeycloakTokenResponse.java`** — record with `access_token` and `refresh_token` fields (moved from test code)
- **`data/dto/request/LoginRequest.java`** — record with `username` and `password` fields
- **`controller/api/AuthAPI.java`** — auth endpoint interface:
  - `POST /auth/login` — login with credentials, sets HTTP-only cookies
  - `POST /auth/refresh` — refresh tokens from `refresh_token` cookie
  - `POST /auth/logout` — clears token cookies
  - `GET /auth/me` — returns current user info from JWT (requires authentication)
- **`controller/impl/AuthController.java`** — implementation that:
  - Calls `KeycloakAuthExchange` to exchange credentials/refresh tokens
  - Sets `access_token` (5min) and `refresh_token` (30min) as HTTP-only, Secure, SameSite=Strict cookies
  - Returns user info via `SecurityUtils.getUserDTOOrThrow()` for `/auth/me`
- **`configuration/RestClientConfiguration.java`** — wires `KeycloakAuthExchange` bean with `RestClient` pointing at Keycloak issuer URI

### Modified Files
- **`data/constant/EndpointConstant.java`** — added `ENDPOINT_AUTH = "/auth"`
- **`application.yaml`** — changed `DELETE /auth/logout/**` to `POST /auth/**` in `no-authenticated-verb-urls`
- **`configuration/SecurityConfiguration.java`** — added `BearerTokenResolver` bean that reads JWT from both:
  - Standard `Authorization: Bearer` header
  - `access_token` HTTP-only cookie

### Deleted Test Files (now in production)
- `test/.../keycloak/KeycloakAuthExchange.java`
- `test/.../data/KeycloakTokenResponse.java`
- `test/.../configuration/RestClientConfiguration.java`
- `test/.../it/IntegrationTestBase.java` — updated import to production `KeycloakAuthExchange`

## HTML Safelist - Details/Summary Tags

Extended the JSoup HTML safelist to allow `<details>` and `<summary>` tags in post content, enabling collapsible sections in Markdown.

### Modified Files
- **`utils/TextSanitizer.java`** — changed `DEFAULT_SAFELIST` from `Safelist.relaxed()` to `Safelist.relaxed().addTags("details", "summary")`
  - Applies to both `sanitize()` and `validateAndPassThrough()` methods
  - All 18 `TextSanitizerTest` tests pass

## Post Content - XSS Validation

Changed post content handling from silent sanitization to strict validation with reject-on-failure.

### New Files
- **`exception/XSSViolationException.java`** — detailed exception for backend investigation with:
  - `fieldName` — which field failed validation
  - `offendingContent` — truncated raw input (max 500 chars)
  - `sanitizedContent` — what Jsoup would have produced (shows the diff)
  - Static factory method `XSSViolationException.of(fieldName, rawContent, sanitizedContent)`

### Modified Files
- **`utils/TextSanitizer.java`** — added `validateAndPassThrough(text, fieldName)` method:
  - Uses `Jsoup.isValid(text, Safelist.relaxed())` to check content
  - If valid: returns raw content unchanged (preserves markdown formatting)
  - If invalid: throws `XSSViolationException` with raw vs sanitized comparison
  - Existing `sanitize()` method unchanged (still used for title, excerpt, slug, tags)
- **`data/dto/request/PostCreationRequest.java`** — `postContent` field changed:
  - Before: `postContent = TextSanitizer.sanitize(StringUtils.normalizeSpace(postContent))`
  - After: `postContent = TextSanitizer.validateAndPassThrough(postContent, "postContent")`
  - XSS validation via Jsoup (`Safelist.relaxed()`) — rejects disallowed HTML with 400
  - Whitespace/line breaks preserved for markdown formatting
- **`exception/GlobalExceptionHandler.java`** — added `XSSViolationException` handler:
  - Returns HTTP 400 Bad Request
  - Logs at INFO level: field name, offending content, and sanitized version
- **`locale/ServiceErrorCode.java`** — added `MESSAGE_XSS_VIOLATION("M9108")`
- **`i18n/messages.properties`** — added `M9108=Content contains potentially dangerous HTML in field [%s]`
- **`i18n/messages_vi.properties`** — added Vietnamese translation for M9108

## Category Search - Post Count

Added post count to category search results using a single QueryDSL query (no N+1).

### New Files
- **`data/projection/CategoryPostCountProjection.java`** — record projection: `(UUID id, String categorySlug, String displayName, long postCount)`

### Modified Files
- **`data/dto/response/CategoryResponse.java`** — added `long postCount` field
- **`data/mapper/CategoryMapper.java`** — added `@Mapping(target = "postCount", constant = "0L")` to `toDto()` for entity-to-DTO mapping (used by `createCategory`)
- **`service/category/CategoryService.java`** — added `searchWithPostCount()` method:
  - Uses `JPAQueryFactory` with QueryDSL
  - `LEFT JOIN Post ON post.category = category` with `GROUP BY` for count aggregation
  - `Projections.constructor(CategoryPostCountProjection.class, ...)` for type-safe projection
  - `PageableExecutionUtils.getPage()` for pagination with lazy count query
  - Reuses `PredicateBuilder.likeIgnoreCase()` for `WHERE` clause (shared between fetch and count queries)
- **`controller/impl/CategoryController.java`** — `searchCategories()` now calls `searchWithPostCount()` instead of generic `search()`

## Jackson Serialization

- **`application.yaml`** — added `spring.jackson.serialization.write-dates-as-timestamps: false`
  - `Instant` fields now serialize as ISO-8601 strings (e.g., `2026-03-22T11:32:00Z`) instead of epoch seconds
  - Fixes frontend date parsing issues (epoch seconds were interpreted as milliseconds → showed 1970 dates)

## Post Slug-Based Identity Resolution

Changed `getSinglePost` from UUID-only to accept both UUID and slug, enabling slug-based URLs on the frontend.

### Modified Files
- **`data/repository/PostRepository.java`** — added `Optional<Post> findBySlug(String slug)`
- **`service/post/PostValidationService.java`** — added `getPostByIdentity(String identity)`:
  - Tries `UUID.fromString(identity)` first → looks up by UUID
  - On `IllegalArgumentException` → falls back to `findBySlug(identity)`
  - Throws post-not-found error if neither match
- **`service/post/PostService.java`** — `getSinglePost` parameter changed from `UUID postId` to `String identity`, delegates to `getPostByIdentity()`
- **`controller/api/PostAPI.java`** — `getSinglePost` path variable changed from `UUID postId` to `String identity`
- **`controller/impl/PostController.java`** — updated to pass `String identity` through

### Database Migration
- **`db/changelog/sql/0018-add-unique-slug-constraint.sql`** — adds `UNIQUE` constraint on `post.slug` column (not `post_revision`)
- **`db/changelog/db.changelog-master.yaml`** — includes migration `0018`

## Comment Author & Count

Added comment author info and per-post comment count to support frontend features.

### Modified Files
- **`data/dto/response/SingleCommentResponse.java`** — added `AuthorResponse author` field
- **`service/comment/CommentFetchingService.java`** — added `LEFT JOIN` on Author entity with `Projections.constructor(AuthorResponse.class, ...)` for type-safe author projection (no N+1)
- **`data/repository/CommentRepository.java`** — added `long countByPostId(UUID postId)` derived query
- **`data/dto/response/SinglePostResponse.java`** — added `long commentCount` field with `@With` for immutable update
- **`service/post/PostService.java`** — `getSinglePost()` now queries comment count via `CommentRepository` and sets via `withCommentCount()`
- **`data/mapper/PostMapper.java`** — added `@Mapping(target = "commentCount", constant = "0L")` to `toSinglePostDTO()`

## Comment Content - Markdown Support

Changed comment content handling from sanitization to validation-only, preserving markdown formatting.

### Modified Files
- **`data/dto/request/NewCommentRequest.java`** — `content` field changed:
  - Before: `content = TextSanitizer.sanitize(StringUtils.normalizeSpace(content))`
  - After: `content = TextSanitizer.validateAndPassThrough(content, "content")`
  - Markdown formatting (line breaks, code blocks, etc.) now preserved
  - XSS validation still enforced via Jsoup safelist
