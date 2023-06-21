package io.github.jvauchel.burgerquizz

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, Topology}

import java.util.Properties
import scala.jdk.CollectionConverters.CollectionHasAsScala

trait KafkaStream extends LazyLogging {

  val conf: Config = ConfigFactory.load
  val kStreamConf: Config = conf.getConfig("kstream")

  val config: Properties = {
    val p = new Properties()
    kStreamConf.entrySet().asScala.foreach(a => {
      p.put(a.getKey, kStreamConf.getString(a.getKey))
    })
    p
  }

  config.put("application.id",java.util.UUID.randomUUID.toString)

  def main(args: Array[String]): Unit = {

    val streams: KafkaStreams = new KafkaStreams(topology, config)
    logger.info("##########Start Stream##############")
    streams.start()
    sys.addShutdownHook({
      logger.info("##########Stop Stream##############")
      streams.close()
    })
  }

  def topology: Topology = {
    val builder = new StreamsBuilder
    topology(builder)
    builder.build()
  }

  def topology(builder: StreamsBuilder): Unit


}

