package ao.general.impl.khun;

import ao.general.GameState;
import ao.odds.agglom.Odds;
import ao.simple.kuhn.KuhnAction;
import ao.simple.kuhn.state.KuhnOutcome;
import ao.simple.kuhn.state.KuhnState;
import ao.simple.kuhn.state.StateFlow;

/**
 * Date: 7/10/11
 * Time: 5:18 PM
 */
public class KuhnGameState
        implements GameState
{
    //------------------------------------------------------------------------
    public static GameState initialSate()
    {
        return new KuhnGameState(
                StateFlow.firstAction());
    }


    //------------------------------------------------------------------------
    private final StateFlow stateFlow;


    //------------------------------------------------------------------------
    private KuhnGameState(StateFlow state)
    {
        this.stateFlow = state;
    }


    //------------------------------------------------------------------------
    @Override
    public boolean nextToActIsFirstPlayer()
    {
        return stateFlow.firstIsNextToAct();
    }

    @Override
    public int actionCount()
    {
        if (isTerminal()) {
            return 0;
        }

        KuhnState state = stateFlow.state();
        return (state.advance( KuhnAction.PASS ) == null ? 0 : 1) +
               (state.advance( KuhnAction.BET  ) == null ? 0 : 1);
    }

    @Override
    public boolean isTerminal()
    {
        return stateFlow.outcome() != null;
    }

    @Override
    public int bucketRound()
    {
        return 0;
    }


    //------------------------------------------------------------------------
    @Override
    public GameState advance(int actionIndex)
    {
        KuhnAction action = KuhnAction.VALUES[ actionIndex ];
        return new KuhnGameState(
                stateFlow.advance( action ));
    }


    //------------------------------------------------------------------------
    @Override
    public double outcomeValue(
            int[]   firstPlayerJointBucketSequence,
            int[]   lastPlayerJointBucketSequence,
            boolean fromPointOfViewOfFirstPlayer)
    {
        if (stateFlow.outcome().isShowdown())
        {
            boolean firstPlayerWins =
                    firstPlayerJointBucketSequence[ 0 ] >
                            lastPlayerJointBucketSequence[ 0 ];

            double toWin = new Odds(
                    firstPlayerWins ? 1 : 0,
                    firstPlayerWins ? 0 : 1,
                    0).strengthVsRandom();

            // as opposed to KuhnOutcome.DOUBLE_SHOWDOWN
            boolean isSingleShowdown =
                    (stateFlow.outcome()== KuhnOutcome.SHOWDOWN);

            return isSingleShowdown
                    ? 2.0 * (toWin - 0.5)
                    : 4.0 * (toWin - 0.5);
        }
        else
        {
            boolean playerOneWins =
                    (stateFlow.outcome() == KuhnOutcome.PLAYER_ONE_WINS);

            return playerOneWins == fromPointOfViewOfFirstPlayer
                   ? 1 : -1;
        }
    }
}
