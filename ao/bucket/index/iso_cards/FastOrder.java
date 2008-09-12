package ao.bucket.index.iso_cards;

import ao.bucket.index.iso_cards.wild.card.FastWildCard;
import ao.bucket.index.iso_cards.wild.suit.WildSuit;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Suit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    H_C_S_D(1, 3, 0, 2) { public FastOrder refine(FastOrder with) {
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
        return FastOrder.refinePair(this, 2, 3, with); }},
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
        return FastOrder.refineTwoPair(this, 2, 3, 0, 1, with); }},
    CS_DH(0, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTwoPair(this, 0, 3, 1, 2, with); }},
    CD_HS(0, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTwoPair(this, 0, 1, 2, 3, with); }},
    DH_CS(1, 0, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTwoPair(this, 1, 2, 0, 3, with); }},
    DS_CH(1, 0, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTwoPair(this, 1, 3, 0, 2, with); }},
    CH_DS(0, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTwoPair(this, 0, 2, 1, 3, with); }},

    C_DHS(0, 1, 1, 1) {public FastOrder refine(FastOrder with){
        return FastOrder.refineTriplet(this, 1, 2, 3, with); }},
    D_CHS(1, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTriplet(this, 0, 2, 3, with); }},
    H_CDS(1, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTriplet(this, 0, 1, 3, with); }},
    S_CDH(1, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return FastOrder.refineTriplet(this, 0, 1, 2, with); }},

    DHS_C(1, 0, 0, 0) {public FastOrder refine(FastOrder with){
        return FastOrder.refineTriplet(this, 1, 2, 3, 0, with); }},
    CHS_D(0, 1, 0, 0) {public FastOrder refine(FastOrder with){
        return FastOrder.refineTriplet(this, 0, 2, 3, 1, with); }},
    CDS_H(0, 0, 1, 0) {public FastOrder refine(FastOrder with){
        return FastOrder.refineTriplet(this, 0, 1, 3, 2, with); }},
    CDH_S(0, 0, 0, 1) {public FastOrder refine(FastOrder with){
        return FastOrder.refineTriplet(this, 0, 1, 2, 3, with); }},
    ;

    public static final FastOrder VALUES[] = values();

    private static final FastOrder INDEX[][][][];
    static
    {
        INDEX = new FastOrder[4][4][4][4];

        for (int a = 0; a < 4; a++)
            for (int b = 0; b < 4; b++)
                for (int c = 0; c < 4; c++)
                    for (int d = 0; d < 4; d++)
                        INDEX[a][b][c][d] = computeValueOf(a, b, c, d);
    }


    //--------------------------------------------------------------------
    private static final FastOrder PAIR_INDEX[][];
    static
    {
        PAIR_INDEX = new FastOrder[Suit.VALUES.length]
                                  [Suit.VALUES.length];
        for (int a = 0; a < Suit.VALUES.length; a++)
            for (int b = 0; b < Suit.VALUES.length; b++)
                PAIR_INDEX[a][b] = computePair(
                        Suit.VALUES[a], Suit.VALUES[b]);
    }
    public static FastOrder pair(Suit aheadA, Suit aheadB)
    {
        return PAIR_INDEX[aheadA.ordinal()]
                         [aheadB.ordinal()];
    }
    private static FastOrder computePair(Suit aheadA, Suit aheadB)
    {
        boolean behind[] = new boolean[4];
        for (Suit suit : Suit.VALUES) behind[suit.ordinal()] =
                                      (suit != aheadA && suit != aheadB);
        return valueOf(behind);
    }

    //--------------------------------------------------------------------
    private static final FastOrder SUITED_INDEX[];
    static
    {
        SUITED_INDEX = new FastOrder[Suit.VALUES.length];
        for (int a = 0; a < Suit.VALUES.length; a++)
            SUITED_INDEX[a] = computeSuited(Suit.VALUES[a]);
    }
    public static FastOrder suited(Suit ahead)
    {
        return SUITED_INDEX[ahead.ordinal()];
    }
    private static FastOrder computeSuited(Suit ahead)
    {
        boolean behind[] = new boolean[4];
        for (Suit suit : Suit.VALUES) behind[suit.ordinal()] =
                                        (suit != ahead);
        return valueOf(behind);
    }

    //--------------------------------------------------------------------
    private static final FastOrder UNSUITED_INDEX[][];
    static
    {
        UNSUITED_INDEX = new FastOrder[Suit.VALUES.length]
                                      [Suit.VALUES.length];
        for (int a = 0; a < Suit.VALUES.length; a++)
            for (int b = 0; b < Suit.VALUES.length; b++)
                UNSUITED_INDEX[a][b] = computeUnsuited(
                        Suit.VALUES[a], Suit.VALUES[b]);
    }
    public static FastOrder unsuited(Suit ahead, Suit behind)
    {
        return UNSUITED_INDEX[ahead .ordinal()]
                             [behind.ordinal()];
    }
    private static FastOrder computeUnsuited(Suit ahead, Suit behind)
    {
        int precedence[] = new int[Suit.VALUES.length];
        for (Suit suit : Suit.VALUES)
        {
            precedence[suit.ordinal()] =
                      (suit == ahead ) ? 0
                    : (suit == behind) ? 1
                                       : 2;
        }
        return valueOf(precedence);
    }

    //--------------------------------------------------------------------
    private static final FastOrder TRIPLET_INDEX[][][];
    static
    {
        TRIPLET_INDEX = new FastOrder[Suit.VALUES.length]
                                     [Suit.VALUES.length]
                                     [Suit.VALUES.length];
        for (Suit a : Suit.VALUES)
            for (Suit b : Suit.VALUES)
                for (Suit c : Suit.VALUES)
                    TRIPLET_INDEX[a.ordinal()][b.ordinal()][c.ordinal()]
                            = computeTriplet(a, b, c);
    }
    public static FastOrder triplet(
            Suit aheadA, Suit aheadB, Suit aheadC)
    {
        return TRIPLET_INDEX[ aheadA.ordinal() ]
                            [ aheadB.ordinal() ]
                            [ aheadC.ordinal() ];
    }
    private static FastOrder computeTriplet(
            Suit aheadA, Suit aheadB, Suit aheadC)
    {
        boolean behind[] = new boolean[4];
        for (Suit suit : Suit.VALUES)
            behind[suit.ordinal()] =
                    !(suit == aheadA ||
                      suit == aheadB ||
                      suit == aheadC);
        return valueOf(behind);
    }

    //--------------------------------------------------------------------
    public static FastOrder partSuited(
            Suit ahead, Suit mid)
    {
        return unsuited(ahead, mid);
    }

    //--------------------------------------------------------------------
    private static final FastOrder PART_INDEX[][][];
    static
    {
        PART_INDEX = new FastOrder[Suit.VALUES.length]
                                  [Suit.VALUES.length]
                                  [Suit.VALUES.length];
        for (Suit a : Suit.VALUES)
            for (Suit b : Suit.VALUES)
                for (Suit c : Suit.VALUES)
                    PART_INDEX[a.ordinal()][b.ordinal()][c.ordinal()]
                            = computePartSuited(a, b, c);
    }
    public static FastOrder partSuited(
            Suit aheadA, Suit aheadB, Suit mid)
    {
        return PART_INDEX[ aheadA.ordinal() ]
                         [ aheadB.ordinal() ]
                         [ mid   .ordinal() ];
    }
    private static FastOrder computePartSuited(
            Suit aheadA, Suit aheadB, Suit mid)
    {
        int precedence[] = new int[Suit.VALUES.length];
        for (Suit suit : Suit.VALUES)
        {
            precedence[suit.ordinal()] =
                    (suit == aheadA || suit == aheadB) ? 0
                  : (suit == mid                     ) ? 1
                                                       : 2;
        }
        return valueOf(precedence);
    }

    //--------------------------------------------------------------------
    private static final FastOrder ORDERED_INDEX[][][];
    static
    {
        ORDERED_INDEX = new FastOrder[Suit.VALUES.length]
                                     [Suit.VALUES.length]
                                     [Suit.VALUES.length];
        for (Suit a : Suit.VALUES)
            for (Suit b : Suit.VALUES)
                for (Suit c : Suit.VALUES)
                    ORDERED_INDEX[a.ordinal()][b.ordinal()][c.ordinal()]
                            = computeOrdered(a, b, c);
    }
    public static FastOrder ordered(
            Suit ahead, Suit mid, Suit behind)
    {
        return ORDERED_INDEX[ ahead .ordinal() ]
                            [ mid   .ordinal() ]
                            [ behind.ordinal() ];
    }
    private static FastOrder computeOrdered(
            Suit ahead, Suit mid, Suit behind)
    {
        int precedence[] = new int[Suit.VALUES.length];
        for (Suit suit : Suit.VALUES)
        {
            precedence[suit.ordinal()] =
                       (suit == ahead ) ? 0
                     : (suit == mid   ) ? 1
                     : (suit == behind) ? 2
                                        : 3;
        }
        return valueOf(precedence);
    }

    //--------------------------------------------------------------------
    public static FastOrder valueOf(
            int precedenceClub,
            int precedenceDiamond,
            int precedenceHeart,
            int precedenceSpade)
    {
        return INDEX [precedenceClub   ]
                     [precedenceDiamond]
                     [precedenceHeart  ]
                     [precedenceSpade  ];
    }

    private static FastOrder valueOf(
            int precedence[])
    {
        return valueOf(precedence[0], precedence[1],
                       precedence[2], precedence[3]);
    }

    private static FastOrder valueOf(
            boolean ones[])
    {
        return valueOf(ones[0] ? 1 : 0,
                       ones[1] ? 1 : 0,
                       ones[2] ? 1 : 0,
                       ones[3] ? 1 : 0);
    }


    //--------------------------------------------------------------------
    private static FastOrder computeValueOf(
            int precedenceClub,
            int precedenceDiamond,
            int precedenceHeart,
            int precedenceSpade)
    {
        int preferences[] = new int[]{precedenceClub,
                                      precedenceDiamond,
                                      precedenceHeart,
                                      precedenceSpade};
        for (FastOrder order : VALUES)
        {
            if (Arrays.equals(order.PRECEDENCE, preferences))
            {
                return order;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------

    // lower precedence means it comes before
//    private final int PRECEDENCE_CLUB;
//    private final int PRECEDENCE_DIAMOND;
//    private final int PRECEDENCE_HEART;
//    private final int PRECEDENCE_SPADE;
    private final int     PRECEDENCE[];
    private final boolean IS_WILD[];


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
        IS_WILD    = new boolean[Suit.VALUES.length];

        for (Suit s : Suit.VALUES)
        {
            for (Suit i : Suit.VALUES)
            {
                if (s == i) continue;
                if (PRECEDENCE[s.ordinal()] ==
                        PRECEDENCE[i.ordinal()])
                {
                    IS_WILD[ s.ordinal() ] = true;
                    break;
                }
            }
        }
    }



    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
    // break suit symmetries
    public abstract FastOrder refine(FastOrder with);


    //--------------------------------------------------------------------
    public Ordering toOrdering()
    {
        List<Suit[]> ordering = new ArrayList<Suit[]>();
        for (int count = 0; count < PRECEDENCE.length; count++)
        {
            List<Suit> countSuits = new ArrayList<Suit>();
            for (Suit suit : Suit.VALUES)
            {
                if (PRECEDENCE[suit.ordinal()] == count)
                {
                    countSuits.add( suit );
                }
            }
            if (! countSuits.isEmpty())
            {
                ordering.add(countSuits.toArray(
                                new Suit[countSuits.size()]));
            }
        }
        return new Ordering(ordering.toArray(
                        new Suit[ ordering.size() ][]));
    }

    //--------------------------------------------------------------------
    public WildSuit asWild(Suit suit)
    {
        return IS_WILD[ suit.ordinal() ]
               ? WildSuit.WILD
               : WildSuit.VALUES[
                    PRECEDENCE[ suit.ordinal() ]];
    }
    public FastWildCard asWild(Card card)
    {
        return FastWildCard.valueOf(
                card.rank(),
                asWild(card.suit()));
    }


    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
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

    private static FastOrder refineTwoPair(
            FastOrder order,
            int       orderZeroDupeA,
            int       orderZeroDupeB,
            int       orderOneDupeA,
            int       orderOneDupeB,
            FastOrder with)
    {
        int offset             = 0;
        int orderPrecedences[] = order.PRECEDENCE.clone();
        if (with.PRECEDENCE[orderZeroDupeA] <
                with.PRECEDENCE[orderZeroDupeB])
        {
            // a < b
            orderPrecedences[orderZeroDupeB] = ++offset;
        }
        else if (with.PRECEDENCE[orderZeroDupeB] <
                    with.PRECEDENCE[orderZeroDupeA])
        {
            // b < a
            orderPrecedences[orderZeroDupeA] = ++offset;
        }

        if (with.PRECEDENCE[orderOneDupeA] <
                with.PRECEDENCE[orderOneDupeB])
        {
            // a < b
            orderPrecedences[orderOneDupeA] += offset++;
            orderPrecedences[orderOneDupeB] += offset;
        }
        else if (with.PRECEDENCE[orderOneDupeB] <
                    with.PRECEDENCE[orderOneDupeA])
        {
            // b < a
            orderPrecedences[orderOneDupeB] += offset++;
            orderPrecedences[orderOneDupeA] += offset;
        }
        else
        {
            orderPrecedences[orderOneDupeA] += offset;
            orderPrecedences[orderOneDupeB] += offset;
        }

        return valueOf( orderPrecedences );
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
        else if (with.PRECEDENCE[orderDupeA] <
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
            int       unique,
            FastOrder with)
    {
        int precedences[] = order.PRECEDENCE.clone();
        int maxPrecedence = refineTriplet(
                precedences, orderDupeA, orderDupeB, orderDupeC, with);
        precedences[unique] = maxPrecedence + 1;
        return valueOf( precedences );
    }
    private static FastOrder refineTriplet(
            FastOrder order,
            int       orderDupeA,
            int       orderDupeB,
            int       orderDupeC,
            FastOrder with)
    {
        int precedences[] = order.PRECEDENCE.clone();
        refineTriplet(
                precedences, orderDupeA, orderDupeB, orderDupeC, with);
        return valueOf( precedences );
    }
    private static int refineTriplet(
            int       precedences[],
            int       orderDupeA,
            int       orderDupeB,
            int       orderDupeC,
            FastOrder with)
    {
        if (with.PRECEDENCE[orderDupeA] ==
                with.PRECEDENCE[orderDupeB] &&
            with.PRECEDENCE[orderDupeB] ==
                with.PRECEDENCE[orderDupeC]) return 0;

        if (refineHalfTriplet(
                orderDupeA, orderDupeB, orderDupeC, with, precedences) ||
            refineHalfTriplet(
                orderDupeB, orderDupeC, orderDupeA, with, precedences) ||
            refineHalfTriplet(
                orderDupeA, orderDupeC, orderDupeB, with, precedences))
            return 1;

        int base = precedences[orderDupeA];
        if (with.PRECEDENCE[orderDupeA] <
                with.PRECEDENCE[orderDupeB])
        {
            if (with.PRECEDENCE[orderDupeA] <
                    with.PRECEDENCE[orderDupeC])
            {
                if (with.PRECEDENCE[orderDupeB] <
                    with.PRECEDENCE[orderDupeC])
                {
                    precedences[orderDupeB] = base + 1;
                    precedences[orderDupeC] = base + 2;
                }
                else
                {
                    precedences[orderDupeC] = base + 1;
                    precedences[orderDupeB] = base + 2;
                }
            }
            else
            {
                precedences[orderDupeA] = base + 1;
                precedences[orderDupeB] = base + 2;
            }
        }
        else
        {
            if (with.PRECEDENCE[orderDupeB] <
                    with.PRECEDENCE[orderDupeC])
            {
                if (with.PRECEDENCE[orderDupeA] <
                        with.PRECEDENCE[orderDupeC])
                {
                    precedences[orderDupeA] = base + 1;
                    precedences[orderDupeC] = base + 2;
                }
                else
                {
                    precedences[orderDupeC] = base + 1;
                    precedences[orderDupeA] = base + 2;
                }
            }
            else
            {
                precedences[orderDupeB] = base + 1;
                precedences[orderDupeA] = base + 2;
            }
        }
        return 2;
    }
    private static boolean refineHalfTriplet(
            int       equalDupeA,
            int       equalDupeB,
            int       oddDupe,
            FastOrder with,
            int       precedences[])
    {
         if (with.PRECEDENCE[equalDupeA] ==
                    with.PRECEDENCE[equalDupeB])
        {
            if (with.PRECEDENCE[equalDupeB] <
                    with.PRECEDENCE[oddDupe])
            {
                precedences[oddDupe] =
                        precedences[equalDupeA] + 1;
            }
            else
            {
                precedences[equalDupeA] =
                        precedences[oddDupe] + 1;
                precedences[equalDupeB] = precedences[equalDupeA];
            }
            return true;
        }
        return false;
    }
}
