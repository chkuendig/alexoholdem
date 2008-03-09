package ao.holdem.v3.model;

import ao.holdem.v3.model.card.Community;

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


    //--------------------------------------------------------------------
    public Round next()
    {
        return (this == RIVER)
                ? null
                : values()[ ordinal() + 1 ];
    }
}
