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
    public static int abOffsetA(IsoFlop isoFlop)
    {
        return aOffsetA(isoFlop) +
               bOffsetA(isoFlop);
    }

    public static int abOffsetB(IsoFlop isoFlop)
    {
        return aOffsetB(isoFlop) +
               bOffsetB(isoFlop);
    }

    public static int abOffsetC(IsoFlop isoFlop)
    {
        return aOffsetC(isoFlop) +
               bOffsetC(isoFlop);
    }


    //--------------------------------------------------------------------
    public static int bOffsetA(IsoFlop isoFlop)
    {
        return (isoFlop.holeB().ordinal() <
                isoFlop.flopA().ordinal()) ? -1 : 0;
    }

    public static int bOffsetB(IsoFlop isoFlop)
    {
        return (isoFlop.holeB().ordinal() <
                isoFlop.flopB().ordinal()) ? -1 : 0;
    }

    public static int bOffsetC(IsoFlop isoFlop)
    {
        return (isoFlop.holeB().ordinal() <
                isoFlop.flopC().ordinal()) ? -1 : 0;
    }


    //--------------------------------------------------------------------
    public static int aOffsetA(IsoFlop isoFlop)
    {
        return (isoFlop.holeA().ordinal() <
                isoFlop.flopA().ordinal()) ? -1 : 0;
    }

    public static int aOffsetB(IsoFlop isoFlop)
    {
        return (isoFlop.holeA().ordinal() <
                isoFlop.flopB().ordinal()) ? -1 : 0;
    }

    public static int aOffsetC(IsoFlop isoFlop)
    {
        return (isoFlop.holeA().ordinal() <
                isoFlop.flopC().ordinal()) ? -1 : 0;
    }
}
