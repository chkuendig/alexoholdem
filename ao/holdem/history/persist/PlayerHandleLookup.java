package ao.holdem.history.persist;

import ao.holdem.history.PlayerHandle;
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
        String fullName = domain + "." + name;

        PlayerHandle player = playerAccess.find(fullName);
        if (player == null)
        {
            player = new PlayerHandle(fullName);
            session.get().saveOrUpdate( player );
        }

        return player;
    }

    public PlayerHandle lookup(String name)
    {
        return lookup("local", name);
    }


    //--------------------------------------------------------------------
//    public static PlayerHandle lookup(
//            String domain, String name)
//    {
//        Session session =
//                HibernateUtil.getSessionFactory()
//                        .getCurrentSession();
//        session.beginTransaction();
//
//        String fullName = domain + "." + name;
//        List matches = session.createQuery(
//                "from PlayerHandle where name = ?")
//                .setString(0, fullName)
//                .list();
//
//        PlayerHandle player;
//        if (matches.isEmpty())
//        {
//            player = new PlayerHandle(fullName);
//            session.save( player );
//        }
//        else
//        {
//            player = (PlayerHandle) matches.get(0);
//        }
//        session.getTransaction().commit();
//        return player;
//    }
//    public static PlayerHandle lookup(String fromName)
//    {
//        return lookup("local", fromName);
//    }
}
