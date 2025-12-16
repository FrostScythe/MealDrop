# Food Ordering API

A Spring Boot REST API for managing restaurants, users, menu items, and orders.

## Technologies Used
- **Spring Boot 3.x**
- **PostgreSQL**
- **JPA/Hibernate**
- **Maven**

## Database Configuration
```yaml
Database: PostgreSQL
URL: jdbc:postgresql://localhost:5432/restaurant
Username: postgres
Password: root
```

## Base URL
```
http://localhost:8080
```

---

## API Endpoints

### 1. User Management APIs

#### Base Path: `/api/users`

#### 1.1 Register New User
- **Method:** `POST`
- **URL:** `/api/users/register`
- **Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "address": "123 Main Street, City, Country"
}
```

#### 1.2 Get All Users
- **Method:** `GET`
- **URL:** `/api/users`
- **Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "address": "123 Main Street, City, Country"
  }
]
```

#### 1.3 Get User by ID
- **Method:** `GET`
- **URL:** `/api/users/{id}`
- **Example:** `/api/users/1`
- **Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "address": "123 Main Street, City, Country"
}
```

#### 1.4 Update User
- **Method:** `PUT`
- **URL:** `/api/users/{id}`
- **Example:** `/api/users/1`
- **Request Body:**
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com",
  "phoneNumber": "+1234567891",
  "address": "456 New Street, City, Country"
}
```

#### 1.5 Delete User
- **Method:** `DELETE`
- **URL:** `/api/users/{id}`
- **Example:** `/api/users/1`

---

### 2. Restaurant Management APIs

#### Base Path: `/api/restaurants`

#### 2.1 Create Restaurant
- **Method:** `POST`
- **URL:** `/restaurants/register`
- **Request Body:**
```json
{
  "name": "Tasty Bites",
  "address": "789 Restaurant Avenue, Food City",
  "phoneNumber": "+1987654321"
}
```

#### 2.2 Get All Restaurants
- **Method:** `GET`
- **URL:** `/api/restaurants`
- **Response:**
```json
[
  {
    "id": 1,
    "name": "Tasty Bites",
    "address": "789 Restaurant Avenue, Food City",
    "phoneNumber": "+1987654321"
  }
]
```

#### 2.3 Get Restaurant by ID
- **Method:** `GET`
- **URL:** `/api/restaurants/{id}`
- **Example:** `/api/restaurants/1`
- **Response:**
```json
{
  "id": 1,
  "name": "Tasty Bites",
  "address": "789 Restaurant Avenue, Food City",
  "phoneNumber": "+1987654321"
}
```

#### 2.4 Update Restaurant
- **Method:** `PUT`
- **URL:** `/api/restaurants/{id}`
- **Example:** `/api/restaurants/1`
- **Request Body:**
```json
{
  "name": "Tasty Bites Updated",
  "address": "999 New Location, Food City",
  "phoneNumber": "+1987654322"
}
```

#### 2.5 Delete Restaurant
- **Method:** `DELETE`
- **URL:** `/api/restaurants/{id}`
- **Example:** `/api/restaurants/1`

---

### 3. Menu Item Management APIs

#### Base Path: `/api/restaurants/{restaurantId}/menu`

#### 3.1 Get Restaurant's Full Menu
- **Method:** `GET`
- **URL:** `/api/restaurants/{restaurantId}/menu`
- **Example:** `/api/restaurants/1/menu`
- **Response:**
```json
[
  {
    "id": 1,
    "name": "Margherita Pizza",
    "description": "Classic pizza with tomato sauce, mozzarella, and basil",
    "price": 12.99
  },
  {
    "id": 2,
    "name": "Caesar Salad",
    "description": "Fresh romaine lettuce with Caesar dressing and croutons",
    "price": 8.99
  }
]
```

#### 3.2 Add Menu Item to Restaurant
- **Method:** `POST`
- **URL:** `/api/restaurants/{restaurantId}/add-menu-item`
- **Example:** `/api/restaurants/1/add-menu-item`
- **Request Body:**
```json
{
  "name": "Spaghetti Carbonara",
  "description": "Traditional Italian pasta with eggs, cheese, and bacon",
  "price": 14.99
}
```

#### 3.3 Get Specific Menu Item
- **Method:** `GET`
- **URL:** `/api/restaurants/{restaurantId}/menu/{menuItemId}`
- **Example:** `/api/restaurants/1/menu/1`
- **Response:**
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato sauce, mozzarella, and basil",
  "price": 12.99
}
```

#### 3.4 Update Menu Item
- **Method:** `PUT`
- **URL:** `/api/restaurants/{restaurantId}/menu/{menuItemId}`
- **Example:** `/api/restaurants/1/menu/1`
- **Request Body:**
```json
{
  "name": "Margherita Pizza Deluxe",
  "description": "Premium pizza with tomato sauce, buffalo mozzarella, and fresh basil",
  "price": 15.99
}
```

#### 3.5 Delete Menu Item
- **Method:** `DELETE`
- **URL:** `/api/restaurants/{restaurantId}/menu/{menuItemId}`
- **Example:** `/api/restaurants/1/menu/1`

---

## Testing with cURL

### User Endpoints
```bash
# Create User
curl -X POST http://localhost:8080/api/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"phoneNumber\":\"+1234567890\",\"address\":\"123 Main St\"}"

# Get All Users
curl -X GET http://localhost:8080/api/users

# Get User by ID
curl -X GET http://localhost:8080/api/users/1

# Update User
curl -X PUT http://localhost:8080/api/users/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"John Updated\",\"email\":\"john.updated@example.com\",\"phoneNumber\":\"+1234567891\",\"address\":\"456 New St\"}"

# Delete User
curl -X DELETE http://localhost:8080/api/users/1
```

### Restaurant Endpoints
```bash
# Create Restaurant
curl -X POST http://localhost:8080/api/restaurants ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Tasty Bites\",\"address\":\"789 Restaurant Ave\",\"phoneNumber\":\"+1987654321\"}"

# Get All Restaurants
curl -X GET http://localhost:8080/api/restaurants

# Get Restaurant by ID
curl -X GET http://localhost:8080/api/restaurants/1

# Update Restaurant
curl -X PUT http://localhost:8080/api/restaurants/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Tasty Bites Updated\",\"address\":\"999 New Location\",\"phoneNumber\":\"+1987654322\"}"

# Delete Restaurant
curl -X DELETE http://localhost:8080/api/restaurants/1
```

### Menu Item Endpoints
```bash
# Add Menu Item
curl -X POST http://localhost:8080/api/restaurants/1/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Margherita Pizza\",\"description\":\"Classic pizza\",\"price\":12.99}"

# Get Restaurant Menu
curl -X GET http://localhost:8080/api/restaurants/1/menu

# Get Specific Menu Item
curl -X GET http://localhost:8080/api/restaurants/1/menu/1

# Update Menu Item
curl -X PUT http://localhost:8080/api/restaurants/1/menu/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Margherita Pizza Deluxe\",\"description\":\"Premium pizza\",\"price\":15.99}"

# Delete Menu Item
curl -X DELETE http://localhost:8080/api/restaurants/1/menu/1
```

---

## Testing with Postman

1. **Import the following collection settings:**
   - Base URL: `http://localhost:8080`
   - Content-Type: `application/json`

2. **Create requests for each endpoint listed above**

3. **Test the following flow:**
   1. Create a restaurant
   2. Add menu items to the restaurant
   3. Create a user
   4. Create an order (if order endpoints are implemented)

---

## Running the Application

1. **Start PostgreSQL Database:**
```bash
# Make sure PostgreSQL is running on localhost:5432
# Database name: restaurant
```

2. **Run the Spring Boot Application:**
```bash
cd order-api
mvnw spring-boot:run
```

Or on Windows:
```cmd
cd order-api
mvnw.cmd spring-boot:run
```

3. **Access the API:**
```
http://localhost:8080
```

---

## Project Structure
```
order-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/restaurantmanagement/order_api/
│   │   │       ├── controller/      # REST Controllers
│   │   │       ├── entity/          # JPA Entities
│   │   │       ├── repository/      # Data Repositories
│   │   │       └── service/         # Business Logic
│   │   └── resources/
│   │       └── application.yaml     # Configuration
│   └── test/                        # Test files
└── pom.xml                          # Maven dependencies
```

---

## Notes
- All fields marked as `nullable = false` in entities are required
- Email and phone numbers must be unique
- The API uses standard HTTP status codes
- IDs are auto-generated
- Deleting a restaurant will cascade delete all its menu items and orders
- Deleting a user will cascade delete all their orders

---

## Future Enhancements
- Order management endpoints
- Authentication and authorization
- Payment integration
- Order status tracking
- Search and filter capabilities
- Pagination for list endpoints


