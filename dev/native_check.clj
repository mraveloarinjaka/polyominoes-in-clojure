;; One-shot sanity check for polyominoes.core-native (neanderthal/MKL):
;; free-polyomino counts, agreement with polyominoes.core, timings and
;; MKL memory-leak deltas.
;;
;; Run: clojure -M:dev:maths -i dev/native_check.clj
;;
;; Deliberately a one-shot script, not a REPL: never benchmark count-n in a
;; long-lived REPL (see TODO.md "Ground rules"); every run here gets a fresh
;; JVM and shuts its agents down.

(require '[taoensso.timbre :as log]
         '[polyominoes.core :as pc]
         '[polyominoes.core-native :as cn])
(log/set-min-level! :warn)
(import 'org.bytedeco.mkl.global.mkl_rt)

(defn mkl-mem []
  (let [n (int-array 1) b (mkl_rt/MKL_Mem_Stat n)]
    {:buffers (aget n 0) :bytes b}))

(defn time-ms [f]
  (let [t0 (System/nanoTime) r (f)]
    [r (long (/ (- (System/nanoTime) t0) 1e6))]))

;; Free-polyomino counts (OEIS A000105).
(println :counts-1..8 (mapv cn/count-n (range 1 9))
         :expected [1 1 2 5 12 35 108 369])

;; Cross-check against the pure-JVM implementation.
(doseq [cells [5 6 7 8]]
  (println :agreement {:cells cells
                       :native (cn/count-n cells)
                       :core (pc/nbOfPolyominoes {:cells cells})}))

;; MKL leak check: buffers allocated by count-n and never released.
(let [before (mkl-mem) _ (cn/count-n 7) after (mkl-mem)]
  (println :leak-count-n-7 {:buffers (- (:buffers after) (:buffers before))
                            :bytes (- (:bytes after) (:bytes before))}))

;; Timings vs the default generator.
(doseq [cells [8 9 10]]
  (let [[a ta] (time-ms #(cn/count-n cells))
        [b tb] (time-ms #(pc/nbOfPolyominoes {:cells cells}))]
    (println :timing {:cells cells :native-ms ta :core-ms tb :agree? (= a b)})))

(shutdown-agents)
