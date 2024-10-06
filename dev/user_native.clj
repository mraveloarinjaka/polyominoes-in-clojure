(ns user-native
  (:require [borkdude.deflet :refer [deflet]]
            [clojure.repl.deps :as deps]
            [clojure.test :as xunit]))

(comment

  (require '[polyominoes.core-native])

  (require
   '[tech.v3.datatype.argops :as ops]
   '[uncomplicate.commons.core :refer [release with-release]]
   '[uncomplicate.neanderthal
     [core :as ucore :refer [dot copy asum copy! row mv mm rk axpy entry! subvector trans mm! zero]]
     [native :as native :refer [dv dge fge]]
     [auxil :as auxil]])

  (with-release [m (native/dge 2 2 [4 3 2 1])]
    (ops/argsort m))

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

