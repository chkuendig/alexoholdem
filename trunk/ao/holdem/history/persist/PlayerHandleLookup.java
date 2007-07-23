package ao.holdem.history.persist;

import ao.holdem.history.PlayerHandle;
import org.hibernate.Session;

import java.util.List;

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
    public static PlayerHandle lookup(
            String domain, String name)
    {
        Session session =
                HibernateUtil.getSessionFactory()
                        .getCurrentSession();
        session.beginTransaction();

        String fullName = domain + "." + name;
        List matches = session.createQuery(
                "from PlayerHandle where name = ?")
                .setString(0, fullName)
                .list();

        if (matches.isEmpty())
        {
            PlayerHandle player = new PlayerHandle(fullName);
            session.save( player );
            session.getTransaction().commit();

            return player;
        }
        return (PlayerHandle) matches.get(0);
    }
    public static PlayerHandle lookup(String fromName)
    {
        return lookup("local", fromName);
    }
}
