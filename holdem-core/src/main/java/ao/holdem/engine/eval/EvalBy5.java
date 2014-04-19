package ao.holdem.engine.eval;

import ao.holdem.model.card.Card;

import java.util.Arrays;

/**
 * All card values are returned in the range of...
 *  0 (lowest) to 7461 (highest)
 *
 * See: http://www.suffecool.net/poker/7462.html
 */
public enum EvalBy5
{;
    //--------------------------------------------------------------------
    public static short valueOf(Card... cards)
    {
        if (cards.length == 5)
        {
            return valueOf(cards[0], cards[1], cards[2],
                           cards[3], cards[4]);
        }
        else if (cards.length == 6)
        {
            return valueOf(cards[0], cards[1], cards[2],
                           cards[3], cards[4], cards[5]);
        }
        else if (cards.length == 7)
        {
            return valueOf(cards[0], cards[1], cards[2],
                           cards[3], cards[4], cards[5],
                           cards[6]);
        }
        throw new Error("Can only handle 5 to 7 cards: " + Arrays.toString(cards));
    }

    public static short valueOf(
            Card c0, Card c1, Card c2, Card c3,
            Card c4, Card c5, Card c6)
    {
        int value;

        value =                 Eval5.valueOf(c0,c1,c2,c3,c4);
        value = Math.max(value, Eval5.valueOf(c0,c1,c2,c3,c5));
        value = Math.max(value, Eval5.valueOf(c0,c1,c2,c3,c6));
        value = Math.max(value, Eval5.valueOf(c0,c1,c2,c4,c5));
        value = Math.max(value, Eval5.valueOf(c0,c1,c2,c4,c6));
        value = Math.max(value, Eval5.valueOf(c0,c1,c2,c5,c6));
        value = Math.max(value, Eval5.valueOf(c0,c1,c3,c4,c5));
        value = Math.max(value, Eval5.valueOf(c0,c1,c3,c4,c6));
        value = Math.max(value, Eval5.valueOf(c0,c1,c3,c5,c6));
        value = Math.max(value, Eval5.valueOf(c0,c1,c4,c5,c6));
        value = Math.max(value, Eval5.valueOf(c0,c2,c3,c4,c5));
        value = Math.max(value, Eval5.valueOf(c0,c2,c3,c4,c6));
        value = Math.max(value, Eval5.valueOf(c0,c2,c3,c5,c6));
        value = Math.max(value, Eval5.valueOf(c0,c2,c4,c5,c6));
        value = Math.max(value, Eval5.valueOf(c0,c3,c4,c5,c6));
        value = Math.max(value, Eval5.valueOf(c1,c2,c3,c4,c5));
        value = Math.max(value, Eval5.valueOf(c1,c2,c3,c4,c6));
        value = Math.max(value, Eval5.valueOf(c1,c2,c3,c5,c6));
        value = Math.max(value, Eval5.valueOf(c1,c2,c4,c5,c6));
        value = Math.max(value, Eval5.valueOf(c1,c3,c4,c5,c6));
        value = Math.max(value, Eval5.valueOf(c2,c3,c4,c5,c6));

        return (short) value;
    }


    //--------------------------------------------------------------------
    // Cactus Kev's signitures.
    //  see Eval5.asCactusKevsFormat( Card )
    public static short valueOf(
            int ck0, int ck1, int ck2,
            int ck3, int ck4, int ck5, int ck6)
    {
        int value;

        value =                 Eval5.valueOf(ck0,ck1,ck2,ck3,ck4);
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck2,ck3,ck5));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck2,ck3,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck2,ck4,ck5));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck2,ck4,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck2,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck3,ck4,ck5));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck3,ck4,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck3,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck4,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck2,ck3,ck4,ck5));
        value = Math.max(value, Eval5.valueOf(ck0,ck2,ck3,ck4,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck2,ck3,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck2,ck4,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck0,ck3,ck4,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck1,ck2,ck3,ck4,ck5));
        value = Math.max(value, Eval5.valueOf(ck1,ck2,ck3,ck4,ck6));
        value = Math.max(value, Eval5.valueOf(ck1,ck2,ck3,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck1,ck2,ck4,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck1,ck3,ck4,ck5,ck6));
        value = Math.max(value, Eval5.valueOf(ck2,ck3,ck4,ck5,ck6));

        return (short) value;
    }


    //--------------------------------------------------------------------
    // Cactus Kev's signitures.
    //  see Eval5.asCactusKevsFormat( Card )
    public static short valueOf(
            int ck0, int ck1, int ck2, int ck3, int ck4, int ck5)
    {
        int value;

        value =                 Eval5.valueOf(ck0,ck1,ck2,ck3,ck4);
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck2,ck3,ck5));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck2,ck4,ck5));
        value = Math.max(value, Eval5.valueOf(ck0,ck1,ck3,ck4,ck5));
        value = Math.max(value, Eval5.valueOf(ck0,ck2,ck3,ck4,ck5));
        value = Math.max(value, Eval5.valueOf(ck1,ck2,ck3,ck4,ck5));

        return (short) value;
    }

    public static short valueOf(
            Card c0, Card c1, Card c2, Card c3, Card c4, Card c5)
    {
        int value;

        value =                 Eval5.valueOf(c0,c1,c2,c3,c4);
        value = Math.max(value, Eval5.valueOf(c0,c1,c2,c3,c5));
        value = Math.max(value, Eval5.valueOf(c0,c1,c2,c4,c5));
        value = Math.max(value, Eval5.valueOf(c0,c1,c3,c4,c5));
        value = Math.max(value, Eval5.valueOf(c0,c2,c3,c4,c5));
        value = Math.max(value, Eval5.valueOf(c1,c2,c3,c4,c5));

        return (short) value;
    }


    //--------------------------------------------------------------------
    public static short valueOf(
            Card c0, Card c1, Card c2, Card c3, Card c4)
    {
        return Eval5.valueOf(c0, c1, c2, c3, c4);
    }

    // Cactus Kev's signitures.
    //  see Eval5.asCactusKevsFormat( Card )
    public static short valueOf(
            int ck0, int ck1, int ck2, int ck3, int ck4)
    {
        return Eval5.valueOf(ck0, ck1, ck2, ck3, ck4);
    }
}
