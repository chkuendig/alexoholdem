package ao.learn.mst.gen2.info.index

import ao.learn.mst.gen2.game._
import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex}
import ao.learn.mst.gen2.player.model.{RationalPlayer, FiniteAction}
import ao.learn.mst.gen2.info.set.{PlayerInformationSet, ValueInformationSet}
import scala.annotation.tailrec
import ao.learn.mst.gen2.info.set.impl.LiteralPlayerInformationSet
import ao.learn.mst.gen2.player.model.RationalPlayer
import scala.collection.mutable
import scala.collection.immutable.SortedSet
import ao.learn.mst.gen2.game.node.{ExtensiveGameTerminal, ExtensiveGameNonTerminal, ExtensiveGameNode, ExtensiveGameDecision}

/**
 * The minimal definition of a formal information set partition.
 */
object PlayerInformationSetIndexer
{
  //--------------------------------------------------------------------------------------------------------------------
  def playerIndex(extensiveGame: ExtensiveGame): InformationSetIndex[PlayerInformationSet] =
    new MappedInformationSetIndex(
      playerActionIndex(extensiveGame.treeRoot))


  private def playerActionIndex(
      node: ExtensiveGameNode): Map[RationalPlayer, Set[FiniteAction]] =
  {
    val acc =
      new mutable.HashMap[RationalPlayer, mutable.Set[FiniteAction]]()
      with mutable.MultiMap[RationalPlayer, FiniteAction]

    @tailrec def accumulateActions(
        stack: List[ExtensiveGameNode])
    {
      stack match {
        case Nil =>
        case (_: ExtensiveGameTerminal) :: rest =>
          accumulateActions(rest)

        case (nonTerminal: ExtensiveGameNonTerminal) :: rest => {

          nonTerminal match {
            case decision: ExtensiveGameDecision =>
              decision.actions.foreach(
                acc.addBinding(decision.player, _))
          }

          val children:List[ExtensiveGameNode] =
            for (action <- nonTerminal.actions.toList)
            yield nonTerminal.child( action )

          accumulateActions(
            children ::: rest)
        }
      }
    }

    accumulateActions(List(node))

    (acc mapValues((v:mutable.Set[FiniteAction]) => {
      SortedSet[FiniteAction]() ++ v
    })).toMap[RationalPlayer, Set[FiniteAction]]
  }



  //--------------------------------------------------------------------------------------------------------------------
  private class MappedInformationSetIndex(
      val index: Map[RationalPlayer, Set[FiniteAction]]
      ) extends InformationSetIndex[PlayerInformationSet]
  {
    def informationSetCount =
      index.size

    def indexOf(informationSet: PlayerInformationSet) =
      players.indexOf(informationSet.nextToAct)

    def actionsOf(informationSet: PlayerInformationSet) =
      index(informationSet.nextToAct)

    def informationSets =
      players.map(LiteralPlayerInformationSet(_))

    def players:Seq[RationalPlayer] =
      index.keys.toList
  }
}
