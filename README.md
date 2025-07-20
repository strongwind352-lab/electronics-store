How to run this project
1. git clone https://github.com/strongwind352-lab/electronics-store.git
2. cd electronics-store/
3. To run this project<br> 
./mvnw spring-boot:run<br>
Now spring boot backend will listen on port 8080<br>
http://localhost:8080/

4. To run all tests for this project<br>
   ./mvnw clean test

5. GET /admin/products - Admin route to get all products <br>
curl --location 'http://localhost:8080/admin/products?page=0&size=999' \
--header 'Authorization: Basic YWRtaW46QWRtaW5AMTIz' \
--header 'Cookie: JSESSIONID=D8683D160526B0AC141A8079BE923DF1'
6. POST /admin/products - Admin route to create a new product <br>
   curl --location 'http://localhost:8080/admin/products' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Basic YWRtaW46QWRtaW5AMTIz' \
   --header 'Cookie: JSESSIONID=D8683D160526B0AC141A8079BE923DF1' \
   --data '{
   "name": "AMD EPYC 9654",
   "category": "ELECTRONICS",
   "price": 6000.00,
   "stock": 5
   }'

7. GET /admin/products/{productId} - Admin route to get product details by its id <br>
   curl --location 'http://localhost:8080/admin/products/1' \
   --header 'Authorization: Basic YWRtaW46QWRtaW5AMTIz' \
   --header 'Cookie: JSESSIONID=D8683D160526B0AC141A8079BE923DF1'
8. DELETE /admin/products/{productId} - Admin route to delete a product by its id <br>
   curl --location --request DELETE 'http://localhost:8080/admin/products/46' \
   --header 'Authorization: Basic YWRtaW46QWRtaW5AMTIz' \
   --header 'Cookie: JSESSIONID=2C4E2C658209A0D47B6347C854723247'

9. POST /admin/deals - Admin route to create a new deal for a product by its productId <br>
   curl --location 'http://localhost:8080/admin/deals' \
   --header 'Content-Type: application/json' \
   --header 'Authorization: Basic YWRtaW46QWRtaW5AMTIz' \
   --header 'Cookie: JSESSIONID=D8683D160526B0AC141A8079BE923DF1' \
   --data '{
   "productId": 1,
   "dealType": "BOGO50",
   "expirationDate": "2026-07-25T23:16:44.264811700"
   }'

10. GET /customer/products - Customer route to browse products<br>
    curl --location 'http://localhost:8080/customer/products?page=0&size=1000&category=ELECTRONICS&minPrice=500&maxPrice=99999&available=true' \
    --header 'Cookie: JSESSIONID=D8683D160526B0AC141A8079BE923DF1'

11. POST /customer/basket/add - Customer route to add a product to their basket <br>
    curl --location 'http://localhost:8080/customer/basket/add' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Basic Y3VzdG9tZXI6Q3VzdG9tZXJAMTIz' \
    --header 'Cookie: JSESSIONID=D8683D160526B0AC141A8079BE923DF1' \
    --data '{
    "productId": 1,
    "quantity": 2
    }'
12. GET /customer/basket/receipt - Customer route to get a receipt of their basket<br>
    curl --location 'http://localhost:8080/customer/basket/receipt' \
    --header 'Authorization: Basic Y3VzdG9tZXI6Q3VzdG9tZXJAMTIz' \
    --header 'Cookie: JSESSIONID=2C4E2C658209A0D47B6347C854723247'
13. POST /customer/basket/remove - Customer route to remove an item from their basket <br>
    curl --location 'http://localhost:8080/customer/basket/remove' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Basic Y3VzdG9tZXI6Q3VzdG9tZXJAMTIz' \
    --header 'Cookie: JSESSIONID=2C4E2C658209A0D47B6347C854723247' \
    --data '{
    "productId": 1,
    "quantity": 2
    }'