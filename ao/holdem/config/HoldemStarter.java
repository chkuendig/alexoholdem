package ao.holdem.config;

import ao.holdem.history.irc.IrcRunner;
import ao.holdem.history.persist.PlayerHandleAccess;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.UnitOfWork;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 */
public class HoldemStarter
{
    public static void main(String args[])
    {
        // configure log4j logging
        BasicConfigurator.configure();
        Logger.getLogger("org.hibernate").setLevel(Level.ERROR);

        Injector injector =
                Guice.createInjector(
                        PersistenceService
                                .usingHibernate()
                                .across(UnitOfWork.TRANSACTION)
                                .addAccessor(PlayerHandleAccess.class)
                                .buildModule(),
                        new HoldemConfig());
        injector.getInstance(PersistenceService.class).start();

//        injector.getInstance(HistoryTest.class).historyTest();
        injector.getInstance(IrcRunner.class).run(
                "C:\\alex\\data\\irc_poker\\holdem\\199507");
//        injector.getInstance(IrcRunner.class).run(
//                "C:\\alex\\data\\limit_holdem\\hand_test");
    }
}
