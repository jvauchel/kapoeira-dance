class StepDefinitions 
  extends ScalaDsl with EN with Matchers with LazyLogging {
  
  val variables = collection.mutable.Map[String,Long]()
  var result: Long = 0

  // capture: GIVEN x = 42
  @Given("^\\s*([a-zA-Z]+)\\s*=\\s*(\\d+)\\s*$") {
    (name: String, value: Long) => {
      variables += (name -> value)
    }
  }

  // capture: WHEN x + y
  @When("^\\s*([a-zA-Z]+)\\s*\\+\\s*([a-zA-Z]+)\\s*$") {
    (left: String, right: String) => {
      val leftValueOption = variables.get(left)
      val rightValueOption = variables.get(right)
      assert(leftValueOption.isDefined, "Unknown variable " + left)
      assert(rightValueOption.isDefined, "Unknown variable " + right)
      result = ((leftValueOption ++ rightValueOption).reduceOption(_ + _)).getOrElse(0)
    }
  }

  // capture: THEN result == 42
  @Then("^\\s*result\\s*==\\s*(\\d+)\\s*$") {
    (expectedResult: Long) => {
      assert(expectedResult == result)
    }
  }
}
