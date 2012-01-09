package ao.learn.mst.gen2.solve

import ao.learn.mst.gen2.state.ExtensiveGameState
import ao.learn.mst.gen2.player.PlayerIdentity

//----------------------------------------------------------------------------------------------------------------------
class ExtensiveGameSolver
{
  //--------------------------------------------------------------------------------------------------------------------
  private val epsilon = 1e-7


  //--------------------------------------------------------------------------------------------------------------------
  def optimize(
      state : ExtensiveGameState)
  {
    // walk tree


  }


  //--------------------------------------------------------------------------------------------------------------------
  def updateRegret(
      state : ExtensiveGameState,
      reach : Map[PlayerIdentity, Double]) : ExpectedValue =

  {
    if (state.isTerminal)
    {
//      int amount = u.win_amount();
//
//      if (u.is_fold()) {
//
//        if (u.who_folded() == 0) {
//
//          ev[0] = -amount*reach[1]*chance;
//          ev[1] = amount*reach[0]*chance;
//        } else {
//
//          ev[0] = amount*reach[1]*chance;
//          ev[1] = -amount*reach[0]*chance;
//        }
//
//      } else {
//
//        /* sequence is a showdown */
//        ev[0] = result*reach[1]*amount*chance;
//        ev[1] = -result*reach[0]*amount*chance;
//      }
    }
//    else if (reach[0] < EPSILON && reach[1] < EPSILON)
//    {
//
//      /* cutoff, do nothing */
//      ev[0] = ev[1] = 0;
//
//    }
//    else if (rA instanceof BucketNode)
//    {
//        BucketNode bucketA = (BucketNode) rA;
//        BucketNode bucketB = (BucketNode) rB;
//
//        PlayerNode nextA = bucketA.accordingTo(b);
//        PlayerNode nextB = bucketB.accordingTo(b);
//
//        return approximate(nextA, nextB, b, pA, pB);
//    }
    else
    {

    }


    null
  }
}