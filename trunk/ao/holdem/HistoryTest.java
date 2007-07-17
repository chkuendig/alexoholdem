package ao.holdem;

import ao.holdem.def.bot.history.HistoryBot;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import ao.holdem.history.persist.HibernateUtil;
import ao.holdem.history.persist.PlayerHandleLookup;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class HistoryTest
{
    //--------------------------------------------------------------------
    public void historyTest()
    {
        final Session session =
                HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();


        Map<PlayerHandle, HistoryBot> bots =
                new HashMap<PlayerHandle, HistoryBot>(){{
//                    PlayerHandle a = new PlayerHandle("a");
//                    PlayerHandle b = new PlayerHandle("b");

//                    session.save( a );
//                    session.save( b );

                    put(PlayerHandleLookup.lookup("a"), new DummyBot());
                    put(PlayerHandleLookup.lookup("b"), new DummyBot());
                }};

        HandHistory hand = new HandHistory(bots.keySet());
        Event       e    = null;
        Snapshot    s    = hand.snapshot(e);
        do
        {
            boolean startOfRound = true;
            while (startOfRound || !s.isRoundDone())
            {
                startOfRound = false;
                
                PlayerHandle p = s.nextToAct();
                HistoryBot   b = bots.get( p );

                TakenAction act = null;
                Action a = b.act(hand, s);
                switch (a)
                {
                    case CHECK_OR_CALL:
                        act = (s.canCheck())
                                ? TakenAction.CHECK
                                : TakenAction.CALL;
                        break;

                    case CHECK_OR_FOLD:
                        act = (s.canCheck())
                                ? TakenAction.CHECK
                                : TakenAction.FOLD;
                        break;

                    case RAISE_OR_CALL:
                        act = (s.canRaise())
                                ? TakenAction.RAISE
                                : TakenAction.CALL;
                        break;
                }
                e = new Event(p, s.round(), act);
//                session.save( e );
                
                hand.addEvent( e );
                s = hand.snapshot( e );
            }
        }
        while (! s.isGameOver());

        hand.commitHandToPlayers();
        session.save( hand );

        session.getTransaction().commit();
        HibernateUtil.getSessionFactory().close();
    }


    //--------------------------------------------------------------------
    private static class DummyBot implements HistoryBot
    {
        public Action act(HandHistory hand, Snapshot env)
        {
            return Action.CHECK_OR_CALL;
        }
    }


    //--------------------------------------------------------------------
    public static void main(String args[])
    {
        new HistoryTest().historyTest();
    }
}
