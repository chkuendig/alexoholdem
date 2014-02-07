package ao.holdem.ai.ai.simple.kuhn.rules;

import ao.holdem.ai.ai.simple.kuhn.KuhnAction;
import ao.holdem.ai.ai.simple.kuhn.state.StateFlow;

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
        this(StateFlow.firstAction());
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
