package ao.holdem.def.state.action;

/**
 *
 */
public class ActionSpecifierImpl implements ActionSpecifier
{
    //--------------------------------------------------------------------
    private Action action;


    //--------------------------------------------------------------------
    public ActionSpecifierImpl() {}
    public ActionSpecifierImpl(Action initial)
    {
        action = initial;
    }


    //--------------------------------------------------------------------
    public void checkOrFold()
    {
        action = Action.CHECK_OR_FOLD;
    }

    public void checkOrCall()
    {
        action = Action.CHECK_OR_CALL;
    }

    public void raise()
    {
        action = Action.RAISE_OR_CALL;
    }


    //--------------------------------------------------------------------
    public Action action()
    {
        return action;
    }
}
