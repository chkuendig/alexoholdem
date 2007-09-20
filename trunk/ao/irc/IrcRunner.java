package ao.irc;

import ao.persist.HandHistory;
import com.google.inject.Inject;

/**
 *
 */
public class IrcRunner
{
    //--------------------------------------------------------------------
    @Inject IrcHistorian       historian;
//    @Inject PlayerHandleLookup players;


    //--------------------------------------------------------------------
    public void run(String ircDir)
    {
        for (HandHistory hist :
                historian.fromSnapshot(ircDir))
        {
//            System.out.println("hist = " + hist);
        }
    }
}
