package ao.bucket.abstraction.bucketize.smart;

import ao.bucket.index.detail.preflop.HoleOdds;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import ao.graph.Graph;
import ao.graph.impl.common.SimpleAbsDomain;
import ao.graph.impl.fast.BufferedFastGraph;
import ao.graph.struct.DataAndWeight;
import ao.graph.struct.Endpoints;
import ao.graph.struct.NodeDataPair;
import ao.graph.user.EdgeWeight;
import ao.graph.user.NodeData;
import ao.odds.agglom.Odds;

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

        Graph<HoleNodeData, OddsEdgeWeight> relations =
                new BufferedFastGraph
                        <HoleNodeData, OddsEdgeWeight>(
                        new SimpleAbsDomain<OddsEdgeWeight>(64),
                        OddsEdgeWeight.NILL);
        for (int i = 0; i < holes.length; i++) {
            relations.add( holes[i] );

            for (int j = 0; j < i; j++) {
                relations.join(
                        holes[i], holes[j],
                        new OddsEdgeWeight(
                                HoleOdds.lookup(i),
                                HoleOdds.lookup(j)));
            }
        }

        HoleNodeData root = agglomerativeClusterAnalysis(relations);
        System.out.println(root);
//        root.
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
        private final HoleNodeData A, B;

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

        @Override public String toString() {
            return (LEAF != null)
                   ? LEAF.toString()
                   : "[" + A + "|" + B +"]";
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

    public static class OddsEdgeWeight
            implements EdgeWeight<OddsEdgeWeight>
    {
        public static final OddsEdgeWeight NILL =
                new OddsEdgeWeight(new Odds(), new Odds());

        private final Odds A, B;

        public OddsEdgeWeight(Odds a, Odds b)
        {
            A = a;
            B = b;
        }

        public OddsEdgeWeight mergeWith(OddsEdgeWeight oddsEdgeWeight)
        {
            return new OddsEdgeWeight(
                    A.plus(oddsEdgeWeight.A),
                    B.plus(oddsEdgeWeight.B));
        }

        public float asFloat()
        {
            return (float) Math.sqrt(
                    Math.pow(A.winPercent()   - B.winPercent(),   2) +
                    Math.pow(A.losePercent()  - B.losePercent(),  2) +
                    Math.pow(A.splitPercent() - B.splitPercent(), 2));
        }
    }
}
