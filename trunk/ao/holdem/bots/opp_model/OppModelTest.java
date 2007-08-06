package ao.holdem.bots.opp_model;

import ao.holdem.bots.opp_model.predict.AnjiPredictFitness;
import ao.holdem.bots.opp_model.predict.PredictEvolver;
import ao.holdem.bots.opp_model.predict.PredictionSet;
import ao.holdem.bots.opp_model.predict.BackpropPredictor;
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
        modelOpponet(playerAccess.find("irc", "Dagon"));
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

    private void anji(PredictionSet predictions) throws Exception
    {
        Properties props = new Properties("anji_predict.properties");

        AnjiPredictFitness ff = new AnjiPredictFitness( predictions );
        ff.init( props );

        PredictEvolver evolver = new PredictEvolver(ff);
        evolver.init( props );
        evolver.run();
    }

    // see http://www.jooneworld.com/docs/sampleEngine.html
    private void backprop(PredictionSet predictions)
    {
        new BackpropPredictor().trainOn( predictions );
//        new BackpropPredictor().trainXor();
    }


    //--------------------------------------------------------------------
    /*  Barrister :: 3760
        Voyeur :: 3088
        BlackBart :: 2903
        pcktkings :: 2517
        BruceZ :: 2051
        beernutz :: 2024
        holdemgod :: 2000
        Wood :: 1840
        Shrink2 :: 1832
        frenchy :: 1742 */
    private void retrieveMostPrevalent()
    {
        for (PlayerHandle p : playerAccess.byPrevalence(100))
        {
            System.out.println(
                    p.getName() + " :: " + p.getHands().size());
        }
    }

}
