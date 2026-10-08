(program 1.1.0 [(lam a [a a]) (lam a
      (lam b
        (case [(builtin lessThanEqualsInteger) b
            (con integer 0)] [(builtin multiplyInteger) b [a a
              [(builtin subtractInteger) b (con integer 1)]]]
          (con integer 1))))])