package ao.holdem.game.impl;

import ao.holdem.def.bot.Bot;
import ao.holdem.def.bot.LocalBot;
import ao.holdem.model.Deck;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.def.state.domain.Domain;
import ao.holdem.game.Outcome;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class HandPlay
{
    //--------------------------------------------------------------------
    private final static Logger log =
            Logger.getLogger(HandPlay.class.getName());
//    static {  log.setLevel( Level.OFF );  }


    //--------------------------------------------------------------------
    private Deck      deck;
//    private List<BotHandle> bots;
    private List<Bot> bots;
    private HandState state;
//    private HandHistory state;
    private Outcome   outcome;


    //--------------------------------------------------------------------
//    public HandPlay(List<BotHandle> players)
    public HandPlay(List<Bot> players)
    {
        assert players.size() >= 2;

        log.debug("initiating hand with " + players.size() + " players.");
        deck = new Deck();
        bots = players;

        state   = new HandState( players.size() );
//        state   = new HandHistory(players);
        outcome = new Outcome(   players.size() );
    }


    //--------------------------------------------------------------------
    public void dealHoleCards()
    {
        log.debug("dealing hole cards.");
        for (int i = 0; i < state.players(); i++)
        {
            state.dealHoleCards(i, deck.nextHole());
        }
//        for (PlayerHandle player : state.getPlayers())
//        {
//            state.addHole( player, deck.nextHole() );
//        }
    }

    public void designateBlinds()
    {
        log.debug("designating blinds.");
        state.designateBlinds();
    }


    //--------------------------------------------------------------------
    public void flop()
    {
        log.debug("dealing flop.");
        state.dealFlop( deck.nextFlop() );
    }
    public void turn()
    {
        log.debug("dealing turn.");
        state.dealTurn( deck.nextCard() );
    }
    public void river()
    {
        log.debug("dealing river.");
        state.dealRiver( deck.nextCard() );
    }


    //--------------------------------------------------------------------
    /**
     * round of betting(first_to_act, bet_size)
        active_players = all players starting from first to act

        remaining_raises = 3
        toCall           = BB if preflop, 0 otherwise
        previous_raiser  = null
        while active_players.count > 1                          
            raise_occurred = false
            for each actor : active_players
                # can't re-raise your own raise
                if previous_raiser == actor: return

                # assuming only correct actions are returned
                action = actor.actOn( current_situation )
                if action == fold
                    active_players.remove actor
                    goto next actor
                if action == raise
                    previous_raiser = actor
                    remaining_raises--
                    toCall += bet_size
                    actor.inFor = toCall
                    raise_occurred = true
            end for
            if not raise_occurred: exit while
        end while

     * @return true if this round has a winner
     *          (false it the game continues)
     */
    public boolean roundOfBetting()
    {
        log.debug("starting round of betting.");
        List<Integer> active =
                state.activeByAwayFromDealerInActionOrder();
        state.replenishBets( 4 );

        int previousRaiser = -1;
        round_circle:
        while (true)
        {
            boolean raiseOccurred = false;
            for (int awayFromDealer : new ArrayList<Integer>( active ))
            {
                log.debug("now acting: " + awayFromDealer +
                          " clockwise from dealer.");
                if (previousRaiser == awayFromDealer)
                {
                    log.debug("can't reraise yourself. stopping hand.");
                    break round_circle;
                }

                SimpleAction action = nextAction(awayFromDealer);
                log.debug(awayFromDealer + " clockwise from dealer acts: "
                          + action + ".");
                switch (action)
                {
                    case FOLD:
//                        log.debug(awayFromDealer +
//                                    " clockwise from dealer folds.");
                        state.folded(awayFromDealer);

                        active.remove( (Integer)awayFromDealer );
                        if (active.size() < 2) break round_circle;
                        break;

                    case RAISE:
//                        log.debug(awayFromDealer +
//                                    " clockwise from dealer raises.");
                        state.raised( awayFromDealer );

                        previousRaiser = awayFromDealer;
                        raiseOccurred = true;
                        break;

                    case CALL:
//                        log.debug(awayFromDealer +
//                                    " clockwise from dealer calls.");
                        state.called( awayFromDealer );
                        break;

//                    case CHECK:
////                        log.debug(awayFromDealer +
////                                    " clockwise from dealer checks.");
//                        state.checked( awayFromDealer );
//                        break;
                }
            }
            if (! raiseOccurred) break;
        }

        // so that their post-action environments are captured.
        for (Integer remaining : active)
        {
            outcome.add( state.envFor(remaining) );
        }

        log.debug("round of betting is done.");
        return (active.size() == 1);
    }


    //--------------------------------------------------------------------
    private SimpleAction nextAction(int awayFromDealer)
    {
        Domain domain = new Domain(state, awayFromDealer);
        //Bot bot = bots.forDomain( domain );
        Bot bot = bots.get(awayFromDealer);

        log.debug(bot + " is actor for: " +
                  awayFromDealer + " clockwise from dealer.");

        GodEnvironment env    = state.envFor(awayFromDealer);
        EasyAction action = notNullAction(bot, env);
        log.debug(awayFromDealer + " clockwise from dealer " +
                  "made choice of: " + action + ".");

        SimpleAction taken = concreteAction(awayFromDealer, action);
        outcome.add(new LocalBot(bot, domain), env, taken);
        return taken;
    }

    private SimpleAction concreteAction(
            int awayFromDealer, EasyAction fuzzyAction)
    {
        switch (fuzzyAction)
        {
            case CHECK_OR_FOLD:
//                return state.canCheck(awayFromDealer)
//                        ? SimpleAction.CHECK
//                        : SimpleAction.FOLD;
                return state.canCheck(awayFromDealer)
                        ? SimpleAction.CALL
                        : SimpleAction.FOLD;

            case CHECK_OR_CALL:
//                return state.canCheck(awayFromDealer)
//                        ? SimpleAction.CHECK
//                        : SimpleAction.CALL;
            return SimpleAction.CALL;

            case RAISE_OR_CALL:
                return state.canRaise()
                        ? SimpleAction.RAISE
                        : SimpleAction.CALL;

            default: return null;
        }
    }

    private EasyAction notNullAction(Bot bot, Environment env)
    {
        EasyAction action = bot.act( env );
        return action == null
                ? EasyAction.CHECK_OR_FOLD
                : action;
    }


    //--------------------------------------------------------------------
    public Outcome outcome()
    {
        return outcome;
    }

    public Outcome showdown()
    {
        outcome.showdown(state.activeByAwayFromDealerInActionOrder());
        return outcome;
    }
}
