Feature: Fries 🍟 feature

  Background:
    Given input topic
      | topic  | alias     | key_type | value_type |
      | potato | potato-in | string   | string     |
    And output topic
      | topic       | alias           | key_type | value_type | readTimeoutInSecond |
      | potato      | potato-out      | string   | string     | 5                   |
      | side-dishes | side-dishes-out | string   | string     | 5                   |
    And var uuid = call function: uuid

  Scenario: Several potatoes
    When records from file with value are sent
      | topic_alias | key        | file                          |
      | potato-in   | 🧑‍🍳_${uuid} | features/records/potatoes.dat |
    Then expected records
      | topic_alias     | key        | value   |
      | potato-out      | 🧑‍🍳_${uuid} | input1  |
      | potato-out      | 🧑‍🍳_${uuid} | input2  |
      | potato-out      | 🧑‍🍳_${uuid} | input3  |
      | potato-out      | 🧑‍🍳_${uuid} | input4  |
      | side-dishes-out | 🧑‍🍳_${uuid} | result1 |
      | side-dishes-out | 🧑‍🍳_${uuid} | result1 |
      | side-dishes-out | 🧑‍🍳_${uuid} | result2 |
      | side-dishes-out | 🧑‍🍳_${uuid} | result3 |
      | side-dishes-out | 🧑‍🍳_${uuid} | result4 |
    And assert input1 $ == "🥔"
    And assert input2 $ == "🥔"
    And assert input3 $ == "🥔"
    And assert input4 $ == "🥔"
    And assert result1 $ == "🍟"
    And assert result2 $ == "🍟"
    And assert result3 $ == "🍟"
    And assert result4 $ == "🍟"

