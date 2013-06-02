package ao.learn.mst.gen3.abstraction.impl

import ao.learn.mst.gen3.abstraction.{ExtensiveGameAbstraction, ExtensiveGameAbstractionBuilder}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.game.ExtensiveGame
import ao.learn.mst.gen3.representation.ExtensiveStateDescriber
import ao.learn.mst.gen3.ExtensiveAction
import ao.learn.mst.gen2.game.node.{ExtensiveGameDecision, ExtensiveGameNonTerminal, ExtensiveGameTerminal, ExtensiveGameNode}
import ao.learn.mst.gen2.player.model.RationalPlayer
import scala.collection.mutable
import scala.annotation.tailrec

/**
 * 01/06/13 9:20 PM
 */
object PerPlayerAbstractionBuilder
  extends ExtensiveGameAbstractionBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  def buildExtensiveGameAbstraction
    [I <: InformationSet, A <: ExtensiveAction] (
      extensiveGame: ExtensiveGame,
      informationSetDescriber: ExtensiveStateDescriber[I, A]
    ): ExtensiveGameAbstraction[I, A] =
  {
    val playerToActionSet: Map[RationalPlayer, Set[ExtensiveAction]] =
      playerActionIndex(extensiveGame.treeRoot)

    val playerToActionToActionIndex: Map[Int, Map[ExtensiveAction, Int]] =
      playerToActionSet.map((e:(RationalPlayer, Set[ExtensiveAction])) => (e._1.index, {
        val extensiveActions:Set[ExtensiveAction] =
          e._2

        var nextActionIndex: Int = 0
        val actionIndexPairs: Traversable[(ExtensiveAction, Int)] =
          extensiveActions.map((_, {
            nextActionIndex += 1
            nextActionIndex
          }))

        Map() ++ actionIndexPairs
      }))

    new DescribedAbstraction(
      extensiveGame,
      informationSetDescriber,
      playerToActionToActionIndex)
  }

  private def playerActionIndex(
      node: ExtensiveGameNode): Map[RationalPlayer, Set[ExtensiveAction]] =
  {
    val acc =
      new mutable.HashMap[RationalPlayer, mutable.Set[ExtensiveAction]]()
      with mutable.MultiMap[RationalPlayer, ExtensiveAction]

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

    (acc mapValues((v:mutable.Set[ExtensiveAction]) => {
      Set[ExtensiveAction]() ++ v
    })).toMap[RationalPlayer, Set[ExtensiveAction]]
  }



  //--------------------------------------------------------------------------------------------------------------------
  private class DescribedAbstraction[I <: InformationSet, A <: ExtensiveAction](
      extensiveGame: ExtensiveGame,
      informationSetDescriber: ExtensiveStateDescriber[I, A],
      playerToActionToActionIndex: Map[Int, Map[ExtensiveAction, Int]])
      extends ExtensiveGameAbstraction[I, A]
  {
    def infoSetCount: Int =
      extensiveGame.rationalPlayerCount

    def infoSetIndex(informationSet: I): Int =
      playedIndex(informationSet)

    def actionCount(informationSet: I): Int =
      playerToActionToActionIndex(playedIndex(informationSet)).size

    def actionIndex(informationSet: I, action: A): Int =
      playerToActionToActionIndex(playedIndex(informationSet))(action)


    private def playedIndex(informationSet: I):Int =
      informationSetDescriber.describe(informationSet).viewer.index
  }
}
