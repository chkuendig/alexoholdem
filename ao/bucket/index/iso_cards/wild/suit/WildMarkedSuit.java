package ao.bucket.index.iso_cards.wild.suit;

/**
 * Date: Aug 23, 2008
 * Time: 1:57:21 PM
 */
public enum WildMarkedSuit implements WildSuitMarker<WildMarkedSuit>
{
    //--------------------------------------------------------------------
    ONE_0(WildSuit.FIRST, 0),
    ONE_1(WildSuit.FIRST, 1),
    ONE_2(WildSuit.FIRST, 2),
    ONE_3(WildSuit.FIRST, 3),

    TWO_0(WildSuit.SECOND, 0),
    TWO_1(WildSuit.SECOND, 1),
    TWO_2(WildSuit.SECOND, 2),
    TWO_3(WildSuit.SECOND, 3),

    THREE_0(WildSuit.THIRD, 0),
    THREE_1(WildSuit.THIRD, 1),
    THREE_2(WildSuit.THIRD, 2),
    THREE_3(WildSuit.THIRD, 3),

    FOUR_0(WildSuit.FOURTH, 0),
    FOUR_1(WildSuit.FOURTH, 1),
    FOUR_2(WildSuit.FOURTH, 2),
    FOUR_3(WildSuit.FOURTH, 3),

    WILD_0(WildSuit.WILD, 0),
    WILD_1(WildSuit.WILD, 1),
    WILD_2(WildSuit.WILD, 2),
    WILD_3(WildSuit.WILD, 3);

    public static final WildMarkedSuit[] VALUES = WildMarkedSuit.values();


    //--------------------------------------------------------------------
//    private static final Map<WildSuit, WildMarkedSuit[]> INDEX;
//    static
//    {
//        INDEX = new EnumMap<WildSuit, WildMarkedSuit[]>(WildSuit.class);
//        for (WildSuit wildSuit : WildSuit.values())
//        {
//            INDEX.put(w)
//        }
//    }

    public static WildMarkedSuit newInstance(
            WildSuit suit,
            int      leftRankMatches)
    {
        assert 0 <= leftRankMatches && leftRankMatches < 4;

        return VALUES[ suit.ordinal() * 4 +
                       leftRankMatches ];
    }


    //--------------------------------------------------------------------
    private final WildSuit SUIT;
    private final int      LEFT_RANK_MATCHES;


    //--------------------------------------------------------------------
    private WildMarkedSuit(
            WildSuit suit,
            int      leftRankMatches)
    {
        SUIT              = suit;
        LEFT_RANK_MATCHES = leftRankMatches;
    }


    //--------------------------------------------------------------------
    public WildSuit suit()
    {
        return SUIT;
    }
    public int leftRankMatches()
    {
        return LEFT_RANK_MATCHES;
    }
}
