package ao.bucket.index.iso_cards;

/**
 * Date: Sep 7, 2008
 * Time: 1:51:10 PM
 *
 * mass boilerplate
 */
public enum FastOrder
{
    //--------------------------------------------------------------------
    C_D_H_S(0, 1, 2, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_D_S_H(0, 1, 3, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_H_S_D(0, 3, 1, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_H_D_S(0, 2, 1, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_S_D_H(0, 2, 3, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_S_H_D(0, 3, 2, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_C_S_H(1, 0, 3, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_C_H_S(1, 0, 2, 3) { public FastOrder refine(FastOrder with) {
        return this;}},
    D_H_S_C(3, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_H_C_S(2, 0, 1, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_S_C_H(2, 0, 3, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_S_H_C(3, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_C_D_S(1, 2, 0, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_C_S_D(1, 3, 0, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_D_C_S(2, 1, 0, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_D_S_C(3, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_S_D_C(3, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_S_C_D(2, 3, 0, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_C_D_H(1, 2, 3, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_C_H_D(1, 3, 2, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_D_H_C(3, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_D_C_H(2, 1, 3, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_H_C_D(2, 3, 1, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_H_D_C(3, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return this; }},

    C_S_DH(0, 2, 2, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 2, with); }},
    C_DS_H(0, 1, 2, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 2, 1, 3, with); }},
    C_HS_D(0, 2, 1, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 2, 3, with); }},
    C_DH_S(0, 1, 1, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 3, 1, 2, with); }},
    C_D_HS(0, 1, 2, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 2, 3, with); }},
    C_H_DS(0, 2, 1, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 3, with); }},
    CD_S_H(0, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 3, 2, 0, 1, with); }},
    D_C_HS(1, 0, 2, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 2, 3, with); }},
    CD_H_S(0, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 2, 3, 0, 1, with); }},
    CH_S_D(0, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 3, 1, 0, 2, with); }},
    CH_D_S(0, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 3, 0, 2, with); }},
    H_C_DS(1, 2, 0, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 3, with); }},
    S_C_DH(1, 2, 2, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 2, with); }},
    CS_D_H(0, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 2, 0, 3, with); }},
    CS_H_D(0, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 2, 1, 0, 3, with); }},
    D_CH_S(1, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 3, 0, 3, with); }},
    D_HS_C(2, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 2, 3, with); }},
    D_CS_H(1, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 2, 0, 3, with); }},
    D_S_CH(2, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 2, with); }},
    D_H_CS(2, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 3, with); }},
    DH_C_S(1, 0, 0, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 3, 1, 2, with); }},
    DH_S_C(2, 0, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 3, 0, 1, 2, with); }},
    H_D_CS(2, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 3, with); }},
    DS_C_H(1, 0, 2, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 2, 1, 3, with); }},
    S_D_CH(2, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 2, with); }},
    DS_H_C(2, 0, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 2, 0, 1, 3, with); }},
    H_CD_S(1, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 3, 0, 1, with); }},
    H_CS_D(1, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 3, with); }},
    H_DS_C(2, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 1, 3, with); }},
    HS_C_D(1, 2, 0, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 1, 2, 3, with); }},
    HS_D_C(2, 1, 0, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 0, 2, 3, with); }},
    S_H_CD(2, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 1, with); }},
    S_CD_H(1, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 2, 0, 1, with); }},
    S_CH_D(1, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 1, 0, 2, with); }},
    S_DH_C(2, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 1, 2, with); }},
    H_S_CD(2, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refinePair(this, 0, 1, with); }},

    HS_CD(1, 1, 0, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CS_DH(0, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CD_HS(0, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DH_CS(1, 0, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DS_CH(1, 0, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CH_DS(0, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},

    C_DHS(0, 1, 1, 1) {public FastOrder refine(FastOrder with){
        return FastOrder.refineTriplet(this, 1, 2, 3, with); }},
    D_CHS(1, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTriplet(this, 0, 2, 3, with); }},
    H_CDS(1, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTriplet(this, 0, 1, 3, with); }},
    S_CDH(1, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTriplet(this, 0, 1, 2, with); }},
    ;


    //--------------------------------------------------------------------
    public static FastOrder valueOf(
            int precedenceClub,
            int precedenceDiamond,
            int precedenceHeart,
            int precedenceSpade)
    {
//        return new FastOrder(precedenceClub,
//                             precedenceDiamond,
//                             precedenceHeart,
//                             precedenceSpade);
        return null;
    }

    private static FastOrder valueOf(
            int precedence[])
    {
        return null;
    }


    //--------------------------------------------------------------------

    // lower precedence means it comes before
//    private final int PRECEDENCE_CLUB;
//    private final int PRECEDENCE_DIAMOND;
//    private final int PRECEDENCE_HEART;
//    private final int PRECEDENCE_SPADE;
    private final int PRECEDENCE[];


    //--------------------------------------------------------------------
    private FastOrder(int precedenceClub,
                      int precedenceDiamond,
                      int precedenceHeart,
                      int precedenceSpade)
    {
        this(new int[]{
                precedenceClub,
                precedenceDiamond,
                precedenceHeart,
                precedenceSpade
            });
    }

    private FastOrder(int precedence[])
    {
        PRECEDENCE = precedence;
    }


    //--------------------------------------------------------------------
    // break suit symmetries
    public abstract FastOrder refine(FastOrder with);



    //--------------------------------------------------------------------
    private static FastOrder refinePair(
            FastOrder order,
            int       orderDupeA,
            int       orderDupeB,
            FastOrder with)
    {
        int precedences[] = refinePair(
                order.PRECEDENCE, orderDupeA, orderDupeB, with);
        return precedences == order.PRECEDENCE
               ? order
               : valueOf( precedences );
    }

    private static FastOrder refinePair(
            FastOrder order,
            int       orderUnique2,
            int       orderDupeA,
            int       orderDupeB,
            FastOrder with)
    {
        int precedences[] = refinePair(
                order.PRECEDENCE, orderDupeA, orderDupeB, with);
        if (precedences == order.PRECEDENCE)
        {
            return order;
        }
        else
        {
            precedences[ orderUnique2 ] = 3;
            return valueOf(precedences);
        }
    }

    private static FastOrder refinePair(
            FastOrder order,
            int       orderUnique1,
            int       orderUnique2,
            int       orderDupeA,
            int       orderDupeB,
            FastOrder with)
    {
         int precedences[] = refinePair(
                order.PRECEDENCE, orderDupeA, orderDupeB, with);
        if (precedences == order.PRECEDENCE)
        {
            return order;
        }
        else
        {
            precedences[ orderUnique1 ] = 2;
            precedences[ orderUnique2 ] = 3;
            return valueOf(precedences);
        }
    }

    private static int[] refinePair(
            int       orderPrecedences[],
            int       orderDupeA,
            int       orderDupeB,
            FastOrder with)
    {
        int swapA, swapB;
        if (with.PRECEDENCE[orderDupeA] >
                    with.PRECEDENCE[orderDupeB])
        {
            swapA = orderDupeA;
            swapB = orderDupeB;
        }
        else if (with.PRECEDENCE[orderDupeA] >
                    with.PRECEDENCE[orderDupeB])
        {
            swapA = orderDupeB;
            swapB = orderDupeA;
        }
        else
        {
            return orderPrecedences;
        }

        int orderPrecedencesDupe[] = orderPrecedences.clone();
        orderPrecedencesDupe[ swapA ] =
                orderPrecedencesDupe[ swapB ] + 1;
        return orderPrecedencesDupe;
    }


    //--------------------------------------------------------------------
    private static FastOrder refineTriplet(
            FastOrder order,
            int       orderDupeA,
            int       orderDupeB,
            int       orderDupeC,
            FastOrder with)
    {
        int precedences[] = order.PRECEDENCE.clone();
        if (with.PRECEDENCE[orderDupeA] <
                with.PRECEDENCE[orderDupeB])
        {
            // a < b
            if (with.PRECEDENCE[orderDupeB] ==
                    with.PRECEDENCE[orderDupeC])
            {
                // a < b = c
                // a < c = b
                with.PRECEDENCE[orderDupeB] = 2;
                with.PRECEDENCE[orderDupeC] = 2;
            }
            else if (with.PRECEDENCE[orderDupeB] <
                        with.PRECEDENCE[orderDupeC])
            {
                // a < b < c
                precedences[orderDupeB] = 2;
                precedences[orderDupeC] = 3;
            }
            else if (with.PRECEDENCE[orderDupeA] <
                        with.PRECEDENCE[orderDupeC])
            {
                // a < c < b
                precedences[orderDupeC] = 2;
                precedences[orderDupeB] = 3;
            }
            else
            {
                // c < a < b
                precedences[orderDupeA] = 2;
                precedences[orderDupeB] = 3;
            }
        }
        else if (with.PRECEDENCE[orderDupeA] <
                    with.PRECEDENCE[orderDupeC])
        {
            // a >= b
            // a < c
            // b <= a < c
            if (with.PRECEDENCE[orderDupeA] ==
                        with.PRECEDENCE[orderDupeB])
            {
                // b = a < c
                precedences[orderDupeA] = 2;
                precedences[orderDupeB] = 2;
            }
            else //if (with.PRECEDENCE[orderDupeB] <
                 //       with.PRECEDENCE[orderDupeA])
            {
                // b < a < c
                precedences[orderDupeA] = 2;
                precedences[orderDupeC] = 3;
            }
        }
        else // if (with.PRECEDENCE[orderDupeB] <
             //         with.PRECEDENCE[orderDupeC])
        {
            // a >= b
            // a >= c
            // b < c

            // can't have this: b = a = c
            if (with.PRECEDENCE[orderDupeA] ==
                        with.PRECEDENCE[orderDupeC])
            {
                // b < c = a
                // b < a = c
                precedences[orderDupeA] = 2;
                precedences[orderDupeC] = 2;
            }
            else //if (with.PRECEDENCE[orderDupeC] <
                 //       with.PRECEDENCE[orderDupeA])
            {
                // b < c < a
                precedences[orderDupeC] = 2;
                precedences[orderDupeA] = 3;
            }
        }

        return valueOf(precedences);
    }
}
