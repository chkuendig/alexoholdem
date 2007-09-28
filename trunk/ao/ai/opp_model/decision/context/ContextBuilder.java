package ao.ai.opp_model.decision.context;

import ao.ai.opp_model.decision.data.ContextImpl;
import ao.ai.opp_model.decision.attr.Attribute;

import java.util.EnumSet;
import java.util.Collection;
import java.util.ArrayList;

/**
 *
 */
public class ContextBuilder
        extends    ContextImpl
        implements HoldemContext
{
    //--------------------------------------------------------------------
    private EnumSet<ContextDomain> domains =
            EnumSet.noneOf(ContextDomain.class);


    //--------------------------------------------------------------------
    public ContextBuilder() {}
    public ContextBuilder(Collection<Attribute> attributes)
    {
        super( attributes );
    }
    public ContextBuilder(HoldemContext context)
    {
        super( context.attributes() );
        for (ContextDomain domain : ContextDomain.values())
        {
            if (context.isApplicableTo( domain ))
            {
                domains.add( domain );
            }
        }
    }


    //--------------------------------------------------------------------
    public void addDomain(ContextDomain domain)
    {
        domains.add( domain );
    }
    public void addDomains(ContextDomain... addends)
    {
        for (ContextDomain domain : addends)
        {
            addDomain( domain );
        }
    }


    //--------------------------------------------------------------------
    public void add(Attribute<?> attr)
    {
        super.add( attr );
    }

    public ContextBuilder merge(HoldemContext with)
    {
        Collection<Attribute> attributes = new ArrayList<Attribute>();
        attributes.addAll( attributes() );
        attributes.addAll( with.attributes() );

        ContextBuilder merged = new ContextBuilder(attributes);

        merged.domains = domains.clone();
        for (ContextDomain domain : merged.domains)
        {
            if (! with.isApplicableTo( domain ))
            {
                merged.domains.remove( domain );
            }
        }

        return merged;
    }


    //--------------------------------------------------------------------
    public boolean isApplicableTo(ContextDomain domain)
    {
        return domains.contains( domain );
    }


    //--------------------------------------------------------------------
//    public String toString()
//    {
//        return super.toString();
//    }
}
