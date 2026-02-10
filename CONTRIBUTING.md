# Contributing to Aura Quiet Living

Thank you for your interest in contributing to Aura! 🎉

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)

---

## Code of Conduct

This project adheres to a code of conduct. By participating, you are expected to uphold this code. Please be respectful and constructive.

---

## How Can I Contribute?

### 🐛 Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce**
- **Expected vs actual behavior**
- **Environment details** (OS, Java version, etc.)
- **Screenshots** (if applicable)

### ✨ Suggesting Features

Feature suggestions are welcome! Please:

- **Check existing feature requests first**
- **Describe the problem** you're trying to solve
- **Propose a solution** with examples
- **Consider alternatives**

### 💻 Code Contributions

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Make your changes**
4. **Commit with clear messages** (`git commit -m 'Add some AmazingFeature'`)
5. **Push to the branch** (`git push origin feature/AmazingFeature`)
6. **Open a Pull Request**

---

## Development Setup

### Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+
- OpenAI API Key

### Quick Start

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/aura-quiet-living.git
cd aura-quiet-living

# Backend setup
cd aura-backend
cp .env.example .env
# Edit .env with your credentials
mvn spring-boot:run

# Frontend setup (in another terminal)
cd ..
npm install
npm run dev
```

See [README.md](./README.md) for detailed setup instructions.

---

## Pull Request Process

1. **Update documentation** if needed
2. **Add tests** for new features
3. **Ensure all tests pass** (`mvn test` for backend, `npm test` for frontend)
4. **Update README.md** with any new dependencies or setup steps
5. **Follow the coding standards** (see below)
6. **Request review** from maintainers

### PR Title Format

Use conventional commits:

```
feat: Add new AI agent for customer service
fix: Resolve order status display issue
docs: Update API documentation
refactor: Improve database query performance
test: Add unit tests for order service
```

---

## Coding Standards

### Java (Backend)

- **Follow Spring Boot best practices**
- **Use meaningful variable names**
- **Add JavaDoc for public methods**
- **Keep methods focused and small**
- **Use Lombok annotations appropriately**

Example:
```java
/**
 * Process customer service request with AI agent
 * 
 * @param message User's message
 * @param sessionId Session identifier
 * @return AI generated response
 */
public String handleCustomerService(String message, String sessionId) {
    // Implementation
}
```

### TypeScript (Frontend)

- **Use TypeScript strict mode**
- **Define proper types/interfaces**
- **Use functional components with hooks**
- **Follow React best practices**

Example:
```typescript
interface ChatMessage {
    role: 'user' | 'model';
    text: string;
    timestamp: number;
}
```

### Documentation

- **Update README.md** for user-facing changes
- **Update ARCHITECTURE.md** for architectural changes
- **Add inline comments** for complex logic
- **Write clear commit messages**

---

## Code Review Process

All submissions require review. We use GitHub pull requests for this purpose.

**Reviewers will check:**
- Code quality and style
- Test coverage
- Documentation updates
- Breaking changes
- Performance implications

---

## Questions?

- Check [ARCHITECTURE.md](./documents/ARCHITECTURE.md)
- Review [DEVELOPMENT_STANDARDS.md](./documents/DEVELOPMENT_STANDARDS.md)
- Open a [discussion](https://github.com/YOUR_USERNAME/aura-quiet-living/discussions)

---

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

**Thank you for contributing to Aura! 🙏**
