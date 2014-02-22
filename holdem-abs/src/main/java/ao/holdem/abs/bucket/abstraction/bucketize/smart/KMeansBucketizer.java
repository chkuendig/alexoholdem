package ao.holdem.abs.bucket.abstraction.bucketize.smart;

import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.bucketize.def.ScalarBucketizer;
import ao.holdem.abs.bucket.abstraction.bucketize.error.HandStrengthMeasure;
import ao.holdem.abs.bucket.abstraction.bucketize.linear.IndexedStrengthList;
import ao.util.math.rand.MersenneTwisterFast;
import ao.util.time.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Date: 12-May-2009
 * Time: 8:51:59 PM
 */
public class KMeansBucketizer implements ScalarBucketizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            LoggerFactory.getLogger(KMeansBucketizer.class);


    //--------------------------------------------------------------------
    private static final double HEAVY_DELTA_CUTOFF = 0.0001;
    private static final int    HEAVY_BEST_OF      = 256;

    private static final double LIGHT_DELTA_CUTOFF = 0.01;
    private static final int    LIGHT_BEST_OF      = 2;


    //--------------------------------------------------------------------
    private double deltaCutoff;
    private int    bestOf;


    //--------------------------------------------------------------------
    public KMeansBucketizer()
    {
        setThorough( false );
    }


    //--------------------------------------------------------------------
    public double bucketize(
            BucketTree.Branch branch,
            int               numBuckets)
    {
        IndexedStrengthList strengths =
                IndexedStrengthList.strengths(branch);
        return bucketize(branch, strengths, numBuckets);
    }
    public double bucketize(
            BucketTree.Branch   branch,
            IndexedStrengthList details,
            int                 numBuckets)
    {
        MersenneTwisterFast rand =
                new MersenneTwisterFast(
                        branch.parentCanons().length *
                        details.length() * numBuckets);

        long seeds[] = new long[ bestOf ];
        for (int i = 0; i < seeds.length; i++) {
            seeds[ i ] = rand.nextLong();
        }

        double maxErr      = Double.NEGATIVE_INFINITY;
        double minErr      = Double.POSITIVE_INFINITY;
        int    minErrIndex = -1;

        HandStrengthMeasure errorMeasure = new HandStrengthMeasure();
        for (int i = 0; i < bestOf; i++) {
            rand.setSeed(seeds[i]);

            bucketize(branch, details, numBuckets, rand, false);
            double err = errorMeasure.error(branch, details, numBuckets);
            LOG.debug("Error {}: {}", i + 1, err);

            if (err < minErr) {
                minErr      = err;
                minErrIndex = i;
            }
            if (err > maxErr) {
                maxErr = err;
            }
        }

        rand.setSeed(seeds[ minErrIndex ]);
        bucketize(branch, details, numBuckets, rand, true);
//        LOG.debug("error range: " + minErr + " .. " + maxErr +
//                    " = " + ((maxErr - minErr) / maxErr) * 100 + "%");
        return minErr;
    }
    private void bucketize(
            BucketTree.Branch   branch,
            IndexedStrengthList strengths,
            int                 numBuckets,
            MersenneTwisterFast rand,
            boolean             verbose)
    {
        Stopwatch time    = new Stopwatch();
        double    means[] = initMeans(strengths, numBuckets, rand);

        int counts  [] = new int[numBuckets];
        int clusters[] = cluster(means, strengths);
        for (int i = 0; i < clusters.length; i++) {
            branch.set(strengths.index(i),
                       clusters[i]);

            counts[ clusters[i] ]++;
        }

        if (! verbose) return;
        LOG.info("bucketized " + branch.round() +
                " into " + numBuckets +
                "\t(p " + branch.parentCanons().length +
                " \tc " + strengths.length() +
                ")\t" + Arrays.toString(counts) +
                "\ttook " + time + " per " + bestOf + " trials");
    }


    //--------------------------------------------------------------------
    private int[] cluster(
            double              means[],
            IndexedStrengthList strengths)
    {
        int clusters[] = new int[ strengths.length() ];

        int iterationCount = 0;
        double delta;
        do
        {
            delta = iterateKMeans(means, strengths, clusters);
            iterationCount++;
            LOG.debug("KMeans iteration delta {}: {}", iterationCount, delta);
        }
        while (delta != 0 && (delta > deltaCutoff || iterationCount < 5));

        return clusters;
    }

    // see http://en.wikipedia.org/wiki/K-means
    private double iterateKMeans(
            double              means[],
            IndexedStrengthList strengths,
            int                 clusters[])
    {
        assignmentStep(means, strengths, clusters);
        return updateStep(means, strengths, clusters);
    }


    //--------------------------------------------------------------------
    // Assign each observation to the cluster with the closest mean
    //  (i.e. partition the observations according to the
    //          Voronoi diagram generated by the means).
    private void assignmentStep(
            double              means[],
            IndexedStrengthList strengths,
            int                 clusters[])
    {
        for (int i = 0; i < strengths.length(); i++) {
            double strength = strengths.realStrength(i);

            byte   leastDistIndex = -1;
            double leastDistance  = Double.POSITIVE_INFINITY;
            for (byte j = 0; j < means.length; j++) {

                double distance = Math.abs(strength - means[j]);
                if (leastDistance > distance) {
                    leastDistance  = distance;
                    leastDistIndex = j;
                }
            }

            clusters[ i ] = leastDistIndex;
        }
    }

    
    //--------------------------------------------------------------------
    // Calculate the new means to be the centroid of the
    //   observations in the cluster.
    private double updateStep(
            double              means[],
            IndexedStrengthList details,
            int                 clusters[])
    {
        double maxDelta = 0;
        for (int i = 0; i < means.length; i++) {

            double sum   = 0;
            int    count = 0;

            for (int j = 0; j < clusters.length; j++) {
                if (clusters[j] != i) continue;

                sum += details.realStrength(j) *
                       details.represents(j);
                count += details.represents(j);
            }

            double newMean = sum / count;
            double delta   = Math.abs(newMean - means[i]);
            if (maxDelta < delta) {
                maxDelta = delta;
            }
            means[i] = newMean;
        }
        return maxDelta;
    }


    //--------------------------------------------------------------------
    /*
     * see
     *   http://en.wikipedia.org/wiki/K-means%2B%2B
     */
    private double[] initMeans(
            IndexedStrengthList details,
            int                 nBuckets,
            MersenneTwisterFast rand)
    {
        int means[] = new int[ nBuckets ];

        // Choose one center uniformly at random
        //  from among the data points.
        means[0] = rand.nextInt(details.length());

        for (int k = 1; k < nBuckets; k++)
        {
            // For each data point x, compute D(x), the distance between
            //   x and the nearest center that has already been chosen.

            double maxChance      = Double.NEGATIVE_INFINITY;
            int    maxChanceIndex = -1;

            next_point:
            for (int i = 0; i < details.length(); i++) {

                double nearestCluster = Double.POSITIVE_INFINITY;
                for (int j = 0; j < k; j++) {
                    if (i == means[j]) continue next_point;

                    double dist = Math.abs(details.realStrength(i) -
                                           details.realStrength(means[j]));
                    if (nearestCluster > dist) {
                        nearestCluster = dist;
                    }
                }

                // Each point x is chosen with
                //  probability proportional to D(x)^2.
                double chance = rand.nextDouble()
                        * nearestCluster * nearestCluster
                        * details.represents(i);
                if (maxChance < chance) {
                    maxChance      = chance;
                    maxChanceIndex = i;
                }
            }

            // Add one new data point as a center.
            means[k] = maxChanceIndex;
        }

        double meanVals[] = new double[ means.length ];
        for (int i = 0; i < means.length; i++) {
            meanVals[ i ] = details.realStrength( means[i] );
        }
        return meanVals;
    }


    //--------------------------------------------------------------------
    public void setThorough(boolean highPrecision)
    {
        if (highPrecision)
        {
            bestOf      = HEAVY_BEST_OF;
            deltaCutoff = HEAVY_DELTA_CUTOFF;
        }
        else
        {
            bestOf      = LIGHT_BEST_OF;
            deltaCutoff = LIGHT_DELTA_CUTOFF;
        }
    }


    //--------------------------------------------------------------------
    public String id()
    {
        return "kmeans";
    }
}
