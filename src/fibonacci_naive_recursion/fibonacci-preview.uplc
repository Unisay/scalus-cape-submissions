(program 1.1.0 [(lam a
      [(lam b [a (lam c [b b c])]) (lam b [a (lam c [b b c])])]) (lam d
      (lam e
        (case [(builtin lessThanEqualsInteger) e
            (con integer 1)] [(builtin addInteger) [d [(builtin subtractInteger)
                e (con integer 1)]] [d [(builtin subtractInteger) e
                (con integer 2)]]] e)))])