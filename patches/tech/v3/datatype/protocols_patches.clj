(ns tech.v3.datatype.protocols-patches
  (:require [tech.v3.datatype.protocols :as dtype-proto]
            [uncomplicate.commons.core :as ccore])
  (:import [uncomplicate.neanderthal.internal.api NativeBlock]))

(extend-type NativeBlock
  dtype-proto/PECount
  (ecount [this] (:dim (ccore/info this))))
