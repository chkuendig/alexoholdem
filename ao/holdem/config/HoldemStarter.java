package ao.holdem.config;

import ao.holdem.bots.opp_model.OppModelTest;
import ao.holdem.bots.opp_model.predict.PredictEvolver;
import ao.holdem.history.persist.PlayerHandleAccess;
import com.anji.util.Properties;
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
    public static void main(String args[]) throws Exception
    {
        Properties props = new Properties("dpbalance.properties");
		PredictEvolver evolver = new PredictEvolver();
		evolver.init( props );
		evolver.run();
		System.exit( 0 );

//        DoublePoleBalanceEvaluator.main(
//                new String[]{"dpbalance.properties"});

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


        injector.getInstance(OppModelTest.class).testOpponentModeling();
//        injector.getInstance(HistoryTest.class).historyTest();
//        injector.getInstance(IrcRunner.class).run(
//                "C:\\alex\\data\\limit_holdem\\holdem\\199504");
//        injector.getInstance(IrcRunner.class).run(
//                "C:\\alex\\data\\limit_holdem\\hand_test");
    }
}
