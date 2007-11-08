package ao.ai.opp_model.decision2.attribute;

import ao.ai.opp_model.decision2.data.Datum;

import java.util.Collection;

/**
 *
 */
public interface Attribute
{
    // true if both this and the given attribute measure the same thing.
    // for multistate attributes, this determines weather or not
    //  they are the same object.
    // for continuous attributes, this will check if they are two
    //  cuts of the same attribute.
    public boolean typeEquals(Attribute availAttr);
    public String  type();


    public boolean isSingleUse();

    public Collection<? extends Datum> partition();

    public Collection<? extends Attribute> views();
    public Attribute randomView();
    public double viewChoiceLength();
}
