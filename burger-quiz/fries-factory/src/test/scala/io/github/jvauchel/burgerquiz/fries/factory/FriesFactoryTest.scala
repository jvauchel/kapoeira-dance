package io.github.jvauchel.burgerquiz.fries.factory

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams._
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen}

import java.io.File
import java.util.UUID

class FriesFactoryTest extends AnyFeatureSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with GivenWhenThen {

  private val stringSerializer = new StringSerializer()
  private val stringDeserializer = new StringDeserializer()

  private var driver: TopologyTestDriver = _
  private var topicPotato: TestInputTopic[String, String] = _
  private var topicSideDishes: TestOutputTopic[String, String] = _

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
    FriesFactory.topology(builder)
    builder.build()
  }

  override def beforeEach(): Unit = {
    FriesFactory.config.put(StreamsConfig.STATE_DIR_CONFIG, tempDir.getAbsolutePath)
    driver = new TopologyTestDriver(buildTopology(), FriesFactory.config)
    topicPotato = driver.createInputTopic(FriesFactory.topicPotato, stringSerializer, stringSerializer)
    topicSideDishes = driver.createOutputTopic(FriesFactory.topicSideDishes, stringDeserializer, stringDeserializer)
  }

  override def afterEach(): Unit = {
    driver.close()
  }

  Feature("Good implementation") {
    Scenario("Nominal") {
      Given("🥔")
      val hungryMan = "🤤"

      When("🧑‍🍳")
      topicPotato.pipeInput(hungryMan, "🥔")

      Then("🍟")
      topicSideDishes.getQueueSize shouldBe 1
      topicSideDishes.readKeyValue() shouldBe new KeyValue(hungryMan, "🍟")
    }
    Scenario("Error") {
      Given("🍞")
      val hungryMan = "🤤"

      When("🧑‍🍳")
      topicPotato.pipeInput(hungryMan, "🍞")

      Then("🍞")
      topicSideDishes.getQueueSize shouldBe 1
      topicSideDishes.readKeyValue() shouldBe new KeyValue(hungryMan, "🍞")
    }
  }
}