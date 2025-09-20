# AGENTS — polyominoes-in-clojure

## Commands
- **Run all tests**: `clj -M:test`
- **Run single test namespace**: `clj -X:test :namespaces '[polyominoes.core-test]'` (or `clj -M:test polyominoes.core-test`)
- **Run main**: `clj -M -m polyominoes.core -- 5 --generator tesser`
- **Lint**: `clj-kondo --lint src test` (install separately)
- **Start REPL (dev)**: `clj -M:dev`

## Style & Conventions
- **Formatting**: Use `zprint`/`cljfmt` and `clj-kondo` for linting; run before PRs.
- **Naming**: Prefer `kebab-case` for new public vars; use `defn-` for private helpers.
- **Requires**: Group requires: core clojure → 3rd-party → local; use short aliases (`r`, `m`, `log`).
- **Refer / Exclude**: Avoid `:refer` unless needed; use `:refer-clojure :exclude` only when required.
- **Validation**: Use `{:pre [...]}` or `ex-info` for input validation; keep pure functions testable.
- **Errors & Logging**: Use `taoensso.timbre` for logging; throw `ex-info` with data for failures; don't swallow exceptions.
- **Performance**: Favor transducers, reducers, `pmap` where appropriate (see `generator/*`).
- **Tests**: Add tests under `test/` using `clojure.test`; keep changes covered and focused.
- **Cursor/Copilot**: No `.cursor` or Copilot rules found in repo—follow defaults.
- **PR etiquette**: Keep changes minimal, update tests, document performance regressions.
