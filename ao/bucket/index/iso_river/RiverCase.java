package ao.bucket.index.iso_river;

import ao.bucket.index.iso_cards.wild.card.WildCard;

/**
 * Date: Sep 1, 2008
 * Time: 8:38:55 PM
 */
public enum RiverCase
{
    //--------------------------------------------------------------------
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    public static RiverCase VALUES[] = values();


    //--------------------------------------------------------------------
    private final int PRERIVER_SUITED;


    //--------------------------------------------------------------------
    private RiverCase(int preriverSuited)
    {
        PRERIVER_SUITED = preriverSuited;
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return 13 - PRERIVER_SUITED;
    }


    //--------------------------------------------------------------------
    public int subIndex(WildCard    river,
                        WildCard... preRiver)
    {
        int offset = 0;
        for (WildCard precedent : preRiver)
        {
            if (precedent.suit()           == river.suit() &&
                precedent.rank().ordinal() <  river.rank().ordinal())
                offset++;
        }
        return river.rank().ordinal() - offset;
    }

//    public int
}
