(
  program
  1.1.0
  [
    (lam __z_combinator__
      [
        (lam fibonacci.FibonacciBase$.fibonacci
          [
            (lam fibonacci.FibonacciBase$.fibonacci25
              [fibonacci.FibonacciBase$.fibonacci25 (con unit ())]) (lam _
              [fibonacci.FibonacciBase$.fibonacci (con integer 25)])
          ]) [
          __z_combinator__ (lam fibonacci.FibonacciBase$.fibonacci
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
                      fibonacci.FibonacciBase$.fibonacci [
                        (builtin subtractInteger) n (con integer 1)
                      ]
                    ] [
                      fibonacci.FibonacciBase$.fibonacci [
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