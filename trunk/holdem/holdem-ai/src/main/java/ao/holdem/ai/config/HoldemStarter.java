package ao.holdem.ai.config;

import ao.irc.IrcRunner;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.BasicConfigurator;

/**
 *
 */
public class HoldemStarter
{
    public static void main(String args[]) throws Exception
    {
        // configure log4j logging
        BasicConfigurator.configure();

        Injector injector =
                Guice.createInjector(
                        new HoldemConfig());

//        injector.getInstance(DealerTest.class).headsUp();
//        injector.getInstance(DecisionTest.class).testDecisionTree();
//        injector.getInstance(OppModelTest.class).testOpponentModeling();
        injector.getInstance(IrcRunner.class).run(
                "irc/holdem/199802");
//                "C:\\alex\\data\\irc_poker\\holdem\\200104");
//        injector.getInstance(IrcRunner.class).runOnSubdirs(
//                "C:\\alex\\data\\limit_holdem\\holdem");
//                "C:\\alex\\data\\irc_poker\\holdem3");
    }
}
