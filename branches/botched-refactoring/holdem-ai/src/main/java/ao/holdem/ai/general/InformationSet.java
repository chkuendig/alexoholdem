package ao.holdem.ai.general;

/**
 * Date: 7/9/11
 * Time: 5:55 PM
 */
public interface InformationSet
{
    //------------------------------------------------------------------------
    double[] positiveRegretStrategy(int actionCount);

    void add(double[] immediateCounterfactualRegret,
             double[] probabilities,
             double   reachProbability);

    int nextProbableAction();
}
