Feature: Fries 🍟 feature

  Background:
    Given input topic
      | topic  | alias     | key_type | value_type |
      | potato | potato-in | string   | string     |
    And output topic
      | topic       | alias           | key_type | value_type | readTimeoutInSecond |
      | side-dishes | side-dishes-out | string   | string     | 5                   |
    And var uuid = call function: uuid

  Scenario: Several potatoes with sauces
    When records from file with key and value are sent
      | topic_alias | separator | file                                     |
      | potato-in   | #         | features/records/potatoesWithHeaders.dat |

    Then expected records
      | topic_alias     | key        | headers | value   |
      | side-dishes-out | 🧑‍🍳_${uuid} | headers1 | result1 |
      | side-dishes-out | 🧑‍🍳_${uuid} | headers2 | result2 |

    And assert headers1 $ == {"sauce":"mayo(🥚+🌻)"}

    And assert headers1 $.sauce == "mayo(🥚+🌻)"
    And assert result1 $ == "🍟"
    And assert headers2 $.sauce == "ketchup(🍅)"
    And assert result2 $ == "🍟"

