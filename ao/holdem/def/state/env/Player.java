package ao.holdem.def.state.env;

/**
 * 
 */
public class Player
{
    //--------------------------------------------------------------------
    private final Position      POSITION;
//    private final PlayerHistory HISTORY;
    private final int           COMMITMENT;
    private final boolean       IS_ACTIVE;
    private final TakenAction   LAST_ACTION;


    //--------------------------------------------------------------------
    public Player(Position      position,
//                  PlayerHistory history,
                  int           commitment,
                  boolean       isActive,
                  TakenAction   lastAction)
    {
        POSITION    = position;
//        HISTORY     = history;
        COMMITMENT  = commitment;
        IS_ACTIVE   = isActive;
        LAST_ACTION = lastAction;
    }


    //--------------------------------------------------------------------
    /**
     * @return true if this player did not fold.
     */
    public boolean isActive()
    {
        return IS_ACTIVE;
    }


    //--------------------------------------------------------------------
    /**
     * @return most recent action taken by this player in the
     *              current round of betting.
     */
    public TakenAction lastAction()
    {
        return LAST_ACTION;
    }

//    public TakenAction lastAction(BettingRound bettingRound)
//    {
//        return null;
//    }
//
//    public List<TakenAction> actions(BettingRound bettingRound)
//    {
//        return null;
//    }

                    
    //--------------------------------------------------------------------
    /**
     * @return how many small blinds worth of betting does this player
     *          have in the pot.
     */
    public int commitment()
    {
        return COMMITMENT;
    }
    

    //--------------------------------------------------------------------
    public Position position()
    {
        return POSITION;
    }


    //--------------------------------------------------------------------
//    public PlayerHistory history()
//    {
//        return HISTORY;
//    }

    //--------------------------------------------------------------------
    public String toString()
    {
        return "commitment: "  + commitment() + ", " +
                                 position()   + ", " +
               "last action: " + lastAction();
    }
}
