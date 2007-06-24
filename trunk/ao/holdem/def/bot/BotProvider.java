package ao.holdem.def.bot;

import ao.holdem.def.state.domain.BetsToCall;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.domain.DealerDistance;
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
    private final List<BotFactory>[][][][] FACTORIES;



    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public BotProvider()
    {
        FACTORIES = (List<BotFactory>[][][][])
                        new List[BetsToCall.values().length]
                                [BettingRound.values().length]
                                [DealerDistance.values().length]
                                [Opposition.values().length];

        for (int bet = 0; bet < FACTORIES.length; bet++)
        {
            int rounds = FACTORIES[bet].length;
            for (int round = 0;
                     round < rounds;
                     round++)
            {
                int positions = FACTORIES[bet][round].length;
                for (int position = 0;
                         position < positions;
                         position++)
                {
                    int opponents =
                            FACTORIES[bet][round][position].length;
                    for (int opponent = 0;
                             opponent < opponents;
                             opponent++)
                    {
                        FACTORIES[bet][round][position][opponent] =
                                new ArrayList<BotFactory>();
                    }
                }
            }
        }
    }


    //--------------------------------------------------------------------
    public void add(BotFactory factory)
    {
        for (BetsToCall bets : factory.betDomain())
        {
            for (BettingRound round : factory.roundDomain())
            {
                for (DealerDistance position : factory.positionDomain())
                {
                    for (Opposition opposition : factory.oppositionDomain())
                    {
                        FACTORIES[bets.ordinal()]
                                 [round.ordinal()]
                                 [position.ordinal()]
                                 [opposition.ordinal()].add( factory );
                    }
                }
            }
        }
    }


    //--------------------------------------------------------------------
    public Bot forDomain(BetsToCall   bets,
                         BettingRound round,
                         DealerDistance position,
                         Opposition   opposition)
    {
        return Rand.fromList(
                    FACTORIES[bets.ordinal()]
                             [round.ordinal()]
                             [position.ordinal()]
                             [opposition.ordinal()]).nextInstance();
    }
}
