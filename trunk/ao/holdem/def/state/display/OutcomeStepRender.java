package ao.holdem.def.state.display;

import ao.holdem.def.state.env.GodEnvironment;
import ao.holdem.game.Outcome;
import ao.util.async.Condition;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 */
public class OutcomeStepRender extends JPanel
{
    //--------------------------------------------------------------------
    public OutcomeStepRender(Outcome.Step step)
    {
        GodEnvironment env = step.environment();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        add(new JLabel("betting round: " + env.bettingRound()));
        add(new JLabel("community: "     + env.community()));
        add(new JLabel("next action: "   + step.action()));
        add(new PlayerGroup(env));

        if (env.activeOpponents() == 0)
        {
//            setBorder(new LineBorder(Color.RED, 5));
            setBorder(new CompoundBorder(
                    new LineBorder(Color.RED, 5),
                    new EmptyBorder(1, 1, 1, 1)));
        }
    }


    //--------------------------------------------------------------------
    public static void display(Outcome.Step step)
    {
        JFrame     envFrame = new JFrame ("Texus Holdem Poker.");
        JComponent view     = new OutcomeStepRender(step);

        envFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        envFrame.setContentPane( view );
        envFrame.pack();
        envFrame.setVisible( true );

        final Condition c = new Condition(false);
        envFrame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                c.setTrue();
            }
        });
        
        try   { c.waitForTrue(); }
        catch (InterruptedException ignored) {}

        envFrame.setVisible(false);
        envFrame.dispose();
    }
}
