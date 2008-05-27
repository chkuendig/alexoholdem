package ao.simple.alexo.card;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class AlexoCardSequence
{
    //--------------------------------------------------------------------
    private final AlexoCard      HOLE_A;
    private final AlexoCard      HOLE_B;
    private final AlexoCommunity COMMUNITY;


    //--------------------------------------------------------------------
    public AlexoCardSequence()
    {
        List<AlexoCard> deck = Arrays.asList(AlexoCard.VALUES);
        Collections.shuffle(deck);

        HOLE_A = deck.get(0);
        HOLE_B = deck.get(1);
        COMMUNITY = new AlexoCommunity(
                deck.get(2), deck.get(3));
    }


    //--------------------------------------------------------------------
    public AlexoCard firstHole()
    {
        return HOLE_A;
    }

    public AlexoCard lastHole()
    {
        return HOLE_B;
    }

    public AlexoCard hole(boolean forFirst)
    {
        return forFirst
                ? HOLE_A
                : HOLE_B;
    }


    //--------------------------------------------------------------------
    public AlexoCommunity community()
    {
        return COMMUNITY;
    }
}
