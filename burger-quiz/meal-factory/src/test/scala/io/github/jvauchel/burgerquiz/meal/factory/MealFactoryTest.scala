package io.github.jvauchel.burgerquiz.meal.factory

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen}

import java.io.File
import java.util.UUID

class MealFactoryTest extends AnyFeatureSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with GivenWhenThen {

  private val stringSerializer = new StringSerializer()
  private val stringDeserializer = new StringDeserializer()

  private var driver: TopologyTestDriver = _
  private var topicBurger: TestInputTopic[String, String] = _
  private var topicSideDishes: TestInputTopic[String, String] = _
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
    MealFactory.topology(builder)
    builder.build()
  }

  override def beforeEach(): Unit = {
    MealFactory.config.put(StreamsConfig.STATE_DIR_CONFIG, tempDir.getAbsolutePath)
    driver = new TopologyTestDriver(buildTopology(), MealFactory.config)
    topicBurger = driver.createInputTopic(MealFactory.topicBurger, stringSerializer, stringSerializer)
    topicSideDishes = driver.createInputTopic(MealFactory.topicSideDishes, stringSerializer, stringSerializer)
    topicMeal = driver.createOutputTopic(MealFactory.topicMeal, stringDeserializer, stringDeserializer)
  }

  override def afterEach(): Unit = {
    driver.close()
  }

  Feature("Meal") {
    Scenario("All items") {
      Given("All items")
      When("🧑‍🍳")
      topicBurger.pipeInput("🧑‍🍳", "🍔")
      topicSideDishes.pipeInput("🧑‍🍳", "🍟")

      Then("Meal")
      topicMeal.getQueueSize shouldBe 1
      topicMeal.readKeyValue() shouldBe new KeyValue("🧑‍🍳", "(🍔 + 🍟)")
    }

    Scenario("Incomplete items") {
      Given("Incomplete items")
      When("🧑‍🍳")
      topicBurger.pipeInput("🧑‍🍳", "🍔")

      Then("Meal")
      topicMeal.getQueueSize shouldBe 0
    }
  }

}
