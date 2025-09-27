(ns tasks
  (:require [cheshire.core :as json]
            [clojure.edn :as edn]))

(def OPENCODE_CONFIG_TEMPLATE "opencode.edn")
(def OPENCODE_CONFIG "opencode.jsonc")

(defn generate-opencode-configuration
  ([nrepl-port]
   (let [template (edn/read-string (slurp OPENCODE_CONFIG_TEMPLATE))]
     (->> (update-in template [:mcp :clojure-mcp :command] conj nrepl-port)
          (#(json/encode % {:pretty true}))
          (spit OPENCODE_CONFIG))))
  ([]
   (generate-opencode-configuration (slurp ".nrepl-port"))))
