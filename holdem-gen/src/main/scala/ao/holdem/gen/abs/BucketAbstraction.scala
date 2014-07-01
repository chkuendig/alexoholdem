package ao.holdem.gen.abs

import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.holdem.gen.{DecisionAction, HoldemAction, HoldemInfo}
import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree
import ao.holdem.bot.regret.HoldemAbstraction
import ao.holdem.abs.bucket.abstraction.access.BucketDecoder
import ao.holdem.model.card.canon.hole.CanonHole
import ao.holdem.canon.flop.Flop

/**
 * 22/02/14 9:17 PM
 */
case class BucketAbstraction(abstraction: HoldemAbstraction)
    extends ExtensiveAbstraction[HoldemInfo, HoldemAction]
{
  def informationSetIndex(informationSet: HoldemInfo): Int = {
    val decoder: BucketDecoder =
      abstraction.decoder()

    val bucketTree: BucketTree =
      abstraction.tree(true)

    val actionSequenceIndex: Int =
      informationSet.actionSequence.index()

    val cardSequenceIndex: Int = {
      val canonHole = CanonHole.create(informationSet.hole)
      val holeBucket = bucketTree.getHole(canonHole.canonIndex())

      val community = informationSet.community

      if (community.hasFlop) {
        val flop = new Flop(canonHole, community)
        val flopSubBucket = bucketTree.getFlop(flop.canonIndex())
        val flopBucket = decoder.decode(holeBucket, flopSubBucket)
        val flopOffset = decoder.holeBucketCount()
        val flopIndex = flopBucket + flopOffset

        if (community.hasTurn) {
          val turn = flop.addTurn(community.turn())
          val turnSubBucket = bucketTree.getTurn(turn.canonIndex())
          val turnBucket = decoder.decode(holeBucket, flopSubBucket, turnSubBucket)
          val turnOffset = flopOffset + decoder.flopBucketCount()
          val turnIndex = turnBucket + turnOffset

          if (community.hasRiver) {
            val river = turn.addRiver(community.river())
            val riverSubBucket = bucketTree.getRiver(river.canonIndex())
            val riverBucket = decoder.decode(holeBucket, flopSubBucket, turnSubBucket, riverSubBucket)
            val riverOffset = turnOffset + decoder.turnBucketCount()
            val riverIndex = riverBucket + riverOffset

            riverIndex
          } else {
            turnIndex
          }
        } else {
          flopIndex
        }
      } else {
        holeBucket
      }
    }

    val cardSequenceCount: Int =
      decoder.holeBucketCount() +
      decoder.flopBucketCount() +
      decoder.turnBucketCount() +
      decoder.riverBucketCount()

    cardSequenceIndex + actionSequenceIndex * cardSequenceCount
  }



  def actionIndex(action: HoldemAction): Int =
    action match {
      case DecisionAction(choice) =>
        choice.ordinal()

      case _ => throw new Error
    }


  def actionSubIndex(informationSet: HoldemInfo, action: HoldemAction): Int =
    actionIndex(action)


  def actionCount(informationSet: HoldemInfo): Int = {
    informationSet.actionSequence.acts().size()
  }
}
