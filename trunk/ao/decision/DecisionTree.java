package ao.decision;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;
import ao.decision.data.Context;
import ao.util.text.Txt;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DecisionTree</*A,*/ T>
{
    //--------------------------------------------------------------------
    private Map<Attribute<?>, DecisionTree<T>> nodes;
    private AttributeSet<?>                    attrSet;


    //--------------------------------------------------------------------
    public DecisionTree(AttributeSet<?> attributeSet)
    {
        nodes   = new HashMap<Attribute<?>, DecisionTree<T>>();
        attrSet = attributeSet;
    }


    //--------------------------------------------------------------------
    public void addNode(Attribute<?>    attribute,
                        DecisionTree<T> tree)
    {
        assert !nodes.containsKey( attribute );
        nodes.put(attribute, tree);
    }


    //--------------------------------------------------------------------
    // minumum number of bits needed to encode the graph.
    public double codingComplexity()
    {

        
        return 0;
    }


    //--------------------------------------------------------------------
    public Histogram<T> predict(Context basedOn)
    {
        Attribute<?>    attribute = basedOn.attribute(attrSet);
        DecisionTree<T> subTree   = nodes.get( attribute );
        if (subTree == null)
        {
            // predicting based on an attribute that was never seen before
            //  assume its the same as an arbitrary seen attribute.
            subTree = nodes.values().iterator().next();
        }
        return subTree.predict( basedOn );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        appendTree(1, b);
        return b.toString();
    }

    private void appendTree(int depth, StringBuilder buf)
    {
		if (attrSet != null)
        {
			buf.append(Txt.nTimes("\t", depth));
			buf.append("***");
            buf.append(attrSet.type()).append("\n");

            for (Attribute<?> attribute : nodes.keySet())
            {
				buf.append(Txt.nTimes("\t", depth + 1));
                buf.append("+").append(attribute);

				DecisionTree child = nodes.get(attribute);
                if (child instanceof DecisionLeaf)
                {
                    buf.append(" ");
                    buf.append( ((DecisionLeaf)child).frequencies() );
                    buf.append("\n");
                }
                else
                {
                    buf.append("\n");
                    child.appendTree(depth + 1, buf);
                }
			}
		}
	}
}
