package ao.simple;

import ao.simple.player.CrmBot;
import ao.simple.player.RandomKuhnPlayer;
import ao.simple.state.KuhnSeat;
import ao.simple.state.StateFlow;

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
        KuhnDealer dealer =
                new KuhnDealer(
                        new CrmBot(1000)
//                    ,   new CrmBot(10000)
//                    ,   new AlwaysBet()
//                    ,   new AlwaysPass()
                    ,   new RandomKuhnPlayer()
        );

        int numHands = 10000000;
        int cumDelta = 0;
        for (int i = 0; i < numHands; i++)
        {
            cumDelta +=
                    (i % 2 == 0 ? 1 : -1) *
                        dealer.playHand();

            dealer.swapPlayers();
        }

        System.out.println(
                (double) cumDelta / numHands);
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

        KuhnSeat a = new KuhnSeat(first, deck.get(0));
        KuhnSeat b = new KuhnSeat(last,  deck.get(1));

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
            case FIRST_TO_ACT_WINS: return  1;
            case LAST_TO_ACT_WINS:  return -1;
            case SHOWDOWN:          return     showdown(a, b);
            case DOUBLE_SHOWDOWN:   return 2 * showdown(a, b);
        }
        throw new Error(flow + " leads to invalid outcome.");
    }

    private int showdown(KuhnSeat a, KuhnSeat b)
    {
        return a.hole().compareTo( b.hole() ) < 0 ? -1 : 1;
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
