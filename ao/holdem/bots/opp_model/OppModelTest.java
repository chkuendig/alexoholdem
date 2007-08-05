package ao.holdem.bots.opp_model;

import ao.holdem.bots.opp_model.predict.AnjiPredictFitness;
import ao.holdem.bots.opp_model.predict.PredictEvolver;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.persist.PlayerHandleAccess;
import com.anji.util.Properties;
import com.google.inject.Inject;
import com.wideplay.warp.persist.Transactional;

/**
 *
 */
public class OppModelTest
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleAccess playerAccess;


    //--------------------------------------------------------------------
    @Transactional
    public void testOpponentModeling()
    {
//        retrieveMostPrevalent();
        modelOpponet(playerAccess.find("irc", "dtm"));
    }


    //--------------------------------------------------------------------
    private void modelOpponet(PlayerHandle p)
    {
        System.out.println("analyzing " + p.getName());

        try
        {
            doModelOpponet( p );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void doModelOpponet(PlayerHandle p) throws Exception
    {
        Properties props = new Properties("anji_predict.properties");

        AnjiPredictFitness ff = new AnjiPredictFitness( p );
        ff.init( props );

        for (HandHistory h : p.getHands())
        {
            ff.addHand( h );
        }

        PredictEvolver evolver = new PredictEvolver(ff);
        evolver.init( props );
        evolver.run();
    }


    //--------------------------------------------------------------------
    /*  irc.sagerbot :: 3156
        irc.leo :: 2691
        irc.greg :: 2604
        irc.perfecto :: 2542
        irc.ab0 :: 2489
        irc.Kai :: 1957
        irc.Lev :: 1896
        irc.derek :: 1869
        irc.EdK :: 1821
        irc.kman :: 1810 */
    private void retrieveMostPrevalent()
    {
        for (PlayerHandle p : playerAccess.byPrevalence(100))
        {
            System.out.println(
                    p.getName() + " :: " + p.getHands().size());
        }
    }

}
