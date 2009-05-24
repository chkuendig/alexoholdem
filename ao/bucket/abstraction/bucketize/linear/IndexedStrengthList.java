package ao.bucket.abstraction.bucketize.linear;

import ao.bucket.abstraction.access.tree.BucketTree;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.bucket.index.detail.turn.TurnDetails;
import ao.holdem.model.Round;
import ao.util.math.Calc;

import java.util.Arrays;

/**
 * User: alex
 * Date: 15-May-2009
 * Time: 5:47:00 PM
 */
public class IndexedStrengthList
{
    //--------------------------------------------------------------------
    public static IndexedStrengthList
            strengths(BucketTree.Branch branch)
    {
        if (branch.round() == Round.RIVER) {
            return strengthsRiver(branch);
        } else {
            return strengthsPreRiver(branch);
        }
    }

    private static IndexedStrengthList
            strengthsRiver(BucketTree.Branch branch)
    {
        int        nRivers       = 0;
        CanonRange toBucketize[] =
                new CanonRange[ branch.parentCanons().length ];
        for (int i = 0; i < branch.parentCanons().length; i++) {

            int canonTurn = branch.parentCanons()[i];
            toBucketize[ i ] = TurnDetails.lookup(canonTurn).range();
            nRivers += toBucketize[ i ].canonIndexCount();
        }
        Arrays.sort(toBucketize);

        final int                 nextIndex[] = {0};
        final IndexedStrengthList rivers =
                new IndexedStrengthList( nRivers );
        RiverEvalLookup.traverse(
                toBucketize,
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long canonIndex, double strengthVsRandom) {

                        rivers.set(nextIndex[0]++,
                                   canonIndex, strengthVsRandom);
                    }
                });

        return rivers;
    }

    private static IndexedStrengthList
            strengthsPreRiver(BucketTree.Branch branch)
    {
        CanonDetail details[] = branch.details();
        IndexedStrengthList strengths =
                new IndexedStrengthList(details.length);

        for (int i = 0; i < details.length; i++) {
            strengths.set(i,
                          details[ i ].canonIndex(),
                          details[ i ].strength());
        }

        return strengths;
    }


    //--------------------------------------------------------------------
    private final int  index   [];
    private final char strength[];


    //--------------------------------------------------------------------
    public IndexedStrengthList(int length) {
        index    = new int [ length ];
        strength = new char[ length ];
    }


    //--------------------------------------------------------------------
    public int length() {
        return index.length;
    }


    //--------------------------------------------------------------------
    public void set(int i, long canonIndex, double handStrength) {
        index   [i] = (int) canonIndex;
        strength[i] = (char)(Character.MAX_VALUE * handStrength);
    }


    //--------------------------------------------------------------------
    public long index(int i) {
        return Calc.unsigned(index[i]);
    }

    public char strength(int i)
    {
        return strength[ i ];
    }
}
