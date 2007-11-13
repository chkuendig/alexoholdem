package ao.ai.opp_model.decision.attribute;

/**
 *
 */
public abstract class TypedAttribute implements Attribute
{
    //--------------------------------------------------------------------
    private final String TYPE;


    //--------------------------------------------------------------------
    public TypedAttribute(String type)
    {
        assert type != null;

        TYPE = type;
    }


    //--------------------------------------------------------------------
    public boolean typeEquals(Attribute availAttr)
    {
        return TYPE.equals( ((TypedAttribute) availAttr).TYPE );
    }

    public String type()
    {
        return TYPE;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return TYPE;
    }
}
