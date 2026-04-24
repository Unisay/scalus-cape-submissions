(program 1.1.0 (case (constr 0 (force (force (builtin chooseList)))
      (force (force (builtin fstPair))) (force (builtin headList))
      (force (builtin ifThenElse)) (force (builtin mkCons))
      (force (force (builtin sndPair))) (force (builtin tailList))
      (force (builtin trace))) (lam __builtin_ChooseList
      (lam __builtin_FstPair
        (lam __builtin_HeadList
          (lam __builtin_IfThenElse
            (lam __builtin_MkCons
              (lam __builtin_SndPair
                (lam __builtin_TailList
                  (lam __builtin_Trace
                    [(lam __Z
                        [(lam scalus_cardano_onchain_plutus_prelude_List__find28
                            [(lam scalus_cardano_onchain_plutus_v3_TxInfo__isSignedBy79
                                [(lam htlc_HtlcValidator__extractPkh88
                                    [(lam scalus_cardano_onchain_plutus_prelude_List__foldLeft101
                                        [(lam htlc_HtlcValidator__countScriptInputs162
                                            [(lam htlc_HtlcValidator__ownScriptHash211
                                                (lam ctx-15678250
                                                  [(lam _match_scrutinee253
                                                      (force (case (constr 0 [(builtin equalsInteger)
                                                            [__builtin_FstPair
                                                              _match_scrutinee253]
                                                            (con integer 1)]
                                                          (delay [(lam _match_datalist256
                                                              [(lam datum-27523265
                                                                  [(lam _match_scrutinee267
                                                                      (force (case (constr 0 [(builtin equalsInteger)
                                                                            [__builtin_FstPair
                                                                              _match_scrutinee267]
                                                                            (con integer 0)]
                                                                          (delay (case (constr 0 [__builtin_SndPair
                                                                                [(builtin unConstrData)
                                                                                  [__builtin_HeadList
                                                                                    [__builtin_SndPair
                                                                                      [(builtin unConstrData)
                                                                                        ctx-15678250]]]]]
                                                                              [__builtin_SndPair
                                                                                [(builtin unConstrData)
                                                                                  [__builtin_HeadList
                                                                                    _match_datalist256]]]
                                                                              datum-27523265
                                                                              [(builtin unBData)
                                                                                [__builtin_HeadList
                                                                                  [__builtin_SndPair
                                                                                    _match_scrutinee267]]]) (lam tx-27586212
                                                                              (lam ownRef-27587213
                                                                                (lam d-27588214
                                                                                  (lam preimage-27589215
                                                                                    [(lam __HTLC_line_79-0217
                                                                                        [(lam __HTLC_line_80-0219
                                                                                            [(lam __HTLC_line_81-0221
                                                                                                (force (case (constr 0 [(builtin equalsInteger)
                                                                                                      [htlc_HtlcValidator__countScriptInputs162
                                                                                                        tx-27586212
                                                                                                        [htlc_HtlcValidator__ownScriptHash211
                                                                                                          tx-27586212
                                                                                                          ownRef-27587213]]
                                                                                                      (con integer 1)]
                                                                                                    (delay (con unit ()))
                                                                                                    (delay (force [__builtin_Trace
                                                                                                      (con string "Double satisfaction")
                                                                                                      (delay (error))]))) __builtin_IfThenElse)))
                                                                                              (force (case (constr 0 [(builtin lessThanInteger)
                                                                                                    [(lam r-2769289
                                                                                                        [(lam _match_scrutinee92
                                                                                                            (force (case (constr 0 [(builtin equalsInteger)
                                                                                                                  [__builtin_FstPair
                                                                                                                    _match_scrutinee92]
                                                                                                                  (con integer 1)]
                                                                                                                (delay [(lam _match_datalist95
                                                                                                                    (force (case (constr 0 (case (constr 0 [(builtin equalsInteger)
                                                                                                                              [__builtin_FstPair
                                                                                                                                [(builtin unConstrData)
                                                                                                                                  [__builtin_HeadList
                                                                                                                                    [__builtin_TailList
                                                                                                                                      [__builtin_SndPair
                                                                                                                                        [(builtin unConstrData)
                                                                                                                                          [__builtin_HeadList
                                                                                                                                            [__builtin_TailList
                                                                                                                                              r-2769289]]]]]]]]
                                                                                                                              (con integer 0)]
                                                                                                                            (con bool False)
                                                                                                                            (con bool True)) __builtin_IfThenElse)
                                                                                                                        (delay [(builtin unIData)
                                                                                                                          [__builtin_HeadList
                                                                                                                            _match_datalist95]])
                                                                                                                        (delay [(builtin subtractInteger)
                                                                                                                          [(builtin unIData)
                                                                                                                            [__builtin_HeadList
                                                                                                                              _match_datalist95]]
                                                                                                                          (con integer 1)])) __builtin_IfThenElse)))
                                                                                                                  [__builtin_SndPair
                                                                                                                    _match_scrutinee92]])
                                                                                                                (delay (force [__builtin_Trace
                                                                                                                  (con string "Claim requires a finite upper bound")
                                                                                                                  (delay (error))]))) __builtin_IfThenElse)))
                                                                                                          [(builtin unConstrData)
                                                                                                            [__builtin_HeadList
                                                                                                              [__builtin_SndPair
                                                                                                                [(builtin unConstrData)
                                                                                                                  [__builtin_HeadList
                                                                                                                    [__builtin_TailList
                                                                                                                      r-2769289]]]]]]])
                                                                                                      [__builtin_SndPair
                                                                                                        [(builtin unConstrData)
                                                                                                          [__builtin_HeadList
                                                                                                            [__builtin_TailList
                                                                                                              [__builtin_TailList
                                                                                                                [__builtin_TailList
                                                                                                                  [__builtin_TailList
                                                                                                                    [__builtin_TailList
                                                                                                                      [__builtin_TailList
                                                                                                                        [__builtin_TailList
                                                                                                                          tx-27586212]]]]]]]]]]]
                                                                                                    [(builtin unIData)
                                                                                                      [__builtin_HeadList
                                                                                                        [__builtin_TailList
                                                                                                          [__builtin_TailList
                                                                                                            [__builtin_TailList
                                                                                                              d-27588214]]]]]]
                                                                                                  (delay (con unit ()))
                                                                                                  (delay (force [__builtin_Trace
                                                                                                    (con string "Claim not permitted at or after timeout")
                                                                                                    (delay (error))]))) __builtin_IfThenElse))])
                                                                                          (force (case (constr 0 [scalus_cardano_onchain_plutus_v3_TxInfo__isSignedBy79
                                                                                                tx-27586212
                                                                                                [htlc_HtlcValidator__extractPkh88
                                                                                                  [__builtin_SndPair
                                                                                                    [(builtin unConstrData)
                                                                                                      [__builtin_HeadList
                                                                                                        [__builtin_TailList
                                                                                                          d-27588214]]]]]]
                                                                                              (delay (con unit ()))
                                                                                              (delay (force [__builtin_Trace
                                                                                                (con string "Missing recipient signature")
                                                                                                (delay (error))]))) __builtin_IfThenElse))])
                                                                                      (force (case (constr 0 [(builtin equalsByteString)
                                                                                            [(builtin sha2_256)
                                                                                              preimage-27589215]
                                                                                            [(builtin unBData)
                                                                                              [__builtin_HeadList
                                                                                                [__builtin_TailList
                                                                                                  [__builtin_TailList
                                                                                                    d-27588214]]]]]
                                                                                          (delay (con unit ()))
                                                                                          (delay (force [__builtin_Trace
                                                                                            (con string "Preimage does not match stored hash")
                                                                                            (delay (error))]))) __builtin_IfThenElse))]))))))
                                                                          (delay (case (constr 0 [__builtin_SndPair
                                                                                [(builtin unConstrData)
                                                                                  [__builtin_HeadList
                                                                                    [__builtin_SndPair
                                                                                      [(builtin unConstrData)
                                                                                        ctx-15678250]]]]]
                                                                              [__builtin_SndPair
                                                                                [(builtin unConstrData)
                                                                                  [__builtin_HeadList
                                                                                    _match_datalist256]]]
                                                                              datum-27523265) (lam tx-27590238
                                                                              (lam ownRef-27591239
                                                                                (lam d-27592240
                                                                                  [(lam __HTLC_line_88-0242
                                                                                      [(lam __HTLC_line_89-0244
                                                                                          (force (case (constr 0 [(builtin equalsInteger)
                                                                                                [htlc_HtlcValidator__countScriptInputs162
                                                                                                  tx-27590238
                                                                                                  [htlc_HtlcValidator__ownScriptHash211
                                                                                                    tx-27590238
                                                                                                    ownRef-27591239]]
                                                                                                (con integer 1)]
                                                                                              (delay (con unit ()))
                                                                                              (delay (force [__builtin_Trace
                                                                                                (con string "Double satisfaction")
                                                                                                (delay (error))]))) __builtin_IfThenElse)))
                                                                                        (force (case (constr 0 [(builtin lessThanInteger)
                                                                                              [(builtin unIData)
                                                                                                [__builtin_HeadList
                                                                                                  [__builtin_TailList
                                                                                                    [__builtin_TailList
                                                                                                      [__builtin_TailList
                                                                                                        d-27592240]]]]]
                                                                                              [(lam r-27747226
                                                                                                  [(lam _match_scrutinee229
                                                                                                      (force (case (constr 0 [(builtin equalsInteger)
                                                                                                            [__builtin_FstPair
                                                                                                              _match_scrutinee229]
                                                                                                            (con integer 1)]
                                                                                                          (delay [(lam _match_datalist232
                                                                                                              (force (case (constr 0 (case (constr 0 [(builtin equalsInteger)
                                                                                                                        [__builtin_FstPair
                                                                                                                          [(builtin unConstrData)
                                                                                                                            [__builtin_HeadList
                                                                                                                              [__builtin_TailList
                                                                                                                                [__builtin_SndPair
                                                                                                                                  [(builtin unConstrData)
                                                                                                                                    [__builtin_HeadList
                                                                                                                                      r-27747226]]]]]]]
                                                                                                                        (con integer 0)]
                                                                                                                      (con bool False)
                                                                                                                      (con bool True)) __builtin_IfThenElse)
                                                                                                                  (delay [(builtin unIData)
                                                                                                                    [__builtin_HeadList
                                                                                                                      _match_datalist232]])
                                                                                                                  (delay [(builtin addInteger)
                                                                                                                    [(builtin unIData)
                                                                                                                      [__builtin_HeadList
                                                                                                                        _match_datalist232]]
                                                                                                                    (con integer 1)])) __builtin_IfThenElse)))
                                                                                                            [__builtin_SndPair
                                                                                                              _match_scrutinee229]])
                                                                                                          (delay (force [__builtin_Trace
                                                                                                            (con string "Refund requires a finite lower bound")
                                                                                                            (delay (error))]))) __builtin_IfThenElse)))
                                                                                                    [(builtin unConstrData)
                                                                                                      [__builtin_HeadList
                                                                                                        [__builtin_SndPair
                                                                                                          [(builtin unConstrData)
                                                                                                            [__builtin_HeadList
                                                                                                              r-27747226]]]]]])
                                                                                                [__builtin_SndPair
                                                                                                  [(builtin unConstrData)
                                                                                                    [__builtin_HeadList
                                                                                                      [__builtin_TailList
                                                                                                        [__builtin_TailList
                                                                                                          [__builtin_TailList
                                                                                                            [__builtin_TailList
                                                                                                              [__builtin_TailList
                                                                                                                [__builtin_TailList
                                                                                                                  [__builtin_TailList
                                                                                                                    tx-27590238]]]]]]]]]]]]
                                                                                            (delay (con unit ()))
                                                                                            (delay (force [__builtin_Trace
                                                                                              (con string "Refund not permitted at or before timeout")
                                                                                              (delay (error))]))) __builtin_IfThenElse))])
                                                                                    (force (case (constr 0 [scalus_cardano_onchain_plutus_v3_TxInfo__isSignedBy79
                                                                                          tx-27590238
                                                                                          [htlc_HtlcValidator__extractPkh88
                                                                                            [__builtin_SndPair
                                                                                              [(builtin unConstrData)
                                                                                                [__builtin_HeadList
                                                                                                  d-27592240]]]]]
                                                                                        (delay (con unit ()))
                                                                                        (delay (force [__builtin_Trace
                                                                                          (con string "Missing payer signature")
                                                                                          (delay (error))]))) __builtin_IfThenElse))])))))) __builtin_IfThenElse)))
                                                                    [(builtin unConstrData)
                                                                      [__builtin_HeadList
                                                                        [__builtin_TailList
                                                                          [__builtin_SndPair
                                                                            [(builtin unConstrData)
                                                                              ctx-15678250]]]]]])
                                                                [(lam _match_scrutinee260
                                                                    [(lam _match_datalist256_t
                                                                        (force (case (constr 0 [(builtin equalsInteger)
                                                                              [__builtin_FstPair
                                                                                _match_scrutinee260]
                                                                              (con integer 0)]
                                                                            (delay [__builtin_SndPair
                                                                              [(builtin unConstrData)
                                                                                [__builtin_HeadList
                                                                                  [__builtin_SndPair
                                                                                    _match_scrutinee260]]]])
                                                                            (delay (force [__builtin_Trace
                                                                              (con string "Missing HTLC datum")
                                                                              (delay (error))]))) __builtin_IfThenElse)))
                                                                      [__builtin_TailList
                                                                        _match_datalist256]])
                                                                  [(builtin unConstrData)
                                                                    [__builtin_HeadList
                                                                      [__builtin_TailList
                                                                        _match_datalist256]]]]])
                                                            [__builtin_SndPair
                                                              _match_scrutinee253]])
                                                          (delay (force [__builtin_Trace
                                                            (con string "Expected SpendingScript")
                                                            (delay (error))]))) __builtin_IfThenElse)))
                                                    [(builtin unConstrData)
                                                      [__builtin_HeadList
                                                        [__builtin_TailList
                                                          [__builtin_TailList
                                                            [__builtin_SndPair
                                                              [(builtin unConstrData)
                                                                ctx-15678250]]]]]]]))
                                              (lam tx-27695200
                                                (lam ownRef-27696201
                                                  [(lam _match_scrutinee204
                                                      (force (case (constr 0 [(builtin equalsInteger)
                                                            [__builtin_FstPair
                                                              _match_scrutinee204]
                                                            (con integer 0)]
                                                          (delay [(lam _match_scrutinee193
                                                              (force (case (constr 0 [(builtin equalsInteger)
                                                                    [__builtin_FstPair
                                                                      _match_scrutinee193]
                                                                    (con integer 1)]
                                                                  (delay [(builtin unBData)
                                                                    [__builtin_HeadList
                                                                      [__builtin_SndPair
                                                                        _match_scrutinee193]]])
                                                                  (delay (force [__builtin_Trace
                                                                    (con string "Expected ScriptCredential")
                                                                    (delay (error))]))) __builtin_IfThenElse)))
                                                            [(builtin unConstrData)
                                                              [__builtin_HeadList
                                                                [__builtin_SndPair
                                                                  [(builtin unConstrData)
                                                                    [__builtin_HeadList
                                                                      [__builtin_SndPair
                                                                        [(builtin unConstrData)
                                                                          [__builtin_HeadList
                                                                            [__builtin_TailList
                                                                              [__builtin_SndPair
                                                                                [(builtin unConstrData)
                                                                                  [__builtin_HeadList
                                                                                    [__builtin_SndPair
                                                                                      _match_scrutinee204]]]]]]]]]]]]]])
                                                          (delay (force [__builtin_Trace
                                                            (con string "Own input not found")
                                                            (delay (error))]))) __builtin_IfThenElse)))
                                                    [(builtin unConstrData)
                                                      [(lam inputs-305446176
                                                          (lam outRef-305447177
                                                            [scalus_cardano_onchain_plutus_prelude_List__find28
                                                              inputs-305446176
                                                              (lam xIn179
                                                                [(lam a-303790171
                                                                    (lam b-303791172
                                                                      (force (case (constr 0 [(builtin equalsByteString)
                                                                            [(builtin unBData)
                                                                              [__builtin_HeadList
                                                                                a-303790171]]
                                                                            [(builtin unBData)
                                                                              [__builtin_HeadList
                                                                                b-303791172]]]
                                                                          (delay [(builtin equalsInteger)
                                                                            [(builtin unIData)
                                                                              [__builtin_HeadList
                                                                                [__builtin_TailList
                                                                                  a-303790171]]]
                                                                            [(builtin unIData)
                                                                              [__builtin_HeadList
                                                                                [__builtin_TailList
                                                                                  b-303791172]]]])
                                                                          (delay (con bool False))) __builtin_IfThenElse))))
                                                                  [__builtin_SndPair
                                                                    [(builtin unConstrData)
                                                                      [__builtin_HeadList
                                                                        [__builtin_SndPair
                                                                          [(builtin unConstrData)
                                                                            xIn179]]]]]
                                                                  outRef-305447177])]))
                                                        [(builtin unListData)
                                                          [__builtin_HeadList
                                                            tx-27695200]]
                                                        ownRef-27696201]]]))])
                                          (lam tx-27693142
                                            (lam scriptHash-27694143
                                              [(lam self-90574120
                                                  (lam p-90575121
                                                    [(builtin unIData)
                                                      (case (constr 0 self-90574120
                                                          (con data (I 0))
                                                          (lam x131132
                                                            (lam x134135
                                                              [(lam xIn128r129
                                                                  (lam xIn130
                                                                    [(builtin iData)
                                                                      (force (case (constr 0 [p-90575121
                                                                            xIn130]
                                                                          (delay [(builtin addInteger)
                                                                            xIn128r129
                                                                            (con integer 1)])
                                                                          (delay xIn128r129)) __builtin_IfThenElse))]))
                                                                [(builtin unIData)
                                                                  x131132]
                                                                x134135]))) scalus_cardano_onchain_plutus_prelude_List__foldLeft101)]))
                                                [(builtin unListData)
                                                  [__builtin_HeadList
                                                    tx-27693142]] (lam xIn145
                                                  [(lam _match_scrutinee153
                                                      (force (case (constr 0 [(builtin equalsInteger)
                                                            [__builtin_FstPair
                                                              _match_scrutinee153]
                                                            (con integer 1)]
                                                          (delay [(builtin equalsByteString)
                                                            [(builtin unBData)
                                                              [__builtin_HeadList
                                                                [__builtin_SndPair
                                                                  _match_scrutinee153]]]
                                                            scriptHash-27694143])
                                                          (delay (con bool False))) __builtin_IfThenElse)))
                                                    [(builtin unConstrData)
                                                      [__builtin_HeadList
                                                        [__builtin_SndPair
                                                          [(builtin unConstrData)
                                                            [__builtin_HeadList
                                                              [__builtin_SndPair
                                                                [(builtin unConstrData)
                                                                  [__builtin_HeadList
                                                                    [__builtin_TailList
                                                                      [__builtin_SndPair
                                                                        [(builtin unConstrData)
                                                                          xIn145]]]]]]]]]]]])]))])
                                      [__Z
                                        (lam scalus_cardano_onchain_plutus_prelude_List__foldLeft101
                                          (lam self-86666102
                                            (lam init-86668103
                                              (lam combiner-86669104
                                                (force (case (constr 0 self-86666102
                                                    (delay init-86668103)
                                                    (delay [(lam consTail107
                                                        [(lam consHead106
                                                            (lam xIn114
                                                              [(lam xIn108
                                                                  (lam xIn109
                                                                    (case (constr 0 consTail107
                                                                        xIn108
                                                                        xIn109) scalus_cardano_onchain_plutus_prelude_List__foldLeft101)))
                                                                [combiner-86669104
                                                                  init-86668103
                                                                  consHead106]
                                                                xIn114]))
                                                          [__builtin_HeadList
                                                            self-86666102]])
                                                      [__builtin_TailList
                                                        self-86666102]
                                                      combiner-86669104])) __builtin_ChooseList))))))]])
                                  (lam addr-2769180
                                    [(lam _match_scrutinee82
                                        (force (case (constr 0 [(builtin equalsInteger)
                                              [__builtin_FstPair
                                                _match_scrutinee82]
                                              (con integer 0)]
                                            (delay [(builtin unBData)
                                              [__builtin_HeadList
                                                [__builtin_SndPair
                                                  _match_scrutinee82]]])
                                            (delay (force [__builtin_Trace
                                              (con string "Expected PubKeyCredential")
                                              (delay (error))]))) __builtin_IfThenElse)))
                                      [(builtin unConstrData)
                                        [__builtin_HeadList addr-2769180]]])])
                              (lam self-30547764
                                (lam pubKeyHash-30547865
                                  [(lam pubKeyHash-305478r71
                                      (lam xIn72
                                        (case (constr 0 [(builtin unListData)
                                              [__builtin_HeadList
                                                [__builtin_TailList
                                                  [__builtin_TailList
                                                    [__builtin_TailList
                                                      [__builtin_TailList
                                                        [__builtin_TailList
                                                          [__builtin_TailList
                                                            [__builtin_TailList
                                                              [__builtin_TailList
                                                                self-30547764]]]]]]]]]]
                                            pubKeyHash-305478r71 (lam xIn73
                                              [(lam xIn73r74
                                                  (lam xIn75
                                                    [xIn72 xIn73r74
                                                      [(builtin unBData)
                                                        xIn75]]))
                                                [(builtin unBData)
                                                  xIn73]])) (lam self-9039345
                                            (lam elem-9039546
                                              (lam eq-9039647
                                                (case (constr 0 (case (constr 0 [(builtin equalsInteger)
                                                          [__builtin_FstPair
                                                            [(builtin unConstrData)
                                                              [scalus_cardano_onchain_plutus_prelude_List__find28
                                                                self-9039345
                                                                (lam __1-21260051
                                                                  [eq-9039647
                                                                    __1-21260051
                                                                    elem-9039546])]]]
                                                          (con integer 0)]
                                                        (con bool False)
                                                        (con bool True)) __builtin_IfThenElse)
                                                    (con bool False)
                                                    (con bool True)) __builtin_IfThenElse)))))))
                                    [(builtin bData) pubKeyHash-30547865]
                                    (builtin equalsByteString)]))]) [__Z
                            (lam scalus_cardano_onchain_plutus_prelude_List__find28
                              (lam self-9039929
                                (lam predicate-9040030
                                  (force (case (constr 0 self-9039929
                                      (delay (con data (Constr 1 [])))
                                      (delay (force (case (constr 0 [predicate-9040030
                                            [__builtin_HeadList self-9039929]]
                                          (delay [(builtin constrData)
                                            (con integer 0) [__builtin_MkCons
                                              [__builtin_HeadList self-9039929]
                                              (con (list data) [])]])
                                          (delay [(lam consTail33
                                              (lam xIn34
                                                [scalus_cardano_onchain_plutus_prelude_List__find28
                                                  consTail33 xIn34]))
                                            [__builtin_TailList self-9039929]
                                            predicate-9040030])) __builtin_IfThenElse)))) __builtin_ChooseList)))))]])
                      (lam ff
                        [(lam xx [ff (lam vv [xx xx vv])]) (lam xx
                            [ff (lam vv [xx xx vv])])])]))))))))))