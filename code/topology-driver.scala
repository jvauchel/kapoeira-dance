package com.lectra.kafka.stream.example

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, GivenWhenThen}

import java.io.File
import java.util.UUID

class KafkaStreamSelectKeyTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with GivenWhenThen {

  private val stringSerializer = new StringSerializer()
  private val stringDeserializer = new StringDeserializer()

  private var driver: TopologyTestDriver = _
  private var inputTopic: TestInputTopic[String, String] = _
  private var outputTopic: TestOutputTopic[String, String] = _

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
    KafkaStreamSelectKey.topology(builder)
    builder.build()
  }

  override def beforeEach(): Unit = {
    KafkaStreamAvro.config.put(StreamsConfig.STATE_DIR_CONFIG, tempDir.getAbsolutePath)
    driver = new TopologyTestDriver(buildTopology(), KafkaStreamSelectKey.config)
    inputTopic = driver.createInputTopic(KafkaStreamSelectKey.topicIn, stringSerializer, stringSerializer)
    outputTopic = driver.createOutputTopic(KafkaStreamSelectKey.topicChangedKey, stringDeserializer, stringDeserializer)
  }

  override def afterEach(): Unit = {
    driver.close()
  }


  "Nominal case for select" should "change the key of records by combining key and value with -" in {
    val key = "mykey"
    val value = "myvalue"
    val key2 = "yourkey"
    val value2 = "yourvalue"

    inputTopic.pipeInput(key, value)
    inputTopic.pipeInput(key2, value2)
    val expectedKey1 = s"$key-$value"
    val expectedKey2 = s"$key2-$value2"

    outputTopic.getQueueSize shouldBe 2
    outputTopic.readKeyValue() shouldBe new KeyValue(expectedKey1, value)
    outputTopic.readKeyValue() shouldBe new KeyValue(expectedKey2, value2)

  }


}