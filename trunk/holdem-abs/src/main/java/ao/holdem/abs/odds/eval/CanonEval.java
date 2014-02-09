package ao.holdem.abs.odds.eval;

import ao.holdem.abs.odds.agglom.OddFinder;
import ao.holdem.abs.odds.agglom.hist.CompactRiverStrengths;
import ao.holdem.abs.odds.eval.eval7.Eval7Faster;
import ao.holdem.canon.river.River;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 *
 */
public class CanonEval
{

    //--------------------------------------------------------------------
    public static short eval(River river)
    {
        Hole h = river.turn().hole().reify();
        Community c = river.turn().community();
        short     v = Eval7Faster.valueOf(
                h.a(), h.b(),
                c.flopA(), c.flopB(), c.flopC(),
                c.turn(),
                river.riverRealCard());
        return CompactRiverStrengths.compact(v);
    }
    public static double vsRandom(River river, OddFinder odds)
    {
        Hole      h = river.turn().hole().reify();
        Community c = river.turn().community();

        return odds.compute(
                h, c.addRiver(river.riverRealCard()), 1
        ).strengthVsRandom();
    }

}
