(ns corewar.instruction-set-tests
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [corewar.generators :refer [operand-generator instruction-generator]]
    [corewar.instruction-set :as instr]
    [corewar.addressing-mode :as addr]
    [corewar.constants :as const]))

(def +test-runs+ 1000)

(gen/sample operand-generator +test-runs+)

(map instr/to-string (gen/sample instruction-generator +test-runs+))

