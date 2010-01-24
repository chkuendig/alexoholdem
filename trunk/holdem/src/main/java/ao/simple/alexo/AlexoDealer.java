package ao.simple.alexo;

import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.player.AlwaysRaise;
import ao.simple.alexo.player.CrmBot;
import ao.simple.alexo.state.AlexoRound;
import ao.simple.alexo.state.AlexoState;
import org.apache.log4j.Logger;

/**
 * Alexo Holdem is a variant of
 *  Rhode Island Holdem where there are no suits,
 *  so there are 13 cards in a deck.
 * Hands are compared based on "High Card" value.
 *
 * Rhode Island Rules:
 *
 * (1) Each player pays an ante of 5 chips which is added to the pot.
 *      Both players initially receive a single card, face down;
 *      these are known as the hole cards.
 *
 * (2) After receiving the hole cards, the players participate in one
 *      betting round. Each player may check (not placing any money in
 *      the pot and passing) or bet (placing 10 chips into the pot) if no
 *      bets have been placed. If a bet has been placed, then the player
 *      may fold (thus forfeiting the game along with any money they have
 *      put into the pot), call (adding chips to the pot equal to the
 *      last player's bet), or raise (calling the current bet and making
 *      an additional bet). In Rhode Island Hold'em, the players are
 *      limited to three bets each per betting round. (A raise equals
 *      two bets.) In the ?rst betting round, the bets are equal
 *      to 10 chips.
 *
 * (3) After the ?rst betting round, a community card is dealt face up.
 *      This is called the ?op card. Another betting round take places
 *      at this point, with bets equal to 20 chips.
 *
 * (4) Following the second betting round, another community card is
 *      dealt face up. This is called the turn card. A ?nal betting round
 *      takes place at this point, with bets again equal to 20 chips.
 *
 * (5) If neither player folds, then the showdown takes place. Both
 *      players turn over their cards. The player who has the best 3-card
 *      poker hand takes the pot. In the event of a draw, the pot
 *      is split evenly.
 */
public class AlexoDealer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(AlexoDealer.class);


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
//        int max = 1000;
//        AlexoPlayer maxPlayer = new CrmBot(max);
//        for (int i = 0; i < max; i += max/100)
//        {
//            touney(new CrmBot(i), maxPlayer);
//        }

        for (double aggression = 0; aggression < 1.0; aggression += 0.01)
        {
            LOG.info("aggression: " +
                     (double) Math.round(aggression * 1000) / 1000);
            touney(new CrmBot(100000, aggression), new AlwaysRaise());
        }
    }

    public static void touney(AlexoPlayer playerA,
                              AlexoPlayer playerB)
    {
        AlexoDealer dealer =
                new AlexoDealer(playerA, playerB);

        boolean           inOrder  = true;
        int               numHands = 0;
        int               cumDelta = 0;
        AlexoCardSequence hands[]  = generate(500000);
//        for (int round = 0; round < 2*2; round++)
//        {
            for (AlexoCardSequence hand : hands)
            {
                cumDelta += (inOrder ? 1 : -1) *
                                dealer.play(hand);
                numHands++;
            }

//            dealer.swapPlayers();
//            inOrder = !inOrder;
//        }

        LOG.info((double) cumDelta / numHands +
                  " for " + playerA + " vs " + playerB);
    }

    private static AlexoCardSequence[] generate(int n)
    {
        AlexoCardSequence sequence[] = new AlexoCardSequence[n];
        for (int i = 0; i < n; i++)
        {
            sequence[ i ] = new AlexoCardSequence();
        }
        return sequence;
    }



    //--------------------------------------------------------------------
    private AlexoPlayer first;
    private AlexoPlayer last;


    //--------------------------------------------------------------------
    public AlexoDealer(AlexoPlayer firstToAct,
                       AlexoPlayer lastToAct)
    {
        first = firstToAct;
        last  = lastToAct;
    }


    //--------------------------------------------------------------------
    public int play(AlexoCardSequence cards)
    {
        AlexoState state = new AlexoState();

        AlexoCardSequence currentCards =
                cards.truncate(AlexoRound.PREFLOP);
        first.handStarted(currentCards, true );
        last .handStarted(currentCards, false);

        do
        {
            if (state.atStartOfRound())
            {
                AlexoRound round = state.round();
                currentCards = cards.truncate(round);

                first.roundAdvanced(round, currentCards);
                last .roundAdvanced(round, currentCards);
            }

            AlexoPlayer nextToAct =
                    state.firstToActIsNext()
                    ? first : last;

            AlexoAction act = nextToAct.act(state, currentCards);

            (nextToAct == first ? last : first)
                    .opponentActed(state, act);

            state = state.advance( act );
        }
        while (! state.endOfHand());

        int delta = state.deltas( currentCards );
        first.handEnded( delta );
        last .handEnded( delta );
        return delta;
    }


    //--------------------------------------------------------------------
    public void swapPlayers()
    {
        AlexoPlayer temp;

        temp  = first;
        first = last;
        last  = temp;
    }
}


