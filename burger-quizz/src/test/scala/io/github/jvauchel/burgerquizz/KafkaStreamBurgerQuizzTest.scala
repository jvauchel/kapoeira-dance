package io.github.jvauchel.burgerquizz


import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen}

import java.io.File
import java.time.Duration
import java.util.UUID

class KafkaStreamBurgerQuizzTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with GivenWhenThen {

  private val stringSerializer = new StringSerializer()
  private val stringDeserializer = new StringDeserializer()

  private var driver: TopologyTestDriver = _
  private var topicTomato: TestInputTopic[String, String] = _
  private var topicBread: TestInputTopic[String, String] = _
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
    KafkaStreamBurgerQuizz.topology(builder)
    builder.build()
  }

  override def beforeEach(): Unit = {
    KafkaStreamBurgerQuizz.config.put(StreamsConfig.STATE_DIR_CONFIG, tempDir.getAbsolutePath)
    driver = new TopologyTestDriver(buildTopology(), KafkaStreamBurgerQuizz.config)
    topicTomato = driver.createInputTopic(KafkaStreamBurgerQuizz.topicTomato, stringSerializer, stringSerializer)
    topicBread = driver.createInputTopic(KafkaStreamBurgerQuizz.topicBread, stringSerializer, stringSerializer)
    topicBurger = driver.createOutputTopic(KafkaStreamBurgerQuizz.topicBurger, stringDeserializer, stringDeserializer)
  }

  override def afterEach(): Unit = {
    driver.close()
  }

  "Left join on two KStreams" should "join two records with the same key sent in the same window (right msg sent before left msg)" in {
    val key = "mykey"

    topicTomato.pipeInput(key, s"🍅")
    topicBread.pipeInput(key, s"🍞")

    topicBurger.getQueueSize shouldBe 1
    topicBurger.readKeyValue() shouldBe new KeyValue(key, s"🍅 + 🍞")
  }
}
