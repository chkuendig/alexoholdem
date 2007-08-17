package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RetroSet<C extends PredictionContext>
{
    //--------------------------------------------------------------------
    private List<Retrodiction<C>> cases;


    //--------------------------------------------------------------------
    public RetroSet()
    {
        cases = new ArrayList<Retrodiction<C>>();
    }


    //--------------------------------------------------------------------
    public void add(Retrodiction<C> retro)
    {
        cases.add( retro );
    }

    public void add(RetroSet<C> retro)
    {
        cases.addAll( retro.cases );
    }

    public void removeOld(final int newSize)
    {
        if (cases.size() <= newSize) return;

        cases = new ArrayList<Retrodiction<C>>(){{
            addAll(cases.subList(
                        cases.size() - newSize - 1,
                        cases.size()           - 1));
        }};
    }

    public void clear()
    {
        cases.clear();
    }


    //--------------------------------------------------------------------
    public List<Retrodiction<C>> cases()
    {
        return cases;
    }

    public Retrodiction<C> get(int index)
    {
        return cases.get( index );
    }


    //--------------------------------------------------------------------
    public boolean isEmpty()
    {
        return cases.isEmpty();
    }

    public int size()
    {
        return cases.size();
    }

    public int caseInputSize()
    {
        return cases.get(0).neuralInputSize();
    }

    public int caseOutputSize()
    {
        return cases.get(0).neuralOutputSize();
    }
}
