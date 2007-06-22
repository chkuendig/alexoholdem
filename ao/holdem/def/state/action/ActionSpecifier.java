package ao.holdem.def.state.action;

/**
 *
 */
public interface ActionSpecifier
{
    //--------------------------------------------------------------------
    public void checkOrFold();
    public void checkOrCall();
    public void raise();
}
