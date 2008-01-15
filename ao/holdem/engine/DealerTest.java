package ao.holdem.engine;

import ao.ai.monte_carlo.SimBot;
import ao.ai.simple.DuaneBot;
import ao.holdem.model.Money;
import ao.holdem.model.Player;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.HandHistoryDao;
import ao.persist.dao.PlayerHandleLookup;
import ao.state.StateManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.*;

/**
 *
 */
public class DealerTest
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleLookup  players;
    @Inject HandHistoryDao      hands;
    @Inject Provider<SimBot>    smarties;


    //--------------------------------------------------------------------
    public void realDealerTest()
    {
//        Rand.nextBoolean();
//        Rand.nextDouble();
//        Rand.nextBoolean();

        List<PlayerHandle> playerHandles =
                new ArrayList<PlayerHandle>();
        Map<PlayerHandle, Player> brains =
                new HashMap<PlayerHandle, Player>();
        for (Map.Entry<String, Player> e :
                new LinkedHashMap<String, Player>(){{
//                    put("real.G", new DuaneBot());
//                    put("real.F", new DuaneBot());
//                    put("real.E", new DuaneBot());
//                    put("real.D", new AlwaysRaiseBot());
                    put("real.C", new DuaneBot());
                    put("real.B", new DuaneBot());
                    put("real.A", smarties.get());
//                    put("real.A", new AlwaysRaiseBot());
//                    put("real.B", new DuaneBot());
//                    put("real.C", new AlwaysRaiseBot());
//                    put("real.D", new MathBot());
//                    put("real.E", new AlwaysRaiseBot());
//                    put("real.F", new DuaneBot());
//                    put("real.G", smarties.get());
                }}.entrySet())
        {
            PlayerHandle playerHandle = players.lookup(e.getKey());
            playerHandles.add( playerHandle );
            brains.put( playerHandle, e.getValue() );
        }

        Map<PlayerHandle, Money> cumDeltas =
                new HashMap<PlayerHandle, Money>();
        for (int i = 0; i < 10000; i++)
        {
            StateManager start  = new StateManager(playerHandles, true);
            Dealer       dealer = new Dealer(start, brains);

            //System.out.println(i);
            StateManager run  = dealer.playOutHand();
            HandHistory  hist = run.toHistory();
            dealer.publishHistory( hist );
//            hands.store(hist);

            Map<PlayerHandle, Money> deltas = hist.getDeltas();
            if (cumDeltas.isEmpty())
            {
                cumDeltas.putAll( deltas );
            }
            else
            {
                for (Map.Entry<PlayerHandle, Money> delta
                        : deltas.entrySet())
                {
                    cumDeltas.put(delta.getKey(),
                                  cumDeltas.get(delta.getKey()).plus(
                                          delta.getValue()));
                }
            }
            //System.out.println(hist.getDeltas());
            //System.out.println(hist.summary());
            System.out.println(cumDeltas);

            playerHandles.add( playerHandles.remove(0) );
        }

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for (Map.Entry<PlayerHandle, Money> delta
                : cumDeltas.entrySet())
        {
            System.out.println(delta.getKey() + "\t" +
                               brains.get(delta.getKey()) + "\t" +
                               delta.getValue());
        }
    }
}
