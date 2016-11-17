# String Generator Using Markov Chains

This is an example application demonstrating a means of generating text using markov chains.

It uses the [probability-monad] to calculate probabilities.

## To run

There are two ways

```
$> sbt test
```

This will run the unit test, which will read the input from `src/test/resources/testinput.txt`, generate 24 words, and print them to stdout.

Or use the sbt console:

```
$> sbt console

scala> import m._
import m._

scala> val mark = new StringMarkov("src/test/resources/testinput.txt")
mark: m.StringMarkov = m.StringMarkov@6ecc603e

scala> println(mark.markovChain(25))
and then they were followed a satisfied smile. "I beg your hair into a large kinds. But the Queen gave them ever be master--that's all."
```