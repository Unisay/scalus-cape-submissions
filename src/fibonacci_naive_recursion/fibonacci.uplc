(program 1.1.0 [(lam a
      [(lam b [a (lam c [b b c])]) (lam b [a (lam c [b b c])])]) (lam d
      (lam e
        (force (case (constr 0 [(builtin lessThanEqualsInteger) e
              (con integer 1)] (delay e) (delay [(builtin addInteger) [d
                [(builtin subtractInteger) e (con integer 1)]] [d
                [(builtin subtractInteger) e
                  (con integer 2)]]])) (force (builtin ifThenElse))))))])