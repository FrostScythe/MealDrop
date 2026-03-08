# Food Ordering API

A Spring Boot REST API for managing restaurants, users, menu items, and orders with advanced features including inventory management, image uploads, concurrent order handling, and business hours validation.

## Technologies Used
- **Spring Boot 4.0**
- **PostgreSQL**
- **JPA/Hibernate**
- **Maven 3.9.11**
- **Java 21**
- **Lombok**

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

## Key Features

- **Inventory Tracking:** Stock levels are tracked per menu item. Orders reduce stock automatically; cancellations restore it.
- **Concurrent Order Handling:** Pessimistic locking + SERIALIZABLE transactions prevent race conditions when multiple users order simultaneously.
- **Business Hours Validation:** Orders are rejected outside operating hours, accounting for preparation time.
- **Image Uploads:** Menu items support image upload (JPEG, PNG, WEBP, max 5MB). Images are served locally during development and can be swapped to GCS in production.
- **Role-Based Registration:** Public registration creates `CUSTOMER` accounts. `OWNER` accounts require a separate protected endpoint (ADMIN only after Spring Security is added).

---

## API Endpoints

### 1. User Management — `/api/users`

#### 1.1 Register Customer (Public)
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
- **Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "address": "123 Main Street, City, Country"
}
```

#### 1.2 Register Owner (Admin Only — unprotected until Spring Security)
- **Method:** `POST`
- **URL:** `/api/users/register/owner`
- **Request Body:** Same as 1.1
- **Response (200 OK):** Same as 1.1 (role will be `OWNER` internally)

#### 1.3 Get All Users
- **Method:** `GET`
- **URL:** `/api/users`
- **Response (200 OK):**
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

#### 1.4 Get User by ID
- **Method:** `GET`
- **URL:** `/api/users/{id}`
- **Response (200 OK):** Single user object (same shape as above)
- **Error (404):** `"User not found with id: 1"`

#### 1.5 Update User
- **Method:** `PUT`
- **URL:** `/api/users/{id}`
- **Request Body:** Any subset of user fields
- **Response (200 OK):** Updated user object

#### 1.6 Delete User
- **Method:** `DELETE`
- **URL:** `/api/users/{id}`
- **Response:** `204 No Content`

---

### 2. Restaurant Management — `/api/restaurants`

#### 2.1 Create Restaurant
- **Method:** `POST`
- **URL:** `/api/restaurants`
- **Request Body:**
```json
{
  "name": "Tasty Bites",
  "address": "789 Restaurant Avenue, Food City",
  "phoneNumber": "+1987654321",
  "openingTime": "09:00:00",
  "closingTime": "23:00:00",
  "preparationTimeMinutes": 30,
  "isOpen": true
}
```
- **Response (201 Created):**
```json
{
  "id": 1,
  "name": "Tasty Bites",
  "address": "789 Restaurant Avenue, Food City",
  "phoneNumber": "+1987654321",
  "openingTime": "09:00:00",
  "closingTime": "23:00:00",
  "preparationTimeMinutes": 30,
  "isOpen": true,
  "currentStatus": "Open now. Last order at 22:30"
}
```

#### 2.2 Get All Restaurants
- **Method:** `GET`
- **URL:** `/api/restaurants`
- **Response (200 OK):** Array of restaurant objects (same shape as above)

#### 2.3 Get Restaurant by ID
- **Method:** `GET`
- **URL:** `/api/restaurants/{id}`
- **Response (200 OK):** Single restaurant object

#### 2.4 Update Restaurant
- **Method:** `PUT`
- **URL:** `/api/restaurants/{id}`
- **Request Body:** Any subset of restaurant fields

#### 2.5 Delete Restaurant
- **Method:** `DELETE`
- **URL:** `/api/restaurants/{id}`
- **Response:** `204 No Content`

---

### 3. Menu Item Management — `/api/restaurants/{restaurantId}/menu`

> ⚠️ **All menu item create/update endpoints use `multipart/form-data`** because they accept an optional image file alongside the JSON fields.

#### 3.1 Get Restaurant Menu
- **Method:** `GET`
- **URL:** `/api/restaurants/{restaurantId}/menu`
- **Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Margherita Pizza",
    "description": "Classic pizza with tomato sauce and mozzarella",
    "price": 12.99,
    "stockQuantity": 50,
    "available": true,
    "imageUrl": "http://localhost:8080/images/menu-items/uuid.jpg"
  }
]
```

#### 3.2 Add Menu Item (with optional image)
- **Method:** `POST`
- **URL:** `/api/restaurants/{restaurantId}/menu`
- **Content-Type:** `multipart/form-data`
- **Form Fields:**

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `name` | text | ✅ | Item name |
| `description` | text | ✅ | Item description |
| `price` | number | ✅ | Must be ≥ 0 |
| `stockQuantity` | number | ❌ | Default: 20 |
| `available` | boolean | ❌ | Default: true |
| `image` | file | ❌ | JPEG/PNG/WEBP, max 5MB |

**In Postman:** Set request type to `POST`, Body → `form-data`, add each field above. For `image`, change the field type dropdown from `Text` to `File`.

- **Response (201 Created):**
```json
{
  "id": 1,
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato sauce and mozzarella",
  "price": 12.99,
  "stockQuantity": 50,
  "available": true,
  "imageUrl": "http://localhost:8080/images/menu-items/550e8400-e29b-41d4-a716.jpg"
}
```

#### 3.3 Get Menu Item by ID
- **Method:** `GET`
- **URL:** `/api/restaurants/{restaurantId}/menu/{menuItemId}`
- **Error (403):** `"Access denied: Menu item 5 does not belong to restaurant 1"`

#### 3.4 Update Menu Item (with optional new image)
- **Method:** `PUT`
- **URL:** `/api/restaurants/{restaurantId}/menu/{menuItemId}`
- **Content-Type:** `multipart/form-data`
- **Form Fields:** Same as 3.2. If a new `image` is provided, the old image is deleted automatically.
- **Response (200 OK):** Updated menu item object

#### 3.5 Delete Menu Item
- **Method:** `DELETE`
- **URL:** `/api/restaurants/{restaurantId}/menu/{menuItemId}`
- **Response:** `204 No Content` (image file is also deleted from storage)

---

### 4. Order Management — `/api/orders`

#### 4.1 Place Order
- **Method:** `POST`
- **URL:** `/api/orders/place-order?userId={userId}&restaurantId={restaurantId}`
- **Content-Type:** `application/json`
- **Request Body:** Map of `menuItemId → quantity`
```json
{
  "items": {
    "1": 2,
    "2": 1
  }
}
```
- **Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "userName": "John Doe",
  "restaurantId": 1,
  "restaurantName": "Tasty Bites",
  "orderedItems": [
    {
      "id": 1,
      "name": "Margherita Pizza",
      "description": "Classic pizza with tomato sauce and mozzarella",
      "price": 12.99,
      "imageUrl": "http://localhost:8080/images/menu-items/uuid.jpg",
      "quantity": 2
    },
    {
      "id": 2,
      "name": "Caesar Salad",
      "description": "Fresh romaine with Caesar dressing",
      "price": 8.99,
      "imageUrl": null,
      "quantity": 1
    }
  ],
  "itemCount": 3,
  "totalPrice": 34.97,
  "status": "PLACED",
  "orderAt": "2025-01-16T14:30:00",
  "deliveryAt": null
}
```

**Order Status Values:** `PLACED` → `PREPARING` → `OUT_FOR_DELIVERY` → `DELIVERED` or `CANCELLED`

**Validation errors:**

Restaurant closed (503):
```json
{
  "error": "Restaurant Closed",
  "restaurantName": "Tasty Bites",
  "reason": "We open at 09:00. You can place orders then.",
  "openingTime": "09:00:00",
  "closingTime": "23:00:00",
  "lastOrderTime": "22:30:00"
}
```

Out of stock (400): `"Item 'Margherita Pizza' only has 1 left in stock"`

#### 4.2 Get Order by ID
- **Method:** `GET`
- **URL:** `/api/orders/{orderId}`
- **Response (200 OK):** Full order object (same shape as 4.1 response)
- **Error (404):** `"Order not found with id: 1"`

#### 4.3 Get Orders by User
- **Method:** `GET`
- **URL:** `/api/orders/user/{userId}`
- **Response (200 OK):** Array of order objects

#### 4.4 Update Order Status
- **Method:** `PUT`
- **URL:** `/api/orders/{orderId}?status={newStatus}`
- **Valid values:** `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`
- **Response (200 OK):** Updated order object
- **Note:** Cancelling an order automatically restores inventory for all items.
- **Error (409):** `"Cannot update order - already delivered"`

---

## Postman Testing Guide

### Setup

Import the base URL as a Postman environment variable:
- Variable: `baseUrl`
- Value: `http://localhost:8080`

Then use `{{baseUrl}}/api/...` in all requests.

### Step-by-Step Test Flow

**Step 1 — Create a restaurant**

`POST {{baseUrl}}/api/restaurants` → Body → raw → JSON:
```json
{
  "name": "Pizza Palace",
  "address": "123 Food Street",
  "phoneNumber": "+1234567890",
  "openingTime": "00:00:00",
  "closingTime": "23:59:00",
  "preparationTimeMinutes": 30,
  "isOpen": true
}
```
> Set `openingTime: "00:00:00"` and `closingTime: "23:59:00"` so the restaurant is always open during testing.

**Step 2 — Add a menu item (with image)**

`POST {{baseUrl}}/api/restaurants/1/menu` → Body → form-data:

| Key | Value | Type |
|-----|-------|------|
| name | Margherita Pizza | Text |
| description | Classic pizza | Text |
| price | 12.99 | Text |
| stockQuantity | 5 | Text |
| available | true | Text |
| image | (select a .jpg file) | File |

**Step 3 — Register a user**

`POST {{baseUrl}}/api/users/register` → Body → raw → JSON:
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "phoneNumber": "+1111111111",
  "address": "456 Customer Ave"
}
```

**Step 4 — Place an order**

`POST {{baseUrl}}/api/orders/place-order?userId=1&restaurantId=1` → Body → raw → JSON:
```json
{
  "items": {
    "1": 2
  }
}
```

**Step 5 — Update order status**

`PUT {{baseUrl}}/api/orders/1?status=PREPARING`

**Step 6 — Cancel and verify inventory restored**

`PUT {{baseUrl}}/api/orders/1?status=CANCELLED`

Then `GET {{baseUrl}}/api/restaurants/1/menu/1` — confirm `stockQuantity` is back to 5.

---

## Running the Application

### Prerequisites
- JDK 21+
- PostgreSQL 12+
- Maven 3.6+ (or use included wrapper)

### Database Setup
```sql
psql -U postgres
CREATE DATABASE restaurant;
\q
```

### Start the Application
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Application starts on `http://localhost:8080`. Hibernate auto-creates all tables on first run.

---

## Project Structure

```
order-api/src/main/java/com/restaurantmanagement/order_api/
├── controller/
│   ├── OrderController.java
│   ├── RestaurantController.java
│   └── UserController.java
├── dto/
│   ├── request/
│   │   ├── MenuItemRequest.java
│   │   ├── PlaceOrderRequest.java
│   │   ├── RestaurantRequest.java
│   │   └── UserRegisterRequest.java
│   └── response/
│       ├── MenuItemResponse.java
│       ├── OrderItemResponse.java       ← order snapshot (no stock fields)
│       ├── OrderResponse.java
│       ├── RestaurantResponse.java
│       └── UserResponse.java
├── entity/
│   ├── MenuItem.java
│   ├── Order.java
│   ├── OrderStatus.java
│   ├── Restaurant.java
│   ├── Role.java
│   └── User.java
├── exception/
│   ├── BadRequestException.java
│   ├── ForbiddenRequestException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidOrderStateException.java
│   ├── NotFoundException.java
│   └── RestaurantClosedException.java
├── repository/
│   ├── MenuItemRepository.java          ← includes pessimistic lock query
│   ├── OrderRepository.java
│   ├── RestaurantRepository.java
│   └── UserRepository.java
├── service/
│   ├── MenuItemService.java
│   ├── OrderService.java                ← interface
│   ├── RestaurantService.java
│   ├── UserService.java
│   ├── imp/
│   │   └── OrderServiceImp.java         ← SERIALIZABLE transaction + locking
│   └── storage/
│       ├── StorageService.java          ← interface
│       ├── LocalStorageService.java     ← active (@Primary)
│       └── GcsStorageService.java       ← stub for GCP switch
└── config/
    └── WebConfig.java                   ← serves /images/** from local disk
```

---

## Configuration Notes

`ddl-auto: update` automatically creates/updates tables from entity classes. Change to `validate` in production and use a proper migration tool (Flyway/Liquibase).

To switch from local storage to GCS: remove `@Primary` from `LocalStorageService`, add `@Service @Primary` to `GcsStorageService`, and fill in the GCS upload/delete logic.