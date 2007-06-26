package ao.holdem.def.bot;

import ao.holdem.def.state.domain.*;

import java.util.EnumSet;

/**
 *
 */
public interface LocalBotFactory
{
    //--------------------------------------------------------------------
    public EnumSet<BetsToCall>     betDomain();
    public EnumSet<DealerDistance> positionDomain();
    public EnumSet<Opposition>     oppositionDomain();
    public EnumSet<BettingRound>   roundDomain();

    public Bot nextInstance();


    //--------------------------------------------------------------------
    public static class Impl
    {
        private Impl() {}

        public static LocalBotFactory newGlobalInstance(
                Class<? extends Bot> clazz)
        {
            return newInstance(EnumSet.allOf(BetsToCall.class),
                               EnumSet.allOf(DealerDistance.class),
                               EnumSet.allOf(Opposition.class),
                               EnumSet.allOf(BettingRound.class),
                               clazz);
        }

        public static LocalBotFactory newInstance(
                final EnumSet<BetsToCall>     bets,
                final EnumSet<DealerDistance> positions,
                final EnumSet<Opposition>     oppositions,
                final EnumSet<BettingRound>   rounds,
                final Class<? extends Bot>    clazz)
        {
            final Bot instance;
            try
            {
                instance = clazz.newInstance();
            }
            catch (Exception e)
            {
                throw new Error( e );
            }

            return new LocalBotFactory() {
                public EnumSet<BetsToCall> betDomain() {
                    return bets;
                }

                public EnumSet<DealerDistance> positionDomain() {
                    return positions;
                }

                public EnumSet<Opposition> oppositionDomain() {
                    return oppositions;
                }

                public EnumSet<BettingRound> roundDomain() {
                    return rounds;
                }

                public Bot nextInstance() {
                    return instance;
                }
            };
        }
    }
}
