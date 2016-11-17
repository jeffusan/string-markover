package m

import java.io.File

import probability_monad._
import probability_monad.Distribution._

import scala.io.Source

class StringMarkov(inputFile: String) {

  private val input = fileToWordSeq(inputFile)
  private val frequencyTable = buildFrequencyTable(input)
  val r = scala.util.Random
  type FrequencyTable = Map[String, Distribution[String]]

  private[m] def fileToWordSeq(filePath: String): Seq[String] = Source.fromFile(new File(filePath)).getLines.flatMap(l => l.split(" ")).toSeq

  private[m] def buildFrequencyTable(input: Seq[String]): FrequencyTable = {
    val dists = for (key <- input.groupBy(identity).keys) yield {
      val nextWords: Seq[String] = input.sliding(2).filter(y => y.head == key).map(i => i(1)).toSeq
      key -> discreteUniform(nextWords)
    }
    dists.toMap
  }

  private[m] def getWordFromDistribution(distribution: Distribution[String]): String = try {
    distribution.sample(1).head
  } catch {
    case i: IllegalArgumentException =>
      // try again
      getNextWord(randomWord, frequencyTable)
  }

  private[m] def getNextWord(lastWord: String, frequencyTable: FrequencyTable): String = frequencyTable.get(lastWord) match {
    case None =>
      println("this shouldn't happen")
      ""
    case Some(dist) => getWordFromDistribution(dist)
  }

  private[m] def generateMessages(word: String, count: Int, frequencyTable: FrequencyTable, result: Seq[String] = Seq.empty): String = count match {
    case 0 => result.mkString(" ")
    case _ =>
      val nextWord = getNextWord(word, frequencyTable)
      generateMessages(nextWord, count - 1, frequencyTable, result :+ nextWord)
  }

  private[m] def randomWord: String = input(r.nextInt(input.size - 1))

  def markovChain(resultCount: Int): String = generateMessages(randomWord, resultCount, frequencyTable)

}
