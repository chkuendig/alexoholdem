package ao.holdem.def.state.display;

import ao.holdem.def.state.env.GodEnvironment;
import ao.holdem.def.state.env.Player;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class PlayerGroup extends JPanel
{
    //--------------------------------------------------------------------
    public PlayerGroup(GodEnvironment env)
    {
        int players = env.opponentCount() + 1;
        int cols    = (players + 1) >> 1;

        setLayout( new GridLayout(2, cols) );

        Component playerViews[] = new Component[players];
        for (int position = 0; position < players; position++)
        {
            Player player     = env.playerAt( position );
            int    fromDealer = env.awayFromDealer( position );

            playerViews[ fromDealer ] =
                new PlayerRender(player,
                                 env.holeOf(position),
                                 env.position(),
                                 fromDealer);
        }

        for (int fromDealer = 0;
                 fromDealer < cols;
                 fromDealer++)
        {
            add(playerViews[fromDealer]);
        }
        if (players % 2 == 1)
        {
            add(new JLabel(""));
        }
        for (int fromDealer = players - 1;
                 fromDealer >= cols;
                 fromDealer--)
        {
            add(playerViews[fromDealer]);
        }
    }
}
