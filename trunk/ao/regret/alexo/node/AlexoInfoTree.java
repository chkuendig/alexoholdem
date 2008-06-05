package ao.regret.alexo.node;

import ao.regret.alexo.AlexoSequencer;
import ao.regret.alexo.JointBucketSequence;
import ao.regret.alexo.pair.BucketPair;
import ao.simple.alexo.state.AlexoState;

/**
 *
 */
public class AlexoInfoTree
{
    //--------------------------------------------------------------------
    public BucketPair root()
    {
        return new BucketPair(
                new AlexoState(),
                new AlexoSequencer().root());
    }


    //--------------------------------------------------------------------
    public static void main(String args[])
    {
        BucketPair root = new AlexoInfoTree().root();

//        System.out.println( root );
        for (int i = 0; i < 100; i++)
        {
            root.approximate(
                    new JointBucketSequence(),
                    1.0, 1.0);
        }
//        System.out.println( root );
    }
}
