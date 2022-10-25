package de.htwg.se.mill.model

import scala.util.{Try, Success, Failure}
import de.htwg.se.mill.util.Messages

sealed trait GameState(val game: Game) {
  override def equals(state: Any): Boolean = state match {
    case s: GameState => s.game.equals(game)
    case _            => false
  }
  def nextState(game: Game, fields: (Field, Option[Field])): GameState
  def execute(fields: (Field, Option[Field])): Try[GameState]
  def handle(
      e: GameEvent,
      fields: (Field, Option[Field])
  ): Try[GameState] = {
    e match {
      case GameEvent.OnSetting =>
        if (!this.isInstanceOf[SettingState])
          return Failure(
            IllegalArgumentException(
              Messages.isNotSettingStateMessage
            )
          )

      case GameEvent.OnMoving =>
        if (!this.isInstanceOf[MovingState])
          return Failure(
            IllegalArgumentException(
              Messages.isNotMovingStateMessage
            )
          )

      case GameEvent.OnFlying =>
        if (!this.isInstanceOf[FlyingState])
          return Failure(
            IllegalArgumentException(
              Messages.isNotFlyingStateMessage
            )
          )
      case GameEvent.OnRemoving =>
        if (!this.isInstanceOf[RemovingState])
          return Failure(
            IllegalArgumentException(
              Messages.isNotRemovingStateMessage
            )
          )

    }
    execute(fields)
  }
}

private trait Moving(game: Game) {
  def movePiece(fields: (Field, Option[Field])): Try[Game] = {
    if (fields(0).color != game.currentPlayer.color)
      return Failure(
        IllegalArgumentException(
          Messages.movedOtherPieceMessage
        )
      )

    Success(
      game.copy(board =
        Board(
          game.board.fields
            .updated(
              game.board.fields.indexOf(fields(0)),
              fields(0).copy(color = fields(0).unsetFieldColor)
            )
            .updated(
              game.board.fields.indexOf(fields(1).get),
              fields(1).get.copy(color = game.currentPlayer.color)
            ),
          game.board.size
        )
      )
    )
  }
}

case class MovingState(override val game: Game)
    extends GameState(game)
    with Moving(game) {
  override def nextState(
      game: Game,
      fields: (Field, Option[Field])
  ): GameState =
    if (game.isMill(fields(1).get)) RemovingState(game)
    else copy(game)
  override def execute(fields: (Field, Option[Field])): Try[GameState] = {
    if (!game.isValidMove(fields(0), fields(1).get))
      return Failure(
        IllegalArgumentException(
          Messages.invalidMoveFieldAlreadyInUseMessage
        )
      )

    movePiece(fields).map(game => nextState(game, fields))
  }
}
case class SettingState(override val game: Game) extends GameState(game) {
  override def nextState(
      game: Game,
      fields: (Field, Option[Field])
  ): GameState =
    if (game.isMill(fields(0))) RemovingState(game)
    else if (
      game.copy(setStones = game.setStones + 1).everyPlayerHasSetItsStones
    ) MovingState(game)
    else copy(game)

  override def execute(fields: (Field, Option[Field])): Try[GameState] = {
    if (!game.isValidSet(fields(0)))
      return Failure(
        IllegalArgumentException(
          Messages.invalidSetFieldAlreadyInUseMessage
        )
      )
    Success(
      nextState(
        game.copy(
          board = Board(
            game.board.fields.updated(
              game.board.fields.indexOf(fields(0)),
              fields(0).copy(color = game.currentPlayer.color)
            ),
            game.board.size
          )
        ),
        fields
      )
    )
  }
}
case class RemovingState(
    override val game: Game
) extends GameState(game) {
  override def nextState(
      game: Game,
      fields: (Field, Option[Field])
  ): GameState = {
    var nextState: GameState = SettingState(game)
    if (game.everyPlayerHasSetItsStones) {
      nextState = MovingState(game)
      val otherPlayersPieces = game.board.fields.count(field =>
        field.color == game.players
          .find(p => !p.equals(game.currentPlayer))
          .get
          .color
      )
      if (otherPlayersPieces == game.board.size) {
        nextState = FlyingState(game)
      }
    }
    nextState
  }
  override def execute(fields: (Field, Option[Field])): Try[GameState] = {
    if (fields(0).color == game.currentPlayer.color)
      return Failure(
        IllegalArgumentException(
          Messages.invalidRemoveFieldOwnPieceMessage
        )
      )

    if (fields(0).color == fields(0).unsetFieldColor)
      return Failure(
        IllegalArgumentException(
          Messages.invalidRemoveFieldUnsetFieldMessage
        )
      )

    if (game.isMill(fields(0)))
      return Failure(
        IllegalArgumentException(
          Messages.invalidRemoveFieldOnAMillMessage
        )
      )

    Success(
      nextState(
        game.copy(
          board = Board(
            game.board.fields
              .updated(
                game.board.fields.indexOf(fields(0)),
                fields(0).copy(color = fields(0).unsetFieldColor)
              ),
            game.board.size
          )
        ),
        fields
      )
    )
  }
}
case class FlyingState(
    override val game: Game
) extends GameState(game)
    with Moving(game) {
  override def nextState(
      game: Game,
      fields: (Field, Option[Field])
  ): GameState = if (game.isMill(fields(1).get)) RemovingState(game)
  else copy(game)
  override def execute(fields: (Field, Option[Field])): Try[GameState] = {
    if (!game.isValidSet(fields(1).get))
      return Failure(
        IllegalArgumentException(
          Messages.invalidFlyingFieldAlreadyInUseMessage
        )
      )
    movePiece(fields).map(game => nextState(game, fields))
  }
}

enum GameEvent {
  case OnSetting extends GameEvent
  case OnMoving extends GameEvent
  case OnRemoving extends GameEvent
  case OnFlying extends GameEvent
}
