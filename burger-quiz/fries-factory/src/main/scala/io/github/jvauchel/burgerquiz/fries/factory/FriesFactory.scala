package io.github.jvauchel.burgerquiz.fries.factory

import com.typesafe.config.Config
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions.{consumedFromSerde, producedFromSerde, streamJoinFromKeyValueOtherSerde}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.serialization.Serdes.stringSerde

import java.time.Duration

object FriesFactory extends KafkaStream {

  val implementation: String = conf.getString("implementation")
  val topicConf: Config = conf.getConfig("topics")
  val topicPotato: String = topicConf.getString("potato")
  val topicSideDishes: String = topicConf.getString("side-dishes")

  override def topology(builder: StreamsBuilder): Unit = {
    builder
      .stream[String, String](topicPotato)
      .flatMapValues{ value => (value, implementation) match {
          case (_, "bad") => Some("🥕")
          case ("🥔", "good") => Some("🍟")
          case (_, "good") => Some(value)
          case (_, _) => None
        }
      }
      .to(topicSideDishes)
  }

}
