package ao.holdem.persist;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.*;

import java.io.File;

/**
 * BerkeleyDB store.
 */
public class HoldemDb
{
    //--------------------------------------------------------------------
    private static final String CLASS_CATALOG = "java_class_catalog";
    private static final String HAND_STORE    = "hand_store";
    private static final String AVATAR_STORE  = "avatar_store";


    //--------------------------------------------------------------------
    private Environment        env;
    private StoredClassCatalog catalog;
    private Database           handDb;
    private Database           avatarDb;
    private TransactionRunner  txn;


    //--------------------------------------------------------------------
    public HoldemDb(String homeDirectory)
    {
        try
        {
            init(homeDirectory);
        }
        catch (DatabaseException e)
        {
            throw new Error( e );
        }
    }

    private void init(String homeDirectory)
            throws DatabaseException
    {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        envConfig.setCacheSize(1024 * 1024 * 20);

        env = new Environment(new File(homeDirectory), envConfig);

        Database catalogDb =
                    env.openDatabase(null, CLASS_CATALOG,
                                     dbConfig(false));

        catalog  = new StoredClassCatalog(catalogDb);
        handDb   = env.openDatabase(null, HAND_STORE,
                                    dbConfig(false));
        avatarDb = env.openDatabase(null, AVATAR_STORE,
                                    dbConfig(true));

        TransactionConfig txnConf = new TransactionConfig();
//        txnConf.setNoSync(true);
        txnConf.setDurability(Durability.COMMIT_NO_SYNC);
//        txnConf.setReadUncommitted(true);
        txn = new TransactionRunner(env, 32, txnConf);
    }

    private DatabaseConfig dbConfig(boolean allowDuplicates)
    {
        DatabaseConfig dbConfig = new DatabaseConfig();

        //dbConfig.setDeferredWrite(true);
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(allowDuplicates);

        return dbConfig;
    }


    //--------------------------------------------------------------------
    public void atomic(TransactionWorker transaction)
    {
        try
        {
            txn.run( transaction );
        }
        catch (Exception e)
        {
            throw new Error( e );
        }
    }


    //--------------------------------------------------------------------
    public Environment environment()
    {
        return env;
    }

    public StoredClassCatalog classCatalog()
    {
        return catalog;
    }

    public Database handDb()
    {
        return handDb;
    }
    public Database avatarDb()
    {
        return avatarDb;
    }

    public Cursor openAvatarCursor()
    {
//        CursorConfig config = new CursorConfig();
//        config.setReadUncommitted( true );
        try
        {
            return avatarDb.openCursor(null, null);
        }
        catch (DatabaseException e)
        {
            throw new Error( e );
        }
    }

    
    //--------------------------------------------------------------------
    public void close()
    {
        try
        {
            doClose();
        }
        catch (DatabaseException e)
        {
            throw new Error( e );
        }
    }
    private void doClose()
            throws DatabaseException
    {
        avatarDb.close();
        handDb.close();
        catalog.close();        
        env.close();
    }
}
