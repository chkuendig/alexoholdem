package ao.ai.opp_model.decision.random;

import ao.ai.opp_model.decision.classification.processed.Frequency;
import ao.ai.opp_model.decision.input.processed.attribute.Attribute;
import ao.ai.opp_model.decision.input.processed.attribute.Continuous;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import ao.ai.opp_model.decision.input.processed.example.LocalContext;
import ao.ai.opp_model.decision.input.processed.example.LocalExample;
import ao.ai.opp_model.decision.input.processed.example.LocalLearningSet;
import ao.util.text.Txt;

import java.util.ArrayList;
import java.util.Collections;
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
                new ArrayList<Attribute>(
                        ls.contextAttributes());
        Collections.shuffle( attributeList );

        int length = (int)(attributeList.size() * 0.55 + 0.99);
        assert length >= 1;

        Attribute attributes[] = new Attribute[ length ];
        for (int i = 0; i < length; i++)
        {
            attributes[ i ] = attributeList.get( i );
        }

        return new RandomTree(attributes, 0);
    }

    
    //--------------------------------------------------------------------
    private Attribute  split; // split on
    private LocalDatum check; // from parent's split

//    private RandomTree parent;
    private RandomTree child;
    private RandomTree sibling;

//    private int dataLabeled;
    private Frequency hist;
    private int       sampleSize;


    //--------------------------------------------------------------------
    public RandomTree() {}

    private RandomTree(Attribute attributes[], int atIndex)
    {
        splitNext(attributes, atIndex);
    }
    private void splitNext(Attribute attributes[], int atIndex)
    {
        if (atIndex >= attributes.length) return;

        split = attributes[atIndex].randomView();
        if (! split.isSingleUse())
        {
            if (! ((Continuous) split).isWellInformed())
            {
                splitNext(attributes, atIndex + 1);
                return;
            }
        }

        RandomTree prevChild = null;
        for (LocalDatum datum : split.partition())
        {
            RandomTree subTree =
                    new RandomTree(attributes, atIndex + 1);
            subTree.check      = datum;

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
            getHistogram().add( exampleContext.target() );

            sampleSize++;
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

                sampleSize++;
                return true;
            }
        }

        // if we're here then the random tree must be rebuilt
//        rebuildSubtree();
//        updateSatistic(exampleContext);
        return false;
    }

    private Frequency getHistogram()
    {
        if (hist == null)
        {
            hist = new Frequency();
        }
        return hist;
    }


    //--------------------------------------------------------------------
    public Frequency frequencyAtLeaf(
            LocalContext exampleContext)
    {
        if (split == null) return getHistogram();

        LocalDatum splitVal = exampleContext.datumOfType( split );
        for (RandomTree kid  = child;
                        kid != null;
                        kid  = kid.sibling)
        {
            if (kid.check.contains( splitVal ))
            {
                return kid.frequencyAtLeaf(exampleContext);
            }
        }
        return new Frequency();
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
