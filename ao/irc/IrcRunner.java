package ao.irc;

import ao.persist.HandHistory;
import ao.persist.dao.HandHistoryDao;
import com.google.inject.Inject;

import java.io.File;

/**
 *
 */
public class IrcRunner
{
    //--------------------------------------------------------------------
    @Inject IrcHistorian   historian;
    @Inject HandHistoryDao hands;


    //--------------------------------------------------------------------
    public void run(String ircDir)
    {
        System.out.println("ircDir: " + ircDir);

        for (HandHistory hist :
                historian.fromSnapshot(ircDir))
        {
//            hands.store( hist );
//            System.out.println("hist = " + hist);
        }
    }

    public void runOnSubdirs(String ircDir)
    {
        File dir = new File(ircDir);
        assert dir.isDirectory();

        for (File subdir : dir.listFiles())
        {
            long start = System.currentTimeMillis();
            if (subdir.isDirectory())
            {
                run( subdir.toString() );
            }
            long end = System.currentTimeMillis();
            System.out.println("took " + (end - start));
        }
    }
}
