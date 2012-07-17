package ao.learn.mst.cfr

import ao.learn.mst.gen2.info.InformationSetIndex
import ao.learn.mst.gen2.game._
import ao.learn.mst.gen2.player.RationalPlayer
import scala._


//----------------------------------------------------------------------------------------------------------------------
class CfrMinimizer
{
  //--------------------------------------------------------------------------------------------------------------------
  def reduceRegret(
      game                : ExtensiveGame,
      informationSetIndex : InformationSetIndex,
      strategyProfile     : StrategyProfile)
  {
//    if (game.rationalPlayerCount != 2) {
//      throw new UnsupportedOperationException(
//        "Number of players must be 2: " + game.rationalPlayerCount);
//    }

    println("\n")

    val rootCounterfactualReachProbabilities =
      Seq.fill( game.rationalPlayerCount )( 1.0 )

    for (playerIndex <- 0 until game.rationalPlayerCount)
    {
      cfrUpdate(
        game,
        game.gameTreeRoot,
        strategyProfile,
        rootCounterfactualReachProbabilities,
        playerIndex)

      println( strategyProfile )
    }
  }

  private def cfrUpdate(
      game               : ExtensiveGame,
      node               : ExtensiveGameNode,
      strategyProfile    : StrategyProfile,
      reachProbabilities : Seq[Double],
      proponentIndex     : Int
      ): Seq[Double] =
  {
    node match {
      case decision: ExtensiveGameDecision =>
        walkDecision(
          game,
          decision,
          strategyProfile,
          reachProbabilities,
          proponentIndex)

      case chance: ExtensiveGameChance =>
        walkChance(
          game,
          chance,
          strategyProfile,
          reachProbabilities,
          proponentIndex)

      case terminal : ExtensiveGameTerminal => {
        val rationalPlayers: Seq[RationalPlayer] =
          (0 until game.rationalPlayerCount).map( RationalPlayer(_) )

        rationalPlayers.map(terminal.payoff.outcomes(_))
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkChance(
      game               : ExtensiveGame,
      node               : ExtensiveGameChance,
      strategyProfile    : StrategyProfile,
      reachProbabilities : Seq[Double],
      proponentIndex     : Int
      ): Seq[Double] =
  {
    val actionProbabilities =
      node.probabilityMass.actionProbabilities

    for ((action, probability) <- actionProbabilities)
    {
      val child:ExtensiveGameNode = node.child( action )

      cfrUpdate(
        game,
        child,
        strategyProfile,
        reachProbabilities.map(_ * probability),
        proponentIndex
      )
    }

//    val playerChildUtilities: Seq[Seq[Double]] =
//      (for ((action, probability) <- actionProbabilities)
//        yield {
//          val child:ExtensiveGameNode = node.child( action )
//
//          walkTree(
//            game,
//            child,
//            strategyProfile,
////            reachProbabilities
//              reachProbabilities.map(_ * probability)
//          )
//        }
//      ).toSeq.transpose

    null
//    val firstPlayerChildUtilities: Seq[Double] =
//      playerChildUtilities( 0 )
//
//    val lastPlayerChildUtilities: Seq[Double] =
//      playerChildUtilities( 1 )
//
//    val firstPlayerCounterfactualUtility: Double =
//      (for ((action, probability) <- actionProbabilities)
////        yield /*probability **/ firstPlayerChildUtilities(action.index)
//        yield probability * firstPlayerChildUtilities(action.index)
//      ).sum
//
//    val lastPlayerCounterfactualUtility:Double =
//      (for ((action, probability) <- actionProbabilities)
////        yield /*probability **/ lastPlayerChildUtilities(action.index)
//        yield probability * lastPlayerChildUtilities(action.index)
//      ).sum
//
//    Seq(firstPlayerCounterfactualUtility, lastPlayerCounterfactualUtility)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkDecision(
      game               : ExtensiveGame,
      node               : ExtensiveGameDecision,
      strategyProfile    : StrategyProfile,
      reachProbabilities : Seq[Double],
      proponentIndex     : Int
      ) : Seq[Double] =
  {
    // Compute σ1(I(r1)) according to Equation 8.
    val actionProbabilities: Seq[Double] =
      strategyProfile.positiveRegretStrategy(
        node.informationSet, node.actions.size)
    
    // for Each action a ∈ A(I(r1))
    //   Compute u1(σ, I(r1), a) and u2(σ, r2, a)
    val playerChildUtilities: Seq[Seq[Double]] =
      childUtilities(
        game,
        node,
        strategyProfile,
        actionProbabilities,
        reachProbabilities,
        proponentIndex
      ).transpose

//    val (firstPlayerChildUtilities : Seq[Double],
//         lastPlayerChildUtilities  : Seq[Double]) =
//        (playerChildUtilities(0), playerChildUtilities(1))



//    if (proponentIndex != node.player.index)
//    {
//      val firstPlayerUtility: Double =
//        (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
//          yield actionProbability * playerChildUtilities(0)(action)
//        ).sum
//
//      val lastPlayerUtility =
//        (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
//          yield actionProbability * playerChildUtilities(1)(action)
//        ).sum
//
//      return Seq(firstPlayerUtility, lastPlayerUtility)
//    }


    // Compute u1(σ, I(r1))) = sum[a∈A(I(r1)) | σ1(I(r1))(a) × u1(σ, I(r1), a)].
    def playerUtility(playerIndex: Int): Double =
      (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
        yield actionProbability * playerChildUtilities(playerIndex)(action)
      ).sum

    val utilities: Seq[Double] =
      (for (playerIndex <- 0 until game.rationalPlayerCount)
        yield playerUtility(playerIndex)
      ).toSeq



//    val proponentIndex = node.player.index
//    val opponentIndex  = 1 - node.player.index
//    val opponentIndex  = game.rationalPlayerCount - proponentIndex
//
//    val proponentChildUtilities: Seq[Double] =
//      playerChildUtilities( proponentIndex )
//
//    val opponentChildUtilities: Seq[Double] =
//      playerChildUtilities( opponentIndex )
//
//    // Compute u1(σ, I(r1))) = sum[a∈A(I(r1)) | σ1(I(r1))(a) × u1(σ, I(r1), a)].
//    val counterfactualUtility: Double =
//      (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
//       yield actionProbability * proponentChildUtilities(action)
//      ).sum
//
//    // Compute u2(σ, r2) = sum[a∈A(I(r1)) | σ1(I(r1))(a) × u2(σ, r2, a)].
//    val opponentUtility =
//      (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
//       yield actionProbability * opponentChildUtilities(action)
//      ).sum

    if (node.player.index == proponentIndex)
    {
      // Update counterfactual regret and average strategy
      val opponentReachProbability =
        (for (playerIndex <- 0 until game.rationalPlayerCount
              if playerIndex != node.player.index)
          yield reachProbabilities(playerIndex)
        ).product

      //    val proponentReachProbability =
      //      reachProbabilities( proponentIndex )
      //    val opponentReachProbability  =
      //      reachProbabilities( opponentIndex )

      val counterfactualRegret: Seq[Double] =
        playerChildUtilities(node.player.index).map(childUtility =>
          (childUtility - utilities(node.player.index)) * opponentReachProbability)

      strategyProfile.update(node.informationSet,
        counterfactualRegret, opponentReachProbability)
      //      counterfactualRegret, proponentReachProbability)
    }

//    // Utility for each player
//    if (proponentIndex == 0) {
//      Seq(counterfactualUtility, opponentUtility)
//    } else {
//      Seq(opponentUtility, counterfactualUtility)
//    }
    utilities
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def childUtilities(
      game                : ExtensiveGame,
      node                : ExtensiveGameDecision,
      strategyProfile     : StrategyProfile,
      actionProbabilities : Seq[Double],
      reachProbabilities  : Seq[Double],
      proponentIndex      : Int
      ): Seq[Seq[Double]] =
  {
    for (action <- node.actions.toSeq)
    yield {
      // Find the associated child of c1 of r1 and c2 of r2.
      val child:ExtensiveGameNode = node.child( action )

      val actionProbability: Double =
        actionProbabilities( action.index )

      val childReachProbabilities:Seq[Double] =
        updateReachProbability(
          reachProbabilities,
          game.rationalPlayerCount,
          node.player.index,
          actionProbability)

      cfrUpdate(
        game,
        child,
        strategyProfile,
        childReachProbabilities,
        proponentIndex)
    }
  }

  private def updateReachProbability(
      reachProbabilities  : Seq[Double],
      rationalPlayerCount : Int,
      actingPlayerIndex   : Int,
      actionProbability   : Double
      ): Seq[Double] =
  {
    reachProbabilities.zipWithIndex map Function.tupled {
      (p: Double, i: Int) =>
        if (i == actingPlayerIndex) p else p * actionProbability}
  }


}