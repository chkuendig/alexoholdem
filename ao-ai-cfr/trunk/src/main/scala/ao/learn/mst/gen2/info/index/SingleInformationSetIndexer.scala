package ao.learn.mst.gen2.info.index

import ao.learn.mst.gen2.game._
import scala.annotation.tailrec
import scala.collection.immutable.SortedSet
import ao.learn.mst.gen2.player.model.FiniteAction
import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex}
import ao.learn.mst.gen2.info.set.ValueInformationSet
import ao.learn.mst.gen2.game.node.{ExtensiveGameTerminal, ExtensiveGameNonTerminal, ExtensiveGameNode, ExtensiveGameDecision}

//----------------------------------------------------------------------------------------------------------------------
/**
 *
 */
object SingleInformationSetIndexer
{
  //--------------------------------------------------------------------------------------------------------------------
  def single(extensiveGame: ExtensiveGame): InformationSetIndex[InformationSet] =
  {
    new MappedInformationSetIndex(
      actionDecisionNodeIndex(
        extensiveGame.treeRoot))
  }

  private def actionDecisionNodeIndex(
      node: ExtensiveGameNode): Set[FiniteAction] =
  {
    @tailrec def accumulateActions(
        stack: List[ExtensiveGameNode],
        acc: SortedSet[FiniteAction]
        ): SortedSet[FiniteAction] =
    {
      stack match {
        case Nil => acc

        case (_: ExtensiveGameTerminal) :: rest =>
          accumulateActions(rest, acc)

        case (nonTerminal: ExtensiveGameNonTerminal) :: rest => {

          val nextAcc:SortedSet[FiniteAction] =
            nonTerminal match {
              case decision: ExtensiveGameDecision =>
                acc ++ decision.actions

              case _ => acc
            }

          val children:List[ExtensiveGameNode] =
            for (action <- nonTerminal.actions.toList)
            yield nonTerminal.child( action )

          accumulateActions(
            children ::: rest,
            nextAcc)
        }
      }
    }

    accumulateActions(List(node), SortedSet())
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class MappedInformationSetIndex(
      val index: Set[FiniteAction]
      ) extends InformationSetIndex[InformationSet]
  {
    def informationSetCount = 1

    def indexOf(informationSet: InformationSet) = 0

    def informationSets = List(new ValueInformationSet("singleton"))

    def actionsOf(informationSet: InformationSet) =
      index
  }
}