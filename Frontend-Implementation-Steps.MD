# Frontend Implementation Steps

> Session UUID: `f704f5e0-81b3-4490-8537-fb614607b50e`

## Tech Stack
- **React + Vite + TypeScript** (SPA, no SSR)
- **React Router v7** for routing
- **TanStack Query** for server state / caching
- **Axios** for HTTP (withCredentials for HTTP-only cookie auth)
- **Tailwind CSS** (dark mode only)
- **react-markdown + remark-gfm** for rendering markdown content

## Phase 1 - Foundation (Complete)
- [x] Vite + React + TypeScript scaffold
- [x] Tailwind CSS setup (dark mode only)
- [x] Path alias (`@/` -> `src/`)
- [x] Vite dev proxy (`/api` -> `localhost:8088/api`)
- [x] TypeScript types mirroring all backend DTOs (`GenericResponse`, `Page`, `Pageable`, post/category/comment/user types)
- [x] Axios client with `GenericResponse` unwrapping and `withCredentials: true`
- [x] Auth layer: BFF pattern via `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`, `GET /auth/me`
- [x] HTTP-only cookie auth (access_token + refresh_token cookies set by backend)
- [x] AuthProvider context (user info, roles, auto-refresh)
- [x] Custom login page (`/login`)
- [x] Auth-guarded routes (redirect to `/login` if not authenticated)
- [x] Shared layout: Header (nav + login/logout) and PageLayout
- [x] Common components: LoadingSpinner, ErrorBanner, Pagination
- [x] TanStack Query hooks: `usePosts`, `usePost`, `usePostRevisions`, `useCategories`, `useComments`
- [x] API modules: posts, categories, comments, subscriptions
- [x] Utility functions: date formatting, role checks

## Phase 2 - Read-Only Features (Complete)
- [x] **Home page** (`/`): paginated post list with PostCard components (category badge, date, title, excerpt)
- [x] **Post detail page** (`/post/:slug`): full post with category, tags, dates, markdown content rendered via `react-markdown`
- [x] **Comments section**: paginated comment list under each post with "(edited)" indicator
- [x] **Categories page** (`/categories`): search form + paginated grid of category cards with post count
- [x] Tailwind Typography plugin for markdown content styling (`prose prose-invert`)

## Phase 3 - Authenticated Features (Complete)
- [x] **Post editor** (`/post/new`, `/post/:slug/edit`): form with title, slug, excerpt, category dropdown, comma-separated tags, markdown textarea editor
- [x] **Create post**: submits to `POST /post`, redirects to new post detail by slug
- [x] **Edit post**: pre-populates form from existing post (fetched by slug), submits to `PATCH /post/:id` (UUID)
- [x] **Delete post**: confirmation dialog on post detail page, calls `DELETE /post/:id` (UUID)
- [x] **Add comment**: inline form below post (visible when authenticated)
- [x] **Edit comment**: inline edit mode on each comment with save/cancel
- [x] **Subscribe to post**: button on post detail (non-author only)
- [x] **Follow author**: button on post detail (non-author only)
- [x] Common components: MarkdownEditor, ConfirmDialog

## Phase 4 - Admin Features & Polish (Complete)
- [x] **Category management**: admin-only create form and delete button on categories page (role-gated via `isAdmin()`)
- [x] **Post revisions page** (`/post/:slug/revisions`): resolves slug to post UUID, then fetches revision history; "Apply" button with confirmation dialog
- [x] Role-based UI: category create/delete only visible to ADMIN users
- [x] Toast notification system: `ToastProvider` with `useToast()` hook, auto-dismiss after 4s, slide-in animation
- [x] Toast feedback on: post create/edit/delete, comment post, subscribe/follow, category create/delete, revision apply
- [x] Error toasts on all mutation failures

## Post-Phase - Markdown Migration
- [x] Replaced TipTap rich text editor with plain markdown textarea (`MarkdownEditor`)
- [x] Replaced `dangerouslySetInnerHTML` with `react-markdown` + `remark-gfm` (`MarkdownRenderer`)
- [x] Removed TipTap dependencies (`@tiptap/react`, `@tiptap/starter-kit`, `@tiptap/pm`)
- [x] Added `react-markdown` and `remark-gfm` dependencies
- [x] Removed unused `RichTextEditor` component
- [x] Bundle size reduced from ~700KB to ~485KB

## Post-Phase - Markdown Enhancements
- [x] **Comment Markdown rendering**: comments now render via `MarkdownRenderer` instead of plain text
- [x] **Comment Write/Preview toggle**: both `CommentForm` (new comment) and `CommentItem` (edit mode) have Write/Preview tabs
- [x] **Post editor full-screen preview**: "Preview" button renders article preview mimicking `PostDetailPage` layout (title, category badge, excerpt, tags, rendered content); "Back to Editor" returns to form with all state preserved
- [x] **`<details>` / `<summary>` HTML support**: added `rehype-raw` plugin to `MarkdownRenderer` for raw HTML passthrough in Markdown
- [x] **`<details>` styling**: rounded border, gray default, blue glow on hover, `<summary>` border-bottom only when `[open]`, hover color sync
- [x] **GitHub-style admonitions**: `> [!NOTE]`, `> [!TIP]`, `> [!IMPORTANT]`, `> [!WARNING]`, `> [!CAUTION]` blockquote syntax
  - Uses `remark-github-blockquote-alert` package (replaced custom Docusaurus-style `:::` directives)
  - 5 alert types with colored left border, thin outer border matching on hover (like `<details>`)
  - CSS styles in `index.css` matching GitHub dark mode aesthetics
- [x] **Syntax highlighting**: `rehype-highlight` with `highlight.js` (`github-dark` theme)
  - Supports all common languages (Java, JS/TS, Python, C#, Go, Rust, Dockerfile, shell, etc.)
- [x] **Code block copy & word wrap buttons**: hover-only buttons (top-right) like Docusaurus
  - `CodeBlock` wrapper component with `extractText()` helper for clipboard copy
  - Tailwind `group/code` + `group-hover/code:opacity-100` for hover visibility
  - Word wrap toggles `white-space: pre-wrap` via CSS class
- [x] **Custom fonts**:
  - **JetBrains Mono** for `code`, `pre code`, `kbd` — loaded via `@font-face` (4 variants: regular, bold, italic, bold-italic) from `/fonts/*.woff2`
  - **Noto Sans** + **Noto Color Emoji** as default body font — loaded via Google Fonts `@import`
- [x] New dependencies: `rehype-raw`, `remark-github-blockquote-alert`, `rehype-highlight`, `highlight.js`
- [x] Removed dependency: `remark-directive` (no longer needed after switching from Docusaurus-style admonitions)

## Post-Phase - Category Post Count
- [x] Categories page now displays the number of posts per category
- [x] Backend uses QueryDSL `LEFT JOIN + COUNT + GROUP BY` query (single query, no N+1)
- [x] `CategoryPostCountProjection` record used as type-safe projection
- [x] `CategoryResponse` extended with `postCount` field
- [x] Categories page loads max 10 categories per page (was 20)

## Post-Phase - Date/Time Formatting
- [x] Fixed date display (`NaN` / `Jan 1, 1970`) caused by field name mismatch and epoch-seconds serialization
- [x] `SinglePostResponse` type: renamed `createdDate`/`updatedDate` to `createdDateTime`/`updatedDateTime` to match backend
- [x] `formatDateTime()` now produces client-local format: `2026-03-22 18:32 UTC+7`
- [x] `formatDate()` now produces: `2026-03-22`

## Post-Phase - Token Expiry & Session Draft Preservation
- [x] **401 interceptor with auto-refresh**: on 401, attempts token refresh; if refresh succeeds, retries the original request automatically
- [x] **Concurrent request handling**: multiple simultaneous 401s share a single refresh call (`isRefreshing` guard)
- [x] **Redirect to login**: if refresh fails, redirects to `/login` preserving the current path via `?redirect=` query param
- [x] **AuthGuard redirect**: passes `location.pathname + search` via React Router `state.from`
- [x] **Login redirect-back**: after successful login, navigates back to the original page with `replace: true`
- [x] **Session draft system** (`sessionDraft.ts` + `useSessionDraft` hook):
  - On `session-expired` event, forms save current state + userId to `sessionStorage`
  - On page load, forms restore draft from `sessionStorage` if available
  - Drafts cleared on successful form submission
- [x] **User identity check on login**:
  - Same user → redirect back to original page, drafts restored automatically
  - Different user → `clearAllDrafts()`, redirect to `/`, toast: "Logged in as a different user. Unsaved work was discarded."
- [x] **Draft-enabled forms**: PostEditorPage (create & edit), CommentForm
- [x] **SSR-safe**: `typeof window` guards on `sessionStorage`, `window.dispatchEvent`, and `window.location` access
- [x] `AuthProvider.login()` returns userId for identity comparison

## Post-Phase - Comment Enhancements
- [x] **Comment author display**: username shown with email tooltip on each comment
- [x] **Lazy-loaded comments**: "Comments (N)" toggle button; comments fetched only when expanded
  - `useComments` hook accepts `enabled` flag for conditional fetching
  - Comment count displayed immediately from `SinglePostResponse.commentCount`
- [x] **Comment count on post**: backend provides `commentCount` in single post response; displayed in toggle button
- [x] **Comment form at bottom**: separated from comment list by `<hr>`, only visible when authenticated
- [x] **Comment card styling**: each comment in a rounded bordered card with blue glow on hover (matches `<details>` style)
- [x] **Full markdown support in comments**: backend uses `validateAndPassThrough()` (preserves formatting, validates XSS)
- [x] `SingleCommentResponse` type updated with `author: AuthorResponse | null`
- [x] `SinglePostResponse` type updated with `commentCount: number`

## Post-Phase - UI Polish
- [x] **Fully clickable cards**: `PostCard` and category cards wrapped in `<Link>` — entire card is clickable
  - Category badge in `PostCard` uses `stopPropagation()` to remain independently clickable
  - Admin delete button on category cards uses `preventDefault()` + `stopPropagation()`
- [x] **Full datetime in post list**: `PostCard` shows `formatDateTime()` instead of `formatDate()`
- [x] **Updated timestamp**: `PostCard` shows "updated {datetime}" when `updatedDateTime !== createdDateTime`
- [x] **Route param fix**: `App.tsx` routes changed from `:id` to `:slug` for edit and revisions pages (fixed empty editor form)

## Post-Phase - Slug-Based Post URLs
- [x] **Routes changed** from `/post/:id` (UUID) to `/post/:slug`
- [x] `PostCard` links to `/post/${post.slug}` instead of `/post/${post.id}`
- [x] `PostDetailPage`: fetches post by slug via `GET /post/:slug`, uses `post.id` (UUID) for mutations (delete, subscribe)
- [x] `PostEditorPage`: fetches by slug for edit mode, uses `existingPost.id` for `PATCH` API, navigates to slug after create/edit
- [x] `PostRevisionsPage`: resolves slug → post via `usePost(slug)`, uses `post.id` for revision API calls
- [x] `postApi.getByIdentity(identity)` renamed from `getById(id)` — backend accepts both UUID and slug
- [x] `usePost(identity)` hook updated to accept slug or UUID

## Backend Changes for Frontend Support
- [x] `server.servlet.context-path: /api` added to expose all endpoints under `/api`
- [x] `KeycloakAuthExchange` moved from test to production (`service/keycloak/`)
- [x] `KeycloakTokenResponse` moved to `data/dto/response/`
- [x] New `AuthAPI` + `AuthController`: BFF auth endpoints (`/auth/login`, `/auth/refresh`, `/auth/logout`, `/auth/me`)
- [x] `BearerTokenResolver` bean: reads JWT from both `Authorization` header and `access_token` cookie
- [x] `RestClientConfiguration` bean wiring `KeycloakAuthExchange` in production
- [x] Security config updated: `POST /auth/**` permitted without authentication
- [x] `LoginRequest` DTO added
- [x] `PostCreationRequest`: `postContent` uses `validateAndPassThrough()` (XSS validation, not sanitization)
- [x] `getSinglePost` accepts `String identity` — resolves by UUID or slug
- [x] `PostRepository.findBySlug()` added
- [x] Unique constraint on `post.slug` column (Liquibase migration `0018`)

## Project Structure
```
frontend/
├── vite.config.ts
├── index.html
└── src/
    ├── api/              # Axios client (401 interceptor + auto-refresh) + per-resource API modules
    ├── auth/             # keycloak.ts (BFF calls), AuthProvider.tsx
    ├── components/
    │   ├── layout/       # Header, PageLayout, AuthGuard
    │   ├── common/       # LoadingSpinner, ErrorBanner, Pagination, MarkdownEditor, MarkdownRenderer, ConfirmDialog, Toast
    │   ├── post/         # PostCard, PostList
    │   ├── comment/      # CommentItem, CommentForm, CommentList
    │   └── category/     # CategoryBadge
    ├── hooks/            # TanStack Query hooks + useSessionDraft
    ├── pages/            # Route pages
    ├── types/            # TypeScript types mirroring backend DTOs
    └── utils/            # Date formatting, role checks, sessionDraft
```
