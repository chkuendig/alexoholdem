package ao.holdem.def.state.env;

import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.state.domain.BettingRound;

/**
 * Only valid within the context of a single action.
 */
public class Environment
{
    //--------------------------------------------------------------------
    private final Hole         HOLE;
    private final Community COMMUNITY;
    private final Player       BY_POSITION[];
    private final int          POSITION;
    private final int          TO_CALL;
    private final int          POT;
    private final int          COMMIT;
    private final int          REMAINING_RAISES;
    private final BettingRound ROUND;
    private final int          fromPositionToDistanceFromDealer[];


    //--------------------------------------------------------------------
    public Environment(Environment copyEnv)
    {
        this(copyEnv.HOLE, copyEnv.COMMUNITY, copyEnv.BY_POSITION,
             copyEnv.POSITION, copyEnv.TO_CALL, copyEnv.POT,
             copyEnv.COMMIT, copyEnv.REMAINING_RAISES, copyEnv.ROUND,
             copyEnv.fromPositionToDistanceFromDealer);
    }
    public Environment(Hole         hole,
                       Community    community,
                       Player       byPosition[],
                       int          youAwayFromFirstToAct,
                       int          toCall,
                       int          pot,
                       int          commit,
                       int          remainingRaises,
                       BettingRound round,
                       int          fromPositionToDistanceFromDealer[])
    {
        HOLE             = hole;
        COMMUNITY        = community;
        BY_POSITION      = byPosition;
        POSITION         = youAwayFromFirstToAct;
        TO_CALL          = toCall;
        POT              = pot;
        COMMIT           = commit;
        REMAINING_RAISES = remainingRaises;
        ROUND            = round;

        this.fromPositionToDistanceFromDealer =
                fromPositionToDistanceFromDealer;
    }


    //--------------------------------------------------------------------
    public Hole hole()
    {
        return HOLE;
    }

    public Community community()
    {
        return COMMUNITY;
    }


    //--------------------------------------------------------------------
    public int playerCount()
    {
        return BY_POSITION.length;
    }
    public int opponentCount()
    {
        return playerCount() - 1;
    }
    public int activePlayers()
    {
        int count = 0;
        for (Player player : BY_POSITION)
        {
            if (player.isActive())
            {
                count++;
            }
        }
        return count;
    }
    public int activeOpponents()
    {
        int activePlayers = activePlayers();
        if (you().isActive()) activePlayers--;
        return activePlayers;
    }


    //--------------------------------------------------------------------
    /**
     * @return 0 = first to act,
     *         1 = second to act,
     *              etc.
     */
    public int awayFromFirstToAct()
    {
        return POSITION;
    }

    public int awayFromDealer(int awayFromFirstToAct)
    {
        return fromPositionToDistanceFromDealer[awayFromFirstToAct];
    }

    public int awayFromDealer()
    {
        return awayFromDealer(you().position().awayFromFirstToAct());
    }

    // dealer has best (highest/latest) position.
    // small blind has worst (lowest/earliest) postion.
    public int position()
    {
        return awayFromDealer() == 0
                ? playerCount()
                : awayFromDealer();
    }

    // 1.0 = dealer.  ~0.1 = small blind.
    // pretty much its a % of latest postion.
    public double realPosition()
    {
        return ((double) position()) / playerCount();
    }


    public BettingRound bettingRound()
    {
        return ROUND;
    }


    //--------------------------------------------------------------------
    /**
     * @return the minimum number of small blinds that you are required
     *          to bet in order to call or check.
     *         if the pot hasn't been raised since the last time you
     *          played then this will be 0, since you can just call.
     */
    public int toCall()
    {
        return TO_CALL;
    }

    /**
     * @return number of small blinds in the pot.
     */
    public int pot()
    {
        return POT;
    }

    /**
     * @return how much money you already have in this current pot
     *          in small blinds.
     */
    public int commit()
    {
        return COMMIT;
    }


    //--------------------------------------------------------------------
    public boolean canCheck()
    {
        return TO_CALL == 0;
    }

    public int bets()
    {
        return 4 - REMAINING_RAISES;
    }
    public int remainingBets()
    {
        return REMAINING_RAISES;
    }

    public boolean canRaise()
    {
        return REMAINING_RAISES > 0;
    }


    //--------------------------------------------------------------------
    public Player you()
    {
        return BY_POSITION[ POSITION ];
    }

//    public Player dealer()
//    {
//        return null;
//    }
//
//    public Player smallBlind()
//    {
//        return null;
//    }
//
//    public Player bigBlind()
//    {
//        return null;
//    }
//
//    public Player underTheGun()
//    {
//        return null;
//    }
//
//    public Player cutoff()
//    {
//        return null;
//    }

    /**
     * @param relativeToYou 0 means you
     *                      1 means acting after you
     *                     -1 means acting before you
     * @return payer at offset
     */
    public Player playerOffset(int relativeToYou)
    {
        int index = POSITION + relativeToYou;
        return isInRange(index)
               ? BY_POSITION[ index ] : null;
    }

    /**
     * @param position starting at zero for first-to-act.
     *                  use -1 for last to act.
     * @return player at absolute postition.
     */
    public Player playerAt(int position)
    {
        int index = (position < 0)
                    ? BY_POSITION.length + position
                    : position;
        return isInRange(index)
                ? BY_POSITION[ index ] : null;
    }

    public Player[] players()
    {
        return BY_POSITION.clone();
    }


    //--------------------------------------------------------------------
//    public List<PlayerAction> roundActions(BettingRound bettingRound)
//    {
//        return null;
//    }

    
    //--------------------------------------------------------------------
    private boolean isInRange(int playerIndex)
    {
        return (0 <= playerIndex && playerIndex < BY_POSITION.length);
    }
}
