package ao.regret;
    
import ao.holdem.model.act.AbstractAction;

/**
 * 
 */
public class InfoNode
{
    //--------------------------------------------------------------------
    public boolean isPlayerNode()
    {
        return false;
    }

    public InfoSet info()
    {
        return null;
    }

    public InfoNode child(AbstractAction act)
    {
        return null;
    }
}
