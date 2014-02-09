package ao.holdem.config;

import ao.irc.IrcRunner;
import com.google.inject.AbstractModule;

/**
 *
 */
public class HoldemConfig extends AbstractModule
{
    protected void configure()
    {
//        bind(Configuration.class)
//                .toInstance(new AnnotationConfiguration().configure());

        bind(IrcRunner.class);
//        bind(OppModelTest.class);
    }
}
