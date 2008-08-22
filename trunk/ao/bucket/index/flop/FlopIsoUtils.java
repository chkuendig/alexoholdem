package ao.bucket.index.flop;

import ao.bucket.index.iso_cards.IsoFlop;
import static ao.util.stats.Combo.colex;

/**
 * Created by IntelliJ IDEA.
 * User: iscott
 * Date: Aug 21, 2008
 * Time: 8:20:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlopIsoUtils
{
    //--------------------------------------------------------------------
    private FlopIsoUtils() {}


    //--------------------------------------------------------------------
    public static int sortColex(int x, int y)
    {
        return x < y ? colex(x, y) : colex(y, x);
    }


    //--------------------------------------------------------------------
    public static int fullOffsetA(IsoFlop isoFlop)
    {
        return offsetA(isoFlop) +
                ((isoFlop.holeB().ordinal() <
                  isoFlop.flopA().ordinal()) ? -1 : 0);
    }

    public static int fullOffsetB(IsoFlop isoFlop)
    {
        return offsetB(isoFlop) +
                ((isoFlop.holeB().ordinal() <
                  isoFlop.flopB().ordinal()) ? -1 : 0);
    }

    public static int fullOffsetC(IsoFlop isoFlop)
    {
        return offsetC(isoFlop) +
                ((isoFlop.holeB().ordinal() <
                  isoFlop.flopC().ordinal()) ? -1 : 0);
    }


    //--------------------------------------------------------------------
    public static int offsetA(IsoFlop isoFlop)
    {
        return (isoFlop.holeA().ordinal() <
                isoFlop.flopA().ordinal()) ? -1 : 0;
    }

    public static int offsetB(IsoFlop isoFlop)
    {
        return (isoFlop.holeA().ordinal() <
                isoFlop.flopB().ordinal()) ? -1 : 0;
    }

    public static int offsetC(IsoFlop isoFlop)
    {
        return (isoFlop.holeA().ordinal() <
                isoFlop.flopC().ordinal()) ? -1 : 0;
    }
}
