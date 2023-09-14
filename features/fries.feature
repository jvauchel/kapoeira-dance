Feature: Fries 🍟 feature

  Background:
    Given input topic
      | topic  | alias     | key_type | value_type |
      | potato | potato-in | string   | string     |
    And output topic
      | topic       | alias           | key_type | value_type | readTimeoutInSecond |
      | side-dishes | side-dishes-out | string   | string     | 20                  |
    And var uuid = call function: uuid

  Scenario: Transformation
    When records with key and value are sent
      | topic_alias | key        | value |
      | potato-in   | 🤤_${uuid} | 🥔    |
    Then expected records
      | topic_alias     | key        | value  |
      | side-dishes-out | 🤤_${uuid} | result |
    And assert result $ == "🍟"
