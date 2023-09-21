Feature: End To End Feature

    Background:
        Given input topic
            | topic     | alias        | key_type | value_type |
            | potato    | potato-in    | string   | string     |
            | bread     | bread-in     | string   | string     |
            | vegetable | vegetable-in | string   | string     |
            | meat      | meat-in      | string   | string     |
        And output topic
            | topic | alias    | key_type | value_type | readTimeoutInSecond |
            | meal  | meal-out | string   | string     | 5                   |
        And var uuid = call function: uuid

    Scenario Outline: Burger Factory
        When records with key and value are sent
            | topic_alias  | key        | value       |
            | potato-in    | 🧑‍🍳_${uuid} | <potato>    |
            | bread-in     | 🧑‍🍳_${uuid} | <bread>     |
            | vegetable-in | 🧑‍🍳_${uuid} | <vegetable> |
            | meat-in      | 🧑‍🍳_${uuid} | <meat>      |

        Then expected records
            | topic_alias | key        | value  |
            | meal-out    | 🧑‍🍳_${uuid} | result |

        And assert result $ == "<output>"

        Examples:
            | potato | bread | vegetable | meat | output              |
            | 🥔     | 🍞    | 🍅        | 🥩   | (🍔 + 🍟)           |
            | 🥔     | 🍞    | 🥕        | 🥩   | (🍞 + 🥕 + 🥩 + 🍟) |
