package ao.ai.equilibrium.limit_cfr;

import ao.ai.AbstractPlayer;
import ao.bucket.abstraction.HoldemAbstraction;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.river.River;
import ao.bucket.index.turn.Turn;
import ao.holdem.engine.analysis.Analysis;
import ao.holdem.engine.state.State;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.sequence.CardSequence;
import ao.regret.holdem.v2.InfoMatrix;

import java.util.Arrays;
import java.util.Map;

/**
 * User: alex
 * Date: 21-Apr-2009
 * Time: 11:04:13 PM
 */
public class CfrBot2 extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private static final boolean DETAILED = false;

    private final HoldemAbstraction ABS;
    private final boolean           DISPLAY;
    private       CardSequence      prevCards;
    private       boolean           mucked;

    private StateTree.Node prevNode = StateTree.headsUpRoot();
    private CanonHole      canonHole;
    private Flop           canonFlop;
    private Turn           canonTurn;
    private Round          prevRound;

    private byte relBucket;
    private char roundBucket;

    private byte holeBucket;
    private byte flopBucket;
    private byte turnBucket;


    //--------------------------------------------------------------------
    public CfrBot2(HoldemAbstraction precomputedAbstraction)
    {
        this(precomputedAbstraction, false);
    }
    public CfrBot2(HoldemAbstraction precomputedAbstraction,
                   boolean           display)
    {
        ABS     = precomputedAbstraction;
        DISPLAY = display;
    }


    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, Chips> deltas)
    {
        prevNode = StateTree.headsUpRoot();
        resetRoundCanons();

        if (! DISPLAY) return;
//        if (! mucked) {
            System.out.println("bot shows cards: " + prevCards);
//        }
    }

    private void resetRoundCanons() {
        canonHole = null;
        canonFlop = null;
        canonTurn = null;
        prevRound = null;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings({"UnusedAssignment"})
    public Action act(State        state,
                      CardSequence cards,
                      Analysis     analysis)
    {
        // todo: remove this
//        if (state.round().ordinal() == Round.RIVER.ordinal() &&
//                state.remainingBetsInRound() <= 2) {
//            return state.reify(FallbackAction.CHECK_OR_CALL);
//        }

        assert state.seats().length == 2
                : "Only works in heads-up mode";

        StateTree.Node gamePath = StateTree.fromState(prevNode, state, 4);
        if (gamePath == null) {
            gamePath = StateTree.fromState(state);
            resetRoundCanons();
        }
        prevNode = gamePath;

        findBucket(cards, state.round());
        InfoMatrix.InfoSet infoSet =
                gamePath.infoSet(roundBucket, ABS.infoPart());

        AbstractAction act = infoSet.nextProbableAction();
        if (DISPLAY) {
            if (DETAILED) {
                System.out.println(
                        Arrays.toString( infoSet.averageStrategy() ) +
                        " from " + Arrays.toString(
                                        infoSet.regret()) +
                        " on "   + relBucket +
                                " (" + (int) roundBucket + ")");
            }

            System.out.println("bot acts: " + act);
        }

        prevRound = state.round();
        prevCards = cards;
        Action realAction = state.reify( act.toFallbackAction() );
        mucked = realAction.is(AbstractAction.QUIT_FOLD);
        return realAction;
    }


    //--------------------------------------------------------------------
    private void findBucket(
            CardSequence cards,
            Round        round)
    {
        if (prevRound == round) return;


        if (canonHole == null) {
            canonHole  = cards.hole().asCanon();
            holeBucket = ABS.tree().getHole(
                                canonHole.canonIndex());
        }
        if (round == Round.PREFLOP) {
            roundBucket = (char) holeBucket;
            relBucket   = holeBucket;
            return;
        }


        if (canonFlop == null) {
            canonFlop  = canonHole.addFlop(cards.community());
            flopBucket = ABS.tree().getFlop( canonFlop.canonIndex() );
        }
        if (round == Round.FLOP) {
            roundBucket = ABS.decoder().decode(holeBucket, flopBucket);
            relBucket   = flopBucket;
            return;
        }


        if (canonTurn == null) {
            canonTurn  = canonFlop.addTurn(cards.community().turn());
            turnBucket = ABS.tree().getTurn( canonTurn.canonIndex() );
        }
        if (round == Round.TURN) {
            roundBucket = ABS.decoder().decode(
                            holeBucket, flopBucket, turnBucket);
            relBucket   = turnBucket;
            return;
        }


        River canonRiver =
                canonTurn.addRiver( cards.community().river() );
        byte riverBucket = ABS.tree().getRiver(canonRiver.canonIndex());

        roundBucket = ABS.decoder().decode(
                        holeBucket, flopBucket, turnBucket, riverBucket);
        relBucket   = riverBucket;
    }
}
