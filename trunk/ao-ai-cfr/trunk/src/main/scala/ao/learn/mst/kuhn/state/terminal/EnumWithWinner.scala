package ao.learn.mst.kuhn.state.terminal

import ao.learn.mst.lib.Enum


trait EnumWithWinner extends Enum {
  type EnumVal <: Value with NamedWinnerIndicator
}