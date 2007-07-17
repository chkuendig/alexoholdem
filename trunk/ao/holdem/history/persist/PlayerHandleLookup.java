package ao.holdem.history.persist;

import ao.holdem.history.PlayerHandle;
import org.hibernate.Session;

/**
 *
 */
public class PlayerHandleLookup
{
    //--------------------------------------------------------------------
//    private static final Map<String, PlayerHandle> HANDLES =
//            new HashMap<String, PlayerHandle>();


    //--------------------------------------------------------------------
    private PlayerHandleLookup() {}


    //--------------------------------------------------------------------
    public static PlayerHandle lookup(String fromName)
    {
        Session session =
                HibernateUtil.getSessionFactory().getCurrentSession();

        return (PlayerHandle) session.createQuery(
                "from PlayerHandle where name = ?")
                .setString(0, fromName)
                .list().get(0);

//        PlayerHandle existing = HANDLES.get(fromName);
//        if (existing == null)
//        {
//            PlayerHandle player = new PlayerHandle();
//            HANDLES.put(fromName, player);
//            return player;
//        }
//        return existing;
    }
}
