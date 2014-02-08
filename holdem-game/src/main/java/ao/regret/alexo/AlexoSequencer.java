package ao.regret.alexo;

import ao.simple.alexo.card.AlexoCard;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AlexoSequencer
{
    //--------------------------------------------------------------------
    private final AlexoBucket ROOT;


    //--------------------------------------------------------------------
    public AlexoSequencer()
    {
        List<AlexoBucket> preflop = new ArrayList<AlexoBucket>();
        for (int i = 0; i < AlexoCard.VALUES.length; i++)
        {

            AlexoCard         hole  = AlexoCard.VALUES[i];
            List<AlexoBucket> flops = new ArrayList<AlexoBucket>();
            for (int j = 0; j < AlexoCard.VALUES.length; j++)
            {
                if (i == j) continue;

                AlexoCard         flop  = AlexoCard.VALUES[j];
                List<AlexoBucket> turns = new ArrayList<AlexoBucket>();
                for (int k = 0; k < AlexoCard.VALUES.length; k++)
                {
                    if (k == i || k == j) continue;

                    AlexoCard turn = AlexoCard.VALUES[k];
                    turns.add( new AlexoBucket(hole, flop, turn) );
                }
                flops.add( new AlexoBucket(hole, flop, turns) );
            }
            preflop.add( new AlexoBucket(hole, flops) );
        }
        ROOT = new AlexoBucket(preflop);
    }


    //--------------------------------------------------------------------
    public AlexoBucket root()
    {
        return ROOT;
    }
}
