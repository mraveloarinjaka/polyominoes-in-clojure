# AGENTS — Working with the polyominoes-in-clojure repo

## Purpose
- Audience: Agents (Codex-style assistants) operating in this repository.
- Goal: Make small, precise, well-tested edits with clear rationale.
- Scope: Clojure library for generating/manipulating polyominoes.

## Project Snapshot
- Tooling: `deps.edn` (Clojure 1.12.3), Java 21.
- Paths: Source `src`, tests `test`, dev helpers `dev`.
- Key namespaces: `polyominoes.core`, `polyominoes.generator.*` (reducer/transducer/tesser).
- Logging: `taoensso/timbre` (keep quiet in tests/CI).
- CI: GitHub Action runs `clj-watson` dependency scans (non-blocking).

## Common Commands
- Run all tests: `clojure -M:test`
- Run tests with dev CP: `clojure -M:test:dev`
- Quick single ns: `clojure -M:dev -e "(require 'clojure.test 'polyominoes.core-test) (clojure.test/run-tests 'polyominoes.core-test)'"
- Babashka tasks: `bb run-tests`, `bb opencode`
- Launch MCP server: `clojure -X:clojure-mcp :port 7888`

## Repository Layout
- Library: `src/polyominoes/*.clj`, `src/polyominoes/generator/*.clj`
- Tests: `test/polyominoes/*_test.clj`
- Docs: `doc/intro.md`, `PROJECT_SUMMARY.MD`, `README.md`
- Scripts: `bb.edn`, `scripts/tasks.clj`
- CI: `.github/workflows/clj-watson.yml`

## Workflow (Agents)
- Plan First: Summarize intent, assumptions, minimal scope before editing.
- Recon: Read `deps.edn`, target namespaces, and relevant tests first.
- Small Diffs: Prefer surgical edits; avoid broad refactors without approval.
- Safety: Don’t change git config, push, or commit unless asked.

## Editing Rules
- Structural Edits: Keep forms balanced; prefer Clojure-aware, top-level form ops.
- Minimal Scope: Touch only relevant files/defs; preserve naming and style.
- No Unrelated Fixes: Note them for follow-up; don’t bundle changes.
- Docs: Update docstrings or `doc/intro.md` if public behavior changes.

## Testing Guidelines
- Runner: `cognitect.test-runner` via `:test` alias.
- Add Tests: For new behavior or bug fixes; mirror existing test patterns.
- Granularity: Start with the affected namespace; expand only if needed.
- Quiet Logs: Tests run with reduced log noise; avoid print-based debugging.

## Code Conventions
- Idiomatic Clojure: Small, pure functions; data-first; leverage seq/transducers.
- Namespaces: Keep file-path ↔ namespace aligned.
- Logging: Use `timbre` sparingly; no debug prints in commits.
- Performance: In generators, avoid unnecessary realization; prefer `reduce`/transducers in hot paths.

## Commit & PRs
- Default: Do not commit unless explicitly requested.
- Message Style: Clear “why” over “what”; short and specific.
- Scope: Stage only relevant files; review diffs before committing.
- PRs: State motivation and impact; include minimal usage/repro notes if behavior changes.

## Security & Compliance
- Dependencies: CI runs `clj-watson`; avoid unnecessary version changes.
- Secrets: Never commit credentials/tokens; scrub debug artifacts.
- Network: Avoid external network access unless required and approved.

## Local Tooling
- Babashka: `bb run-tests`, `bb opencode` (generates `opencode.jsonc` from `opencode.edn`/`.nrepl-port`).
- Dev REPL: `clojure -M:dev` to include `dev` and `test` on classpath.

## Acceptance Checklist (per change)
- Understood: Inputs, outputs, constraints verified.
- Minimal Diff: Single, coherent change; no drive-by edits.
- Tests: New/changed behavior covered; `clojure -M:test` green.
- Docs: Public behavior changes documented.
- Clean: No debug prints; no secrets; logs minimal.

If you want, I can commit and open a PR for this file; tell me to proceed.
