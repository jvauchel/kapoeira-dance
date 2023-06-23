Feature: Burger 🍔 feature

  Background:
    Given input topic
      | topic  | alias     | key_type | value_type |
      | bread  | bread-in  | string   | string     |
      | tomato | tomato-in | string   | string     |
      | meat   | meat-in   | string   | string     |
    And output topic
      | topic  | alias      | key_type | value_type | readTimeoutInSecond |
      | burger | burger-out | string   | string     | 5                   |
    And var uuid = call function: uuid

  Scenario: Nominal
    When records with key and value are sent
      | topic_alias | key        | value |
      | bread-in    | 🤤_${uuid} | 🍞    |
      | tomato-in   | 🤤_${uuid} | 🍅    |
      | meat-in     | 🤤_${uuid} | 🥩    |
    Then expected records
      | topic_alias | key        | value |
      | burger-out  | 🤤_${uuid} | order |
    And assert order $ == "🍔"

  Scenario Outline: Many customers
    When records with key and value are sent
      | topic_alias | key            | value    |
      | bread-in    | <user>_${uuid} | <bread>  |
      | tomato-in   | <user>_${uuid} | <tomato> |
      | meat-in     | <user>_${uuid} | <meat>   |
    Then expected records
      | topic_alias | key            | value |
      | burger-out  | <user>_${uuid} | order |
    And assert order $ == "<result>"

    Examples:
      | user | bread | tomato | meat | result |
      | 🤤   | 🍞    | 🍅     | 🥩   | 🍔     |
      | 😋   | 🍞    | 🍅     | 🍗   | 🍔     |
      | 😡   | 🍞    | 🍅     | 🐟   | 🍔     |