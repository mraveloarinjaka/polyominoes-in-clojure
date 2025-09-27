# Project Summary — polyominoes-in-clojure

Short description
- Small Clojure library for generating and manipulating polyominoes (generator/transducer/reducer implementations).

Environment
- Clojure: 1.12.3
- Java: 21.0.8
- Project type: deps.edn

Source & tests
- Source path: `src`
- Test path: `test`
- Run all tests: `clj -M:test`
- Quick single-namespace test: `clj -M:dev -e "(require 'clojure.test) (require 'polyominoes.core-test) (clojure.test/run-tests 'polyominoes.core-test)"`

Key namespaces & files
- `polyominoes.core` — `src/polyominoes/core.clj`
- `polyominoes.core-native` — `src/polyominoes/core_native.clj`
- `polyominoes.generator` — `src/polyominoes/generator.clj`
- `polyominoes.generator.reducer` — `src/polyominoes/generator/reducer.clj`
- `polyominoes.generator.tesser` — `src/polyominoes/generator/tesser.clj`
- `polyominoes.generator.transducer` — `src/polyominoes/generator/transducer.clj`
- Tests: `test/polyominoes/core_test.clj`

Dependencies (high-level)
- `org.clojure/clojure` 1.12.3
- `com.taoensso/timbre` (logging)
- `tesser.core/tesser.core` (tesser utilities)
- other helpers listed in `deps.edn`

Aliases
- `:dev` includes `dev` and `test` on the classpath
- `:test` configured to run `cognitect.test-runner` for CI

Notes
- Generator code is split into reducer/transducer/tesser implementations under `src/polyominoes/generator/`.
- Use `clj -M:dev` to load dev and test paths for iterative REPL development.

Contact / next steps
- See `README.md` and `doc/intro.md` for usage and design notes.
