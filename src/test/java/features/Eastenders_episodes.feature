Feature: Next EastEnders episode 

Scenario: Verify date of next EastEnders episode in BBC website	
Given user is on EastEnders programme on BBC website
When user finds Next episode card
Then user should correct date of the next episode
