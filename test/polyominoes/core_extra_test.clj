(ns polyominoes.core-extra-test
  (:require [clojure.test :refer [deftest testing is]]
            [polyominoes.core]))

(deftest retrieve-rotations-and-mirror-test
  (testing "retrieveRotationsAndMirror returns 8 orientations for asymmetric shapes"
    (let [retrieve-orients (var-get #'polyominoes.core/retrieveRotationsAndMirror)
          shape [[0 0] [1 0] [2 0] [2 1]]
          orients (retrieve-orients shape)]
      (is (= 8 (count orients)))
      (is (every? vector? orients)))))

(deftest canonical-form-invariance-test
  (testing "retrieveCanonicalForm yields same canonical for all orientations"
    (let [retrieve-orients (var-get #'polyominoes.core/retrieveRotationsAndMirror)
          retrieve-canonical (var-get #'polyominoes.core/retrieveCanonicalForm)
          shape [[0 0] [1 0] [2 0] [2 1]]
          orients (retrieve-orients shape)
          canons (map retrieve-canonical orients)]
      (is (apply = canons)))))
