Feature: Meal 🛍 feature

  Background:
    Given input topic
      | topic       | alias          | key_type | value_type |
      | bread       | bread-in       | string   | string     |
      | vegetable   | vegetable-in   | string   | string     |
      | meat        | meat-in        | string   | string     |
      | side-dishes | side-dishes-in | string   | string     |
    And output topic
      | topic | alias    | key_type | value_type | readTimeoutInSecond |
      | meal  | meal-out | string   | string     | 20                  |
    And var uuid = call function: uuid

  Scenario: Left Join with Left first
    When records with key and value are sent
      | topic_alias    | key        | value |
      | bread-in       | 🤤_${uuid} | 🍞    |
      | vegetable-in   | 🤤_${uuid} | 🍅    |
      | meat-in        | 🤤_${uuid} | 🥩    |
      | side-dishes-in | 🤤_${uuid} | 🥔🍺  |
    Then expected records
      | topic_alias | key        | value |
      | meal-out    | 🤤_${uuid} | notif |
      | meal-out    | 🤤_${uuid} | order |
    And assert notif $ == "🍔"
    And assert order $ == "🛍(🍔 + 🍟🍺)"

  Scenario: Left Join with Right first
    When records with key and value are sent
      | topic_alias    | key        | value |
      | side-dishes-in | 🤤_${uuid} | 🥔🍷  |
      | bread-in       | 🤤_${uuid} | 🍞    |
      | vegetable-in   | 🤤_${uuid} | 🍅    |
      | meat-in        | 🤤_${uuid} | 🥩    |
    Then expected records
      | topic_alias | key        | value |
      | meal-out    | 🤤_${uuid} | order |
    And assert order $ == "🛍(🍔 + 🍟🍷)"
