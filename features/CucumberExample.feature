Feature: A calculator example

  Scenario: The sum example
    Given a = 2
    And b = 3
    When a + b
    Then result == 5

  Scenario: The mult example
    Given a = 2
    And b = 3
    When a * b
    Then result == 6
