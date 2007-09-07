package ao.holdem.bots.opp_model.predict;

import ao.holdem.def.history_bot.BotHandle;
import ao.holdem.history.persist.PlayerHandleLookup;
import ao.holdem.history_game.Dealer;
import ao.holdem.bots.hist.PredictorBot;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Arrays;

/**
 *
 */
public class PredictPersistTest
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleLookup players;
    @Inject Provider<Dealer>   dealerProvider;


    //--------------------------------------------------------------------
    public void testPredictionPersistance()
    {
        Dealer dealer = dealerProvider.get();

        dealer.configure(Arrays.<BotHandle>asList(
                new BotHandle(
                        players.lookup("test", "a"),
                        new PredictorBot()),
                new BotHandle(
                        players.lookup("test", "b"),
                        new PredictorBot())));

        for (int i = 0; i < 1000; i++)
        {
            dealer.play();
        }
    }
}
