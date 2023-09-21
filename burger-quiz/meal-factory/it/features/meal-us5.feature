Feature: Meal feature

  Background:
    Given input topic
      | topic       | alias          | key_type | value_type |
      | burger      | burger-in      | string   | string     |
      | side-dishes | side-dishes-in | string   | string     |
    And output topic
      | topic | alias    | key_type | value_type | readTimeoutInSecond |
      | meal  | meal-out | string   | string     | 5                   |
    And var uuid = call function: uuid

  Scenario Outline: Meal Factory
    When records with key and value are sent
      | topic_alias    | key           | value         |
      | burger-in      | 🧑‍🍳_${uuid} | <burger>      |
      | side-dishes-in | 🧑‍🍳_${uuid} | <side-dishes> |

    Then expected records
      | topic_alias | key           | value  |
      | meal-out    | 🧑‍🍳_${uuid} | result |

    And assert result $ == "<output>"

    Examples:
      | burger       | side-dishes | output              |
      | 🍔           | 🍟          | (🍔 + 🍟)           |
      | 🍔           | 🥗          | (🍔 + 🥗)           |
      | 🍞 + 🥕 + 🥩 | 🍟          | (🍞 + 🥕 + 🥩 + 🍟) |

