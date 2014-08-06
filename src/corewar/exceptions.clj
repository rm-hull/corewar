(ns corewar.exceptions
  (:require
    [corewar.instruction-set :as instr]))

(defn ^:private throw-illegal-argument [msg context]
  (let [index (:index context)
        memory (:memory context)
        instr (memory index)]
    (throw
      (IllegalArgumentException.
        (str msg " '" (instr/to-string instr) "' at memory location " index)))))

(defn invalid-addressing-mode [context]
  (throw-illegal-argument "Invalid addressing mode" context))

(defn invalid-instruction [context]
  (throw-illegal-argument "Cannot execute" context))
