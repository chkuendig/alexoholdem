package ao.holdem.ai.ai.main;

import ao.holdem.ai.ai.human.ConsoleBot;
import ao.holdem.engine.Player;
import ao.holdem.engine.dealer.Dealer;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.card.chance.DeckCards;
import ao.holdem.model.card.chance.SwapCards;
import ao.util.math.rand.Rand;
import ao.util.time.Progress;

import java.util.*;

/**
 *
 */
public class DealerTest
{
    //--------------------------------------------------------------------
    private final long DUPLICATE_ROUNDS;


    //--------------------------------------------------------------------
    public DealerTest()
    {
        this(100 * 1000 * 1000);
    }
    public DealerTest(long rounds)
    {
        DUPLICATE_ROUNDS = Math.max(rounds + 10 - (rounds % 10), 10);
    }


    //--------------------------------------------------------------------
    public void vsHuman(final Player brain) {
        vsHuman(brain, Rand.nextBoolean(), true);
    }
    public void vsHuman(final Player  brain,
                              boolean humanDealerFirst,
                              boolean swap) {
        final Avatar bot = Avatar.local("bot");
        final Avatar you = Avatar.local("you");
        Dealer d = new Dealer(true, new HashMap<Avatar, Player>(){{
            put(bot, brain);
            put(you, new ConsoleBot());
        }});

        boolean humanDealer = humanDealerFirst;
        for (long i = 0; i < DUPLICATE_ROUNDS; i++) {
            System.out.println(
                    "\n-----------------------------" +
                            "  Hand " + (i + 1) + ", " +
                            (humanDealer
                             ? "you deal"
                             : "bot deals") +
                      "  -----------------------------");

            d.play( humanDealer
                    ? Arrays.asList(bot, you)
                    : Arrays.asList(you, bot),

                    new DeckCards());

            if (swap) {
                humanDealer = !humanDealer;
            }
        }
    }


    //--------------------------------------------------------------------
    public void headsUp(Map<Avatar, Player> brains)
    {
        headsUp(brains, true);
    }
    public void headsUp(Map<Avatar, Player> brains, boolean swap)
    {
        assert brains.size() == 2;
        assert DUPLICATE_ROUNDS >= 10;

        List<Avatar> orderA = new ArrayList<Avatar>(brains.keySet());
        List<Avatar> orderB = new ArrayList<Avatar>(orderA);
        Collections.swap(orderB, 0, 1);

        Random r = new Random(Rand.nextLong());

        List<Avatar>       order       = orderA;
        Progress           progress    = new Progress(DUPLICATE_ROUNDS);
        Dealer             dealer      = new Dealer(true, brains);
        Map<Avatar, ChipStack> cumDeltas   = new HashMap<Avatar, ChipStack>();
        ChipStack dealerDelta = ChipStack.ZERO;
        for (int trialSet = 0; trialSet < DUPLICATE_ROUNDS; trialSet++)
        {
            SwapCards cards = new SwapCards(r);
            Map<Avatar, ChipStack> deltas =
                    dealer.play(order, cards).deltas();
            for (Map.Entry<Avatar, ChipStack> delta : deltas.entrySet()) {
                cumDeltas.put(
                        delta.getKey(),
                        ChipStack.orZero(
                                cumDeltas.get(delta.getKey()))
                                .plus(delta.getValue()));
            }
            dealerDelta = dealerDelta.plus(deltas.get(order.get(1)));

            cards.swap();
            deltas = dealer.play(order, cards).deltas();
            for (Map.Entry<Avatar, ChipStack> delta : deltas.entrySet()) {
                cumDeltas.put(
                        delta.getKey(),
                        cumDeltas.get(delta.getKey())
                                .plus(delta.getValue()));
            }
            dealerDelta = dealerDelta.plus(deltas.get(order.get(1)));

            if (swap) {
                order = ((order == orderA)
                        ? orderB : orderA);

//                deltas = dealer.play(order, cards).deltas();
//                for (Map.Entry<Avatar, Chips> delta : deltas.entrySet()) {
//                    cumDeltas.put(
//                            delta.getKey(),
//                            Chips.orZero(
//                                cumDeltas.get(delta.getKey()))
//                                    .plus(delta.getValue()));
//                }
//                dealerDelta = dealerDelta.plus(deltas.get(order.get(1)));
//
//                cards.swap();
//                deltas = dealer.play(order, cards).deltas();
//                for (Map.Entry<Avatar, Chips> delta : deltas.entrySet()) {
//                    cumDeltas.put(
//                            delta.getKey(),
//                            cumDeltas.get(delta.getKey())
//                                    .plus(delta.getValue()));
//                }
//                dealerDelta = dealerDelta.plus(deltas.get(order.get(1)));
            }

            progress.checkpoint();
            if (((trialSet + 1) % (DUPLICATE_ROUNDS / 1000)) == 0) {
                displayDeltas(
                        cumDeltas, dealerDelta, brains, trialSet + 1);
            }
        }

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    private void displayDeltas(
            Map<Avatar, ChipStack>  cumDeltas,
            ChipStack dealerDelta,
            Map<Avatar, Player> brains,
            long                dupeRounds)
    {
        for (Map.Entry<Avatar, ChipStack> delta
                : cumDeltas.entrySet())
        {
            System.out.println(
                    delta.getKey() + "\t" +
                    brains.get(delta.getKey()) + "\t" +
                    delta.getValue() + "\t" +
                    (((double) delta.getValue().smallBets())
                            / (2 * dupeRounds)));
        }

        System.out.println("dealer delta: " +
                ((double) dealerDelta.smallBets())
                    / (2 * dupeRounds));
    }


//    private String formatCumulativeDeltas(
//            int                dataIndex,
//            Map<Avatar, Chips> cumDeltas)
//    {
//        StringBuilder str = new StringBuilder();
//
//        for (Map.Entry<Avatar, Chips> delta :
//                cumDeltas.entrySet())
//        {
//            str.append(dataIndex)
//               .append("\t")
//               .append(delta.getKey().name())
//               .append("\t")
//               .append(delta.getValue())
//               .append("\n");
//        }
//
//        str.deleteCharAt( str.length() - 1 );
//        return str.toString();
//    }
}
