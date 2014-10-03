(ns corewar.compat
  "Compatibility layer for clojure & clojurescript"
  (:require
    [clojure.tools.reader :as r]
    ;[cljs.reader :as r]
    ))

(defn starts-with [s prefix]
  (let [slice (subs s 0 (min (count s) (count prefix)))]
    (= slice prefix)))

(defn parse-int [x]
  (if-not (number? x)
    (r/read-string x)
    x))

