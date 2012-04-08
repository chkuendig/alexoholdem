package ao.learn.mst.gen2.info

import ao.learn.mst.gen2.game.{ExtensiveGameNonTerminal, ExtensiveGameDecision, ExtensiveGameNode, ExtensiveGame}
import collection.mutable


/**
 * Date: 07/04/12
 * Time: 7:54 PM
 */
object TraversingInformationSetIndexer
{
  //--------------------------------------------------------------------------------------------------------------------
  def index(extensiveGame: ExtensiveGame): InformationSetIndex =
  {
    var informationSets = Map[InformationSet, Int]()

    traverse(extensiveGame.gameTreeRoot, (informationSet: InformationSet) => {
      if (! informationSets.contains( informationSet )) {
        informationSets = informationSets +
          (informationSet -> informationSets.size)
      }
    })

    new MappedInformationSetIndex(
      informationSets)
  }

  private def traverse(node: ExtensiveGameNode, visitor: (InformationSet) => Unit)
  {
    node match {
      case decision: ExtensiveGameDecision => {
        visitor( decision.informationSet )
      }
      case _ => {}
    }

    node match {
      case nonTerminal: ExtensiveGameNonTerminal => {
        for (action <- nonTerminal.actions) {
          traverse(nonTerminal.child( action ), visitor)
        }
      }

      case _ => {}
    }
  }
  
  
  //--------------------------------------------------------------------------------------------------------------------
  private class MappedInformationSetIndex(val index: Map[InformationSet, Int])
      extends InformationSetIndex
  {
    def informationSetCount = index.size

    def indexOf(informationSet: InformationSet) =
      index(informationSet)

    def informationSets = index.keys
  }
}
