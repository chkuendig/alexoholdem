package ao.holdem.bot.main;

import ao.holdem.bot.simple.*;
import ao.holdem.engine.Player;
import ao.holdem.model.Avatar;

import java.util.HashMap;

/**
 * 22/02/14 8:23 PM
 */
public class Tournament {
    public static void main(String[] args) {

        DealerTest runner = new DealerTest();

        runner.headsUp(new HashMap<Avatar, Player>() {{
//            put(Avatar.local("duane"), new DuaneBot());
            put(Avatar.local("opt-s"), new OptSklanskyBot());
            put(Avatar.local("mostly-raise"), new MostlyRaiseBot());
//            put(Avatar.local("call"), new AlwaysCallBot());
//            put(Avatar.local("raise"), new AlwaysRaiseBot());
        }});
    }
}
