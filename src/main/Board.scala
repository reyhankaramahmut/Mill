import java.lang.IllegalArgumentException
import scala.collection.immutable.ListMap

case class Board(fields: List[Field], size: Int) {
  val endOfLine = sys.props("line.separator")
  val lineHeight = 1
  val barWidth = 4
  val spaceWidth = 3
  @throws(classOf[IllegalArgumentException])
  // auxiliary constructor that creates the initial board with its fields respectively
  def this(size: Int) = this(
    // check if board size is valid
    if (size < 3 || size % 2 == 0)
      throw new IllegalArgumentException(
        "Invalid board size. Should be uneven and greater than 2."
      )
    // construct initial fields
    else
      (for {
        ring <- 0 until size
        row <- 0 until size
        col <- 0 until size;
        // filter out inner fields
        if !(row > 0 && row < size - 1 && col > 0 && col < size - 1)
      } yield Field(col, row, ring)).toList,
    size
  )
  override def toString(): String = {
    def bar(width: Int = barWidth) = "―" * width
    def line(height: Int = lineHeight) = "│" * height
    def space(width: Int = spaceWidth) = " " * width
    def dividerRow =
      (line() + space()) * size + space() * (size - 1) + (space() + line()) * size
    +endOfLine
    def formattedFieldsByRing = (fieldsByRing: (Int, List[Field])) =>
      (line() + space()) * fieldsByRing(0) + fieldsByRing(1)
        .mkString(bar((((size - 1) - fieldsByRing(0)) + 1) * barWidth))
    +(space() + line()) * fieldsByRing(0)
    val middleSection =
      fields
        .filter(field => field.y > 0 && field.y < size - 1)
    val middleSections = middleSection.splitAt(middleSection.length / 2)

    val upperSection = fields
      .filter(field => field.y == 0)
      .groupBy(_.ring)
      .map(formattedFieldsByRing)
      .mkString(endOfLine)
    +endOfLine
    val lowerSection = ListMap(
      fields
        .filter(field => field.y == size - 1)
        .groupBy(_.ring)
        .toSeq
        .sortWith(_._1 > _._1): _*
    )
      .map(formattedFieldsByEing)
      .mkString(endOfLine)

    endOfLine
    +upperSection
    +dividerRow
    +middleSection(0).mkString(
      bar(barWidth / 2)
    ) + space() * size + middleSections(1).mkString(
      bar(barWidth / 2)
    ) + endOfLine
    +dividerRow
    +lowerSection
  }
  def fieldsDump = fields
    .map(field => s"(${field.x}, ${field.y}, ${field.ring})")
    .mkString(",")
}
