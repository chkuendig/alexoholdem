package ao.holdem.bots.opp_model.neat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 *
 */
public class Genome
{
    //--------------------------------------------------------------------
    private final Map<InnovationNumber, Connection> GENES;
    

    //--------------------------------------------------------------------
    public Genome()
    {
        GENES = new HashMap<InnovationNumber, Connection>();
    }

    private Genome(
            Map<InnovationNumber, Connection> genes,
            Connection... mutations)
    {
        GENES = new HashMap<InnovationNumber, Connection>(genes);

        for (Connection mutation : mutations)
        {
            GENES.put( mutation.innovationNumber(), mutation );
        }
    }


    //--------------------------------------------------------------------
    public Genome addConnection()
    {
        Map<Node, Set<Node>> matrix =
                new HashMap<Node, Set<Node>>();

        for (Connection c : GENES.values())
        {
            outSet(matrix, c.in()).add( c.out() );
        }

        return null;
    }

    private Set<Node> outSet(
            Map<Node, Set<Node>> matrix,
            Node                 in)
    {
        Set<Node> outSet = matrix.get(in);
        if (outSet == null)
        {
            outSet = new HashSet<Node>();
            matrix.put(in, outSet);
        }
        return outSet;
    }



    public Genome addNode()
    {
        return null;
    }

}
