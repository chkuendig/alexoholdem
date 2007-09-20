package ao.persist.dao;

import ao.persist.Event;
import ao.persist.HandHistory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.Transactional;
import org.hibernate.Session;

/**
 *
 */
public class HandHistoryDao
{
    //--------------------------------------------------------------------
    @Inject Provider<Session> session;


    //--------------------------------------------------------------------
    public HandHistoryDao() {}


    //--------------------------------------------------------------------
    @Transactional
    public void store(HandHistory history)
    {
        history.commitHandToPlayers();
        for (Event event : history.getEvents())
        {
            session.get().saveOrUpdate( event );
        }
        session.get().saveOrUpdate( history );
    }
}
