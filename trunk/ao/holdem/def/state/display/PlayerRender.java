package ao.holdem.def.state.display;

import ao.holdem.model.Hole;
import ao.holdem.def.state.env.Player;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class PlayerRender extends JPanel
{
    //--------------------------------------------------------------------
    public PlayerRender(
            Player toRender,
            Hole hole,
            int    nextToActPosition,
            int    awayFromDealer)
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(bgColor(awayFromDealer));

        add(new JLabel(hole.toString()));
        add(new JLabel("commitment: " + toRender.commitment()));
        add(new PositionRender(this, toRender.position(), nextToActPosition));
        add(new JLabel("last action: " + toRender.lastAction()));
        add(new JLabel("active: " + toRender.isActive()));

        if (! toRender.isActive())
        {
            setBackground(getBackground().darker().darker());
        }
    }

    private Color bgColor(int awayFromDealer)
    {
        return new Color(150 + (50 + baseColor(awayFromDealer)) % 100,
                         150 + (10 + baseColor(awayFromDealer)) % 100,
                         150 + (90 + baseColor(awayFromDealer)) % 100);
    }
    private int baseColor(int awayFromDealer)
    {
        return ((420 + awayFromDealer) * awayFromDealer
                        * awayFromDealer) % 155 + 10*awayFromDealer;
    }
}
