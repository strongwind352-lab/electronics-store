Assessment
We are building the Java back-end of a web-based electronics store’s checkout system.
From the requirements below develop a set of RESTful endpoints and their implementation.
Usage of a framework such as Spring Boot is encouraged but not mandatory - if you feel more comfortable using something
else, then please do so.
Persistence should be achieved either through an In-memory DB, Java data structures or any persistence layer – in either
case being sure to demonstrate an understanding of safe concurrent usage.
Electronic Store’s Requirements
Admin User Operations

- Create a new product
- Remove a product
- Admin endpoints should support pagination.
- Add discount deals for products (Example: Buy 1 get 50% off the second)
- Admin can add an expiration date/time to deals.
  Customer Operations
- Add and remove products to and from a basket
- Calculate a receipt of items, including all purchases, deals applied and total price
- Products can be filtered by category, price range, or availability.
- Customer endpoints should support pagination.
- Products have limited stock.
- When a customer adds a product to the basket, decrement stock accordingly.
- If stock is insufficient, the operation should fail gracefully.
  Extra
- If any part fails (e.g., insufficient stock, invalid promo), no partial update should persist.
  Your Submission
- Everything should be tested with automated tests. If any requirement is not satisfied, a test should fail
- Include this document in your repo
- There should be one documented command to start the app - Docker also can be the options
- There should be one documented command to run the test
- Optimise for code clarify instead
- Please applied the necessary skills/codes to make sure the high performance
- Push you code to publicly available git repository and provide a link (GitHub is our preferred.)
  Out of scope
- Continuous Integration
- Deployment
- Metrics
  Reminder
- If you do well in the assignment then your next round will based on adding features to it
- Please designed to be easy to extend