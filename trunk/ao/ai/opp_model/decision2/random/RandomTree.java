package ao.ai.opp_model.decision2.random;

import ao.ai.opp_model.decision2.attribute.Attribute;
import ao.ai.opp_model.decision2.classification.Histogram;
import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.example.Context;
import ao.ai.opp_model.decision2.example.LearningSet;
import ao.ai.opp_model.decision2.example.Example;
import ao.util.rand.Rand;
import ao.util.text.Txt;

import java.util.Arrays;
import java.util.List;

/**
 * 
 */
public class RandomTree
{
    //--------------------------------------------------------------------
    public static RandomTree nextRandom(LearningSet ls)
    {
        if (ls.isEmpty()) return new RandomTree();

        List<Attribute> attributeList = ls.contextAttributes();
        Attribute attributes[] =
                    attributeList.toArray(
                            new Attribute[ attributeList.size() ]);

//        return new RandomTree(attributes,
//                             (int)(attributes.length / 2 + 0.99));
//        return new RandomTree(attributes, attributes.length);
        return new RandomTree(attributes,
                             (int)(attributes.length * 0.6 + 0.99));
    }

    
    //--------------------------------------------------------------------
    private Attribute split; // split on
    private Datum     check; // from parent's split

//    private RandomTree parent;
    private RandomTree child;
    private RandomTree sibling;

//    private int dataLabeled;
    private Histogram hist;


    //--------------------------------------------------------------------
    public RandomTree() {}

    private RandomTree(Attribute attributes[], int remainDepth)
    {
        if (remainDepth < 0) return;

        int splitIndex = Rand.nextInt(attributes.length);

        split = attributes[splitIndex].randomView();

        Attribute childAttributes[];
        if (split.isSingleUse())
        {
            childAttributes =
                Arrays.copyOf(attributes,
                              attributes.length - 1);
            
            if (splitIndex != attributes.length - 1)
            {
                childAttributes[splitIndex] =
                        attributes[attributes.length - 1];
            }
        }
        else
        {
            childAttributes = attributes.clone();
            childAttributes[splitIndex] = split;
        }

        RandomTree prevChild = null;
        for (Datum datum : split.partition())
        {
            RandomTree subTree =
                    new RandomTree(
                            childAttributes,
                            remainDepth - 1);
            subTree.check  = datum;
//            subTree.parent = this;

            if (prevChild == null)
            {
                child = subTree;
            }
            else
            {
                prevChild.sibling = subTree;
            }

            prevChild = subTree;
        }
    }


    //--------------------------------------------------------------------
    public void updateSatistic(Example exampleContext)
    {
        if (split == null)
        {
            if (hist == null)
            {
                hist = new Histogram();
            }
            hist.add( exampleContext.target() );
            return;
        }

        Datum splitVal = exampleContext.datumOfType( split );
        for (RandomTree kid  = child;
                        kid != null;
                        kid  = kid.sibling)
        {
            if (kid.check.contains( splitVal ))
            {
                kid.updateSatistic(exampleContext);
                return;
            }
        }
    }


    //--------------------------------------------------------------------
//    public Classification classify(Context context)
//    {
//        Datum splitVal = context.datumOfType( split );
//        for (RandomTree kid  = child;
//                        kid != null;
//                        kid  = kid.sibling)
//        {
//            if (kid.check.contains( splitVal ))
//            {
//                return kid.classify( context );
//            }
//        }
//        return data.classify();
//    }


    //--------------------------------------------------------------------
    public double proportionAtLeaf(
            Context exampleContext,
            Datum   ofTarget)
    {
        if (split == null)
        {
            return (hist == null)
                    ? 0
                    : hist.probabilityOf( ofTarget );
        }

        Datum splitVal = exampleContext.datumOfType( split );
        for (RandomTree kid  = child;
                        kid != null;
                        kid  = kid.sibling)
        {
            if (kid.check.contains( splitVal ))
            {
                return kid.proportionAtLeaf(
                                exampleContext, ofTarget);
            }
        }
        return 0;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        if (split == null) return "emty tree: " + hist;

        StringBuilder b = new StringBuilder();
        appendTree(1, b);
        return b.toString();
    }

    private void appendTree(int depth, StringBuilder buf)
    {
		if (split == null) return;

        buf.append(Txt.nTimes("\t", depth));
        buf.append("***");
        buf.append(split).append("\n");

        for (RandomTree kid  = child;
                        kid != null;
                        kid  = kid.sibling)
        {
            buf.append(Txt.nTimes("\t", depth + 1));
            buf.append("+").append( kid.check );

            if (kid.child == null)
            {
                buf.append(" ");
                buf.append( kid.hist);
                buf.append("\n");
            }
            else
            {
                buf.append("\n");
                kid.appendTree(depth + 1, buf);
            }
        }
	}
}
