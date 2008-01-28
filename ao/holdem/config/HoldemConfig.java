package ao.holdem.config;

import ao.ai.opp_model.OppModelTest;
import ao.irc.IrcRunner;
import ao.holdem.engine.persist.dao.PlayerHandleLookup;
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
        bind(IrcRunner.class);
        bind(OppModelTest.class);
    }
}
