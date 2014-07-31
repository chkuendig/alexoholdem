package ao.holdem.model.card.chance;

import ao.holdem.model.Round;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.util.data.Arrs;

import java.util.Random;

/**
 * User: alex
 * Date: 23-Apr-2009
 * Time: 1:28:41 AM
 *
 * Works for heads-up only.
 */
public class SwapCards implements ChanceCards
{
    //--------------------------------------------------------------------
    private final Community community;
    private final Hole      holeA;
    private final Hole      holeB;

    private int playerA = -1;
    private boolean swap = false;

    
    //--------------------------------------------------------------------
    public SwapCards(Random rand)
    {
        int cards[] = {-1, -1,
                       -1, -1,
                       -1, -1, -1, -1, -1};

        for (int i = 0; i < cards.length; i++)
        {
            int nextCard;
            do
            {
                nextCard = rand.nextInt(52);
            }
            while ( Arrs.indexOf(cards, nextCard) != -1 );

            cards[ i ] = nextCard;
        }

        holeA = Hole.valueOf(card(cards[0]), card(cards[1]));
        holeB = Hole.valueOf(card(cards[2]), card(cards[3]));

        community = new Community(
                        card(cards[4]), card(cards[5]), card(cards[6]),
                        card(cards[7]), card(cards[8]));
    }

    private Card card(int i) {
        return Card.VALUES[ i ];
    }


    //--------------------------------------------------------------------
    public Hole hole(int forPlayer) {
        if (playerA == -1) {
            playerA = forPlayer;
        }

        return swap ^ (playerA == forPlayer)
               ? holeA
               : holeB;
    }

    public void swap()
    {
        swap = !swap;
    }


    //--------------------------------------------------------------------
    public Community community(Round asOf) {
        return community.asOf( asOf );
    }
}
