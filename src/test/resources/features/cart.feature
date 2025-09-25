Feature: Shopping Cart Management
  As a logged-in user
  I want to manage my shopping cart
  So that I can purchase products

  Scenario: Add product to cart
    Given a logged-in user
    And a product is available
    When the user adds a product to the cart
    Then the product should be added successfully

  Scenario: View cart with items
    Given a logged-in user
    And a product is available
    And the user adds a product to the cart
    When the user views the cart
    Then the cart should be accessible

  Scenario: Remove product from cart
    Given a logged-in user
    And a product is available
    And the user adds a product to the cart
    When the user removes a product from the cart
    Then the product should be removed successfully

  Scenario: View empty cart
    Given a logged-in user
    When the user views the cart
    Then the cart should be empty

  Scenario: Unauthorized cart access
    When an unauthorized user views the cart
    Then the user should be redirected to login