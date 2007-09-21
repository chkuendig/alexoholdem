package ao.holdem.engine;

import ao.ai.simple.DuaneBot;
import ao.holdem.model.Player;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.HandHistoryDao;
import ao.persist.dao.PlayerHandleLookup;
import ao.state.StateManager;
import com.google.inject.Inject;

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
    @Inject PlayerHandleLookup players;
    @Inject HandHistoryDao     hands;


    //--------------------------------------------------------------------
    public void realDealerTest()
    {
        List<PlayerHandle> playerHandles =
                new ArrayList<PlayerHandle>();
        playerHandles.add( players.lookup("real.A") );
        playerHandles.add( players.lookup("real.B") );
        playerHandles.add( players.lookup("real.C") );
        playerHandles.add( players.lookup("real.D") );

        Map<PlayerHandle, Player> brains =
                new HashMap<PlayerHandle, Player>();
        brains.put(playerHandles.get(0), new DuaneBot());
        brains.put(playerHandles.get(1), new DuaneBot());
        brains.put(playerHandles.get(2), new DuaneBot());
        brains.put(playerHandles.get(3), new DuaneBot());

        for (int i = 0; i < 100; i++)
        {
            StateManager start  = new StateManager(playerHandles);
            Dealer dealer = new Dealer(start, brains);

            System.out.println(i);
            StateManager run  = dealer.playOutHand();
            HandHistory  hist = run.toHistory();
//            hands.store(hist);
        }
    }
}
