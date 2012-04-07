package ao.learn.mst.cfr

import ao.learn.mst.gen2.player.RationalPlayer
import ao.learn.mst.gen2.game.{ExtensiveGame, ExtensiveGameNode}


//----------------------------------------------------------------------------------------------------------------------
class CfrMinimizer
{
  //--------------------------------------------------------------------------------------------------------------------
  def walkTree(
      firstPlayerNode             : PlayerViewNode,
      lastPlayerNode              : PlayerViewNode)
  {
    walkTree(
      firstPlayerNode,
      lastPlayerNode,
//      new JointBucketSequence,
      1.0,
      1.0)
  }

  private def walkTree(
      firstPlayerNode             : PlayerViewNode,
      lastPlayerNode              : PlayerViewNode,
//      joinBucketSequence          : JointBucketSequence,
      firstPlayerReachProbability : Double,
      lastPlayerReachProbability  : Double) : Seq[Double] =
  {
    firstPlayerNode match {
      case decision : DecisionNode =>
        walkProponent(
          firstPlayerNode.asInstanceOf[DecisionNode],
          lastPlayerNode.asInstanceOf[DecisionNode],
//          joinBucketSequence,
          firstPlayerReachProbability,
          lastPlayerReachProbability,
          decision.isInstanceOf[ProponentNode])

      case bucket : ChanceNode => {
        throw new UnsupportedOperationException
      }

      case terminal : TerminalNode =>
        terminal.outcome
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkProponent(
      firstPlayerNode             : DecisionNode,
      lastPlayerNode              : DecisionNode,
//      joinBucketSequence          : JointBucketSequence,
      firstPlayerReachProbability : Double,
      lastPlayerReachProbability  : Double,
      firstPlayerIsNextToAct      : Boolean) : Seq[Double] =
  {
    val proponent: ProponentNode =
      (if (firstPlayerIsNextToAct) firstPlayerNode else lastPlayerNode)
        .asInstanceOf[ProponentNode];

    // Compute σ1(I(r1)) according to Equation 8.
    val actionProbabilities: Seq[Double] =
      proponent.positiveRegretStrategy()

    // for Each action a ∈ A(I(r1))
    //   Compute u1(σ, I(r1), a) and u2(σ, r2, a)
    val playerChildUtilities: Seq[Seq[Double]] =
      childUtilities(
        firstPlayerNode, lastPlayerNode, proponent,
        actionProbabilities,
        firstPlayerReachProbability, lastPlayerReachProbability,
        firstPlayerIsNextToAct
      ).transpose

    val (firstPlayerChildUtilities : Seq[Double],
         lastPlayerChildUtilities  : Seq[Double]) =
        (playerChildUtilities(0), playerChildUtilities(1))
    
    val proponentChildUtilities: Seq[Double] =
      if (firstPlayerIsNextToAct) firstPlayerChildUtilities else lastPlayerChildUtilities
    val opponentChildUtilities: Seq[Double] =
      if (firstPlayerIsNextToAct) lastPlayerChildUtilities else firstPlayerChildUtilities

    // Compute u1(σ, I(r1))) = sum[a∈A(I(r1)) | σ1(I(r1))(a) × u1(σ, I(r1), a)].
    val counterfactualUtility: Double =
      (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
       yield actionProbability * proponentChildUtilities(action)
      ).sum

    // Compute u2(σ, r2) = sum[a∈A(I(r1)) | σ1(I(r1))(a) × u2(σ, r2, a)].
    val opponentUtility =
      (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
       yield actionProbability * opponentChildUtilities(action)
      ).sum

    // Update counterfactual regret and average strategy
    val proponentReachProbability =
      if (firstPlayerIsNextToAct) firstPlayerReachProbability else lastPlayerReachProbability
    val opponentReachProbability  =
      if (firstPlayerIsNextToAct) lastPlayerReachProbability else firstPlayerReachProbability

    val counterfactualRegret: Seq[Double] =
      proponentChildUtilities.map(childUtility =>
        (childUtility - counterfactualUtility) * opponentReachProbability)

    proponent.update(
      counterfactualRegret, proponentReachProbability)

    // Utility for each player
    if (firstPlayerIsNextToAct) {
      Seq(counterfactualUtility, opponentUtility)
    } else {
      Seq(opponentUtility, counterfactualUtility)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def childUtilities(
      firstPlayerNode             : DecisionNode,
      lastPlayerNode              : DecisionNode,
      proponent                   : ProponentNode,
      actionProbabilities         : Seq[Double],
      firstPlayerReachProbability : Double,
      lastPlayerReachProbability  : Double,
      firstPlayerIsNextToAct      : Boolean
      ): Seq[Seq[Double]] =
  {
    for (action <- 0 until proponent.kids.size)
    yield {
      // Find the associated child of c1 of r1 and c2 of r2.
      val firstPlayerChild: PlayerViewNode =
        firstPlayerNode.kids( action )

      val lastPlayerChild: PlayerViewNode =
        lastPlayerNode.kids( action )

      // Compute u1(σ, I(r1), a) and u2(σ, r2, a) from walkTree(c1, c2, b, p1 × σ1(I(r1))(a), p2).
      val actionProbability: Double =
        actionProbabilities( action )

      val firstPlayerChildReachProbability =
        firstPlayerReachProbability * (if (firstPlayerIsNextToAct) actionProbability else 1.0)

      val lastPlayerChildReachProbability =
        lastPlayerReachProbability  * (if (firstPlayerIsNextToAct) 1.0 else actionProbability)

      walkTree(
          firstPlayerChild,
          lastPlayerChild,
          firstPlayerChildReachProbability,
          lastPlayerChildReachProbability)
    }
  }
}