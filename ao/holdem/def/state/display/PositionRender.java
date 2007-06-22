package ao.holdem.def.state.display;

import ao.holdem.def.state.env.Position;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 *
 */
public class PositionRender extends JPanel
{
    //--------------------------------------------------------------------
    public PositionRender(
            JComponent parent,
            Position   toRender,
            int        nextToActPosition)
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setOpaque(false);

//        add(new JLabel("Position:"));
        add(new JLabel("away from first to act: " +
                       toRender.awayFromFirstToAct()));

        if (toRender.isDealer())
        {
            add(new JLabel("DEALER"));
        }
        else if (toRender.isSmallBlind())
        {
            add(new JLabel("SMALL BLIND"));
        }
        else if (toRender.isBigBlind())
        {
            add(new JLabel("BIG BLIND"));
        }

        if (toRender.isUnderTheGun())
        {
            add(new JLabel("UNDER THE GUN"));
        }
        if (toRender.awayFromFirstToAct() == nextToActPosition)
        {
            add(new JLabel("NEXT TO ACT"));
//            parent.setBorder(new BevelBorder(BevelBorder.RAISED));
//            parent.setBorder(new LineBorder(Color.BLACK, 3));
            parent.setBorder(
                    new CompoundBorder(new LineBorder(Color.BLACK, 3),
                                       new EmptyBorder(1, 1, 1, 1)));

        }
    }
}
