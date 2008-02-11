package ao.ai.supervised.classifier.raw;

import ao.ai.supervised.classifier.processed.LocalClassifier;
import ao.ai.supervised.decision.classification.raw.Prediction;
import ao.ai.supervised.decision.input.raw.example.Context;
import ao.ai.supervised.decision.input.raw.example.Example;
import ao.ai.supervised.decision.input.raw.example.LearningSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DomainedClassifier implements Classifier
{
    //--------------------------------------------------------------------
    private final Map<Collection<String>, Classifier> pools;
    private final LocalClassifier.Factory             classifierFactory;


    //--------------------------------------------------------------------
    public DomainedClassifier(
            LocalClassifier.Factory localClassifierFactory)
    {
        pools             = new HashMap<Collection<String>, Classifier>();
        classifierFactory = localClassifierFactory;
    }


    //--------------------------------------------------------------------
    public void set(LearningSet ls)
    {
        getClassifier( ls.types() ).set( ls );
    }

    public void add(LearningSet ls)
    {
        getClassifier( ls.types() ).add( ls );
    }

    public void add(Example example)
    {
        getClassifier( example.types() ).add( example );
    }

    public Prediction classify(Context context)
    {
        return getClassifier( context.types() ).classify( context );
    }


    //--------------------------------------------------------------------
    public void limitPopulation(int toMostRecent)
    {

    }


    //--------------------------------------------------------------------
    private Classifier getClassifier(Collection<String> types)
    {
        Classifier classifier = pools.get( types );
        if (classifier == null)
        {
            classifier = newClassifier();
            pools.put(types, classifier);
        }
        return classifier;
    }

    private Classifier newClassifier()
    {
        return new ClassifierImpl(
                    classifierFactory.newInstance());
    }
}