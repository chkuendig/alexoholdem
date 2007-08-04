package ao.holdem.bots.opp_model.neat;

/**
 * 
 */
public class Node
{
    //--------------------------------------------------------------------
    private static volatile int nextId;


    //--------------------------------------------------------------------
    private final int id;


    //--------------------------------------------------------------------
    public Node()
    {
        id = nextId++;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return String.valueOf( id );
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }
}
