package ao.learn.mst.cfr

import ao.learn.mst.gen2.player.RationalPlayer
import ao.learn.mst.gen2.game.{ExtensiveGame, ExtensiveGameDecision, ExtensiveGameTerminal, ExtensiveGameNode}


//----------------------------------------------------------------------------------------------------------------------
object PlayerViewBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  def expand(game : ExtensiveGame, protagonist : RationalPlayer) : PlayerViewNode =
    expand(game, game.gameTreeRoot, protagonist)
  
  private def expand(
      game        : ExtensiveGame,
      root        : ExtensiveGameNode,
      protagonist : RationalPlayer
      ): PlayerViewNode =
  {
    root match {
      case decision : ExtensiveGameDecision => {
//        decision.informationSet

        val kids : Seq[PlayerViewNode] =
          decision.actions
            .map(decision.child(_))
            .map(expand(game, _, protagonist))
            .toIndexedSeq

        if (decision.player == protagonist) {
          new ProponentNode( kids, decision.informationSet )
        } else {
          new OpponentNode( kids )
        }
      }

      case t : ExtensiveGameTerminal => {
        val rationalPlayers: Seq[RationalPlayer] =
          (0 until game.rationalPlayerCount).map( RationalPlayer(_) )
        
        new TerminalNode( rationalPlayers.map(t.payoff.outcomes(_)) )
      }
    }
  }
}