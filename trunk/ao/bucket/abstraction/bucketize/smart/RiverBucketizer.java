package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.index.detail.CanonRange;
import ao.bucket.index.detail.river.RiverEvalLookup;
import ao.bucket.index.detail.river.StrengthCode;
import ao.odds.agglom.hist.RiverStrengths;

/**
 * User: alex
 * Date: 2-Jun-2009
 * Time: 4:00:31 PM
 */
public class RiverBucketizer
{
    //--------------------------------------------------------------------
    public RiverBucketizer()
    {
        
    }


    //--------------------------------------------------------------------
    public void bucketize(
            CanonRange riverRanges[],
            BucketList into)
    {
        int byStrength[] = byStrength(riverRanges);
        
    }


    //--------------------------------------------------------------------
    private int[] byStrength(CanonRange riverRanges[])
    {
        final int byStrength[] = new int[ RiverStrengths.COUNT ];
        RiverEvalLookup.traverse(
                riverRanges,
                new RiverEvalLookup.VsRandomVisitor() {
                    public void traverse(
                            long   canonIndex,
                            double strengthVsRandom,
                            byte   represents) {
                        byStrength[ StrengthCode.encodeWinProb(
                                        strengthVsRandom) ] += represents;
                    }
                }
        );
        return byStrength;
    }

}
