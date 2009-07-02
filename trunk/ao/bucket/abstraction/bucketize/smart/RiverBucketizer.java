package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.abstraction.access.tree.BucketList;
import ao.bucket.index.detail.river.compact.CompactProbabilityCounts;
import ao.bucket.index.detail.river.compact.CompactRiverProbabilities;
import ao.unsupervised.cluster.analysis.ClusterAnalysis;
import ao.unsupervised.cluster.analysis.KMeans;
import ao.unsupervised.cluster.error.TwoPassWcss;
import ao.unsupervised.cluster.space.central_tendency.Mean;
import ao.unsupervised.cluster.space.impl.RealDomain;
import ao.unsupervised.cluster.trial.Clustering;
import ao.unsupervised.cluster.trial.ParallelTrial;

/**
 * User: alex
 * Date: 2-Jun-2009
 * Time: 4:00:31 PM
 */
public class RiverBucketizer
{
    //--------------------------------------------------------------------
    public static void main(String[] args) {
        new RiverBucketizer().bucketize(null);
    }


    //--------------------------------------------------------------------
    public RiverBucketizer() {}


    //--------------------------------------------------------------------
    public void bucketize(BucketList into)
    {
        RealDomain domain = new RealDomain();
        for (char prob = 0;
                  prob < CompactRiverProbabilities.COUNT;
                  prob++)
        {
            domain.add((double) prob / CompactRiverProbabilities.COUNT,
                    CompactProbabilityCounts.countOf(prob));
        }
        domain.normalize();

        ClusterAnalysis<Mean> analyzer = new KMeans<Mean>();
        for (byte nClusters = 1; nClusters <= 50; nClusters++)
        {
            Clustering clustering =
                new ParallelTrial<Mean>(
                        new KMeans<Mean>(), new TwoPassWcss<Mean>(), 512
                ).cluster(domain, nClusters);
            System.out.println(nClusters + "\t" + clustering.error());

//            byte   clusters[] = analyzer.cluster(domain, nClusters);
//            double error      = new TwoPassWcss<Mean>().error(
//                    domain, clusters, nClusters);
//            System.out.println(nClusters + "\t" + error);
        }
    }
}
