package ao.general.impl;

import ao.general.GameState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Date: 7/10/11
 * Time: 7:28 PM
 */
public class GameStateTree
{
    //------------------------------------------------------------------------
    private final List<GameState>     states;
    private final List<List<Integer>> stateKids;

    private final int initialStateIndex;


    //------------------------------------------------------------------------
    public GameStateTree(GameState initialState)
    {
        states    = new ArrayList<GameState>();
        stateKids = new ArrayList<List<Integer>>();

        initialStateIndex =
                populateStates( initialState );
    }

    private int populateStates(GameState state)
    {
        int index = states.size();
        states.add( state );

        List<Integer> kids = new LinkedList<Integer>();
        stateKids.add( kids );

        if (! state.isTerminal())
        {
            for (int i = 0; i < state.actionCount(); i++)
            {
                GameState kid      = state.advance( i );
                int       kidIndex = populateStates( kid );

                kids.add( kidIndex );
            }
        }

        return index;
    }


    //------------------------------------------------------------------------
    public int initialStateIndex()
    {
        return initialStateIndex;
    }


    //------------------------------------------------------------------------
    public int childCount(int stateIndex)
    {
        return stateKids.get( stateIndex ).size();
    }

    public boolean isTerminal(int stateIndex)
    {
        return stateKids.get( stateIndex ).isEmpty();
    }


    //------------------------------------------------------------------------
    public int stateIndexOfChild(int stateIndex, int childIndex)
    {
        return stateKids.get( stateIndex ).get( childIndex );
    }

    public boolean isFirstPlayerNextToAct(int stateIndex)
    {
        return states.get( stateIndex ).nextToActIsFirstPlayer();
    }

    public int bucketRound(int stateIndex)
    {
        return states.get( stateIndex ).bucketRound();
    }

    public double outcomeValue(
            int     stateIndex,
            int[]   firstPlayerJointBucketSequence,
            int[]   lastPlayerJointBucketSequence,
            boolean fromPointOfViewOfFirstPlayer)
    {
        return states.get( stateIndex ).outcomeValue(
                firstPlayerJointBucketSequence,
                lastPlayerJointBucketSequence,
                fromPointOfViewOfFirstPlayer);
    }
}
