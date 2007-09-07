package ao.holdem.bots.opp_model.predict.def.context;

import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class GenericContext
{
    //--------------------------------------------------------------------
    private double  immedatePotOdds;
    private int     raisesThisRound;
    private double  betRatio;
    private double  potRatio;
    private boolean committedThisRound;
    private int     betsToCall;
    private int     numOpps;
    private int     numActiveOpps;
    private int     numUnactedThisRound;
    private int     position;
    private int     activePosition;
    private int     lastBetsToCall;
    private double  winPercent;

    private TakenAction act;
    private TakenAction lastAct;
    private Community   currCommunity;

    private boolean      isHistAware = false;
    private boolean      isHoleAware = false;
    private BettingRound round;


    //--------------------------------------------------------------------
    public GenericContext(
            Snapshot    prev,
            TakenAction prevAct,
            Snapshot    curr,
            TakenAction currAct,
            Community   community,
            Hole        hole)
    {
        act         = currAct;
        round       = curr.comingRound();
        isHoleAware = (hole != null);
        isHistAware = (prev != null);

        immedatePotOdds =
                ((double) curr.toCall().smallBlinds()) /
                (curr.toCall().smallBlinds() +
                    curr.pot().smallBlinds());

        raisesThisRound = 4 - curr.remainingRaises();
        betRatio = raisesThisRound /
                   (raisesThisRound + curr.numCalls() + 0.001);
        potRatio =
                ((double) curr.stakes().smallBlinds()) /
                          curr.pot().smallBlinds();
        committedThisRound =
                (curr.latestRoundCommitment().smallBlinds() > 0);

        betsToCall = curr.toCall().bets( curr.isSmallBet() );

        numOpps = curr.opponents().size();
        numActiveOpps = curr.activeOpponents().size();
        numUnactedThisRound = curr.unactedThisRound();

        position =
                (curr.players().indexOf(
                        curr.nextToAct() ) + 1);
        activePosition =
                (curr.activePlayers().indexOf(
                        curr.nextToAct() ) + 1);

        // false warning "possible null" warning.
        lastBetsToCall =
                isHistAware
                ? prev.toCall().bets( curr.isSmallBet() ) : -1;
        lastAct = prevAct;

        currCommunity = community;
        if (isHoleAware)
        {
//            OddFinder oddFinder = new ApproximateOddFinder();
//            Odds odds = oddFinder.compute(
//                            hole, community,
//                            curr.activeOpponents().size());
//
//            winPercent = odds.strengthVsRandom();
        } else { winPercent = -1; }
    }


    //--------------------------------------------------------------------
    public double  immedatePotOdds()     { return immedatePotOdds; }
    public int     raisesThisRound()     { return raisesThisRound; }
    public double  betRatio()            { return betRatio; }
    public double  potRatio()            { return potRatio; }
    public boolean committedThisRound()  { return committedThisRound; }
    public int     betsToCall()          { return betsToCall; }
    public int     numOpps()             { return numOpps; }
    public int     numActiveOpps()       { return numActiveOpps; }
    public int     numUnactedThisRound() { return numUnactedThisRound; }
    public int     position()            { return position; }
    public int     activePosition()      { return activePosition; }
    public int     lastBetsToCall()      { return lastBetsToCall; }
    public double  winPercent()          { return winPercent; }
    public boolean isHoleAware()         { return isHoleAware; }
    public boolean isHistAware()         { return isHistAware; }

    public BettingRound round()     { return round; }
    public TakenAction  lastAct()   { return lastAct; }
    public TakenAction  currAct()   { return act; }
    public Community    community() { return currCommunity; }

}
