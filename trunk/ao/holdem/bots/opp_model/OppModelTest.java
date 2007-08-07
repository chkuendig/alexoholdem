package ao.holdem.bots.opp_model;

import ao.holdem.bots.opp_model.predict.BackpropPredictor;
import ao.holdem.bots.opp_model.predict.PredictionSet;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.persist.PlayerHandleAccess;
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
        modelOpponet(playerAccess.find("irc", "sagerbot"));
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
        PredictionSet predictions = new PredictionSet();
        predictions.addPlayerHands( p );

//        anji( predictions );
        backprop( predictions );
    }

//    private void anji(PredictionSet predictions) throws Exception
//    {
//        Properties props = new Properties("anji_predict.properties");
//
//        AnjiPredictFitness ff = new AnjiPredictFitness( predictions );
//        ff.init( props );
//
//        PredictEvolver evolver = new PredictEvolver(ff);
//        evolver.init( props );
//        evolver.run();
//    }

    // see http://www.jooneworld.com/docs/sampleEngine.html
    private void backprop(PredictionSet predictions)
    {
        new BackpropPredictor().trainOn( predictions );
//        new BackpropPredictor().trainXor();
    }


    //--------------------------------------------------------------------
    /*  sagerbot :: 2785
        gsr :: 2515
        oejack4 :: 2293
        Shazbot :: 1934
        alfalfa :: 1861
        pliu :: 1650
        num :: 1596
        jb :: 1586
        bmk :: 1470
        perfecdoh :: 1461 */
    private void retrieveMostPrevalent()
    {
        for (PlayerHandle p : playerAccess.byPrevalence(100))
        {
            System.out.println(
                    p.getName() + " :: " + p.getHands().size());
        }
    }

}
