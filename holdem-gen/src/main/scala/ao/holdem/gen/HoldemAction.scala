package ao.holdem.gen

import ao.holdem.model.act.AbstractAction
import ao.holdem.model.card.Card


trait HoldemAction

case class DecisionAction(choice: AbstractAction) extends HoldemAction

case class HoleChance(holeA: Card, holeB: Card) extends HoldemAction
case class FlopChance(flopA: Card, flopB: Card, flopC: Card) extends HoldemAction
case class TurnChance(turn: Card) extends HoldemAction
case class RiverChance(river: Card) extends HoldemAction