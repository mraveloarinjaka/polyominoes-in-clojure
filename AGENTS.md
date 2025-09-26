Build / Lint / Test
- Run all tests: `clj -M:test`
- Run REPL with dev/test paths: `clj -M:dev`
- Run a single test namespace (quick):
  `clj -M:dev -e "(require 'clojure.test) (require 'polyominoes.core-test) (clojure.test/run-tests 'polyominoes.core-test)"`
- Run a single test interactively: start `clj -M:dev` then `(clojure.test/test-var #'polyominoes.core-test/your-test)`

Style & Conventions
- Formatting: follow existing 2-space indentation and idiomatic Clojure layout.
- Naming: use kebab-case for vars/fns/keys; use `defn-` for private helpers.
- Namespaces: hyphenated namespaces matching file paths (e.g. `polyominoes.core`).
- Requires/imports: group `:require` entries, use `:as` aliases; avoid unused imports.
- Types: prefer plain Clojure collections (vectors/sets/maps); document expectations with `:pre`/`post` where useful.
- Error handling: use preconditions (`{:pre [...]}`), explicit `throw` only for unrecoverable errors; log via `taoensso.timbre`.
- Performance: prefer transducers/reducers where appropriate (project already uses them).
- Tests: keep tests under `test/` and use `cognitect.test-runner` alias `:test` for CI.

Repo rules
- Cursor/Copilot rules: none found (`.cursor/` or `.github/copilot-instructions.md` absent).

Agents: be conservative — run `clj -M:dev` before experimenting, and open PRs for non-trivial changes.