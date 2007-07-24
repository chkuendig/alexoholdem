package ao.holdem.config;

import ao.holdem.HistoryTest;
import ao.holdem.history.irc.IrcRunner;
import ao.holdem.history.persist.PlayerHandleLookup;
import com.google.inject.AbstractModule;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 *
 */
public class HoldemConfig extends AbstractModule
{
    protected void configure()
    {
        bind(Configuration.class)
                .toInstance(new AnnotationConfiguration().configure());

        bind(PlayerHandleLookup.class);
        bind(HistoryTest.class);
        bind(IrcRunner.class);
    }
}
