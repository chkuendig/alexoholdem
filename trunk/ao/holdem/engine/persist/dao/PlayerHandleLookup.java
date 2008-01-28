package ao.holdem.engine.persist.dao;

import ao.holdem.engine.persist.PlayerHandle;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.Transactional;
import org.hibernate.Session;

/**
 *
 */
public class PlayerHandleLookup
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleAccess playerAccess;
    @Inject Provider<Session>  session;


    //--------------------------------------------------------------------
    @Transactional
    public PlayerHandle lookup(
            String domain, String name)
    {
        PlayerHandle player = playerAccess.find(domain, name);
        if (player == null)
        {
            player = new PlayerHandle(domain, name);
            session.get().saveOrUpdate( player );
        }
        return player;
    }

    public PlayerHandle lookup(String name)
    {
        return lookup("local", name);
    }
}
