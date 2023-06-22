package io.github.jvauchel.burgerquiz


import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen}

import java.io.File
import java.time.Duration
import java.util.UUID

class KafkaStreamBurgerQuizTest extends AnyFeatureSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with GivenWhenThen {

  private val stringSerializer = new StringSerializer()
  private val stringDeserializer = new StringDeserializer()

  private var driver: TopologyTestDriver = _
  private var topicTomato: TestInputTopic[String, String] = _
  private var topicBread: TestInputTopic[String, String] = _
  private var topicMeat: TestInputTopic[String, String] = _
  private var topicBurger: TestOutputTopic[String, String] = _
  private var topicPotato: TestInputTopic[String, String] = _
  private var topicDrink: TestInputTopic[String, String] = _
  private var topicMeal: TestOutputTopic[String, String] = _

  private def tempDir: File = {
    val ioDir = System.getProperty("java.io.tmpdir")
    val f = new File(ioDir, "kafka-" + UUID.randomUUID().toString)
    f.mkdirs()
    f.deleteOnExit()
    f
  }

  private def buildTopology(): Topology = {
    import org.apache.kafka.streams.scala.StreamsBuilder
    val builder = new StreamsBuilder
    KafkaStreamBurgerQuiz.topology(builder)
    builder.build()
  }

  override def beforeEach(): Unit = {
    KafkaStreamBurgerQuiz.config.put(StreamsConfig.STATE_DIR_CONFIG, tempDir.getAbsolutePath)
    driver = new TopologyTestDriver(buildTopology(), KafkaStreamBurgerQuiz.config)

    topicTomato = driver.createInputTopic(KafkaStreamBurgerQuiz.topicTomato, stringSerializer, stringSerializer)
    topicBread = driver.createInputTopic(KafkaStreamBurgerQuiz.topicBread, stringSerializer, stringSerializer)
    topicMeat = driver.createInputTopic(KafkaStreamBurgerQuiz.topicMeat, stringSerializer, stringSerializer)
    topicBurger = driver.createOutputTopic(KafkaStreamBurgerQuiz.topicBurger, stringDeserializer, stringDeserializer)

    topicPotato = driver.createInputTopic(KafkaStreamBurgerQuiz.topicPotato, stringSerializer, stringSerializer)
    topicDrink = driver.createInputTopic(KafkaStreamBurgerQuiz.topicDrink, stringSerializer, stringSerializer)
    topicMeal = driver.createOutputTopic(KafkaStreamBurgerQuiz.topicMeal, stringDeserializer, stringDeserializer)
  }

  override def afterEach(): Unit = {
    driver.close()
  }

  Feature("Burger") {
    Scenario("Join with all items") {
      val hungryMan = "🤤"

      topicBread.pipeInput(hungryMan, "🍞")
      topicTomato.pipeInput(hungryMan, "🍅")
      topicMeat.pipeInput(hungryMan, "🥩")

      topicBurger.getQueueSize shouldBe 1
      topicBurger.readKeyValue() shouldBe new KeyValue(hungryMan, "🍔")
    }
    Scenario("Join with incomplete items") {
      val hungryMan = "🤤"

      topicBread.pipeInput(hungryMan, "🍞")
      topicMeat.pipeInput(hungryMan, "🥩")

      topicBurger.getQueueSize shouldBe 0
    }
  }

  Feature("Meal") {
    Scenario("Left join with all items") {
      val hungryMan = s"🤤"

      topicBread.pipeInput(hungryMan, "🍞")
      topicTomato.pipeInput(hungryMan, "🍅")
      topicMeat.pipeInput(hungryMan, "🥩")
      topicMeal.readKeyValue() shouldBe new KeyValue(hungryMan, "🍔")

      topicPotato.pipeInput(hungryMan, "🥔")
      topicMeal.readKeyValue() shouldBe new KeyValue(hungryMan, "🍔 + 🍟")

      topicDrink.pipeInput(hungryMan, "🍺")
      topicMeal.readKeyValue() shouldBe new KeyValue(hungryMan, "🍔 + 🍺")
      topicMeal.readKeyValue() shouldBe new KeyValue(hungryMan, "🍔 + 🍟 + 🍺")
    }

    Scenario("Left join with incomplete items") {
      val hungryMan = s"🤤"

      topicBread.pipeInput(hungryMan, "🍞")
      topicTomato.pipeInput(hungryMan, "🍅")
      topicMeat.pipeInput(hungryMan, "🥩")
      topicDrink.pipeInput(hungryMan, "🍷")

      topicMeal.getQueueSize shouldBe 2
      topicMeal.readKeyValue() shouldBe new KeyValue(hungryMan, "🍔")
      topicMeal.readKeyValue() shouldBe new KeyValue(hungryMan, "🍔 + 🍷")
    }
  }

}
