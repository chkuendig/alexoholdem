package ao.ai.opp_model.decision.random;

import ao.ai.opp_model.decision.classification.processed.Frequency;
import ao.ai.opp_model.decision.input.processed.attribute.Attribute;
import ao.ai.opp_model.decision.input.processed.attribute.Continuous;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import ao.ai.opp_model.decision.input.processed.example.LocalContext;
import ao.ai.opp_model.decision.input.processed.example.LocalExample;
import ao.ai.opp_model.decision.input.processed.example.LocalLearningSet;
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
    public static RandomTree nextRandom(LocalLearningSet ls)
    {
        if (ls.isEmpty()) return new RandomTree();

        List<Attribute> attributeList =
//                new ArrayList<Attribute>(
                        ls.contextAttributes();//);
        //Collections.shuffle( attributeList );

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
    private LocalDatum check; // from parent's split

//    private RandomTree parent;
    private RandomTree child;
    private RandomTree sibling;

//    private int dataLabeled;
    private Frequency hist;


    //--------------------------------------------------------------------
    public RandomTree() {}

    private RandomTree(Attribute attributes[], int remainDepth)
    {
        splitNext(attributes, remainDepth);
    }
    private void splitNext(Attribute attributes[], int remainDepth)
    {
        if (remainDepth < 0 || attributes.length == 0) return;

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
            if (((Continuous) split).isWellInformed())
            {
                childAttributes = attributes.clone();
                childAttributes[splitIndex] = split;
            }
            else
            {
                // need to remove nasty duplication with above
                childAttributes =
                    Arrays.copyOf(attributes,
                                  attributes.length - 1);

                if (splitIndex != attributes.length - 1)
                {
                    childAttributes[splitIndex] =
                            attributes[attributes.length - 1];
                }
                splitNext(childAttributes, remainDepth - 1);
            }
        }

        RandomTree prevChild = null;
        for (LocalDatum datum : split.partition())
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
    public boolean updateSatistic(LocalExample exampleContext)
    {
        if (split == null)
        {
            if (hist == null)
            {
                hist = new Frequency();
            }
            hist.add( exampleContext.target() );
            return true;
        }

        LocalDatum splitVal = exampleContext.datumOfType( split );
        for (RandomTree kid  = child;
                        kid != null;
                        kid  = kid.sibling)
        {
            if (kid.check.contains( splitVal ))
            {
                kid.updateSatistic(exampleContext);
                return true;
            }
        }

        // if we're here then the random tree must be rebuilt
//        rebuildSubtree();
//        updateSatistic(exampleContext);
        return false;
    }


    //--------------------------------------------------------------------
    public double proportionAtLeaf(
            LocalContext exampleContext,
            LocalDatum ofTarget)
    {
        if (split == null)
        {
            return (hist == null)
                    ? 0
                    : hist.probabilityOf( ofTarget );
        }

        LocalDatum splitVal = exampleContext.datumOfType( split );
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
