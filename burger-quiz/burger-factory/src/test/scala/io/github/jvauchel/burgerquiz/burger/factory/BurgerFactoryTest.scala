package io.github.jvauchel.burgerquiz.burger.factory

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen}

import java.io.File
import java.util.UUID

class BurgerFactoryTest extends AnyFeatureSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with GivenWhenThen {

  private val stringSerializer = new StringSerializer()
  private val stringDeserializer = new StringDeserializer()

  private var driver: TopologyTestDriver = _
  private var topicVegetable: TestInputTopic[String, String] = _
  private var topicBread: TestInputTopic[String, String] = _
  private var topicMeat: TestInputTopic[String, String] = _
  private var topicBurger: TestOutputTopic[String, String] = _

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
    BurgerFactory.topology(builder)
    builder.build()
  }

  override def beforeEach(): Unit = {
    BurgerFactory.config.put(StreamsConfig.STATE_DIR_CONFIG, tempDir.getAbsolutePath)
    driver = new TopologyTestDriver(buildTopology(), BurgerFactory.config)

    topicVegetable = driver.createInputTopic(BurgerFactory.topicVegetable, stringSerializer, stringSerializer)
    topicBread = driver.createInputTopic(BurgerFactory.topicBread, stringSerializer, stringSerializer)
    topicMeat = driver.createInputTopic(BurgerFactory.topicMeat, stringSerializer, stringSerializer)
    topicBurger = driver.createOutputTopic(BurgerFactory.topicBurger, stringDeserializer, stringDeserializer)
  }

  override def afterEach(): Unit = {
    driver.close()
  }

  Feature("Burger") {
    Scenario("Join with all items for a burger") {
      Given("good inputs")
      When("🧑‍🍳")
      topicBread.pipeInput("🧑‍🍳", "🍞")
      topicVegetable.pipeInput("🧑‍🍳", "🍅")
      topicMeat.pipeInput("🧑‍🍳", "🥩")
      Then("🍔")
      topicBurger.getQueueSize shouldBe 1
      topicBurger.readKeyValue() shouldBe new KeyValue("🧑‍🍳", "🍔")
    }
    Scenario("Join with all items for a non burger") {
      Given("bad inputs")
      When("🧑‍🍳")
      topicBread.pipeInput("🧑‍🍳", "🍞")
      topicVegetable.pipeInput("🧑‍🍳", "🥕")
      topicMeat.pipeInput("🧑‍🍳", "🥩")
      Then("not a 🍔")
      topicBurger.getQueueSize shouldBe 1
      topicBurger.readKeyValue() shouldBe new KeyValue("🧑‍🍳", "🍞 + 🥕 + 🥩")
    }
    Scenario("Join with incomplete items") {
      Given("incomplete inputs")
      When("🧑‍🍳")
      topicBread.pipeInput("🧑‍🍳", "🍞")
      topicMeat.pipeInput("🧑‍🍳", "🥩")
      Then("nothing")
      topicBurger.getQueueSize shouldBe 0
    }
  }
}
