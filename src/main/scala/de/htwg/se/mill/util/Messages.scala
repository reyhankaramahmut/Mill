package de.htwg.se.mill.util

object Messages {
  // GameState
  val pieceNotMovedMessage = "The piece was not moved.\n"
  val pieceNotSetMessage = "The piece was not set.\n"
  val pieceNotRemovedMessage = "The piece was not removed.\n"
  val isNotSettingStateMessage =
    s"${pieceNotSetMessage} All pieces are already set."
  val isNotMovingStateMessage =
    s"${pieceNotMovedMessage} Please provide a valid input for moving or flying a piece."
  val isNotFlyingStateMessage =
    s"${pieceNotMovedMessage} Please provide a valid input for moving or flying a piece."
  val isNotRemovingStateMessage =
    s"${pieceNotRemovedMessage} Please provide a valid input for removing a piece."
  val movedOtherPieceMessage =
    s"${pieceNotMovedMessage} You can only move your own pieces."
  val invalidMoveFieldAlreadyInUseMessage =
    s"${pieceNotMovedMessage} Please use a valid field that is not already in use."
  val invalidSetFieldAlreadyInUseMessage =
    s"${pieceNotSetMessage}Please use a valid field that is not already in use."
  val invalidRemoveFieldOwnPieceMessage =
    s"${pieceNotRemovedMessage}You cannot remove your own pieces."
  val invalidRemoveFieldUnsetFieldMessage =
    s"${pieceNotRemovedMessage}You cannot remove unset fields."
  val invalidRemoveFieldOnAMillMessage =
    s"${pieceNotRemovedMessage}You cannot remove pieces on a mill."
  val invalidFlyingFieldAlreadyInUseMessage =
    s"${pieceNotMovedMessage}Please use a valid field that is not already in use."
  // TUI
  val wrongCommandMessage = "Your command is wrong. Please check it again. "
  val wrongSettingOrRemovingCommandMessage =
    wrongCommandMessage + "Should be something like 111 for removing or setting pieces."
  val wrongMovingOrFlyingCommandMessage =
    wrongCommandMessage + "Should be something like 111 121 for moving pieces."
  val wrongFieldPositionMessage =
    wrongCommandMessage + "The field position provided is invalid."
  val wrongTargetFieldPositionMessage =
    wrongCommandMessage + "The field position where the piece should be moved to is invalid."
}
