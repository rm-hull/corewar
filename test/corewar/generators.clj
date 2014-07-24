(ns corewar.generators
  (:require
    [clojure.test.check.generators :as gen]
    [corewar.addressing-mode :as addr]
    [corewar.instruction-set :as instr]))


(def addressing-mode-generator
  (gen/elements
    [addr/immediate addr/relative addr/indirect]))

(def operand-generator
  (gen/fmap
    (fn [[f v]] (f v))
    (gen/tuple addressing-mode-generator gen/int)))

(defn jmp-stub [a b]
  (instr/jmp b))

(defn dat-stub [a b]
  (instr/dat b))

(def opcode-generator
  (gen/elements
    [dat-stub instr/mov instr/add instr/sub jmp-stub instr/jmz instr/djz instr/cmp]))

(def instruction-generator
  (gen/fmap
    (fn [[opcode a b]] (opcode a b))
    (gen/tuple opcode-generator operand-generator operand-generator)))
