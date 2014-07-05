package ao.holdem.bot.main;

import ao.Infrastructure;
import ao.holdem.bot.limit_cfr.CfrBot2;
import ao.holdem.bot.simple.RandomBot;
import ao.holdem.bot.regret.HoldemAbstraction;
import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer;
import ao.holdem.engine.Player;
import ao.holdem.engine.state.ActionStateFlow;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.*;
import ao.holdem.model.card.sequence.CardSequence;
import ca.ualberta.cs.poker.free.client.ClientPokerDynamics;
import ca.ualberta.cs.poker.free.client.PokerClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * User: alex
 * Date: 9-Jun-2009
 * Time: 8:44:12 PM
 */
public class AaaiAdapter extends PokerClient
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(AaaiAdapter.class);


    //--------------------------------------------------------------------
    private static final Avatar AWAY = new Avatar("aaai", "enemy");
    private        final Avatar HOME;


    //--------------------------------------------------------------------
    /**
     * @param args the command line parameters (IP and port)
     */
    public static void main(String[] args)
    {
        Infrastructure.setWorkingDirectory(args[2]);
        System.out.println(
                "Starting in " + Infrastructure.workingDirectory());

        byte nHoleBuckets  =    16;
        char nFlopBuckets  =   640;
        char nTurnBuckets  =  4480;
        char nRiverBuckets = 31360;

        System.out.println("Using [" +
                (int) nHoleBuckets + ", " + (int) nFlopBuckets + ", " +
                (int) nTurnBuckets + ", " + (int) nRiverBuckets +
                "] smart bucket CFR for AAAI 2009");

        HoldemAbstraction abs =
                new HoldemAbstraction(
                        new KMeansBucketizer(),
                        nHoleBuckets,
                        nFlopBuckets,
                        nTurnBuckets,
                        nRiverBuckets);

//        CfrBot2 bot = new CfrBot2("serial", abs, true, false, false);
        CfrBot2 bot = new CfrBot2("serial", abs, true, false, false);
        BucketizerTest.precompute(bot, false);

        AaaiAdapter rpc = new AaaiAdapter(bot);
        LOG.info("Attempting to connect to " + args[0] +
                        " on port " + args[1] + "...");

        try {
            rpc.connect(
                InetAddress.getByName(args[0]),
                Integer.parseInt(args[1]));
        } catch (IOException e) {
            LOG.error("connection failed", e);
            return;
        }

        LOG.info("Successful connection!");
        rpc.run();
    }

//    private static void precompute(final CfrBot2 bot)
//    {
//        long before = System.currentTimeMillis();
//
//        StateFlow sf = new StateFlow(Arrays.asList(
//                Avatar.local("a"), Avatar.local("a")), true);
//        bot.act(sf.head(),
//                new LiteralCardSequence(
//                        Hole.valueOf(Card.ACE_OF_CLUBS,
//                                     Card.FIVE_OF_SPADES)),
//                sf.analysis());
//
//        Hole hole = Hole.valueOf(Card.ACE_OF_CLUBS, Card.FIVE_OF_SPADES);
//        Community flop = new Community(Card.ACE_OF_HEARTS,
//                Card.FIVE_OF_HEARTS, Card.THREE_OF_DIAMONDS);
//        hole.asCanon().addFlop(flop)
//                .addTurn(Card.NINE_OF_CLUBS)
//                .addRiver(Card.TEN_OF_DIAMONDS).canonIndex();
//
//        System.out.println("Done Loading!  Took " +
//                (System.currentTimeMillis() - before) / 1000);
//    }

    //--------------------------------------------------------------------
    /**
     * A reproduction of what is happening on the server side.
     */
    private final ClientPokerDynamics state;
    private final Player              deleget;

    private       double              prevBankroll = 0;


    //--------------------------------------------------------------------
    /**
     * Creates a new instance of AdvancedPokerClient.
     *   Must call connect(), then run() to start process
     * @param delegateTo ao player
     */
    public AaaiAdapter(Player delegateTo)
    {
        state   = new ClientPokerDynamics();
        deleget = delegateTo;

        HOME    = Avatar.local(deleget.toString());
    }

    public AaaiAdapter()
    {
        this( new RandomBot() );
    }


    //--------------------------------------------------------------------
    /**
     * Handles the state change.
     * Updates state and calls takeAction()
     */
    public void handleStateChange() {
        LOG.debug("state: " + currentGameStateString + "\t" + state.bankroll);

        state.setFromMatchStateMessage(currentGameStateString);
        if (state.handOver) {
            final double bankrollDelta =
                    state.bankroll - prevBankroll;
            prevBankroll = state.bankroll;

            LOG.debug("hand ended: " + bankrollDelta);
            int dealeeDelta = (int) ((state.winnerIndex == 0 ? 1 : -1) * bankrollDelta);
            deleget.handEnded(Arrays.asList(
                    ChipStack.newInstance(dealeeDelta),
                    ChipStack.newInstance(-dealeeDelta)));

             // is the above equivalent to this?
//            deleget.handEnded(new HashMap<Avatar, ChipStack>(){{
//                int sbDelta = (int) bankrollDelta;
//                put(HOME, ChipStack.newInstance(sbDelta));
//                put(AWAY, ChipStack.newInstance(-sbDelta));
//            }});
        }
        else if (state.isOurTurn())
        {
//            LOG.debug("acting");
            takeAction();
        }
    }


    //--------------------------------------------------------------------
    /**
     * Overload to take actions.
     */
    public void takeAction() {
        try{
            doTakeAction();
        } catch (Exception e) {
            LOG.error("take action failed", e);
        }
    }


    //--------------------------------------------------------------------
    private void doTakeAction() throws IOException
    {
        ActionStateFlow stateFlow = new ActionStateFlow(2,true);

        for (int i = 0; i < state.bettingSequence.length(); i++)
        {
            switch (state.bettingSequence.charAt(i))
            {
                case 'c':
                    stateFlow.advance(
                            stateFlow.head().reify(
                                    FallbackAction.CHECK_OR_CALL));
                    break;

                case 'r':
                    stateFlow.advance(
                            stateFlow.head().reify(
                                    FallbackAction.RAISE_OR_CALL));
                    break;

                case 'f':
                    stateFlow.advance(
                            stateFlow.head().reify(
                                    FallbackAction.CHECK_OR_FOLD));
                    break;
            }
        }

        int    seat = state.seatTaken;
        Action act  = deleget.act(
                stateFlow.head(),
                new CardSequence(
                        Hole.valueOf(
                                toAoCard(state.hole[seat][0]),
                                toAoCard(state.hole[seat][1])),
                        new Community(
                                toAoCard(state.board[0]),
                                toAoCard(state.board[1]),
                                toAoCard(state.board[2]),
                                toAoCard(state.board[3]),
                                toAoCard(state.board[4])
                        ))/*,
                stateFlow.analysis()*/);

        switch (act.abstraction())
        {
            case QUIT_FOLD:  sendFold();  break;
            case CHECK_CALL: sendCall();  break;
            case BET_RAISE:  sendRaise(); break;
        }
    }

    private static Card toAoCard(
            ca.ualberta.cs.poker.free.dynamics.Card aaaiCard)
    {
        if (aaaiCard == null) return null;

        Suit aoSuit = null;
        switch (aaaiCard.suit)
        {
            case CLUBS:    aoSuit = Suit.CLUBS;    break;
            case DIAMONDS: aoSuit = Suit.DIAMONDS; break;
            case HEARTS:   aoSuit = Suit.HEARTS;   break;
            case SPADES:   aoSuit = Suit.SPADES;   break;
        }

        Rank aoRank = null;
        switch (aaaiCard.rank)
        {
            case TWO:   aoRank = Rank.TWO;   break;
            case THREE: aoRank = Rank.THREE; break;
            case FOUR:  aoRank = Rank.FOUR;  break;
            case FIVE:  aoRank = Rank.FIVE;  break;
            case SIX:   aoRank = Rank.SIX;   break;
            case SEVEN: aoRank = Rank.SEVEN; break;
            case EIGHT: aoRank = Rank.EIGHT; break;
            case NINE:  aoRank = Rank.NINE;  break;
            case TEN:   aoRank = Rank.TEN;   break;
            case JACK:  aoRank = Rank.JACK;  break;
            case QUEEN: aoRank = Rank.QUEEN; break;
            case KING:  aoRank = Rank.KING;  break;
            case ACE:   aoRank = Rank.ACE;   break;
        }

        return Card.valueOf(aoRank, aoSuit);
    }
}
