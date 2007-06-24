package ao.holdem.def.bot;

import ao.holdem.def.state.domain.*;

import java.util.EnumSet;

/**
 *
 */
public interface BotFactory
{
    //--------------------------------------------------------------------
    public EnumSet<BetsToCall>   betDomain();
    public EnumSet<DealerDistance>  positionDomain();
    public EnumSet<Opposition>   oppositionDomain();
    public EnumSet<BettingRound> roundDomain();

    public Bot nextInstance();


    //--------------------------------------------------------------------
    public static class Impl
    {
        private Impl() {}

        public static BotFactory newInstance(
                final EnumSet<BetsToCall>   bets,
                final EnumSet<DealerDistance>  positions,
                final EnumSet<Opposition>   oppositions,
                final EnumSet<BettingRound> rounds,
                final Class<? extends Bot>  clazz)
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

            return new BotFactory() {
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
