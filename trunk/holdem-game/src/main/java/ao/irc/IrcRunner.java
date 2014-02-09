package ao.irc;

import ao.holdem.model.replay.Replay;
import com.google.common.base.Preconditions;
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
    private final IrcHistorian historian;
//    @Inject HandHistoryDao hands;

    public IrcRunner(IrcHistorian historian) {
        this.historian = historian;
    }


    //--------------------------------------------------------------------
    public void run(String ircDir)
    {
        LOG.info("ircDir: " + ircDir);

        for (Replay hist :
                historian.fromSnapshot(ircDir))
        {
            System.out.println("hist = " + hist);
        }
    }

    public void runOnSubdirs(String ircDir)
    {
        File dir = new File(ircDir);
        Preconditions.checkArgument(dir.isDirectory());

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


    public static void main(String[] args) {
        IrcHistorian historian = new IrcHistorian();
        IrcRunner runner = new IrcRunner(historian);

//        runner.runOnSubdirs(
//                "C:\\alex\\data\\limit_holdem\\holdem");
//                "C:\\alex\\data\\irc_poker\\holdem3");
    }
}
