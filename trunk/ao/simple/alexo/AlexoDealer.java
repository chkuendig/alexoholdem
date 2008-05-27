package ao.simple.alexo;

import ao.simple.alexo.card.AlexoCardSequence;
import ao.simple.alexo.player.AlexoRandom;
import ao.simple.alexo.player.AlwaysRaise;
import ao.simple.alexo.state.AlexoState;

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
 *      last player’s bet), or raise (calling the current bet and making
 *      an additional bet). In Rhode Island Hold’em, the players are
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
    public static void main(String[] args)
    {
        AlexoDealer dealer = new AlexoDealer(
                                    new AlwaysRaise(),
                                    new AlexoRandom());

        boolean           inOrder  = true;
        int               numHands = 0;
        int               cumDelta = 0;
        AlexoCardSequence hands[]  = generate(1000000);
        for (int round = 0; round < 2; round++)
        {
            for (AlexoCardSequence hand : hands)
            {
                cumDelta += (inOrder ? 1 : -1) *
                                dealer.play(hand);
                numHands++;
            }

            dealer.swapPlayers();
            inOrder = !inOrder;
        }

        System.out.println(
                (double) cumDelta / numHands);
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

        do
        {
            AlexoPlayer nextToAct =
                    state.firstToActIsNext()
                    ? first : last;

            AlexoAction act = nextToAct.act(state, cards);
            state = state.advance( act );
        }
        while (! state.endOfHand());
        
        return state.deltas( cards );
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


