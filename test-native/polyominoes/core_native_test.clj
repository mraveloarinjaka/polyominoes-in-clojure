(ns polyominoes.core-native-test
  "Tests for polyominoes.core-native, the neanderthal/MKL implementation.

  Lives in test-native/ so the default :test runner never requires
  neanderthal. Run with clojure -M:test:maths:test-native."
  (:require [clojure.test :refer [deftest testing is]]
            [polyominoes.core :as pc]
            [polyominoes.core-native :as cn]
            [uncomplicate.commons.core :as ccore]
            [uncomplicate.neanderthal.core :as ucore]
            [uncomplicate.neanderthal.native :as native])
  (:import (org.bytedeco.mkl.global mkl_rt)))

;; Reference shapes as flat [x0 y0 x1 y1 ...] cell coordinates, matching
;; the 2xN native/dge convention in polyominoes.core-native.
(def shapes
  [[0 0 1 0]                                  ; domino
   [0 0 1 0 0 1]                              ; L-tromino
   [0 0 1 0 2 0]                              ; I-tromino
   [0 0 1 0 0 1 1 1]                          ; square (fully symmetric)
   [0 0 1 0 2 0 1 1]                          ; T-tetromino
   [0 1 1 1 1 0 2 0]                          ; S-tetromino
   [0 0 1 0 2 0 3 0 0 1]                      ; L-pentomino
   [0 0 1 0 0 1 1 1 0 2]                      ; P-pentomino
   [0 0 1 0 2 0 0 1 0 2 1 2]])                ; U-shape, 6 cells

;; The 8 dihedral symmetries, built from the transformations under test.
(def transforms
  [identity
   cn/rotate90! cn/rotate180! cn/rotate270!
   cn/mirror!
   (comp cn/mirror! cn/rotate90!)
   (comp cn/mirror! cn/rotate180!)
   (comp cn/mirror! cn/rotate270!)])

(defn- ->matrix
  "Builds a 2xN native matrix from flat [x0 y0 x1 y1 ...] cell coords."
  [coords]
  (native/dge 2 (/ (count coords) 2) coords))

(defn- canonical-key
  "Canonicalises a copy of native matrix `m` and returns its ->cols-hash-key."
  [m]
  (ccore/with-release [c (ucore/copy m)]
    (cn/->canonical-form! c)
    (cn/->cols-hash-key c)))

(defn- mkl-mem
  "MKL's own view of the buffers it holds (mkl_rt/MKL_Mem_Stat)."
  []
  (let [n (int-array 1) b (mkl_rt/MKL_Mem_Stat n)]
    {:buffers (aget n 0) :bytes b}))

(deftest count-n-test
  (testing "free-polyomino counts for 1..8 cells (OEIS A000105)"
    (is (= [1 1 2 5 12 35 108 369]
           (mapv cn/count-n (range 1 9))))))

(deftest count-n-agrees-with-core-test
  (testing "count-n matches polyominoes.core/nbOfPolyominoes for 5..8 cells"
    (doseq [cells [5 6 7 8]]
      (is (= (pc/nbOfPolyominoes {:cells cells})
             (cn/count-n cells))))))

(deftest canonical-form-invariance-test
  (testing "->canonical-form! is invariant under all 8 symmetries"
    (doseq [coords shapes
            :let [expected (ccore/with-release [p (->matrix coords)]
                            (cn/->canonical-form! p)
                            (cn/->cols-hash-key p))]
            t transforms]
      (ccore/with-release [p (->matrix coords)]
        (t p)
        (is (= expected (canonical-key p)))))))

(deftest canonical-form-idempotence-test
  (testing "->canonical-form! is idempotent"
    (doseq [coords shapes]
      (ccore/with-release [p (->matrix coords)]
        (cn/->canonical-form! p)
        (let [once (cn/->cols-hash-key p)]
          (cn/->canonical-form! p)
          (is (= once (cn/->cols-hash-key p))))))))

(deftest count-n-does-not-leak-mkl-memory-test
  (testing "MKL buffer count is unchanged across (count-n 6)"
    (let [before (mkl-mem)
          _ (cn/count-n 6)
          after (mkl-mem)]
      (is (zero? (- (:buffers after) (:buffers before)))))))
