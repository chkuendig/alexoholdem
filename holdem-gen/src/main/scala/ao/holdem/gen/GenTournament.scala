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

/**
 *
 */
object GenTournament extends App
{
  val holdemAbstraction =
    new HoldemAbstraction(
      new FastBucketTreeBuilder(new KMeansBucketizer),
      5, 25.toChar, 125.toChar, 625.toChar)

  val strategy = new ArrayStrategyStore
  strategy.read(
    new File("work/opt/fast;kmeans;5.25.125.625 - 39_450_000/strategy.bin"))

  val getPlayer = new GenPlayer(
    holdemAbstraction,
    strategy,
    false)

  //new DealerTest().vsHuman(getPlayer)

  new DealerTest(100 * 1000).headsUp(Map(
//    Avatar.local("raise") -> new AlwaysRaiseBot(),
//    Avatar.local("duane") -> new DuaneBot(),
    Avatar.local("mostly-raise") -> new MostlyRaiseBot(),
    Avatar.local("gen") -> getPlayer
  ))
}
