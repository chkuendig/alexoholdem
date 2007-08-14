package ao.holdem;

import ao.holdem.def.history_bot.BotHandle;
import ao.holdem.def.history_bot.HistoryBot;
import ao.holdem.def.model.Money;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.HandHistory;
import ao.holdem.history.Snapshot;
import ao.holdem.history.persist.PlayerHandleLookup;
import ao.holdem.history_game.Dealer;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Arrays;

/**
 *
 */
public class HistoryTest
{
    //--------------------------------------------------------------------
    @Inject Provider<Dealer>   dealerProvider;
    @Inject PlayerHandleLookup players;


    //--------------------------------------------------------------------
    public void historyTest()
    {        
        Dealer dealer = dealerProvider.get();
        dealer.configure(Arrays.<BotHandle>asList(
                new BotHandle(players.lookup("a"), new DummyBot()),
                new BotHandle(players.lookup("b"), new DummyBot())));

        dealer.play();
    }


    //--------------------------------------------------------------------
    public static class DummyBot implements HistoryBot
    {
        public void introduce() {}
        public void retire()    {}

        public void opponentToAct(HandHistory hand, Snapshot env) {}
        public void opponentActed(
                HandHistory hand, Snapshot env, TakenAction act)  {}

        public void handEnded(
                HandHistory atEndOfHand, Money stackDelta) {}

        public Action act(HandHistory hand, Snapshot env)
        {
            return Action.CHECK_OR_CALL;
        }
    }
}
