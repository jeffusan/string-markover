package m

import org.scalatest.{FlatSpec, Matchers}


class MarkovSpec extends FlatSpec with Matchers {

  "When provided an input file it" should " generate a markov chain" in {
    val markov = new StringMarkov("src/test/resources/testinput.txt")
    println(markov.markovChain(24) )
  }
}
