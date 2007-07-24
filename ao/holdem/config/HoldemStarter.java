package ao.holdem.config;

import ao.holdem.HistoryTest;
import ao.holdem.history.persist.PlayerHandleAccess;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.UnitOfWork;

/**
 *
 */
public class HoldemStarter
{
    public static void main(String args[])
    {
        Injector injector =
                Guice.createInjector(
                        PersistenceService
                                .usingHibernate()
                                .across(UnitOfWork.TRANSACTION)
                                .addAccessor(PlayerHandleAccess.class)
                                .buildModule(),
                        new HoldemConfig());
        injector.getInstance(PersistenceService.class).start();

        injector.getInstance(HistoryTest.class).historyTest();
    }
}
