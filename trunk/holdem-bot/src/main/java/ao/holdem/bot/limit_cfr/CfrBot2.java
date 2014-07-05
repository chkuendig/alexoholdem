package ao.holdem.bot.limit_cfr;

import ao.holdem.bot.AbstractPlayer;
import ao.holdem.bot.regret.HoldemAbstraction;
import ao.holdem.bot.regret.InfoMatrix;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.engine.eval.EvalBy5;
import ao.holdem.engine.eval.HandRank;
import ao.holdem.engine.state.ActionState;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.model.card.sequence.CardSequence;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 21-Apr-2009 11:04:13 PM
 */
public class CfrBot2 extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CfrBot2.class);


    //--------------------------------------------------------------------
    private final boolean           DOUBLE_PRECISION;
    private final HoldemAbstraction ABS;
    private final boolean           DISPLAY;
    private final boolean           DETAILED;
    private final String            NAME;
    private       CardSequence      prevCards;

    @SuppressWarnings({"FieldCanBeLocal"})
    private       boolean           mucked;

    private StateTree.Node prevNode = StateTree.headsUpRoot();
    private CanonHole      canonHole;
    private Flop           canonFlop;
    private Turn           canonTurn;
    private Round          prevRound;

    @SuppressWarnings({"FieldCanBeLocal"})
    private int  relBucket;
    private char roundBucket;

    private int  holeBucket;
    private int  flopBucket;
    private int  turnBucket;

    private final List<String> handProbabilities =
            new ArrayList<String>();


    //--------------------------------------------------------------------
    public CfrBot2(HoldemAbstraction precomputedAbstraction)
    {
        this(null, precomputedAbstraction, true, false, false);
    }
    public CfrBot2(String            equalibriumName,
                   HoldemAbstraction precomputedAbstraction,
                   boolean           doublePrecision,
                   boolean           display,
                   boolean           detailed)
    {
        ABS      = precomputedAbstraction;
        DISPLAY  = display;
        DETAILED = detailed;
        NAME     = equalibriumName;

        DOUBLE_PRECISION = doublePrecision;
    }


    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas)
    {
        prevNode = StateTree.headsUpRoot();
        resetRoundCanons();

        if (! DISPLAY) return;
//        if (! mucked) {
            System.out.println(
                    "bot shows cards: " + prevCards +
                    "   " + handType(prevCards));

            System.out.println(
                    "thinking: " + handProbabilities);
            handProbabilities.clear();

//        }
    }

    public static String handType(CardSequence cards) {
        if (! cards.community().hasFlop()) return "";

        short value = EvalBy5.valueOf(
                cards.knowCards());
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
    public Action act(ActionState state,
                      CardSequence cards/*,
                      Analysis     analysis*/)
    {
        assert state.seats().length == 2
                : "Only works in heads-up mode";

        if (prevRound != state.round())
        {
            if (state.round() == Round.PREFLOP)
            {
                handProbabilities.clear();
            }
            else
            {
                handProbabilities.add(
                        state.round().toString().substring(0, 1));
            }
        }

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
                ABS.infoPart(NAME, true, DOUBLE_PRECISION)
                    .infoSet(gamePath, roundBucket);

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
            handProbabilities.add( infoSet.toShortString() );
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
            try {
                canonHole  = CanonHole.create(cards.hole());
                holeBucket = ABS.tree(true).getHole(
                                    canonHole.canonIndex());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (round == Round.PREFLOP) {
            roundBucket = (char) holeBucket;
            relBucket   = holeBucket;
            return;
        }


        if (canonFlop == null) {
            canonFlop  = new Flop(canonHole, cards.community());
            flopBucket = ABS.tree(true).getFlop( canonFlop.canonIndex() );
        }
        if (round == Round.FLOP) {
            roundBucket = ABS.decoder().decode(holeBucket, flopBucket);
            relBucket   = flopBucket;
            return;
        }


        if (canonTurn == null) {
            canonTurn  = canonFlop.addTurn(cards.community().turn());
            turnBucket = ABS.tree(true).getTurn( canonTurn.canonIndex() );
        }
        if (round == Round.TURN) {
            roundBucket = ABS.decoder().decode(
                            holeBucket, flopBucket, turnBucket);
            relBucket   = turnBucket;
            return;
        }


        River canonRiver =
                canonTurn.addRiver( cards.community().river() );
        int  riverBucket = ABS.tree(true)
                .getRiver(canonRiver.canonIndex());

        roundBucket = ABS.decoder().decode(
                        holeBucket, flopBucket, turnBucket, riverBucket);
        relBucket   = riverBucket;
    }
}
