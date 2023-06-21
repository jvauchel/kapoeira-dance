package io.github.jvauchel.burgerquizz

import com.typesafe.config.Config
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions.{consumedFromSerde, producedFromSerde, streamJoinFromKeyValueOtherSerde}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.serialization.Serdes.stringSerde

import java.time.Duration

object KafkaStreamBurgerQuizz extends KafkaStream {

  val topicConf: Config = conf.getConfig("topics")
  val topicTomato: String = topicConf.getString("tomato")
  val topicBread: String = topicConf.getString("bread")
  val topicBurger: String = topicConf.getString("burger")
  val windowConf: Config = conf.getConfig("window")
  val windowDuration = Duration.ofSeconds(windowConf.getInt("duration"))

  override def topology(builder: StreamsBuilder): Unit = {

    val tomatoStream = builder
      .stream[String, String](topicTomato)

    val breadStream = builder
      .stream[String, String](topicBread)

    tomatoStream
      .join(breadStream)(joinOperation, JoinWindows.of(windowDuration))
      .to(topicBurger)

  }

  private def joinOperation(leftEvent: String, rightEvent: String): String = s"$leftEvent + $rightEvent"

}
