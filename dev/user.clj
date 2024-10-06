(ns user
  (:require [borkdude.deflet :refer [deflet]]
            [clojure.repl.deps :as deps]
            [clojure.test :as xunit]
            [polyominoes.core]
            [polyominoes.core-test]
            [taoensso.timbre :as log]))

(set! *warn-on-reflection* true)

(log/set-min-level! :debug)

(println "user.clj loaded...")

#_(polyominoes.core/-main "6" "--generator" "tesser")

(comment

  (deps/add-lib 'criterium/criterium)
  (require '[criterium.core :as crt])

  (let [cells 9]
    (doseq [generator [:default :reducer :tesser :transducer]]
      (log/set-min-level! :info)
      (log/info "generator" generator)
      (crt/quick-bench (polyominoes.core/nbOfPolyominoes {:cells cells
                                                          :generator generator}))))

  (require '[polyominoes.core-native :as poly-pnative])

  (crt/quick-bench (poly-pnative/count-n 9))

  (crt/with-progress-reporting
    (crt/quick-bench (polyominoes.core/nbOfPolyominoes {:cells 9
                                                        :generator :tesser})))

  (comment))

(comment
  (xunit/run-tests 'polyominoes.core-test)
  (comment))



