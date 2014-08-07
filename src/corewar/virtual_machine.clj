(ns corewar.virtual-machine
  (:require
    [corewar.exceptions :as ex]
    [corewar.instruction-set :as instr]
    [corewar.addressing-mode :as addr]))

(defn ^:private operand-result
  ([value] (operand-result value nil))
  ([value address] {:value value :address address}))

; TODO: move into a context namespace
(defn curr-instr
  "Extracts the current instruction from the context, returns memory[index]"
  [{:keys [index memory]}]
  (memory index))

; TODO: move into a context namespace
(defn inc-index
  "Non-destructive incrementing update on the index/address-pointer, ensuring that the
   index always wraps round the limit of the memory"
  [{:keys [memory] :as context}]
  (let [mem-size (count memory)
        inc-mod  #(mod (inc %) mem-size)]
    (update-in context [:index] inc-mod)))

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
  (let [instr   (curr-instr context)
        operand (which-operand instr)]

    (case (addr/addressing-mode operand)
      :immediate
      (operand-result (addr/value operand))

      :relative
      (let [mem-size (count memory)
            address  (mod (+ index (addr/value operand)) mem-size)
            value    (memory address)]
        (operand-result value address))

      :indirect
      (let [mem-size (count memory)
            pointer  (mod (+ index (addr/value operand)) mem-size)
            address  (mod (+ pointer (memory pointer)) mem-size)
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
    (fn [& path]
      (if-let [result (get-in operands path)]
        result
        (ex/invalid-addressing-mode context)))))

(defn execute-instr [context]
  (let [operand (operand-accessor context)
        ; order is important here: always get the current instruction BEFORE incrementing the index
        instr (curr-instr context)
        new-context (inc-index context)]

    (case (instr/opcode instr)

      :mov
      (let [address (operand :b :address)
            value   (operand :a :value)]
        (assoc-in new-context [:memory address] value))

      :add
      (let [address (operand :b :address)
            answer  (+ (operand :b :value) (operand :a :value))]
        (assoc-in new-context [:memory address] answer))

      :sub
      (let [address (operand :b :address)
            answer  (- (operand :b :value) (operand :a :value))]
        (assoc-in new-context [:memory address] answer))

      :jmp
      (assoc-in new-context [:index] (operand :b :value))

      :jmz
      (if (zero? (operand :a :value))
        (assoc-in new-context [:index] (operand :b :value))
        new-context)

      :djz
      (let [address (operand :a :address)
            answer  (dec (operand :a :value))
            new-context (assoc-in new-context [:memory address] answer)]
        (if (zero? answer)
          (assoc-in new-context [:index] (operand :b :value))
          new-context))

      :cmp
      (if (= (operand :a :value) (operand :b :value))
        (inc-index new-context)
        new-context)

      ; default
      (ex/invalid-instruction context))))

;(ex/invalid-addressing-mode context)


(def mov (instr/mov (addr/immediate 4) (addr/relative 3)))
(def core (vec (cons  mov (repeat 100 0))))
(def context {:memory core :index 0})
(def context {:memory core :index 2})
(def context {:memory core :index 100})
(inc-index context)
(eval-operands context)
(println core)



