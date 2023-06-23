package io.github.jvauchel.burgerquiz

import com.typesafe.config.Config
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions.{consumedFromSerde, producedFromSerde, streamJoinFromKeyValueOtherSerde}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.serialization.Serdes.stringSerde

import java.time.Duration

object KafkaStreamBurgerQuiz extends KafkaStream {

  val topicConf: Config = conf.getConfig("topics")
  val topicTomato: String = topicConf.getString("tomato")
  val topicBread: String = topicConf.getString("bread")
  val topicMeat: String = topicConf.getString("meat")
  val topicBurger: String = topicConf.getString("burger")
  val topicSideDishes: String = topicConf.getString("side-dishes")
  val topicMeal: String = topicConf.getString("meal")
  val windowConf: Config = conf.getConfig("window")
  val windowDuration = Duration.ofSeconds(windowConf.getInt("duration"))

  override def topology(builder: StreamsBuilder): Unit = {

    val breadStream = builder
      .stream[String, String](topicBread)
    val tomatoStream = builder
      .stream[String, String](topicTomato)
    val meatStream = builder
      .stream[String, String](topicMeat)
    val sideDishesStream = builder
      .stream[String, String](topicSideDishes)

    breadStream
      .join(tomatoStream)(joinBurger, JoinWindows.of(windowDuration))
      .join(meatStream)(joinBurger, JoinWindows.of(windowDuration))
      .to(topicBurger)

    builder.stream[String, String](topicBurger)
      .leftJoin(sideDishesStream)(joinMeal, JoinWindows.of(windowDuration))
      .to(topicMeal)

  }

  private def joinBurger(leftEvent: String, rightEvent: String): String = {
    s"$leftEvent + $rightEvent" match {
      case "🍞 + 🍅 + 🥩" => "🍔"
      case "🍞 + 🍅 + 🍗" => "🍔"
      case "🍞 + 🍅 + 🐟" => "🍔"
      case other => other
    }
  }

  private def joinMeal(leftEvent: String, rightEvent: String): String = {
    Option(rightEvent) match {
      case None => leftEvent
      case Some(value) => s"🛍($leftEvent + $value)".replace("🥔", "🍟")
    }
  }

}
