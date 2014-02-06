package ao.general;

/**
 * Date: 7/9/11
 * Time: 5:46 PM
 */
public interface GameState
{
    //------------------------------------------------------------------------
    boolean nextToActIsFirstPlayer();

    int actionCount();
    int bucketRound();


    //------------------------------------------------------------------------
    GameState advance(int action);


    //------------------------------------------------------------------------
    boolean isTerminal();

    double outcomeValue(
            int[]   firstPlayerJointBucketSequence,
            int[]   lastPlayerJointBucketSequence,
            boolean fromPointOfViewOfFirstPlayer);
}
