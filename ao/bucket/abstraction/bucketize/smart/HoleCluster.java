package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.index.canon.hole.CanonHole;
import ao.bucket.index.canon.hole.HoleLookup;
import ao.bucket.index.detail.preflop.HoleOdds;
import ao.graph.Graph;
import ao.graph.common.RealEdgeWeight;
import ao.graph.impl.common.SimpleAbsDomain;
import ao.graph.impl.fast.BufferedFastGraph;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.Endpoints;
import ao.graph.struct.NodeDataPair;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;
import ao.util.math.rand.Rand;
import ao.util.text.Txt;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: Feb 3, 2009
 * Time: 12:18:53 PM
 */
public class HoleCluster
{
    //--------------------------------------------------------------------


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        HoleNodeData[] holes =
                new HoleNodeData[ HoleLookup.CANONS ];

        for (char i = 0; i < holes.length; i++) {
            holes[ i ] = new HoleNodeData(
                                HoleLookup.lookup(i));
        }

//        Graph<HoleNodeData, OddsEdgeWeight> relations =
//                new BufferedFastGraph
//                        <HoleNodeData, OddsEdgeWeight>(
//                        new SimpleAbsDomain<OddsEdgeWeight>(64),
//                        OddsEdgeWeight.NILL);
        Graph<HoleNodeData, RealEdgeWeight> relations =
                new BufferedFastGraph
                        <HoleNodeData, RealEdgeWeight>(
                        new SimpleAbsDomain<RealEdgeWeight>(64),
                        new RealEdgeWeight(0.0f));
        for (int i = 0; i < holes.length; i++) {
            relations.add( holes[i] );

            for (int j = 0; j < i; j++) {
                float a = (float) HoleOdds.lookup(i).strengthVsRandom();
                float b = (float) HoleOdds.lookup(i).strengthVsRandom();

                if (Rand.nextBoolean(5)) {
                    relations.join(
                            holes[i], holes[j],
                            new RealEdgeWeight(Math.abs(a - b)));
                }
            }
        }

        HoleNodeData root = agglomerativeClusterAnalysis(relations);
//        System.out.println(root);
        List<HoleNodeData> s = new  ArrayList<HoleNodeData>();
        split(root, s, 20);
        for (HoleNodeData n : s) {
            System.out.println( n.leafs() );
        }
    }

    private static int split(
            HoleNodeData       root,
            List<HoleNodeData> split,
            int                into) {
        if (into <= 1 || root.LEAF != null) {
            split.add(root);
            return 1;
        } else {
            int i = split(root.A, split, ((into + 1)/2));
            int j = split(root.B, split, (into - i));
            return i + j;
        }
    }

    //--------------------------------------------------------------------
    // doesn't work when there is only one cluster.
    private static <I extends NodeData<I>,
                    J extends EdgeWeight<J>> I
            agglomerativeClusterAnalysis(
                    Graph<I, J> graph)
    {
        //int n = 0;
        I root = null;
        while (true)
        {
            //System.out.println("n = " + (n++));
            Endpoints<I, J> mostRelatedClusters =
                    graph.nodesIncidentHeaviestEdge();

            NodeDataPair<I> toMerge;
            if (mostRelatedClusters == null)
            {
                toMerge = graph.antiEdge();

                if (toMerge == null && root == null)
                {
                    return null;
                }
            }
            else
            {
                toMerge = mostRelatedClusters.nodes();
            }

            if (toMerge == null) break;

            DataAndWeight<I, J> merged =
                    graph.merge( toMerge.dataA(), toMerge.dataB() );

            root = merged.data();
        }
        return root;
    }


    //--------------------------------------------------------------------
    public static class HoleNodeData implements NodeData<HoleNodeData>
    {
        private final CanonHole    LEAF;
        public  final HoleNodeData A, B;

        public HoleNodeData(CanonHole leaf) {
            LEAF = leaf;
            A    = null;
            B    = null;
        }
        private HoleNodeData(HoleNodeData a, HoleNodeData b) {
            LEAF = null;
            A    = a;
            B    = b;
        }

        public HoleNodeData mergeWith(HoleNodeData holeNodeData)
        {
            return new HoleNodeData(this, holeNodeData);
        }

        public String toString() {
            return toString(0);
        }
        public String toString(int depth) {
            return Txt.nTimes("\t", depth) +
                    ((LEAF != null)
                      ? LEAF.toString()
                      : leafs() + "\n" +
                         (A.toString(depth + 1) + "\n" +
                          B.toString(depth + 1)));
        }

        private List<CanonHole> leafs() {
            List<CanonHole> leafs = new ArrayList<CanonHole>();
            if (LEAF != null) {
                leafs.add(LEAF);
            } else {
                leafs.addAll(A.leafs());
                leafs.addAll(B.leafs());
            }
            return leafs;
        }


        @Override public boolean equals(Object o){
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HoleNodeData that = (HoleNodeData) o;
            return !(A != null ? !A.equals(that.A) : that.A != null) &&
                   !(B != null ? !B.equals(that.B) : that.B != null) &&
                   !(LEAF != null ? !LEAF.equals(that.LEAF)
                                  : that.LEAF != null);
        }

        @Override public int hashCode() {
            int result = LEAF != null ? LEAF.hashCode() : 0;
            result = 31 * result + (A != null ? A.hashCode() : 0);
            result = 31 * result + (B != null ? B.hashCode() : 0);
            return result;
        }
    }

//    public static class OddsEdgeWeight
//            implements EdgeWeight<OddsEdgeWeight>
//    {
//        public static final OddsEdgeWeight NILL =
//                new OddsEdgeWeight(new Odds(), new Odds());
//
//        private final Odds A, B;
//
//        public OddsEdgeWeight(Odds a, Odds b)
//        {
//            A = a;
//            B = b;
//        }
//
//        public OddsEdgeWeight mergeWith(OddsEdgeWeight oddsEdgeWeight)
//        {
//            return new OddsEdgeWeight(
//                    A.plus(oddsEdgeWeight.A),
//                    B.plus(oddsEdgeWeight.B));
//        }
//
//        public float asFloat()
//        {
//            return (float) Math.sqrt(
//                    Math.pow(A.winPercent()   - B.winPercent(),   2) +
//                    Math.pow(A.losePercent()  - B.losePercent(),  2) +
//                    Math.pow(A.splitPercent() - B.splitPercent(), 2));
//        }
//    }
}
