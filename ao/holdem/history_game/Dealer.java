package ao.holdem.history_game;

import ao.holdem.def.history_bot.BotHandle;
import ao.holdem.def.history_bot.HistoryBot;
import ao.holdem.def.model.Money;
import ao.holdem.def.model.cards.Deck;
import ao.holdem.def.model.cards.Hand;
import ao.holdem.def.state.action.Action;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.Transactional;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Dealer
{
    //--------------------------------------------------------------------
    @Inject Provider<Session> session;


    //--------------------------------------------------------------------
    private List<PlayerHandle>            playerHandels;
    private Map<PlayerHandle, HistoryBot> byHandle;


    //--------------------------------------------------------------------
    public void configure(
            List<BotHandle> players)
    {
        assert players.size() >= 2;

        byHandle      = new HashMap<PlayerHandle, HistoryBot>();
        playerHandels = new ArrayList<PlayerHandle>();

        for (BotHandle player : players)
        {
            playerHandels.add( player.handle() );
            byHandle.put(player.handle(), player.bot());
        }
    }


    //--------------------------------------------------------------------
    @Transactional
    public HandHistory play()
    {
        Deck        deck = new Deck();
        HandHistory hand = new HandHistory(playerHandels);

        // deal hole cards
        for (PlayerHandle player : playerHandels)
        {
            hand.addHole(player, deck.nextHole());
        }
        session.get().save( hand );

        Event e = null;
        Snapshot s = hand.snapshot(e);
        do
        {
            boolean startOfRound = true;
            while (startOfRound || !s.isRoundDone())
            {
                startOfRound = false;

                PlayerHandle p = s.nextToAct();
                HistoryBot   b = byHandle.get( p );

                TakenAction act = null;
                Action a = b.act(hand, s);
                switch (a)
                {
                    case CHECK_OR_CALL:
                        act = (s.canCheck())
                                ? TakenAction.CHECK
                                : TakenAction.CALL;
                        break;

                    case CHECK_OR_FOLD:
                        act = (s.canCheck())
                                ? TakenAction.CHECK
                                : TakenAction.FOLD;
                        break;

                    case RAISE_OR_CALL:
                        act = (s.canRaise())
                                ? TakenAction.RAISE
                                : TakenAction.CALL;
                        break;
                }
                e = hand.addEvent(p, s.round(), act);
                session.get().save( e );

                s = s.addNextEvent( e );
            }

            assignCommunity(hand, s, deck);
        }
        while (! s.isGameOver());

        assignDeltas( hand, s );

        hand.commitHandToPlayers();
        session.get().saveOrUpdate( hand );

        return hand;
    }


    //--------------------------------------------------------------------
    private void assignCommunity(
            HandHistory hand,
            Snapshot    lastOfPreviouseRound,
            Deck        deck)
    {
        switch (lastOfPreviouseRound.round())
        {
            case PREFLOP:
                hand.setCommunity(deck.nextFlop());
                break;
            case FLOP:
                hand.setCommunity(
                        hand.getCommunity().addTurn(
                                deck.nextCard()));
                break;
            case TURN:
                hand.setCommunity(
                        hand.getCommunity().addRiver(
                                deck.nextCard()));
                break;
        }
    }


    //--------------------------------------------------------------------
    public static void assignDeltas(
            HandHistory hand,
            Snapshot    finalSnapshot)
    {
        if (finalSnapshot.activePlayers().size() == 1)
        {
            assignSingleWinnerDeltas(hand, finalSnapshot);
        }
        else
        {
            assignShowdownDeltas(hand, finalSnapshot);
        }
    }

    private static void assignShowdownDeltas(
            HandHistory hand,
            Snapshot    finalSnapshot)
    {
        List<PlayerHandle> topWinners =
                new ArrayList<PlayerHandle>();
        short topWinnerHandRank = Short.MIN_VALUE;

        for (PlayerHandle winner :
                finalSnapshot.activePlayers())
        {
            short handRank =
                    new Hand(hand.getHoles().get(winner),
                             hand.getCommunity()).value();

            if (handRank > topWinnerHandRank)
            {
                topWinners.clear();
            }
            if (handRank >= topWinnerHandRank)
            {
                topWinners.add( winner );
                topWinnerHandRank = handRank;
            }
        }

        Money totalLost = new Money();
        for (PlayerHandle player : hand.getPlayers())
        {
            if (! topWinners.contains(player))
            {
                Money commit = finalSnapshot.commitment(player);
                hand.setDelta(player,
                              Money.ZERO.minus(commit));
                totalLost = totalLost.plus(commit);
            }
        }
        Money winnings = totalLost.split( topWinners.size() );
        for (PlayerHandle winner : topWinners)
        {
            hand.setDelta(winner, winnings);
        }

        // uneven split pot
        hand.setDelta(topWinners.get(0),
                      hand.getDeltas().get(
                              topWinners.get(0))
                        .plus( totalLost.remainder(
                                    topWinners.size()) ));
    }

    private static void assignSingleWinnerDeltas(
            HandHistory hand,
            Snapshot    finalSnapshot)
    {
        PlayerHandle winner =
                finalSnapshot.activePlayers().get(0);

        for (PlayerHandle player : hand.getPlayers())
        {
            Money commit = finalSnapshot.commitment(player);

            if (winner.equals( player ))
            {
                hand.setDelta(player,
                              finalSnapshot.pot().minus(
                                      commit));
            }
            else
            {
                hand.setDelta(player,
                              Money.ZERO.minus(commit));
            }
        }
    }


}
