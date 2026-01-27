# Aura Project - Development Standards

## 📋 Code Standards

### Java (Backend)

#### Naming Conventions
- **Classes**: `PascalCase` (e.g., `ProductService`)
- **Methods**: `camelCase` (e.g., `getProductById`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
- **Packages**: `lowercase` (e.g., `com.aura.service`)

#### Documentation
```java
/**
 * Brief description of the class/method.
 * 
 * Detailed explanation if needed.
 * 
 * @param paramName Description of parameter
 * @return Description of return value
 * @throws ExceptionType When this exception is thrown
 */
```

#### Error Handling
```java
// ✅ Good
try {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
} catch (DataAccessException e) {
    log.error("Database error while fetching product: {}", id, e);
    throw new ServiceException("Failed to retrieve product", e);
}

// ❌ Bad
try {
    return productRepository.findById(id).get();
} catch (Exception e) {
    e.printStackTrace();
    return null;
}
```

### TypeScript (Frontend)

#### Naming Conventions
- **Components**: `PascalCase` (e.g., `ProductCard`)
- **Functions**: `camelCase` (e.g., `fetchProducts`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `API_BASE_URL`)
- **Interfaces**: `PascalCase` with `I` prefix (e.g., `IProduct`)

#### Type Safety
```typescript
// ✅ Good
interface Product {
  id: string;
  name: string;
  price: number;
}

const fetchProduct = async (id: string): Promise<Product> => {
  const response = await api.get<Product>(`/products/${id}`);
  return response.data;
};

// ❌ Bad
const fetchProduct = async (id) => {
  const response = await api.get(`/products/${id}`);
  return response.data;
};
```

---

## 🔄 Git Workflow

### Branch Strategy

```
main (production-ready code)
  └── develop (integration branch)
       ├── feature/product-api
       ├── feature/ai-agent
       └── bugfix/order-validation
```

### Branch Naming

- **Feature**: `feature/short-description`
- **Bug Fix**: `bugfix/issue-description`
- **Hotfix**: `hotfix/critical-issue`

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance

**Examples:**
```bash
feat(ai): implement RAG service for product manuals
fix(order): validate shipping address format
docs(api): add OpenAPI documentation
refactor(product): optimize database queries
```

### Workflow Steps

1. **Create Branch**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   ```bash
   # Make your changes
   git add .
   git commit -m "feat(scope): description"
   ```

3. **Push and Create PR**
   ```bash
   git push origin feature/your-feature-name
   # Create Pull Request on GitHub
   ```

4. **Code Review**
   - At least 1 approval required
   - All tests must pass
   - No merge conflicts

5. **Merge**
   ```bash
   # Squash and merge to develop
   git checkout develop
   git pull origin develop
   ```

---

## 🧪 Testing Standards

### Backend Tests

#### Unit Tests
```java
@Test
void shouldReturnProductWhenIdExists() {
    // Given
    String productId = "p1";
    Product product = new Product();
    product.setId(productId);
    when(productRepository.findById(productId))
        .thenReturn(Optional.of(product));
    
    // When
    Product result = productService.getProductById(productId);
    
    // Then
    assertNotNull(result);
    assertEquals(productId, result.getId());
}
```

#### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturnProductList() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }
}
```

### Frontend Tests

```typescript
describe('ProductCard', () => {
  it('should render product information', () => {
    const product = {
      id: 'p1',
      name: 'Aura Harmony',
      price: 429
    };
    
    render(<ProductCard product={product} />);
    
    expect(screen.getByText('Aura Harmony')).toBeInTheDocument();
    expect(screen.getByText('$429')).toBeInTheDocument();
  });
});
```

---

## 📝 Code Review Checklist

### Before Submitting PR

- [ ] Code follows style guidelines
- [ ] All tests pass
- [ ] New tests added for new features
- [ ] Documentation updated
- [ ] No console.log or debug statements
- [ ] No commented-out code
- [ ] Commit messages follow convention

### Reviewer Checklist

- [ ] Code is readable and maintainable
- [ ] Logic is correct
- [ ] Edge cases handled
- [ ] Performance considerations
- [ ] Security vulnerabilities checked
- [ ] Tests are comprehensive

---

## 🚀 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
name: CI/CD

on:
  push:
    branches: [ develop, main ]
  pull_request:
    branches: [ develop, main ]

jobs:
  backend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build and Test
        run: |
          cd aura-backend
          mvn clean test
  
  frontend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install and Test
        run: |
          npm install
          npm test
```

---

## 📊 Performance Guidelines

### Backend

- Database queries: < 100ms
- API response time: < 500ms
- AI response time: < 3s

### Frontend

- First Contentful Paint: < 1.5s
- Time to Interactive: < 3s
- Bundle size: < 500KB

---

## 🔒 Security Best Practices

### Backend

1. **Input Validation**
   ```java
   @PostMapping("/orders")
   public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest request) {
       // @Valid triggers validation
   }
   ```

2. **SQL Injection Prevention**
   ```java
   // ✅ Use JPA/JPQL
   @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
   List<Product> searchByName(@Param("keyword") String keyword);
   ```

3. **API Rate Limiting**
   ```java
   @RateLimiter(name = "aiChat", fallbackMethod = "rateLimitFallback")
   public ChatResponse chat(ChatRequest request) {
       // Implementation
   }
   ```

### Frontend

1. **XSS Prevention**
   ```typescript
   // ✅ React automatically escapes
   <div>{userInput}</div>
   
   // ❌ Dangerous
   <div dangerouslySetInnerHTML={{__html: userInput}} />
   ```

2. **API Key Protection**
   ```typescript
   // ✅ Call backend API
   const response = await fetch('/api/ai/chat', { ... });
   
   // ❌ Never expose API keys
   const response = await fetch('https://api.openai.com/...', {
     headers: { 'Authorization': 'Bearer sk-...' }
   });
   ```

---

## 📚 Documentation Standards

### API Documentation

Use OpenAPI/Swagger annotations:

```java
@Operation(summary = "Get product by ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Product found"),
    @ApiResponse(responseCode = "404", description = "Product not found")
})
@GetMapping("/{id}")
public ResponseEntity<Product> getProduct(@PathVariable String id) {
    // Implementation
}
```

### README Files

Every module should have a README with:
- Purpose
- Setup instructions
- Usage examples
- API documentation (if applicable)

---

## 🎯 Definition of Done

A task is considered "done" when:

- [ ] Code is written and follows standards
- [ ] Unit tests written and passing
- [ ] Integration tests passing
- [ ] Code reviewed and approved
- [ ] Documentation updated
- [ ] Merged to develop branch
- [ ] Deployed to dev environment
- [ ] Tested in dev environment

---

## 📞 Getting Help

- **Technical Questions**: Ask in #dev-help Slack channel
- **Architecture Decisions**: Consult with tech lead
- **Blockers**: Raise in daily standup

---

**Last Updated**: 2026-01-27  
**Version**: 1.0
