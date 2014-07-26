;redcode-88
;name Dwarf
;author A.K.Dewdney

        org dwarf

dwarf:  ADD #4, 3
        MOV 2, @2

; comment
        JMP dwarf
bomb:   DAT #0

        end