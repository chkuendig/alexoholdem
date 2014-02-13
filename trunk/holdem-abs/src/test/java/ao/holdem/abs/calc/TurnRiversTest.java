package ao.holdem.abs.calc;

import ao.holdem.abs.bucket.index.detail.turn.TurnRivers;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;

/**
 * 13/02/14 6:32 PM
 */
public class TurnRiversTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        System.out.println(
                TurnRivers.rangeOf(Turn.CANONS - 1));
        System.out.println(
                TurnRivers.turnFor(River.CANONS - 1));

        for (long river = 0; river < River.CANONS; river++)
        {
            if (river % 10000000 == 0) System.out.print(".");

            int turn = TurnRivers.turnFor(river);
            if (turn == -1) {
                System.out.println("err for: " + river);
            }
        }
    }
}
