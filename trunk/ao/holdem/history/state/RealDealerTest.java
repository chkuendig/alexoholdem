package ao.holdem.history.state;

import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.HandHistoryDao;
import ao.persist.dao.PlayerHandleLookup;
import ao.holdem.history_game.RealDealer;
import ao.holdem.model.Player;
import ao.state.StatePlayerImpl;
import ao.state.Context;
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
    @Inject
    PlayerHandleLookup players;
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
        brains.put(playerHandles.get(0), new StatePlayerImpl());
        brains.put(playerHandles.get(1), new StatePlayerImpl());
        brains.put(playerHandles.get(2), new StatePlayerImpl());
        brains.put(playerHandles.get(3), new StatePlayerImpl());

        for (int i = 0; i < 100; i++)
        {
            Context start  = new Context(playerHandles);
            RealDealer   dealer = new RealDealer(start, brains);

            System.out.println(i);
            Context run  = dealer.playOutHand();
            HandHistory  hist = run.toHistory();
//            hands.store(hist);
        }
    }
}
