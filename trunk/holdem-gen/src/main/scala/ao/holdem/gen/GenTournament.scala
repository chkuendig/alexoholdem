package ao.holdem.gen

import ao.holdem.bot.regret.HoldemAbstraction
import ao.holdem.abs.bucket.abstraction.bucketize.build.FastBucketTreeBuilder
import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer
import ao.learn.mst.gen5.state.strategy.impl.ArrayStrategyStore
import java.io.File
import ao.holdem.bot.main.DealerTest
import ao.holdem.model.Avatar
import ao.holdem.bot.simple.{MostlyRaiseBot, DuaneBot, AlwaysRaiseBot}
import scala.collection.JavaConversions._
import scala.util.Random
import org.apache.commons.math3.random.{Well512a, RandomAdaptor}
import ao.util.math.rand.Rand

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

  new DealerTest().vsHuman(lateSmallGenPlayer)

//  new DealerTest(100 * 1000).headsUp(Map(
////    Avatar.local("raise") -> new AlwaysRaiseBot(),
////    Avatar.local("duane") -> new DuaneBot(),
////    Avatar.local("mostly-raise") -> new MostlyRaiseBot(),
////    Avatar.local("gen-early") -> earlyGenPlayer,
////    Avatar.local("gen-mid") -> midGetPlayer,
//    Avatar.local("gen-small-late") -> lateSmallGenPlayer,
////    Avatar.local("gen-small-mid") -> midSmallGetPlayer,
////    Avatar.local("gen-mid-early") -> earlyMidGetPlayer,
//    Avatar.local("gen-mid-mid") -> midMidGetPlayer
//  ))
}
