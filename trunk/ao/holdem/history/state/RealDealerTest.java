package ao.holdem.history.state;

import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.persist.HandHistoryDao;
import ao.holdem.history.persist.PlayerHandleLookup;
import ao.holdem.history_game.RealDealer;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RealDealerTest
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

        Map<PlayerHandle, StatePlayer> brains =
                new HashMap<PlayerHandle, StatePlayer>();
        brains.put(playerHandles.get(0), new StatePlayerImpl());
        brains.put(playerHandles.get(1), new StatePlayerImpl());
        brains.put(playerHandles.get(2), new StatePlayerImpl());
        brains.put(playerHandles.get(3), new StatePlayerImpl());

        for (int i = 0; i < 100; i++)
        {
            RunningState start  = new RunningState(playerHandles);
            RealDealer   dealer = new RealDealer(start, brains);

            System.out.println(i);
            RunningState run  = dealer.playOutHand();
            HandHistory  hist = run.toHistory();
//            hands.store(hist);
        }
    }
}
