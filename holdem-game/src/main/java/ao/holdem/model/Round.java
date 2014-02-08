package ao.holdem.model;

import ao.holdem.model.card.Community;

/**
 * 
 */
public enum Round
{
    //--------------------------------------------------------------------
    PREFLOP {
        public Community ofCommunity(Community full) {
            return full.asPreflop();
        }},

    FLOP {
        public Community ofCommunity(Community full) {
            return full.asFlop();
        }},

    TURN {
        public Community ofCommunity(Community full) {
            return full.asTurn();
        }},

    RIVER {
        public Community ofCommunity(Community full) {
            return full.asRiver();
        }};

    public static final Round[] VALUES = values();
    public static final int     COUNT  = VALUES.length;


//    //--------------------------------------------------------------------
//    private final Round NEXT;
//
//
//    //--------------------------------------------------------------------
//    private Round(Round next)
//    {
//        NEXT = next;
//    }


    //--------------------------------------------------------------------
    public abstract Community ofCommunity(Community full);


    //------------------------------------------------------------------------
    /**
     * @return PREFLOP -> FLOP, FLOP -> TURN, TURN -> RIVER, RIVER -> null
     */
    public Round next()
    {
        return (this == RIVER)
                ? null
                : values()[ ordinal() + 1 ];
    }

    /**
     * @return RIVER -> TURN, TURN -> FLOP, FLOP -> PREFLOP, PREFLOP -> null
     */
    public Round previous()
    {
        return (this == PREFLOP)
                ? null
                : values()[ ordinal() - 1 ];
    }
}
