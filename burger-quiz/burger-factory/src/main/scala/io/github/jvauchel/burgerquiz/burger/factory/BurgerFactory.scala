package io.github.jvauchel.burgerquiz.burger.factory

import com.typesafe.config.Config
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions.{consumedFromSerde, producedFromSerde, streamJoinFromKeyValueOtherSerde}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.serialization.Serdes.stringSerde

import java.time.Duration

object BurgerFactory extends KafkaStream {

  val topicConf: Config = conf.getConfig("topics")
  val topicVegetable: String = topicConf.getString("vegetable")
  val topicBread: String = topicConf.getString("bread")
  val topicMeat: String = topicConf.getString("meat")
  val topicBurger: String = topicConf.getString("burger")
  val windowConf: Config = conf.getConfig("window")
  val windowDuration = Duration.ofSeconds(windowConf.getInt("duration"))

  override def topology(builder: StreamsBuilder): Unit = {

    val breadStream = builder
      .stream[String, String](topicBread)
    val vegetableStream = builder
      .stream[String, String](topicVegetable)
    val meatStream = builder
      .stream[String, String](topicMeat)

    breadStream
      .join(vegetableStream)(joinBurger, JoinWindows.of(windowDuration))
      .join(meatStream)(joinBurger, JoinWindows.of(windowDuration))
      .to(topicBurger)
  }

  private def joinBurger(leftEvent: String, rightEvent: String): String = {
    s"$leftEvent + $rightEvent" match {
      case "🍞 + 🍅 + 🥩" => "🍔"
      case "🍞 + 🍅 + 🍗" => "🍔"
      case "🍞 + 🍅 + 🐟" => "🍔"
      case other => other
    }
  }
}
