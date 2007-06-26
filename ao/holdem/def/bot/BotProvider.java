package ao.holdem.def.bot;

import ao.util.rand.Rand;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BotProvider
{
    //--------------------------------------------------------------------
    private final List<BotFactory> BOTS;


    //--------------------------------------------------------------------
    public BotProvider()
    {
        BOTS = new ArrayList<BotFactory>();
    }


    //--------------------------------------------------------------------
    public void add(BotFactory botFactory)
    {
        BOTS.add( botFactory );
    }

    
    //--------------------------------------------------------------------
    public Bot nextBot()
    {
        return Rand.fromList(BOTS).newInstance();
    }

    public List<Bot> nextBots(int howMany)
    {
        List<Bot> bots = new ArrayList<Bot>();

        for (int i = 0; i < howMany; i++)
        {
            bots.add( nextBot() );
        }

        return bots;
    }
}
