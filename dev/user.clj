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

  (let [cells 5]
    (doseq [generator [:default :reducer :tesser :transducer]]
      (log/set-min-level! :info)
      (log/info "generator" generator)
      (crt/quick-bench (polyominoes.core/nbOfPolyominoes {:cells cells
                                                          :generator generator}))))

  (crt/with-progress-reporting
    (crt/quick-bench (polyominoes.core/nbOfPolyominoes {:cells 9
                                                        :generator :tesser})))

  (comment))

(comment
  (xunit/run-tests 'polyominoes.core-test)
  (comment))

(comment

  (require '[polyominoes.core-native])

  (require '[uncomplicate.clojurecl.core :as opencl]
           '[uncomplicate.commons.core :refer [with-release]]
           '[uncomplicate.fluokitten.core :as kitten :refer [foldmap]]
           '[uncomplicate.neanderthal
             [core :as ucore :refer [dot copy asum copy! row mv mm rk axpy entry! subvector trans mm! zero]]
             [vect-math :as v-math :refer [mul]]
             [native :as native :refer [dv dge fge]]
             ;[cuda :refer [cuv cuge with-default-engine]]
             ;[opencl :as cl :refer [clv]]
             [random :refer [rand-uniform!]]
             [linalg :as linalg]
             [auxil :as auxil]])

  (deflet
    (def m1 (native/dge 2 2 [4 3 2 1]))
    (def m2 (native/dge 2 2 [4 3 2 1]))

    (ucore/axpby! 1 m1 -1 m2))

  (deflet

    (def m (fge 5 5 [-1.01, 3.98, 3.30, 4.43, 7.31,
                     0.86, 0.53, 8.26, 4.96, -6.43,
                     -4.60, -7.04, -3.89, -7.66, -6.16,
                     3.31, 5.29, 8.20, -7.33, 2.47,
                     -4.81, 3.55, -1.51, 6.18, 5.58] {:layout :row}))

    (auxil/sort-! m))

  (comment))

