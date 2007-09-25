package ao.irc;

import ao.persist.HandHistory;
import ao.persist.dao.HandHistoryDao;
import com.google.inject.Inject;

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
        for (HandHistory hist :
                historian.fromSnapshot(ircDir))
        {
//            hands.store( hist );
//            System.out.println("hist = " + hist);
        }
    }
}
