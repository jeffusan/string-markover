package m

import java.io.File

import probability_monad._
import probability_monad.Distribution._

import scala.io.Source
import scala.util.Try

class StringMarkov(inputFile: String) {

  private val input = fileToWordSeq(inputFile)
  private val frequencyTable = buildFrequencyTable(input)
  private val r = scala.util.Random
  type FrequencyTable = Map[String, Distribution[String]]

  private[m] def fileToWordSeq(filePath: String): Seq[String] = {
    Source.fromFile(new File(filePath)).getLines.flatMap(l => l.split(" ")).map(a => a.toUpperCase).toSeq
  }

  /*
   * Create a map representing a frequency table. It contains a key representing a word,
   * whose value is a distribution of words that follow within the input corpus.
   */
  private[m] def buildFrequencyTable(input: Seq[String]): FrequencyTable = {
    val dists = for (key <- input.groupBy(identity).keys) yield {
      val nextWords: Seq[String] = input.sliding(2).filter(y => y.head == key).map(i => i(1)).toSeq
      key -> discreteUniform(nextWords)
    }
    dists.toMap
  }

  /*
   * Get a sample word from the distribution.
   * Note that generating the distribution is a bit rough, hence the try/catch.
   * In the event that the distribution has no values, get a random word and try again.
   */
  private[m] def getNextWord(lastWord: String, frequencyTable: FrequencyTable): Try[String] = {
    Try({
      val distribution = frequencyTable.get(lastWord).get
      distribution.sample(1).head
    }).recoverWith({
      case i: IllegalArgumentException =>
        // try again
        getNextWord(randomWord, frequencyTable)
    })
  }

  private[m] def generateMessages(word: String, count: Int, frequencyTable: FrequencyTable, result: Seq[String] = Seq.empty): String = count match {
    case 0 => result.mkString(" ")
    case _ =>
      val nextWord = getNextWord(word, frequencyTable).get//This seems reasonable to Throw the exception here and halt the program.
      generateMessages(nextWord, count - 1, frequencyTable, result :+ nextWord)
  }

  private[m] def randomWord: String = input(r.nextInt(input.size - 1))

  def markovChain(resultCount: Int): String = {
    generateMessages(randomWord, resultCount, frequencyTable)
  }

  def markovChain(resultCount: Int, startWord: String): String = {
    generateMessages(startWord.toUpperCase, resultCount, frequencyTable, Seq(startWord.toUpperCase))
  }

}
