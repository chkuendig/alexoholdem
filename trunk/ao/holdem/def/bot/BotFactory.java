package ao.holdem.def.bot;

import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.domain.Decider;
import ao.holdem.def.state.domain.Opposition;

import java.util.EnumSet;

/**
 *
 */
public interface BotFactory
{
    //--------------------------------------------------------------------
    public EnumSet<Decider>      decitionDomain();
    public EnumSet<Opposition>   oppositionDomain();
    public EnumSet<BettingRound> roundDomain();

    public Bot nextInstance();


    //--------------------------------------------------------------------
    public static class Impl
    {
        private Impl() {}

        public static BotFactory newInstance(
                final EnumSet<Decider>      deciders,
                final EnumSet<Opposition>   oppositions,
                final EnumSet<BettingRound> rounds,
                final Class<? extends Bot>  clazz)
        {
            return new BotFactory() {
                public EnumSet<Decider> decitionDomain() {
                    return deciders;
                }

                public EnumSet<Opposition> oppositionDomain() {
                    return oppositions;
                }

                public EnumSet<BettingRound> roundDomain() {
                    return rounds;
                }

                public Bot nextInstance() {
                    try
                    {
                        return clazz.newInstance();
                    }
                    catch (Exception e)
                    {
                        throw new Error( e );
                    }
                }
            };
        }
    }
}
