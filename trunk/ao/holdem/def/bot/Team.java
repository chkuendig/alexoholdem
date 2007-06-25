package ao.holdem.def.bot;

import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class Team
{
    //--------------------------------------------------------------------
    private List<LocalBot> members;
    private double         stackDelta;


    //--------------------------------------------------------------------
    public Team()
    {
        members = new ArrayList<LocalBot>();
    }


    //--------------------------------------------------------------------
    public void setStackDelta(double stackDelta)
    {
        this.stackDelta = stackDelta;
    }


    //--------------------------------------------------------------------
    public void add(LocalBot bot)
    {
        members.add( bot );
    }



    //--------------------------------------------------------------------
    /**
     * @return members of team in action order.
     */
    public List<LocalBot> members()
    {
        return members;
    }

    /**
     * @return wins or losses in small blinds.
     */
    public double stackDelta()
    {
        return stackDelta;
    }
}
