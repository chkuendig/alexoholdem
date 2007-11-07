package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.example.ContextImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

/**
 *
 */
public class HoldemContext extends ContextImpl
{
    //--------------------------------------------------------------------
    private EnumSet<ContextDomain> domains =
            EnumSet.noneOf(ContextDomain.class);


    //--------------------------------------------------------------------
    public HoldemContext() {}
    public HoldemContext(Collection<Datum> attributes)
    {
        super( attributes );
    }
    public HoldemContext(HoldemContext context)
    {
        super( context.attributeData() );
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
    public void add(Datum attr)
    {
        super.add( attr );
    }

    public HoldemContext merge(HoldemContext with)
    {
        Collection<Datum> attributes = new ArrayList<Datum>();
        attributes.addAll( attributeData() );
        attributes.addAll( with.attributeData() );

        HoldemContext merged = new HoldemContext(attributes);

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
}
