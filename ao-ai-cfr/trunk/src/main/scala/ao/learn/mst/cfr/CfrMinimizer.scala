package ao.learn.mst.cfr

import ao.learn.mst.gen2.player.RationalPlayer
import ao.learn.mst.gen2.info.InformationSetIndex
import ao.learn.mst.gen2.game._


//----------------------------------------------------------------------------------------------------------------------
class CfrMinimizer
{
  //--------------------------------------------------------------------------------------------------------------------
  def walkTree(
      game                : ExtensiveGame,
      informationSetIndex : InformationSetIndex,
      strategyProfile     : StrategyProfile)
  {
    walkTree(game,
             game.gameTreeRoot,
//             informationSetIndex,
             strategyProfile,
             Seq(1.0, 1.0))
  }

  private def walkTree(
      game                        : ExtensiveGame,
      node                        : ExtensiveGameNode,
//      informationSetIndex         : InformationSetIndex,
      strategyProfile             : StrategyProfile,
//      joinBucketSequence          : JointBucketSequence,
      reachProbabilities          : Seq[Double]
      ): Seq[Double] =
  {
    node match {
      case decision: ExtensiveGameDecision => {
        walkProponent(
          game,
          decision,
//          informationSetIndex,
          strategyProfile,
          reachProbabilities)
      }

      case chance: ExtensiveGameChance =>
        throw new UnsupportedOperationException

      case terminal : ExtensiveGameTerminal => {
        val rationalPlayers: Seq[RationalPlayer] =
          (0 until game.rationalPlayerCount).map( RationalPlayer(_) )

        rationalPlayers.map(terminal.payoff.outcomes(_))
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
//  def walkTree(
//      firstPlayerNode             : PlayerViewNode,
//      lastPlayerNode              : PlayerViewNode)
//  {
//    walkTree(
//      firstPlayerNode,
//      lastPlayerNode,
////      new JointBucketSequence,
//      1.0,
//      1.0)
//  }
//
//  private def walkTree(
//      firstPlayerNode             : PlayerViewNode,
//      lastPlayerNode              : PlayerViewNode,
////      joinBucketSequence          : JointBucketSequence,
//      firstPlayerReachProbability : Double,
//      lastPlayerReachProbability  : Double) : Seq[Double] =
//  {
//    firstPlayerNode match {
//      case decision : DecisionNode =>
//        walkProponent(
//          firstPlayerNode.asInstanceOf[DecisionNode],
//          lastPlayerNode.asInstanceOf[DecisionNode],
////          joinBucketSequence,
//          firstPlayerReachProbability,
//          lastPlayerReachProbability,
//          decision.isInstanceOf[ProponentNode])
//
//      case bucket : ChanceNode => {
//        throw new UnsupportedOperationException
//      }
//
//      case terminal : TerminalNode =>
//        terminal.outcome
//    }
//  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkProponent(
      game                        : ExtensiveGame,
      node                        : ExtensiveGameDecision,
//      informationSetIndex         : InformationSetIndex,
      strategyProfile             : StrategyProfile,                           
//      joinBucketSequence          : JointBucketSequence,
      reachProbabilities          : Seq[Double]/*,
      firstPlayerIsNextToAct      : Boolean*/
      ) : Seq[Double] =
  {
//    val proponent: ProponentNode =
//      (if (firstPlayerIsNextToAct) firstPlayerNode else lastPlayerNode)
//        .asInstanceOf[ProponentNode];

    // Compute σ1(I(r1)) according to Equation 8.
    val actionProbabilities: Seq[Double] =
      strategyProfile.positiveRegretStrategy(
        node.informationSet, node.actions.size)
//      proponent.positiveRegretStrategy()
    
    // for Each action a ∈ A(I(r1))
    //   Compute u1(σ, I(r1), a) and u2(σ, r2, a)
    val playerChildUtilities: Seq[Seq[Double]] =
      childUtilities(
        game,
        node,
        strategyProfile,
        actionProbabilities,
        reachProbabilities
      ).transpose

//    val (firstPlayerChildUtilities : Seq[Double],
//         lastPlayerChildUtilities  : Seq[Double]) =
//        (playerChildUtilities(0), playerChildUtilities(1))
    
    if (game.rationalPlayerCount != 2) {
      throw new UnsupportedOperationException(
        "Number of players must be 2: " + game.rationalPlayerCount);
    }
    val proponentIndex = node.player.index
    val opponentIndex  = 1 - node.player.index
    
    val proponentChildUtilities: Seq[Double] =
      playerChildUtilities( proponentIndex )
    
    val opponentChildUtilities: Seq[Double] =
      playerChildUtilities( opponentIndex )

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
      reachProbabilities( proponentIndex )
    val opponentReachProbability  =
      reachProbabilities( opponentIndex )

    val counterfactualRegret: Seq[Double] =
      proponentChildUtilities.map(childUtility =>
        (childUtility - counterfactualUtility) * opponentReachProbability)

//    proponent.update(
    strategyProfile.update(node.informationSet,
      counterfactualRegret, proponentReachProbability)

    // Utility for each player
    if (proponentIndex == 0) {
      Seq(counterfactualUtility, opponentUtility)
    } else {
      Seq(opponentUtility, counterfactualUtility)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def childUtilities(
      game                : ExtensiveGame,
      node                : ExtensiveGameDecision,
      strategyProfile     : StrategyProfile,
      actionProbabilities : Seq[Double],
      reachProbabilities  : Seq[Double]
      ): Seq[Seq[Double]] =
  {
    for (action <- node.actions.toSeq)
    yield {
      // Find the associated child of c1 of r1 and c2 of r2.
      val child:ExtensiveGameNode = node.child( action )
      
//      val firstPlayerChild: PlayerViewNode =
//        firstPlayerNode.kids( action )
//
//      val lastPlayerChild: PlayerViewNode =
//        lastPlayerNode.kids( action )

      // Compute u1(σ, I(r1), a) and u2(σ, r2, a) from walkTree(c1, c2, b, p1 × σ1(I(r1))(a), p2).
      val actionProbability: Double =
        actionProbabilities( action.index )

      val childReachProbabilities =
        reachProbabilities.updated(node.player.index,
          reachProbabilities(node.player.index) * actionProbability) 
      
//      val firstPlayerChildReachProbability =
//        firstPlayerReachProbability * (if (firstPlayerIsNextToAct) actionProbability else 1.0)
//
//      val lastPlayerChildReachProbability =
//        lastPlayerReachProbability  * (if (firstPlayerIsNextToAct) 1.0 else actionProbability)

      walkTree(
        game,
        child, 
        strategyProfile,
        childReachProbabilities)
    }
  }
}