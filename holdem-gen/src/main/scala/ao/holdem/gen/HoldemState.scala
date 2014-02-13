package ao.holdem.gen

import ao.holdem.engine.state.tree.StateTree
import ao.holdem.model.card.{Card, Community, Hole}

case class HoldemState(
    actionSequence: StateTree.Node,
    holes: Seq[Hole],
    community: Community)
{
  private val cards: Set[Card] =
    Card.values().toSet

  def deck: Set[Card] =
    cards.filterNot(card =>
      community.contains(card) ||
      holes.exists(_.contains(card)))
}