package ao.holdem.abs.calc;

import ao.holdem.abs.bucket.index.detail.turn.TurnDetailFlyweight;
import ao.holdem.abs.bucket.index.detail.turn.TurnDetails;
import ao.holdem.abs.bucket.index.detail.turn.TurnOdds;
import ao.holdem.canon.enumeration.HandEnum;
import ao.holdem.canon.turn.Turn;
import ao.util.pass.Traverser;

/**
 * 13/02/14 6:30 PM
 */
public class TurnDetailsTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        HandEnum.uniqueTurns(
                new Traverser<Turn>() {
                    public void traverse(Turn turn) {
                        int index = turn.canonIndex();
                        TurnDetailFlyweight.CanonTurnDetail details = TurnDetails.lookup(index);

                        if (details.canonIndex() != index) {
                            System.out.println("TurnDetailFlyweight index error");
                        }
                        if (((char) (details.strength() * Character.MAX_VALUE)) !=
                                ((char) (TurnOdds.lookup(index).strengthVsRandom()
                                        * Character.MAX_VALUE))) {
                            System.out.println(
                                    "TurnDetailFlyweight strength error: " +
                                            details.strength() + " vs " +
                                            TurnOdds.lookup(index).strengthVsRandom());
                        }

//                System.out.println(
//                        details.represents());
                    }
                });
    }


}
