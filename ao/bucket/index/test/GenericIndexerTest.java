package ao.bucket.index.test;

import ao.bucket.index.enumeration.CardEnum;
import ao.bucket.index.enumeration.CardEnum.UniqueFilter;
import ao.bucket.index.river.River;
import ao.util.misc.Traverser;

/**
 * Date: Aug 21, 2008
 * Time: 7:05:20 PM
 */
public class GenericIndexerTest
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        test();
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static void test()
    {
        final UniqueFilter  holeFilter = new UniqueFilter("%1$s");
        final UniqueFilter  flopFilter = new UniqueFilter();
        final UniqueFilter  turnFilter = new UniqueFilter();
        final UniqueFilter riverFilter = new UniqueFilter();

        CardEnum.traverseRivers(
                holeFilter, flopFilter, turnFilter, riverFilter,
                new Traverser<River>() {
                    public void traverse(River river) {}
                });

        System.out.println("Hole Gapper Status:");
        holeFilter.gapper().displayStatus();

        System.out.println("Flop Gapper Status:");
        flopFilter.gapper().displayStatus();

        System.out.println("Turn Gapper Status:");
        turnFilter.gapper().displayStatus();

        System.out.println("River Gapper Status:");
        riverFilter.gapper().displayStatus();
    }
}
