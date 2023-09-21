package io.github.jvauchel.burgerquiz.meal.factory

import com.typesafe.config.Config
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions.{consumedFromSerde, producedFromSerde, streamJoinFromKeyValueOtherSerde}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.serialization.Serdes.stringSerde

import java.time.Duration

object MealFactory extends KafkaStream {

  val topicConf: Config = conf.getConfig("topics")
  val topicBurger: String = topicConf.getString("burger")
  val topicSideDishes: String = topicConf.getString("side-dishes")
  val topicMeal: String = topicConf.getString("meal")
  val windowConf: Config = conf.getConfig("window")
  val windowDuration = Duration.ofSeconds(windowConf.getInt("duration"))

  override def topology(builder: StreamsBuilder): Unit = {

    val sideDishesStream = builder
      .stream[String, String](topicSideDishes)

    builder.stream[String, String](topicBurger)
      .join(sideDishesStream)(joinMeal, JoinWindows.of(windowDuration))
      .to(topicMeal)
  }

  private def joinMeal(leftEvent: String, rightEvent: String): String = {
    Option(rightEvent) match {
      case None => leftEvent
      case Some(value) => s"($leftEvent + $value)"
    }
  }
}
