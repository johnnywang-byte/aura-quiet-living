# Aura Backend - Development Guide

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- OpenAI API Key

### Setup Steps

1. **Clone and Navigate**
   ```bash
   cd aura-backend
   ```

2. **Configure Environment**
   ```bash
   # Create .env file
   echo "OPENAI_API_KEY=your-api-key-here" > .env
   echo "DB_PASSWORD=your-mysql-password" >> .env
   ```

3. **Create Database**
   ```sql
   CREATE DATABASE aura_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Verify**
   ```bash
   curl http://localhost:8080/api/products
   ```

---

## 📁 Project Structure

```
aura-backend/
├── src/main/java/com/aura/
│   ├── AuraApplication.java          # Main application
│   ├── config/                        # Configuration classes
│   ├── controller/                    # REST controllers
│   ├── service/                       # Business logic
│   │   └── ai/                        # AI services
│   ├── repository/                    # Data access
│   ├── model/                         # Data models
│   │   ├── entity/                    # JPA entities
│   │   └── dto/                       # DTOs
│   ├── ai/                            # AI components
│   │   ├── agent/                     # AI agents
│   │   ├── function/                  # Function calling
│   │   └── prompt/                    # Prompt templates
│   └── util/                          # Utilities
├── src/main/resources/
│   ├── application.yml                # Configuration
│   ├── data.sql                       # Initial data
│   └── manuals/                       # Product PDFs
└── pom.xml                            # Maven config
```

---

## 🔧 Development Standards

### Code Style

- **Naming**: Use camelCase for variables, PascalCase for classes
- **Comments**: Use Javadoc for public methods
- **Formatting**: Follow Google Java Style Guide
- **Imports**: No wildcard imports

### Example:
```java
/**
 * Retrieves product by ID.
 * 
 * @param id Product identifier
 * @return Product entity
 * @throws ResourceNotFoundException if product not found
 */
public Product getProductById(String id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
}
```

### Package Organization

- `controller`: REST endpoints only
- `service`: Business logic (no direct DB access)
- `repository`: Data access only
- `model.entity`: JPA entities
- `model.dto`: Data transfer objects

---

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
```

---

## 📝 API Documentation

### Endpoints

#### Products
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product details
- `GET /api/products/search?q={keyword}` - Search products

#### Orders
- `POST /api/orders` - Create order
- `GET /api/orders/{orderNumber}` - Get order details

#### AI Assistant
- `POST /api/ai/chat` - Send message to AI
- `GET /api/ai/chat/history/{sessionId}` - Get chat history

---

## 🐛 Debugging

### Enable Debug Logging
```yaml
logging:
  level:
    com.aura: DEBUG
    org.springframework.ai: TRACE
```

### Common Issues

**Issue**: Vector store file not found  
**Solution**: Check `./data/` directory exists

**Issue**: OpenAI API error  
**Solution**: Verify `OPENAI_API_KEY` in environment

**Issue**: MySQL connection failed  
**Solution**: Check database is running and credentials are correct

---

## 📦 Building for Production

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/aura-backend-1.0.0.jar
```

---

## 🔐 Security Notes

- Never commit `.env` file
- Use environment variables for secrets
- Validate all user inputs
- Sanitize AI responses

---

## 📚 Additional Resources

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API](https://platform.openai.com/docs)
- [Project Architecture](../ARCHITECTURE.md)
