package ao.irc;

import ao.holdem.model.replay.Replay;
import com.google.inject.Inject;
import org.apache.log4j.Logger;

import java.io.File;

/**
 *
 */
public class IrcRunner
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
        Logger.getLogger(IrcRunner.class);


    //--------------------------------------------------------------------
    @Inject IrcHistorian   historian;
//    @Inject HandHistoryDao hands;


    //--------------------------------------------------------------------
    public void run(String ircDir)
    {
        LOG.info("ircDir: " + ircDir);

        for (Replay hist :
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
            LOG.info("took " + (end - start));
        }
    }
}
