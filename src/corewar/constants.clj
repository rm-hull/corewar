(ns corewar.constants)

(def ^:const opcode-bits 4)
(def ^:const mode-bits 2)
(def ^:const operand-bits 12)

(def ^:const operand-position (+ operand-bits mode-bits))
(def ^:const opcode-position (* 2 operand-position))

(defn ^:private mask [bits]
  (dec (int (Math/pow 2 bits))))

(def ^:const core-size (int (Math/pow 2 operand-bits)))
(def ^:const value-mask (mask operand-bits))
(def ^:const opcode-mask (mask opcode-bits))
(def ^:const operand-mask (mask (+ operand-bits mode-bits)))
