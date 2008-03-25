package ao.holdem.engine.dealer;

import ao.ai.simple.AlwaysRaiseBot;
import ao.ai.simple.DuaneBot;
import ao.ai.simple.MathBot;
import ao.holdem.engine.Player;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.card.chance.DeckCards;
import ao.holdem.model.replay.StackedReplay;

import java.util.*;

/**
 *
 */
public class DealerTest
{
    //--------------------------------------------------------------------
//    @Inject Provider<UctBot>    smarties;


    //--------------------------------------------------------------------
    public void realDealerTest()
    {
//        Rand.nextBoolean();
//        Rand.nextDouble();
//        Rand.nextBoolean();

        List<Avatar> players =
                new ArrayList<Avatar>();
        Map<Avatar, Player> brains =
                new HashMap<Avatar, Player>();
        for (Map.Entry<String, Player> e :
                new LinkedHashMap<String, Player>(){{
//                    put("real.G", new DuaneBot());
//                    put("real.F", new DuaneBot());
//                    put("real.E", new DuaneBot());
                    put("math", new MathBot());
//                    put("raise", new AlwaysRaiseBot());
                    put("duane", new DuaneBot());
//                    put("uct", smarties.get());
                    put("raise", new AlwaysRaiseBot());
//                    put("real.B", new DuaneBot());
//                    put("real.C", new AlwaysRaiseBot());
//                    put("real.D", new MathBot());
//                    put("real.E", new AlwaysRaiseBot());
//                    put("real.F", new DuaneBot());
//                    put("real.G", smarties.get());
                }}.entrySet())
        {
            Avatar playerHandle = Avatar.local(e.getKey());
            players.add( playerHandle );
            brains.put( playerHandle, e.getValue() );
        }

        Map<Avatar, Chips> cumDeltas =
                new HashMap<Avatar, Chips>();

        Dealer dealer = new Dealer(true, brains);

        for (int i = 0; i < 1000; i++)
        {
//            System.out.println(i);
            StackedReplay replay =
                    dealer.play(players, new DeckCards());
//            hands.store(hist);

            Map<Avatar, Chips> deltas = replay.deltas();
            if (cumDeltas.isEmpty())
            {
                cumDeltas.putAll( deltas );
            }
            else
            {
                for (Map.Entry<Avatar, Chips> delta
                        : deltas.entrySet())
                {
                    cumDeltas.put(delta.getKey(),
                                  cumDeltas.get(delta.getKey()).plus(
                                          delta.getValue()));
                }
            }
            System.out.println(replay.deltas());
            //System.out.println(hist.summary());
//            System.out.println(
//                    formatCumulativeDeltas(i, cumDeltas));

            players.add( players.remove(0) );
        }

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for (Map.Entry<Avatar, Chips> delta
                : cumDeltas.entrySet())
        {
            System.out.println(delta.getKey() + "\t" +
                               brains.get(delta.getKey()) + "\t" +
                               delta.getValue());
        }
    }

    private String formatCumulativeDeltas(
            int                dataIndex,
            Map<Avatar, Chips> cumDeltas)
    {
        StringBuilder str = new StringBuilder();

        for (Map.Entry<Avatar, Chips> delta :
                cumDeltas.entrySet())
        {
            str.append(dataIndex)
               .append("\t")
               .append(delta.getKey().name())
               .append("\t")
               .append(delta.getValue())
               .append("\n");
        }

        str.deleteCharAt( str.length() - 1 );
        return str.toString();
    }

}
