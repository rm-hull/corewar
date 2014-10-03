(ns corewar.virtual-machine
  (:require
    [corewar.memory :as mem]
    [corewar.context :as ctx]
    [corewar.assembler :as asm]
    [corewar.exceptions :as ex]
    [corewar.constants :as const]
    [corewar.instruction-set :as instr]
    [corewar.addressing-mode :as addr]))

(defn ^:private operand-result
  ([value] (operand-result value nil))
  ([value address] {:value value :address address}))

(defn eval-operand
  "Returns the result of evaluating the operand against the memory:

     :immediate - no address;
                  value given is the field itself

     :relative  - address of operand is index + field;
                  value is the content of the memory at this address

     :indirect  - address is value of the pointer + content of location it points to;
                  value is the content of the memory at this address

   Returns a map with :value and :address keys.

   An operand with an invalid/undefined addressing mode will yield a nil result."
  [which-operand {:keys [memory index] :as context}]
  (let [instr   (ctx/read-memory context)
        operand (which-operand instr)]

    (case (addr/addressing-mode operand)

      :immediate
      (operand-result (addr/value operand))

      :relative
      (let [address  (mod (+ index (addr/value operand)) const/core-size)
            value    (memory address)]
        ;(println "relative: index =" index ", value" (addr/value operand))
        (operand-result value address))

      :indirect
      (let [pointer  (mod (+ index (addr/value operand)) const/core-size)
            address  (mod (+ pointer (memory pointer)) const/core-size)
            value    (memory address)]
        (operand-result value address))

      ; default
      nil)))

(defn ^:private eval-operands
  "Assembles the evaluated operands in a map like

     {:a {:value AX :address AY} :b {:value BX :address BY}}

   according to the addressing modes of the operands of the instruction
   at memory position :index in the context."
  [context]
  {:a (eval-operand instr/operand-a context)
   :b (eval-operand instr/operand-b context)})

(defn operand-accessor [context]
  (let [operands (eval-operands context)]
    ;(println operands)
    (fn [& path]
      (if-let [result (get-in operands path)]
        result
        (ex/invalid-addressing-mode context)))))

(defn execute-instr [context]
  (let [operand (operand-accessor context)
        instr   (ctx/read-memory context)]

    (case (instr/opcode instr)
      ; MOV: Move A into B, then continue to the next instruction
      :mov
      (let [address (operand :b :address)
            value   (operand :a :value)]
        (->
          context
          (ctx/write-memory address value)
          (ctx/inc-index)))

      ; ADD: Add A and B and store the result in B,
      ; then continue to the next instruction
      :add
      (let [address (operand :b :address)
            answer  (+ (operand :b :value) (operand :a :value))]
        (->
          context
          (ctx/write-memory address answer)
          (ctx/inc-index)))

      ; SUB: Subtract A from B and store the result in B,
      ; then continue to the next instruction
      :sub
      (let [address (operand :b :address)
            answer  (- (operand :b :value) (operand :a :value))]
        (->
          context
          (ctx/write-memory address answer)
          (ctx/inc-index)))

      ; JMP: Unconditionally jump to B
      :jmp
      (ctx/set-index context (-> instr instr/operand-b addr/value))

      ; JMZ: If A is zero, jump to B,
      ; else continue to the next instruction
      :jmz
      (if (zero? (operand :a :value))
        (ctx/set-index context (-> instr instr/operand-b addr/value))
        (ctx/inc-index context))

      ; DJZ: Decrement A and store the result.
      ; If the result is zero then jump to B,
      ; else continue to the next instruction
      :djz
      (let [address (operand :a :address)
            answer  (dec (operand :a :value))
            context (ctx/write-memory context address answer)]
        (if (zero? answer)
          (ctx/set-index context (-> instr instr/operand-b addr/value))
          (ctx/inc-index context)))

      ; CMP: If the operands are equal then skip the next instruction,
      ; else continue to the next instruction
      :cmp
      (if (= (operand :a :value) (operand :b :value))
        (ctx/set-index context 2)
        (ctx/inc-index context))

      ; default: report failure
      (ex/invalid-instruction context))))


(defn execute-program [context max-steps]
  (loop [ctx (assoc context :updated #{} :executed #{})
         n max-steps]
    (if (zero? n)
      ctx
      (recur
        (execute-instr ctx)
        (dec n)))))

(comment
  (def assembly (asm/assemble (slurp "resources/dwarf.red")))

  (mem/initial-state 200 assembly)

  (def start-posn 23)
  (def context (assoc assembly
                 :index (+ start-posn (:start assembly))
                 :memory (mem/load-program core start-posn (:instr assembly))))

  (asm/disassemble (:instr assembly))
  (println context)
  (def result (execute-program context 40))
  (asm/disassemble (:memory result))
)

(def x #{1 2 3 4 5})
(def y #{2 4 7})

(clojure.set/union x y)


