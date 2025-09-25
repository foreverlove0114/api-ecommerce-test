Feature: User Authentication
  As a user
  I want to authenticate into the e-commerce system
  So that I can access protected features

  Scenario: Successful user registration
    Given a new user is registered
    Then the registration should be successful

  Scenario: Successful login with valid credentials
    Given a new user is registered
    When the user logs in with valid credentials
    Then the login should be successful

  Scenario: Failed login with invalid credentials
    Given a new user is registered
    When the user logs in with invalid credentials
    Then the login should fail

  Scenario: User logout
    Given a new user is registered
    And the user logs in with valid credentials
    When the user logs out
    Then the user should be logged out

  Scenario: Access profile after login
    Given a new user is registered
    And the user logs in with valid credentials
    When the user accesses the profile page
    Then the profile page should be accessible