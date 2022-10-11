import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Board
import scala.collection.immutable.ListMap

val nineMensMorris = new Board(3)
val largestRow = nineMensMorris.fields.maxBy(_.y).y
val upperSection = nineMensMorris.fields
  .filter(field => field.y == 0)
val lowerSection = nineMensMorris.fields.filter(field => field.y == largestRow)
val test = ListMap(
  lowerSection
    .groupBy(_.ring)
    .toSeq
    .sortWith(_._1 > _._1): _*
)
  .map(fields =>
    fields(1)
      .map(field => s"(${field.x}, ${field.y}, ${field.ring})")
      .mkString(",")
  )
  .mkString(",")
val upperSectionFormatted = upperSection
  .map(field => s"(${field.x}, ${field.y}, ${field.ring})")
  .mkString(",")
nineMensMorris.fieldsDump
val Melanie = Player("Melanie", "🔴")
val Reyhan = Player("Reyhan", "🔵")
val bigBoard = new Board(5)
bigBoard.fieldsDump
