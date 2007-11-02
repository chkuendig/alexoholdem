package ao.ai.opp_model.decision2.example;

import ao.ai.opp_model.decision2.data.Datum;

/**
 *
 */
public class Example extends Context
{
    //--------------------------------------------------------------------
    private Datum TARGET;


    //--------------------------------------------------------------------
    public Example(Context context, Datum target)
    {
        super(context);
        TARGET = target;
    }


    //--------------------------------------------------------------------
    public Datum target()
    {
        return TARGET;
    }
}
