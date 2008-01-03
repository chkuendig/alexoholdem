package ao.holdem.engine;

import ao.ai.monte_carlo.SimBot;
import ao.ai.simple.AlwaysRaiseBot;
import ao.ai.simple.DuaneBot;
import ao.ai.simple.MathBot;
import ao.ai.simple.RandomBot;
import ao.holdem.model.Money;
import ao.holdem.model.Player;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.HandHistoryDao;
import ao.persist.dao.PlayerHandleLookup;
import ao.state.StateManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DealerTest
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleLookup     players;
    @Inject HandHistoryDao         hands;
    @Inject Provider<SimBot> smarties;


    //--------------------------------------------------------------------
    public void realDealerTest()
    {
        List<PlayerHandle> playerHandles =
                new ArrayList<PlayerHandle>();
        playerHandles.add( players.lookup("real.A") );
        playerHandles.add( players.lookup("real.B") );
        playerHandles.add( players.lookup("real.C") );
        playerHandles.add( players.lookup("real.D") );
        playerHandles.add( players.lookup("real.E") );
        playerHandles.add( players.lookup("real.F") );
        playerHandles.add( players.lookup("real.G") );

        Map<PlayerHandle, Player> brains =
                new HashMap<PlayerHandle, Player>();
        brains.put(playerHandles.get(0), new AlwaysRaiseBot());
        brains.put(playerHandles.get(1), new DuaneBot());
        brains.put(playerHandles.get(2), new RandomBot());
        brains.put(playerHandles.get(3), new MathBot());
        brains.put(playerHandles.get(4), new AlwaysRaiseBot());
        brains.put(playerHandles.get(5), new DuaneBot());
        brains.put(playerHandles.get(6), smarties.get());

        Map<PlayerHandle, Money> cumDeltas =
                new HashMap<PlayerHandle, Money>();
        for (int i = 0; i < 5000; i++)
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
            System.out.println(hist.getDeltas());

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
