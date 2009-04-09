package ao.holdem.engine.dealer;

import ao.ai.human.ConsoleBot;
import ao.ai.simple.AlwaysRaiseBot;
import ao.ai.simple.DuaneBot;
import ao.holdem.engine.Player;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.card.chance.DeckCards;
import ao.holdem.model.replay.StackedReplay;
import ao.util.math.rand.Rand;
import ao.util.math.stats.Combo;
import ao.util.math.stats.Permuter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DealerTest
{
    //--------------------------------------------------------------------
    private final long TARGET_ROUNDS;


    //--------------------------------------------------------------------
    public DealerTest()
    {
        this(100 * 1000);
    }
    public DealerTest(long rounds)
    {
        TARGET_ROUNDS = rounds;
    }


    //--------------------------------------------------------------------
    public void vsHuman(final Player brain)
    {
        final Avatar bot = Avatar.local("bot");
        final Avatar you = Avatar.local("you");
        Dealer d = new Dealer(true, new HashMap<Avatar, Player>(){{
            put(bot, brain);
            put(you, new ConsoleBot());
        }});

        boolean humanDealer = Rand.nextBoolean();
        for (long i = 0; i < TARGET_ROUNDS; i++) {
            d.play( humanDealer
                    ? Arrays.asList(bot, you)
                    : Arrays.asList(you, bot),

                    new DeckCards());

            humanDealer = !humanDealer;
        }
    }


    //--------------------------------------------------------------------
    public void roundRobin()
    {
        Map<Avatar, Player> brains =
                new HashMap<Avatar, Player>() {{
                    put(Avatar.local("duane0"), new DuaneBot());
                    put(Avatar.local("duane1"), new DuaneBot());
                    put(Avatar.local("raise0"), new AlwaysRaiseBot());
                    put(Avatar.local("raise1"), new AlwaysRaiseBot());
                    put(Avatar.local("raise2"), new AlwaysRaiseBot());
//                    put(Avatar.local("math0"),  new MathBot());
                }};
        roundRobin(brains);
    }


    //--------------------------------------------------------------------
    public void roundRobin(Map<Avatar, Player> brains)
    {

        long roundRobins = Combo.factorial( brains.size() );
        int  decksTrials = (int) Math.ceil(Math.sqrt(
                            (double) TARGET_ROUNDS / roundRobins));

        DeckCards decks[] = new DeckCards[decksTrials];
        for (int deck = 0; deck < decks.length; deck++)
        {
            decks[deck] = new DeckCards();
        }

        Dealer             dealer    = new Dealer(true, brains);
        Map<Avatar, Chips> cumDeltas = new HashMap<Avatar, Chips>();
        for (int trialSet = 0; trialSet < decksTrials; trialSet++)
        {
            roundrobinTournament(
                    dealer, brains.keySet(), decks, cumDeltas);
        }

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for (Map.Entry<Avatar, Chips> delta
                : cumDeltas.entrySet())
        {
            System.out.println(
                    delta.getKey() + "\t" +
                    brains.get(delta.getKey()) + "\t" +
                    delta.getValue() + "\t" +
                    (((double) delta.getValue().smallBets())
                            / TARGET_ROUNDS));
        }
    }

    private void roundrobinTournament(
            Dealer             dealer,
            Collection<Avatar> players,
            DeckCards          decks[],
            Map<Avatar, Chips> cumDeltas)
    {
        for (Avatar permutation[] :
                new Permuter<Avatar>(
                        players.toArray(new Avatar[players.size()])))
        {
            for (DeckCards deck : decks)
            {
                StackedReplay replay =
                        dealer.play(Arrays.asList(permutation),
                                    deck);

                Map<Avatar, Chips> deltas = replay.deltas();
                if (cumDeltas.isEmpty())
                {
                    cumDeltas.putAll(deltas);
                }
                else
                {
                    for (Map.Entry<Avatar, Chips> delta :
                            deltas.entrySet())
                    {
                        cumDeltas.put(
                                delta.getKey(),
                                cumDeltas.get(delta.getKey())
                                        .plus(delta.getValue()));
                    }
                }
//                System.out.println(replay.deltas());

                deck.reset();
            }
        }
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
