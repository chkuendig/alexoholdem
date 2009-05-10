package ao.ai.equilibrium.limit_cfr;

import ao.ai.AbstractPlayer;
import ao.bucket.abstraction.BucketizerTest;
import ao.bucket.abstraction.HoldemAbstraction;
import ao.bucket.index.canon.flop.Flop;
import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.river.River;
import ao.bucket.index.canon.turn.Turn;
import ao.holdem.engine.analysis.Analysis;
import ao.holdem.engine.state.State;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;
import ao.odds.eval.HandRank;
import ao.odds.eval.eval_567.EvalSlow;
import ao.regret.holdem.InfoMatrix;
import org.apache.log4j.Logger;

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
    private static final Logger LOG =
            Logger.getLogger(BucketizerTest.class);


    //--------------------------------------------------------------------
    private final HoldemAbstraction ABS;
    private final boolean           DISPLAY;
    private final boolean           DETAILED;
    private final String            NAME;
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
        this(null, precomputedAbstraction, false, false);
    }
    public CfrBot2(String            equalibriumName,
                   HoldemAbstraction precomputedAbstraction,
                   boolean           display,
                   boolean           detailed)
    {
        ABS      = precomputedAbstraction;
        DISPLAY  = display;
        DETAILED = detailed;
        NAME     = equalibriumName;
    }


    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, Chips> deltas)
    {
        prevNode = StateTree.headsUpRoot();
        resetRoundCanons();

        if (! DISPLAY) return;
//        if (! mucked) {
            System.out.println(
                    "bot shows cards: " + prevCards +
                    "   " + handType(prevCards));
//        }
    }

    public static String handType(CardSequence cards) {
        if (! cards.community().hasFlop()) return "";

        short value = EvalSlow.valueOf(
                CardSequence.Util.knowCards(cards));
        return "(" + HandRank.fromValue( value ) +")";
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
        assert state.seats().length == 2
                : "Only works in heads-up mode";

        StateTree.Node gamePath = StateTree.fromState(prevNode, state, 4);
        if (gamePath == null) {
            gamePath = StateTree.fromState(state);
            if (gamePath == null) {
                LOG.debug("out of game path");
                return state.reify(FallbackAction.CHECK_OR_CALL);
            }

            resetRoundCanons();
        }
        prevNode = gamePath;

        findBucket(cards, state.round());
        InfoMatrix.InfoSet infoSet =
                gamePath.infoSet(roundBucket, ABS.infoPart(NAME, true));

        AbstractAction act = infoSet.nextProbableAction();
        Action realAction = state.reify( act.toFallbackAction() );

        if (DISPLAY) {
            if (DETAILED) {
                System.out.println(
                        Arrays.asList(
                            (int)(infoSet.averageStrategy()[0] * 100),
                            (int)(infoSet.averageStrategy()[1] * 100),
                            (int)(infoSet.averageStrategy()[2] * 100)
                        ));
//                System.out.println(
//                        Arrays.toString( infoSet.averageStrategy() ) +
//                        " from " + Arrays.toString(
//                                        infoSet.regret()) +
//                        " on "   + relBucket +
//                                " (" + (int) roundBucket + ")");
            }

            System.out.println("bot acts: " + realAction);
        }

        prevRound = state.round();
        prevCards = cards;
        mucked    = realAction.is(AbstractAction.QUIT_FOLD);
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
