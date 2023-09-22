Feature: My first Kapoeira feature

  Background:
    Given input topic
      | topic  | alias     | key_type | value_type |
      | topic1 | topic1-in | string   | string     |
    And output topic
      | topic  | alias      | key_type | value_type | readTimeoutInSecond |
      | topic2 | topic2-out | string   | string     | 5                   |

  Scenario: Nominal
    When records with key and value are sent
      | topic_alias | key   | value   |
      | topic1-in   | myKey | myValue |
    Then expected records
      | topic_alias | key   | value  |
      | topic2-out  | myKey | result |
    And assert result $ == "myValue-suffix"