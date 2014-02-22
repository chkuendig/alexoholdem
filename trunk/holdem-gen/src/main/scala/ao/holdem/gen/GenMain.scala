package ao.holdem.gen

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensivePlayer, ExtensiveGame}
import ao.learn.mst.gen5.cfr.{OutcomeSampling2CfrMinimizer, OutcomeSamplingCfrMinimizer, ProbingCfrMinimizer}
import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.holdem.gen.abs.{SingleInfoAbstraction, SklanskyInfoAbstraction}
import ao.learn.mst.gen5.strategy.ExtensiveStrategyProfile
import ao.holdem.bot.simple.starting_hands.Sklansky
import ao.learn.mst.lib.{DisplayUtils, NumUtils}

/**
 *
 */
object GenMain extends App
{
  val game: ExtensiveGame[HoldemState, HoldemInfo, HoldemAction] =
    HoldemGame

  val solver: ExtensiveSolver[HoldemState, HoldemInfo, HoldemAction] =
//    new ProbingCfrMinimizer[HoldemState, HoldemInfo, HoldemAction](
    new OutcomeSampling2CfrMinimizer[HoldemState, HoldemInfo, HoldemAction](
      true)

  val abstraction: ExtensiveAbstraction[HoldemInfo, HoldemAction] =
    SingleInfoAbstraction
//    HoleInfoAbstraction
//    SklanskyInfoAbstraction

  val solution: SolutionApproximation[HoldemInfo, HoldemAction] =
    solver.initialSolution(game)

  (0 to 10000000).foreach(i => {
    solution.optimize(abstraction)

    val strategy: ExtensiveStrategyProfile =
      solution.strategy

    if (i % 1000 == 0)
    {
      println(s"\n\n$i")
      println("-" * 79)

      val rootActions: Seq[Double] =
        strategy.actionProbabilityMass(0)

      println(s"$i\t$rootActions")

//      for (g <- 0 to Sklansky.HIGH) {
//        val probs: Seq[Double] =
//          strategy.actionProbabilityMass(g)
//
//        val name: String =
//          if (g == 0) {
//            "Post-flop"
//          } else {
//            s"S$g"
//          }
//
//        println(s"$name\t${DisplayUtils.displayProbabilities(probs)}")
//      }

//      for (h <- 0 until CanonHole.CANONS) {
//        val hole: CanonHole =
//          HoleLookup.lookup(h)
//
//        val probs: Seq[Double] =
//          strategy.actionProbabilityMass(h)
//
//        println(s"$hole\t${CommonUtils.displayProbabilities(probs)}")
//      }
    }
  })


//  val players: Seq[ExtensivePlayer[HoldemInfo, HoldemAction]] =
//    Seq.fill(2)(new RandomPlayer[HoldemInfo, HoldemAction](new Random()))
//
//  val outcome: Seq[Double] =
//    SimpleGameDemo.playout[HoldemState, HoldemInfo, HoldemAction](
//      game, players)
//
//  println(outcome)
}
