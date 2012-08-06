package ao.learn.mst.example.kuhn.state.terminal

import ao.learn.mst.example.kuhn.state.KuhnPosition._

/**
 * Date: 10/11/11
 * Time: 4:23 AM
 */
trait NamedWinnerIndicator {
  def preShowdownWinner : Option[KuhnPosition]
  def name              : String
}