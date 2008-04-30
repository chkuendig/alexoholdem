package ao.simple.rules;

import ao.simple.KuhnAction;
import ao.simple.state.KuhnState;

import java.util.Map;

/**
 *
 */
public class KuhnRules
{
    public boolean nextToActIsDealer()
    {
        return false;
    }

    public KuhnState state()
    {
        return null;
    }

    public Map<KuhnAction, KuhnRules> transitions()
    {
        return null;
    }
}
