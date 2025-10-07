(
  program
  1.1.0
  [
    (lam __z_combinator__
      [
        (lam fibonacci_FibonacciOpen__fibonacci
          [
            (lam fibonacci_FibonacciOpen__fibonacci25
              [fibonacci_FibonacciOpen__fibonacci25 (con unit ())]) (lam _
              [fibonacci_FibonacciOpen__fibonacci (con integer 25)])
          ]) (lam n
          (force [
            (force (builtin ifThenElse)) [
              (builtin lessThanEqualsInteger) n (con integer 1)
            ] (delay (force [
              (force (builtin ifThenElse)) [
                (builtin equalsInteger) n (con integer 0)
              ] (delay (con integer 0)) (delay (con integer 1))
            ])) (delay (force [
              (force (builtin ifThenElse)) [
                (builtin equalsInteger) n (con integer 2)
              ] (delay (con integer 1)) (delay [
                (lam fibIter
                  [fibIter (con integer 1) (con integer 1) n]) [
                  __z_combinator__ (lam fibIter
                    (lam a
                      (lam b
                        (lam count
                          (force [
                            (force (builtin ifThenElse)) [
                              (builtin lessThanEqualsInteger) count
                              (con integer 2)
                            ] (delay b) (delay [
                              fibIter b [(builtin addInteger) a b] [
                                (builtin subtractInteger) count (con integer 1)
                              ]
                            ])
                          ])))))
                ]
              ])
            ]))
          ]))
      ]) (lam ff
      [
        (lam xx
          [ff (lam vv   [xx xx vv])]) (lam xx
          [ff (lam vv   [xx xx vv])])
      ])
  ]
)