package ao.holdem.model.act;

import ao.holdem.persist.EnumBinding;

/**
 *
 */
public class ActionBinding {
    public static final EnumBinding<Action> INSTANCE =
            new EnumBinding<Action>(Action.class);
}
