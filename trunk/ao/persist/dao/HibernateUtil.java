package ao.persist.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * 
 */
public class HibernateUtil
{
    //--------------------------------------------------------------------
    private HibernateUtil() {}


    //--------------------------------------------------------------------
    private static final SessionFactory sessionFactory;
    static
    {
        try
        {
            sessionFactory =
                    new AnnotationConfiguration()
                            .configure()
                            .buildSessionFactory();
        }
        catch (Throwable ex)
        {
            // Log exception!
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public static Session getSession()
            throws HibernateException
    {
        return sessionFactory.openSession();
    }
}
