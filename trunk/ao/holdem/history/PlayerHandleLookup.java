package ao.holdem.history;

import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class PlayerHandleLookup
{
    //--------------------------------------------------------------------
    private static final Map<String, PlayerHandle> HANDLES =
            new HashMap<String, PlayerHandle>();


    //--------------------------------------------------------------------
    private PlayerHandleLookup() {}


    //--------------------------------------------------------------------
    public PlayerHandle lookup(String fromName)
    {
        PlayerHandle existing = HANDLES.get(fromName);
        if (existing == null)
        {
            PlayerHandle player = new PlayerHandle();
            HANDLES.put(fromName, player);
            return player;
        }
        return existing;
    }
}
