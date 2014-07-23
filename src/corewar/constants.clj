(ns corewar.constants)

(def ^:const operand-bits 12)
(def ^:const mode-bits 2)
(def ^:const type-bits 4)

(def ^:const core-size (int (Math/pow 2 operand-bits)))
(def ^:const value-mask (dec core-size))
