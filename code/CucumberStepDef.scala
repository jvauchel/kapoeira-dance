class StepDefinitions {
  var variables = collection.mutable.Map[String,Long]()
  var result: Long = 0

  @Given("^\\s*([a-zA-Z]+)\\s*=\\s*(\\d+)\\s*$")
  def addVariable(name: String, value: Long) : Unit = {
    variables = variables ++ (name , value)
  }

  @When("^\\s*([a-zA-Z]+)\\s*\\+\\s*([a-zA-Z]+)\\s*$")
  def sum(left: String, right: String): Unit = {
    val leftValue = variables.get(left)
    val rightValue = variables.get(right)
    assertNotNull(leftValue, "Unknown variable " + left)
    assertNotNull(rightValue, "Unknown variable " + right)
    result = leftValue + rightValue
  }

  @When("^\\s*([a-zA-Z]+)\\s*\\*\\s*([a-zA-Z]+)\\s*$")
  def mult(left: String, right: String): Unit = {
    val leftValue = variables.get(left)
    val rightValue = variables.get(right)
    assertNotNull(leftValue, "Unknown variable " + left)
    assertNotNull(rightValue, "Unknown variable " + right)
    result = leftValue * rightValue
  }

  @Then("^\\s*result\\s*==\\s*(\\d+)\\s*$")
  def result(expectedResult: Long) = {
    assertEquals(expectedResult, result)
  }
}
