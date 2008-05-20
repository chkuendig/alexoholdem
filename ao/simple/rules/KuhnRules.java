package ao.simple.rules;

import ao.simple.KuhnAction;
import ao.simple.state.StateFlow;

import java.util.EnumMap;
import java.util.Map;

/**
 *
 */
public class KuhnRules
{
    //--------------------------------------------------------------------
    private final StateFlow STATE;


    //--------------------------------------------------------------------
    public KuhnRules()
    {
        this(StateFlow.FIRST_ACTION);
    }

    private KuhnRules(StateFlow state)
    {
        STATE = state;
    }


    //--------------------------------------------------------------------
    public StateFlow state()
    {
        return STATE;
    }

    public Map<KuhnAction, KuhnRules> transitions()
    {
        Map<KuhnAction, KuhnRules> transitions =
                new EnumMap<KuhnAction, KuhnRules>(KuhnAction.class);

        for (KuhnAction act : KuhnAction.VALUES)
        {
            StateFlow nextState = STATE.advance(act);
            transitions.put(act, new KuhnRules(nextState));
        }

        return transitions;
    }
}
