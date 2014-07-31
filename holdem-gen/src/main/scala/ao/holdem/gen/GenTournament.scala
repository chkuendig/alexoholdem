package ao.holdem.gen

import ao.holdem.bot.regret.HoldemAbstraction
import ao.holdem.abs.bucket.abstraction.bucketize.build.FastBucketTreeBuilder
import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer
import ao.learn.mst.gen5.state.strategy.impl.ArrayStrategyStore
import java.io.File
import ao.holdem.bot.main.DealerTest
import ao.holdem.model.Avatar
import ao.holdem.bot.simple._
import scala.collection.JavaConversions._
import scala.util.Random
import org.apache.commons.math3.random.{Well512a, RandomAdaptor}
import ao.util.math.rand.Rand
import ao.holdem.ai.abs.card.CardAbstraction
import ao.holdem.abs.bucket.v2.PercentileImperfectAbstractionBuilder
import ao.holdem.ai.abs.{CompoundStateAbstraction, StateAbstraction}
import ao.holdem.ai.abs.act.{BasicActionView, ActionAbstraction}
import ao.holdem.abs.ViewActionAbstraction
import ao.holdem.abs.odds.agglom.impl.OddsFinderEvaluator
import ao.holdem.ai.odds.OddsBy5
import ao.learn.mst.gen5.state.impl.ArrayOptimizationState
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.holdem.gen.abs.GenStateAbstraction

/**
 *
 */
object GenTournament extends App
{
  val rand =
    new Random(new RandomAdaptor(new Well512a()))

  val smallHoldemAbstraction =
    new HoldemAbstraction(
      new FastBucketTreeBuilder(new KMeansBucketizer),
      5, 25.toChar, 125.toChar, 625.toChar)

//  val midHoldemAbstraction =
//    new HoldemAbstraction(
//      new FastBucketTreeBuilder(new KMeansBucketizer),
//      8, 64.toChar, 512.toChar, 4096.toChar)

//  val earlyStrategy = new ArrayStrategyStore
//  earlyStrategy.read(
//    new File("work/opt/fast;kmeans;5.25.125.625 - 15_625_000/strategy.bin"))
//  val earlyGenPlayer = new GenPlayer(
//    smallHoldemAbstraction, earlyStrategy, false, rand)
  
//  val midSmallStrategy = new ArrayStrategyStore
//  midSmallStrategy.read(
//    new File("work/opt/fast;kmeans;5.25.125.625 - 39_450_000/strategy.bin"))
//  val midSmallGetPlayer = new GenPlayer(
//    smallHoldemAbstraction, midSmallStrategy, false, rand)

  val lateSmallStrategy = new ArrayStrategyStore
  lateSmallStrategy.read(
    new File("work/opt/fast;kmeans;5.25.125.625 - 68_075_000/strategy.bin"))
  val lateSmallGenPlayer = new GenPlayer(
    smallHoldemAbstraction, lateSmallStrategy, false, rand)

//  val earlyMidStrategy = new ArrayStrategyStore
//  earlyMidStrategy.read(
//    new File("work/opt/fast;kmeans;8.64.512.4096 - 38_825_000/strategy.bin"))
//  val earlyMidGetPlayer = new GenPlayer(
//    midHoldemAbstraction, earlyMidStrategy, false, rand)

//  val midMidStrategy = new ArrayStrategyStore
//  midMidStrategy.read(
//    new File("work/opt/fast;kmeans;8.64.512.4096 - 60_850_000/strategy.bin"))
//  val midMidGetPlayer = new GenPlayer(
//    midHoldemAbstraction, midMidStrategy, false, rand)



  val cardAbstraction: CardAbstraction = PercentileImperfectAbstractionBuilder.loadOrBuildAndSave(
    20, 30, 30, 50)

  val stateAbstraction: StateAbstraction = {
    val actionAbstraction: ActionAbstraction = ViewActionAbstraction.loadOrBuildAndSave(
      new File("lookup/bucket/BasicActionView.bin"),
      BasicActionView.VIEW)

    new CompoundStateAbstraction(
      cardAbstraction, actionAbstraction,
      OddsBy5.INSTANCE)
  }

  val smallMobileStrategy = new ArrayStrategyStore
  smallMobileStrategy.read(
    new File("work/opt/b_20_30_30_50_b/strategy.bin"))
//    new File("work/opt/b_20_30_30_50_b - Copy (12)/strategy.bin"))

  val smallMobileStrategyAgro = new ArrayStrategyStore
  smallMobileStrategyAgro.read(
    new File("work/opt/b_20_30_30_50_agro/strategy.bin"))

  val smallMobileAbstraction: ExtensiveAbstraction[HoldemInfo, HoldemAction] =
    new GenStateAbstraction(stateAbstraction)

  val smallMobileGenPlayer = new MobGenPlayer(
    smallMobileAbstraction, smallMobileStrategy, false, rand)

  val smallMobileGenPlayerAgro = new MobGenPlayer(
    smallMobileAbstraction, smallMobileStrategyAgro, false, rand)
  
  
  
//  new DealerTest().vsHuman(lateSmallGenPlayer)

  new DealerTest(100 * 1000).headsUp(Map(
//    Avatar.local("raise") -> new AlwaysRaiseBot(),
//    Avatar.local("call") -> new AlwaysCallBot(),
//    Avatar.local("sean") -> new SeanBot(),
//    Avatar.local("math") -> new MathBot(),
//    Avatar.local("sean2") -> new ShawnBot()
//    Avatar.local("duane") -> new DuaneBot(),
//    Avatar.local("mostly-raise") -> new MostlyRaiseBot()
//    Avatar.local("gen-early") -> earlyGenPlayer
////    Avatar.local("gen-mid") -> midGetPlayer,
//    Avatar.local("gen-small-late") -> lateSmallGenPlayer
////    Avatar.local("gen-small-mid") -> midSmallGetPlayer,
////    Avatar.local("gen-mid-early") -> earlyMidGetPlayer,
//    Avatar.local("gen-mid-mid") -> midMidGetPlayer
    Avatar.local("mob-small-late") -> smallMobileGenPlayer,
//    Avatar.local("mob-small-lateC") -> smallMobileGenPlayerC
    Avatar.local("mob-small-agro") -> smallMobileGenPlayerAgro
  ))
}
