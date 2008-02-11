package ao.ai.supervised.decision.tree;

import ao.ai.supervised.decision.input.processed.attribute.Attribute;
import ao.ai.supervised.decision.classification.processed.Classification;
import ao.ai.supervised.decision.input.processed.data.LocalDatum;
import ao.ai.supervised.decision.input.processed.data.ValueRange;
import ao.ai.supervised.decision.input.processed.example.LocalContext;
import ao.ai.supervised.decision.input.processed.example.LocalLearningSet;
import ao.util.stats.Info;
import ao.util.text.Txt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class GeneralTree
{
    //--------------------------------------------------------------------
    private static final double ALPHA       = 0.3;
    private static final double DATA_WEIGHT = 1.0;


    //--------------------------------------------------------------------
    private LocalLearningSet      data;
    private Collection<Attribute> availAttrs;
    private Attribute             split; // split on
    private LocalDatum            check; // from parent's split

    private GeneralTree parent;
    private GeneralTree child;
    private GeneralTree sibling;


    //--------------------------------------------------------------------
    public GeneralTree(LocalLearningSet learningSet)
    {
        this(null, learningSet, learningSet.contextAttributes());
    }

    private GeneralTree(LocalDatum parentSplitVal,
                        LocalLearningSet learningSet,
                        Collection<Attribute> availableAttributs)
    {
        check      = parentSplitVal;
        data       = learningSet;
        availAttrs = availableAttributs;
    }


    //--------------------------------------------------------------------
    public Collection<Attribute> availableAttributes()
    {
        return availAttrs;
    }

    public Collection<GeneralTree> leaves()
    {
        List<GeneralTree> leaves = new ArrayList<GeneralTree>();
        addLeavesTo(leaves);
        return leaves;
    }
    private void addLeavesTo(Collection<GeneralTree> leaves)
    {
        if (isLeaf()) leaves.add( this );

        for (GeneralTree kid  = child;
                         kid != null;
                         kid  = kid.sibling)
        {
            kid.addLeavesTo( leaves );
        }
    }

    private int numKids()
    {
        int kids = 0;
        for (GeneralTree kid  = child;
                         kid != null;
                         kid  = kid.sibling) kids++;
        return kids;
    }


    //--------------------------------------------------------------------
    public void split(Attribute on)
    {
        unsplit();

        Collection<Attribute> baseSubAvailAttrs =
                new ArrayList<Attribute>( availAttrs.size() );
        for (Attribute availAttr : availAttrs)
        {
            if (! on.typeEquals(availAttr))
            {
                baseSubAvailAttrs.add( availAttr );
            }
        }

        split = on;
        GeneralTree prevChild = null;
        for (Map.Entry<LocalDatum, LocalLearningSet> splinter :
                data.split(on).entrySet())
        {
            Collection<Attribute> subAvailAttrs;
            if (on.isSingleUse())
            {
                subAvailAttrs = baseSubAvailAttrs;
            }
            else
            {
                subAvailAttrs =
                        new ArrayList<Attribute>(
                                baseSubAvailAttrs);
                subAvailAttrs.add(
                        ((ValueRange) splinter.getKey()).asAttribute());
            }

            GeneralTree subTree =
                    new GeneralTree(splinter.getKey(),
                                    splinter.getValue(),
                                    subAvailAttrs);

            subTree.parent = this;
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

    public void unsplit()
    {
        split = null;
        child = null;
    }


    //--------------------------------------------------------------------
    public double messageLength()
    {
        assert data != null : "cannot count length of frozen tree";
        return codingComplexity(
                        data.contextAttributes().size());
    }

    public double codingComplexity(int numAttributes)
    {
        return typeLength(numAttributes) +
                (isInternal()
                 ? attributeAndChildLength(numAttributes)
                 : data.classify().transmissionCost(ALPHA)
                    * DATA_WEIGHT);
    }

    private double attributeAndChildLength(int availAttributes)
    {
        int nextAvailAttrs =
                nextAvailableAttributes(availAttributes);

        double childComplexity = 0;
        for (GeneralTree kid  = child;
                         kid != null;
                         kid  = kid.sibling)
        {
            childComplexity +=
                    kid.codingComplexity( nextAvailAttrs );
        }

        return branchChoiceLength(availAttributes) +
                childComplexity;
    }

    // A discrete attribute cannot be tested by more than
    //  one branch node in any path,  If a branch tests a
    //  discrete attribute, A is decremented for each of
    //  the child trees.
    private int nextAvailableAttributes(int availAttributes)
    {
        return split.isSingleUse()
                ? availAttributes - 1
                : availAttributes;
    }

    // For each 'branch' codeward, we need to sate which of the
    //  input attributes will be tested.  This requires
    //  lg(A) bits, where A is the number of attributes able to
    //  be tested at that branch.
    // If the input attribute to be tested is continuous, we also
    //  need to state the cut-point to be used.
    private double branchChoiceLength(int availAttributes)
    {
        return Info.log2( availAttributes ) +
                split.viewChoiceLength();
    }

    // For each node we firstly need to state whether
    //  it is a branch of a leaf.
    private double typeLength(int numAttributes)
    {
        if (isRoot())
        {
            // At the root of the tree:
            //   * a branch costs lg(n+1) - lg(n) bits
            //	 * a leaf   costs lg(n+1)         bits
            // where n is the number of input attributes.

            double log2nPlusOne = Info.log2(numAttributes + 1);
            return isInternal()
                    ? log2nPlusOne - Info.log2(numAttributes)
                    : log2nPlusOne;
        }
        else
        {
            // For any non-root node, the codeward:
            //	 * a branch requires lg(s) bits
            //	 * a leaf requires -lg((s - 1)/s) bits
            // where s represents the number of childred of
            //  the node's parent (always greater than 1).

            int parentKids = parent.numKids();
            return isInternal()
                    ?  Info.log2(parentKids)
                    : -Info.log2((double)(parentKids - 1) /
                                            parentKids);
        }
    }


    //--------------------------------------------------------------------
    private boolean isRoot()
    {
        return parent == null;
    }

    private boolean isLeaf()
    {
        return child == null;
    }

    private boolean isInternal()
    {
        return !isLeaf();
    }


    //--------------------------------------------------------------------
    public Classification classify(LocalContext exampleContext)
    {
        if (split == null) return data.classify();

        LocalDatum splitVal = exampleContext.datumOfType( split );
        for (GeneralTree kid  = child;
                         kid != null;
                         kid  = kid.sibling)
        {
            if (kid.check.contains( splitVal ))
            {
                return kid.classify( exampleContext );
            }
        }
        return data.classify();
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        if (split == null) return data.classify().toString();

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

        for (GeneralTree kid  = child;
                         kid != null;
                         kid  = kid.sibling)
        {
            buf.append(Txt.nTimes("\t", depth + 1));
            buf.append("+").append( kid.check );

            if (kid.isLeaf())
            {
                buf.append(" ");
                buf.append( kid.data.classify() );
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
