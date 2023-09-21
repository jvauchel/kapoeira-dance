Feature: Fries 🍟 feature

  Background:
    Given input topic
      | topic  | alias     | key_type | value_type |
      | potato | potato-in | string   | string     |
    And output topic
      | topic       | alias           | key_type | value_type | readTimeoutInSecond |
      | side-dishes | side-dishes-out | string   | string     | 5                   |

  Scenario: Nominal
    When records with key and value are sent
      | topic_alias | key | value |
      | potato-in   | 🧑‍🍳  | 🥔    |
    Then expected records
      | topic_alias     | key | value  |
      | side-dishes-out | 🧑‍🍳  | result |
    And assert result $ == "🍟"

  Scenario: Alternative
    When records with key and value are sent
      | topic_alias | key | value |
      | potato-in   | 👩🏿‍🍳  | 🥦    |
    Then expected records
      | topic_alias     | key | value  |
      | side-dishes-out | 👩🏿‍🍳  | result |
    And assert result $ == "🥦"