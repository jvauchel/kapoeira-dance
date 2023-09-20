class MyKafkaStreamTest extends ... {
  private var driver: TopologyTestDriver = _
  private var inputTopic: TestInputTopic[String, String] = _
  private var outputTopic: TestOutputTopic[String, String] = _
  // init ...

  "Nominal test" should "be ok" in {
    inputTopic.pipeInput("myKey", "myValue")

    outputTopic.getQueueSize shouldBe 1
    outputTopic.readKeyValue() shouldBe new KeyValue("myKey", "myNewValue")
  }
}