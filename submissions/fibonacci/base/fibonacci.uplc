(
  program
  1.1.0
  [
    (lam __z_combinator__
      [
        (lam fibonacci_FibonacciBase__fibonacci
          [
            (lam fibonacci_FibonacciBase__fibonacci25
              [fibonacci_FibonacciBase__fibonacci25 (con unit ())]) (lam _
              [fibonacci_FibonacciBase__fibonacci (con integer 25)])
          ]) [
          __z_combinator__ (lam fibonacci_FibonacciBase__fibonacci
            (lam n
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
                    (builtin addInteger) [
                      fibonacci_FibonacciBase__fibonacci [
                        (builtin subtractInteger) n (con integer 1)
                      ]
                    ] [
                      fibonacci_FibonacciBase__fibonacci [
                        (builtin subtractInteger) n (con integer 2)
                      ]
                    ]
                  ])
                ]))
              ])))
        ]
      ]) (lam ff
      [
        (lam xx
          [ff (lam vv   [xx xx vv])]) (lam xx
          [ff (lam vv   [xx xx vv])])
      ])
  ]
)