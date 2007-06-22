package ao.holdem.def.bot;

import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.domain.Decider;
import ao.holdem.def.state.domain.Opposition;
import ao.util.rand.Rand;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BotProvider
{
    //--------------------------------------------------------------------
    private final List<BotFactory>[][][] FACTORIES;



    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public BotProvider()
    {
        FACTORIES = (List<BotFactory>[][][])
                        new List[Decider.values().length]
                                [Opposition.values().length]
                                [BettingRound.values().length];

        for (int decider = 0; decider < FACTORIES.length; decider++)
        {
            int oppositions = FACTORIES[decider].length;
            for (int opposition = 0;
                     opposition < oppositions;
                     opposition++)
            {
                int bettingRounds = FACTORIES[decider][opposition].length;
                for (int bettingRound = 0;
                         bettingRound < bettingRounds;
                         bettingRound++)
                {
                    FACTORIES[decider][opposition][bettingRound] =
                            new ArrayList<BotFactory>();
                }
            }
        }
    }


    //--------------------------------------------------------------------
    public void add(BotFactory factory)
    {
        for (Decider decider : factory.decitionDomain())
        {
            for (Opposition opposition : factory.oppositionDomain())
            {
                for (BettingRound round : factory.roundDomain())
                {
                    FACTORIES[decider.ordinal()]
                             [opposition.ordinal()]
                             [round.ordinal()].add( factory );
                }
            }
        }
    }


    //--------------------------------------------------------------------
    public Bot forDomain(Decider      decider,
                         Opposition   opposition,
                         BettingRound round)
    {
        return Rand.fromList(
                    FACTORIES[decider.ordinal()]
                             [opposition.ordinal()]
                             [round.ordinal()]).nextInstance();
    }
}
