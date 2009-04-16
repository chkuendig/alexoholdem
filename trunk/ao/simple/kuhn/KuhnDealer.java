package ao.simple.kuhn;

import ao.simple.kuhn.player.CrmBot;
import ao.simple.kuhn.state.KuhnSeat;
import ao.simple.kuhn.state.StateFlow;
import ao.util.math.stats.Combo;
import ao.util.math.stats.Permuter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class KuhnDealer
{
    //--------------------------------------------------------------------
    public static void main(String args[])
    {
        CrmBot bot = new CrmBot(100 * 1000 * 1000);
        KuhnDealer dealer =
                new KuhnDealer(
                        bot
                    ,   bot
//                    ,   new AlwaysBet()
//                    ,   new AlwaysPass()
//                    ,   new RandomKuhnPlayer()
                );

//        boolean  inOrder   = true;
        int      numHands  = 0;
        int      cumDelta  = 0;
        KuhnCard hands[][] = generateHands();
        for (int round = 0; round < 1000 * 1000; round++)
        {
            for (KuhnCard[] hand : hands)
            {
//                cumDelta += (inOrder ? 1 : -1) *
//                                dealer.playHand(
//                                        hand[0], hand[1]);
                cumDelta += dealer.playHand(hand[0], hand[1]);

                numHands++;
            }
//            dealer.swapPlayers();
//            inOrder = !inOrder;
        }

        System.out.println(
                (double) cumDelta / numHands);
    }

    private static KuhnCard[][] generateHands()
    {
        KuhnCard hands[][] = new KuhnCard[ (int)
                Combo.factorial(KuhnCard.values().length) ][2];

        int i = 0;
        for (KuhnCard c[] :
                new Permuter<KuhnCard>(KuhnCard.values(), 2)) {
            hands[ i++ ] = c;
        }

        return hands;
    }



    //--------------------------------------------------------------------
    private KuhnPlayer first;
    private KuhnPlayer last;


    //--------------------------------------------------------------------
    public KuhnDealer(KuhnPlayer firstToAct,
                      KuhnPlayer lastToAct)
    {
        first = firstToAct;
        last  = lastToAct;
    }


    //--------------------------------------------------------------------
    /**
     * @return first to act delta
     */
    public int playHand()
    {
        List<KuhnCard> deck = Arrays.asList( KuhnCard.values() );
        Collections.shuffle(deck);

        return playHand(deck.get(0), deck.get(1));
    }
    public int playHand(KuhnCard firstCard, KuhnCard lastCard)
    {
        KuhnSeat a = new KuhnSeat(first, firstCard);
        KuhnSeat b = new KuhnSeat(last,  lastCard);

        StateFlow flow = StateFlow.FIRST_ACTION;
        do
        {
            KuhnSeat   seat   = flow.firstIsNextToAct()
                                 ? a : b;
            KuhnAction action = seat.act(flow.state());
            flow = flow.advance( action );
        }
        while (! flow.endOfHand());

        switch (flow.outcome())
        {
            case PLAYER_ONE_WINS:   return  1;
            case PLAYER_TWO_WINS:   return -1;
            case SHOWDOWN:          return      showdown(a, b);
            case DOUBLE_SHOWDOWN:   return  2 * showdown(a, b);
        }
        throw new Error(flow + " leads to invalid outcome.");
    }

    private int showdown(KuhnSeat a, KuhnSeat b)
    {
        return a.hole().compareTo( b.hole() ) > 0 ? 1 : -1;
    }


    //--------------------------------------------------------------------
    public void swapPlayers()
    {
        KuhnPlayer temp;

        temp  = first;
        first = last;
        last  = temp;
    }
}
