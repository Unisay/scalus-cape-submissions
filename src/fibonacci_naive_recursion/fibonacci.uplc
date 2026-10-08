(program 1.1.0 [(lam a [a a]) (lam b
      (lam c
        (case [(builtin lessThanEqualsInteger) c
            (con integer 1)] (case [(builtin equalsInteger) c
              (con integer 2)] [(lam d
                [(builtin addInteger) [d [(builtin subtractInteger) c
                      (con integer 1)]] [d [(builtin subtractInteger) c
                      (con integer 2)]]]) [b b]] (con integer 1)) c)))])