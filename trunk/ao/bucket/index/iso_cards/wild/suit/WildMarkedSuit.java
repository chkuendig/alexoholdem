package ao.bucket.index.iso_cards.wild.suit;

/**
 * Date: Aug 23, 2008
 * Time: 1:57:21 PM
 */
public enum WildMarkedSuit implements WildSuitMarker<WildMarkedSuit>
{
    //--------------------------------------------------------------------
    ONE_0(WildSuit.ONE, 0),
    ONE_1(WildSuit.ONE, 1),
    ONE_2(WildSuit.ONE, 2),
    ONE_3(WildSuit.ONE, 3),

    TWO_0(WildSuit.TWO, 0),
    TWO_1(WildSuit.TWO, 1),
    TWO_2(WildSuit.TWO, 2),
    TWO_3(WildSuit.TWO, 3),

    THREE_0(WildSuit.THREE, 0),
    THREE_1(WildSuit.THREE, 1),
    THREE_2(WildSuit.THREE, 2),
    THREE_3(WildSuit.THREE, 3),

    FOUR_0(WildSuit.FOUR, 0),
    FOUR_1(WildSuit.FOUR, 1),
    FOUR_2(WildSuit.FOUR, 2),
    FOUR_3(WildSuit.FOUR, 3),

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
