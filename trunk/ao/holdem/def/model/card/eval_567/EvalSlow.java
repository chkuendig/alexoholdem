package ao.holdem.def.model.card.eval_567;

import ao.holdem.def.model.card.Card;

/**
 *
 */
public class EvalSlow
{
    //--------------------------------------------------------------------
    public static short valueOf(Card... sevenCards)
    {
        return valueOf(sevenCards[0], sevenCards[1], sevenCards[2],
                       sevenCards[3], sevenCards[4], sevenCards[5],
                       sevenCards[6]);
    }

    public static short valueOf(
            Card c0, Card c1, Card c2, Card c3,
            Card c4, Card c5, Card c6)
    {
        int value;

        value =                 Card.handValue(c0,c1,c2,c3,c4);
        value = Math.max(value, Card.handValue(c0,c1,c2,c3,c5));
        value = Math.max(value, Card.handValue(c0,c1,c2,c3,c6));
        value = Math.max(value, Card.handValue(c0,c1,c2,c4,c5));
        value = Math.max(value, Card.handValue(c0,c1,c2,c4,c6));
        value = Math.max(value, Card.handValue(c0,c1,c2,c5,c6));
        value = Math.max(value, Card.handValue(c0,c1,c3,c4,c5));
        value = Math.max(value, Card.handValue(c0,c1,c3,c4,c6));
        value = Math.max(value, Card.handValue(c0,c1,c3,c5,c6));
        value = Math.max(value, Card.handValue(c0,c1,c4,c5,c6));
        value = Math.max(value, Card.handValue(c0,c2,c3,c4,c5));
        value = Math.max(value, Card.handValue(c0,c2,c3,c4,c6));
        value = Math.max(value, Card.handValue(c0,c2,c3,c5,c6));
        value = Math.max(value, Card.handValue(c0,c2,c4,c5,c6));
        value = Math.max(value, Card.handValue(c0,c3,c4,c5,c6));
        value = Math.max(value, Card.handValue(c1,c2,c3,c4,c5));
        value = Math.max(value, Card.handValue(c1,c2,c3,c4,c6));
        value = Math.max(value, Card.handValue(c1,c2,c3,c5,c6));
        value = Math.max(value, Card.handValue(c1,c2,c4,c5,c6));
        value = Math.max(value, Card.handValue(c1,c3,c4,c5,c6));
        value = Math.max(value, Card.handValue(c2,c3,c4,c5,c6));

        return (short) value;
    }

    //--------------------------------------------------------------------
    // card signitures
    public static short valueOf(
            int c0, int c1, int c2, int c3, int c4, int c5, int c6)
    {
        int value;

        value =                 Card.handValue(c0,c1,c2,c3,c4);
        value = Math.max(value, Card.handValue(c0,c1,c2,c3,c5));
        value = Math.max(value, Card.handValue(c0,c1,c2,c3,c6));
        value = Math.max(value, Card.handValue(c0,c1,c2,c4,c5));
        value = Math.max(value, Card.handValue(c0,c1,c2,c4,c6));
        value = Math.max(value, Card.handValue(c0,c1,c2,c5,c6));
        value = Math.max(value, Card.handValue(c0,c1,c3,c4,c5));
        value = Math.max(value, Card.handValue(c0,c1,c3,c4,c6));
        value = Math.max(value, Card.handValue(c0,c1,c3,c5,c6));
        value = Math.max(value, Card.handValue(c0,c1,c4,c5,c6));
        value = Math.max(value, Card.handValue(c0,c2,c3,c4,c5));
        value = Math.max(value, Card.handValue(c0,c2,c3,c4,c6));
        value = Math.max(value, Card.handValue(c0,c2,c3,c5,c6));
        value = Math.max(value, Card.handValue(c0,c2,c4,c5,c6));
        value = Math.max(value, Card.handValue(c0,c3,c4,c5,c6));
        value = Math.max(value, Card.handValue(c1,c2,c3,c4,c5));
        value = Math.max(value, Card.handValue(c1,c2,c3,c4,c6));
        value = Math.max(value, Card.handValue(c1,c2,c3,c5,c6));
        value = Math.max(value, Card.handValue(c1,c2,c4,c5,c6));
        value = Math.max(value, Card.handValue(c1,c3,c4,c5,c6));
        value = Math.max(value, Card.handValue(c2,c3,c4,c5,c6));

        return (short) value;
    }


    //--------------------------------------------------------------------
    public static short valueOf(
            int c0, int c1, int c2, int c3, int c4, int c5)
    {
        int value;

        value =                 Card.handValue(c0,c1,c2,c3,c4);
        value = Math.max(value, Card.handValue(c0,c1,c2,c3,c5));
        value = Math.max(value, Card.handValue(c0,c1,c2,c4,c5));
        value = Math.max(value, Card.handValue(c0,c1,c3,c4,c5));
        value = Math.max(value, Card.handValue(c0,c2,c3,c4,c5));
        value = Math.max(value, Card.handValue(c1,c2,c3,c4,c5));

        return (short) value;
    }


    //--------------------------------------------------------------------
    public static short valueOf(
            Card c0, Card c1, Card c2, Card c3, Card c4)
    {
        return Card.handValue(c0, c1, c2, c3, c4);
    }

    public static short valueOf(
            int c0, int c1, int c2, int c3, int c4)
    {
        return Card.handValue(c0, c1, c2, c3, c4);
    }
}
