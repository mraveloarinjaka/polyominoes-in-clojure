(ns polyominoes.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [polyominoes.core :refer [nbOfPolyominoes]]))

(deftest nbOfPolyominoes-test
  (testing "generators"
    (is (= 12 (nbOfPolyominoes {:cells 5})))
    (is (= 12 (nbOfPolyominoes {:cells 5 :generator :tesser})))
    (is (= 12 (nbOfPolyominoes {:cells 5 :generator :transducer})))
    (is (= 12 (nbOfPolyominoes {:cells 5 :generator :reducer})))))
