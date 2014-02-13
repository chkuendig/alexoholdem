package ao.holdem.gen

import ao.holdem.engine.state.tree.StateTree
import ao.holdem.model.card.{Community, Hole}


case class HoldemInfo(
    actionSequence: StateTree.Node,
    hole: Hole,
    community: Community)
