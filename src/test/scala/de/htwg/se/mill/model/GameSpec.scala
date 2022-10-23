package de.htwg.se.mill.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameSpec extends AnyWordSpec with Matchers {
  "A new Game" when {
    "created with a board and two players" should {
      val board = Board.withSize().get
      val melanie = Player("Melanie", "🔴")
      val players = Array(melanie, Player("Reyhan", "🔵"))
      val game = Game(board, players)
      "have the board" in {
        game.board should be(board)
      }
      "have the players" in {
        game.players should be(players)
      }
      "have the game state Setting" in {
        game.state should be(GameState.Setting)
      }
      "not equal a game with a different board" in {
        game.equals(
          game.copy(board =
            Board(
              board.fields.updated(
                board.fields.indexOf(Field(0, 0, 0)),
                Field(3, 0, 0, melanie.color)
              ),
              board.size
            )
          )
        ) should be(false)
      }
      "not equal a game with different players" in {
        game.equals(
          game.copy(players = Array(melanie, Player("max", melanie.color, 2)))
        ) should be(false)
      }
      "not equal a game with a different game state" in {
        game.equals(
          game.copy(state = GameState.Moving)
        ) should be(false)
      }
      "not equal a different object" in {
        game.equals("") should be(false)
      }
      "equal the same game" in {
        game.equals(game) should be(true)
      }
    }
  }
  "A game" when {
    val board = Board.withSize().get
    val melanie = Player("Melanie", "🔴")
    val reyhan = Player("Reyhan", "🔵")
    val players = Array(melanie, reyhan)
    val game = Game(board, players)
    val firstField = Field(0, 0, 0)
    "is set a piece correctly" should {
      "be not valid if its horizontal location is outside of the board" in {
        game.isValidSet(Field(3, 0, 0)) should be(false)
        game.isValidSet(Field(-1, 0, 0)) should be(false)
      }
      "be not valid if its vertical location is outside of the board" in {
        game.isValidSet(Field(0, 3, 0)) should be(false)
        game.isValidSet(Field(0, -1, 0)) should be(false)
      }

      "be not valid if its ring location is outside of the board" in {
        game.isValidSet(Field(0, 0, 3)) should be(false)
        game.isValidSet(Field(0, 0, -1)) should be(false)
      }
      "be not valid if its set on a field that is already in use" in {
        Game(
          Board(
            game.board.fields.updated(
              game.board.fields.indexOf(
                firstField
              ),
              new Field(firstField, reyhan.color)
            ),
            game.board.size
          ),
          players,
          game.state
        ).isValidSet(firstField) should be(false)
      }
      "be valid if its set on a correct field" in {
        Game(
          Board(
            game.board.fields.updated(
              game.board.fields.indexOf(
                firstField
              ),
              new Field(firstField, reyhan.color)
            ),
            game.board.size
          ),
          players,
          game.state
        ).isValidSet(Field(1, 0, 0)) should be(true)
      }
    }
    "is moved a piece correctly" should {
      "be not valid if there is a difference in x or y or ring" in {
        game.isValidMove(firstField, Field(-1, 0, 1)) should be(false)
        game.isValidMove(firstField, Field(1, 1, 1)) should be(false)
        game.isValidMove(firstField, Field(1, 1, 0)) should be(false)
        game.isValidMove(firstField, Field(1, 0, 1)) should be(false)
        game.isValidMove(firstField, Field(0, 1, 1)) should be(false)
        game.isValidMove(firstField, firstField) should be(false)
      }
      "be valid if there is only one difference in either x or y or ring" in {
        game.isValidMove(firstField, Field(1, 0, 0)) should be(true)
        game.isValidMove(firstField, Field(0, 1, 0)) should be(true)
        game.isValidMove(firstField, Field(0, 0, 1)) should be(true)
      }
    }
    "is moved a piece to a mill correctly" should {
      val boardWithMillOnFirstRow = Board(
        game.board.fields
          .updated(
            game.board.fields.indexOf(
              firstField
            ),
            new Field(firstField, melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 0)
            ),
            new Field(Field(1, 0, 0), melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(2, 0, 0)
            ),
            new Field(Field(2, 0, 0), melanie.color)
          ),
        game.board.size
      )
      val boardWithMillOnFirstColumn = Board(
        game.board.fields
          .updated(
            game.board.fields.indexOf(
              firstField
            ),
            new Field(firstField, melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(0, 1, 0)
            ),
            new Field(Field(0, 1, 0), melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(0, 2, 0)
            ),
            new Field(Field(0, 2, 0), melanie.color)
          ),
        game.board.size
      )
      val boardWithMillOnFirstRing = Board(
        game.board.fields
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 0)
            ),
            new Field(Field(1, 0, 0), melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 1)
            ),
            new Field(Field(1, 0, 1), melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 2)
            ),
            new Field(Field(1, 0, 2), melanie.color)
          ),
        game.board.size
      )

      "be invalid if there is no mill" in {
        game.isMill(
          firstField.copy(color = melanie.color),
          game.board
        ) should be(false)
      }
      "be invalid if there is a mill but the target field is wrong" in {
        new Game(boardWithMillOnFirstRow, game.players, game.state)
          .isMill(
            Field(1, 0, 1, melanie.color),
            boardWithMillOnFirstRow
          ) should be(false)
      }
      "be valid if there is a mill on a row and its a middle point" in {
        new Game(boardWithMillOnFirstRow, game.players, game.state)
          .isMill(
            Field(1, 0, 0, melanie.color),
            boardWithMillOnFirstRow
          ) should be(true)
      }
      "be valid if there is a mill on a column and its a middle point" in {
        new Game(boardWithMillOnFirstColumn, game.players, game.state)
          .isMill(
            Field(0, 1, 0, melanie.color),
            boardWithMillOnFirstColumn
          ) should be(true)
      }
      "be valid if there is a mill on a row" in {
        new Game(boardWithMillOnFirstRow, game.players, game.state)
          .isMill(
            Field(0, 0, 0, melanie.color),
            boardWithMillOnFirstRow
          ) should be(true)
      }
      "be valid if there is a mill on a column" in {
        new Game(boardWithMillOnFirstColumn, game.players, game.state)
          .isMill(
            Field(0, 0, 0, melanie.color),
            boardWithMillOnFirstColumn
          ) should be(true)
      }
      "be valid if there is a mill on a ring" in {
        new Game(boardWithMillOnFirstRing, game.players, game.state)
          .isMill(
            Field(1, 0, 1, melanie.color),
            boardWithMillOnFirstRing
          ) should be(true)
      }
    }
    "every player has set its stones" should {
      "be invalid if one player has not set its stones" in {
        game.everyPlayerHasSetItsStones(game.players) should be(false)
      }
      "be invalid if both players have set too many stones" in {
        game.everyPlayerHasSetItsStones(
          game.players
            .updated(
              0,
              game
                .players(0)
                .copy(setStones = Math.pow(game.board.size, 2).toInt + 1)
            )
            .updated(
              1,
              game
                .players(1)
                .copy(setStones = Math.pow(game.board.size, 2).toInt)
            )
        ) should be(false)
      }
      "be valid if both players have set their stones" in {
        game.everyPlayerHasSetItsStones(
          game.players
            .updated(
              0,
              game
                .players(0)
                .copy(setStones = Math.pow(game.board.size, 2).toInt)
            )
            .updated(
              1,
              game
                .players(1)
                .copy(setStones = Math.pow(game.board.size, 2).toInt)
            )
        ) should be(true)
      }
    }
    "is set a piece to a field" should {
      "be invalid if game state is not Setting" in {
        game
          .copy(state = GameState.Moving)
          .setPiece(melanie, firstField)
          .isFailure should be(true)
      }
      "be invalid if the field to be set is already in use" in {
        game
          .copy(board =
            Board(
              game.board.fields.updated(
                game.board.fields.indexOf(firstField),
                firstField.copy(color = melanie.color)
              ),
              game.board.size
            )
          )
          .setPiece(melanie, firstField)
          .isFailure should be(true)
      }
      "be valid with a new game state of Removing" in {
        val boardWithMillOnFirstRow = Board(
          game.board.fields
            .updated(
              game.board.fields.indexOf(
                Field(1, 0, 0)
              ),
              new Field(Field(1, 0, 0), melanie.color)
            )
            .updated(
              game.board.fields.indexOf(
                Field(2, 0, 0)
              ),
              new Field(Field(2, 0, 0), melanie.color)
            ),
          game.board.size
        )
        val gameAfterTurn = game
          .copy(board = boardWithMillOnFirstRow)
          .setPiece(melanie, firstField)
        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Removing)
      }
      "be valid with a new game state of Moving" in {
        val gameAfterTurn = game
          .copy(players =
            players
              .updated(
                1,
                reyhan.copy(setStones = Math.pow(game.board.size, 2).toInt)
              )
          )
          .setPiece(
            melanie.copy(setStones = Math.pow(game.board.size, 2).toInt - 1),
            firstField
          )
        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Moving)
      }
      "be valid with the same game state of Setting" in {
        val gameAfterTurn = game.setPiece(melanie, firstField)
        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Setting)
      }
    }
    "a piece is moved to a field" should {
      "fail if the game state is Setting" in {
        game
          .movePiece(melanie, firstField, Field(1, 0, 0))
          .isFailure should be(true)
      }
      "fail if the piece moved was not the players in turn" in {
        game
          .copy(state = GameState.Moving)
          .movePiece(
            melanie,
            firstField.copy(color = reyhan.color),
            Field(1, 0, 0)
          )
          .isFailure should be(true)
      }
      "fail if the game state is Moving but the move is not valid" in {
        game
          .copy(state = GameState.Moving)
          .movePiece(
            melanie,
            firstField.copy(color = melanie.color),
            Field(2, 0, 0)
          )
          .isFailure should be(true)
      }
      "fail if the game state is Flying but setting the piece is not valid" in {
        game
          .copy(state = GameState.Flying)
          .movePiece(
            melanie,
            firstField.copy(color = melanie.color),
            Field(-1, 0, 0)
          )
          .isFailure should be(true)
      }
      "succeed with a state of Removing if it is a mill" in {
        val boardWithMillOnFirstRow = Board(
          game.board.fields
            .updated(
              game.board.fields.indexOf(
                Field(1, 0, 0)
              ),
              new Field(Field(1, 0, 0), melanie.color)
            )
            .updated(
              game.board.fields.indexOf(
                Field(2, 0, 0)
              ),
              new Field(Field(2, 0, 0), melanie.color)
            ),
          game.board.size
        )
        val gameAfterTurn = game
          .copy(board = boardWithMillOnFirstRow, state = GameState.Moving)
          .movePiece(melanie, Field(0, 1, 0, melanie.color), firstField)
        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Removing)
      }
      "succeed with a state of Moving if the piece is moved correctly" in {
        val gameAfterTurn =
          game
            .copy(state = GameState.Moving)
            .movePiece(
              melanie,
              firstField.copy(color = melanie.color),
              Field(1, 0, 0)
            )
        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Moving)
      }
    }
    "a piece is removed from a field" should {
      "fail if the game state is not Removing" in {
        game.removePiece(melanie, firstField).isFailure should be(true)
      }
      "fail if the removed field has the same color as the player in turn" in {
        game
          .copy(
            board = Board(
              game.board.fields.updated(
                game.board.fields.indexOf(firstField),
                firstField.copy(color = melanie.color)
              ),
              game.board.size
            ),
            state = GameState.Removing
          )
          .removePiece(melanie, firstField.copy(color = melanie.color))
          .isFailure should be(true)
      }
      "fail if the removed field is unset" in {
        game
          .copy(state = GameState.Removing)
          .removePiece(melanie, firstField)
          .isFailure should be(true)
      }
      "fail if the field is part of a mill" in {
        val boardWithMillOnFirstRow = Board(
          game.board.fields
            .updated(
              game.board.fields.indexOf(firstField),
              firstField.copy(color = reyhan.color)
            )
            .updated(
              game.board.fields.indexOf(
                Field(1, 0, 0)
              ),
              new Field(Field(1, 0, 0), reyhan.color)
            )
            .updated(
              game.board.fields.indexOf(
                Field(2, 0, 0)
              ),
              new Field(Field(2, 0, 0), reyhan.color)
            ),
          game.board.size
        )
        val gameAfterTurn = game
          .copy(board = boardWithMillOnFirstRow, state = GameState.Removing)
          .removePiece(melanie, firstField.copy(color = reyhan.color))
          .isFailure should be(true)
      }
      "succeed with a state of Moving if every player has set its stones " +
        "and the other players pieces are more than the board size" in {
          val gameAfterTurn = game
            .copy(
              players = players
                .updated(
                  0,
                  melanie.copy(setStones = Math.pow(game.board.size, 2).toInt)
                )
                .updated(
                  1,
                  reyhan.copy(setStones = Math.pow(game.board.size, 2).toInt)
                ),
              board = Board(
                game.board.fields
                  .updated(
                    game.board.fields.indexOf(firstField),
                    firstField.copy(color = melanie.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(1, 0, 0)),
                    Field(1, 0, 0, reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(2, 0, 0)),
                    Field(2, 0, 0, reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(0, 1, 0)),
                    Field(0, 1, 0, reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(0, 2, 0)),
                    Field(0, 2, 0, reyhan.color)
                  ),
                game.board.size
              ),
              state = GameState.Removing
            )
            .removePiece(melanie, firstField.copy(color = reyhan.color))

          gameAfterTurn.isSuccess should be(true)
          gameAfterTurn.get.state should be(GameState.Moving)
        }
      "succeed with a state of Won if the other players pieces are less than the board size" in {
        val gameAfterTurn = game
          .copy(
            players = players
              .updated(
                0,
                melanie.copy(setStones = Math.pow(game.board.size, 2).toInt)
              )
              .updated(
                1,
                reyhan.copy(setStones = Math.pow(game.board.size, 2).toInt)
              ),
            board = Board(
              game.board.fields
                .updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = melanie.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(1, 0, 0)),
                  Field(1, 0, 0, reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(2, 0, 0)),
                  Field(2, 0, 0, reyhan.color)
                ),
              game.board.size
            ),
            state = GameState.Removing
          )
          .removePiece(melanie, firstField.copy(color = reyhan.color))

        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Won)
      }
      "succeed with a state of Flying if the other players pieces are equal to the board size" in {
        val gameAfterTurn = game
          .copy(
            players = players
              .updated(
                0,
                melanie.copy(setStones = Math.pow(game.board.size, 2).toInt)
              )
              .updated(
                1,
                reyhan.copy(setStones = Math.pow(game.board.size, 2).toInt)
              ),
            board = Board(
              game.board.fields
                .updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = melanie.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(1, 0, 0)),
                  Field(1, 0, 0, reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(2, 0, 0)),
                  Field(2, 0, 0, reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(0, 1, 0)),
                  Field(0, 1, 0, reyhan.color)
                ),
              game.board.size
            ),
            state = GameState.Removing
          )
          .removePiece(melanie, firstField.copy(color = reyhan.color))

        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Flying)
      }
      "succeed with a state of Setting if the piece is removed correctly" in {
        val gameAfterTurn = game
          .copy(
            board = Board(
              game.board.fields
                .updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = melanie.color)
                ),
              game.board.size
            ),
            state = GameState.Removing
          )
          .removePiece(melanie, firstField.copy(color = reyhan.color))

        gameAfterTurn.isSuccess should be(true)
        gameAfterTurn.get.state should be(GameState.Setting)
      }
    }
  }
}
