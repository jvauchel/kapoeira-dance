Feature: Burger 🍔 feature

  Background:
    Given input topic
      | topic     | alias        | key_type | value_type |
      | bread     | bread-in     | string   | string     |
      | vegetable | vegetable-in | string   | string     |
      | meat      | meat-in      | string   | string     |
      | meat      | potatoes-in  | string   | string     |
    And output topic
      | topic  | alias      | key_type | value_type | readTimeoutInSecond |
      | burger | meal-out | string   | string     | 5                   |
    And var uuid = call function: uuid

  Scenario: Nominal
    When records with key and value are sent
      | topic_alias  | key        | value |
      | bread-in     | 🧑‍🍳_${uuid} | 🍞    |
      | vegetable-in | 🧑‍🍳_${uuid} | 🍅    |
      | meat-in      | 🧑‍🍳_${uuid} | 🥩    |
      | potatoes-in  | 🧑‍🍳_${uuid} | 🥔    |
    Then expected records
      | topic_alias | key        | value |
      | burger-out  | 🤤_${uuid} | order |
    And assert order $ == "(🍔+🍟)"

