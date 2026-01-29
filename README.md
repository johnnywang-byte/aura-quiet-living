# Aura E-Commerce Platform

> Spring AI powered e-commerce platform with intelligent agent capabilities

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2-blue)](https://react.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 📋 Project Overview

**Aura** is a demonstration e-commerce platform showcasing **10 core Spring AI Agent capabilities**:

1. ✅ **Prompt Engineering** - Brand personality
2. ✅ **RAG** - Product manual Q&A (PDF)
3. ✅ **Memory** - Multi-layer conversation memory
4. ✅ **Function Calling** - 8 core functions
5. ✅ **Complex Task** - Multi-step workflows
6. ✅ **Multi-Agent** - Agent collaboration
7. ✅ **Planning** - Task planning (ReAct)
8. ✅ **Tool Chain** - Tool orchestration
9. ✅ **Error Handling** - Smart error recovery
10. ✅ **Reflection** - Self-improvement

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **MySQL 8.0+**
- **Maven 3.8+**
- **OpenAI API Key**

### 1. Clone Repository

```bash
git clone <repository-url>
cd aura-quiet-living
```

### 2. Setup Backend

```bash
cd aura-backend

# Configure environment
cp .env.example .env
# Edit .env and add your OPENAI_API_KEY and DB_PASSWORD

# Create database
mysql -u root -p < src/main/resources/data.sql

# Run backend
mvn spring-boot:run
```

Backend will start at `http://localhost:8080`

### 3. Setup Frontend

```bash
cd ..  # Back to root
npm install
npm run dev
```

Frontend will start at `http://localhost:5173`

### 4. Verify

Open browser: `http://localhost:5173`

---

## 📁 Project Structure

```
aura-quiet-living/
├── aura-backend/              # Spring Boot backend
│   ├── src/main/java/com/aura/
│   │   ├── config/            # Configuration
│   │   ├── controller/        # REST API
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Data access
│   │   ├── model/             # Entities & DTOs
│   │   └── ai/                # AI agents & functions
│   ├── src/main/resources/
│   │   ├── application.yml    # Config file
│   │   ├── data.sql           # Database init
│   │   └── manuals/           # Product PDFs
│   └── pom.xml
│
├── aura-frontend/             # React frontend
│   ├── components/            # React components
│   ├── services/              # API services
│   ├── types.ts               # TypeScript types
│   ├── package.json
│   └── vite.config.ts
│
├── documents/                 # All documentation
│   ├── ARCHITECTURE.md        # Architecture handbook
│   ├── DEVELOPMENT_STANDARDS.md
│   ├── 团队分工详细方案.md
│   ├── 团队分工与Git工作流.md
│   ├── 立项与可行性分析.md
│   └── 开发规范.md
│
├── README.md                  # This file
└── README_CN.md               # Chinese README
```

---

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2** - Application framework
- **Spring AI** - AI integration
- **OpenAI (gpt-4o-mini)** - LLM
- **MySQL 8.0** - Database
- **SimpleVectorStore** - Vector storage for RAG
- **Apache PDFBox** - PDF processing

### Frontend
- **React 19.2** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool
- **Tailwind CSS** - Styling

---

## 📚 Documentation

- **[Team Assignment (Detailed)](./documents/团队分工详细方案.md)** - Detailed 6-person team assignment ⭐
- **[Team Assignment & Git Workflow](./documents/团队分工与Git工作流.md)** - Git workflow and collaboration
- **[Project Proposal](./documents/立项与可行性分析.md)** - Project proposal and feasibility analysis (CN)
- **[Development Standards (CN)](./documents/开发规范.md)** - Chinese development standards
- **[DEVELOPMENT_STANDARDS.md](./documents/DEVELOPMENT_STANDARDS.md)** - English development standards
- **[ARCHITECTURE.md](./documents/ARCHITECTURE.md)** - Complete architecture handbook (100+ pages)
- **[aura-backend/README.md](./aura-backend/README.md)** - Backend development guide
- **[aura-frontend/README.md](./aura-frontend/README.md)** - Frontend development guide
- **[README_CN.md](./documents/README_CN.md)** - 中文版 README

---

## 🎯 Key Features

### E-Commerce
- Product catalog with 6 premium products
- Shopping cart
- Order management
- Mock payment (auto-success)

### AI Agent
- **Intelligent Chat** - Natural conversation with brand personality
- **Product Recommendations** - Context-aware suggestions
- **Order Assistance** - Query status, modify address
- **Knowledge Base** - Answer questions from product manuals (PDF)
- **Multi-step Tasks** - Handle complex requests automatically

---

## 🧪 Testing

### Backend Tests
```bash
cd aura-backend
mvn test
```

### Frontend Tests
```bash
npm test
```

---

## 📊 API Endpoints

### Products
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product details
- `GET /api/products/search?q={keyword}` - Search products

### Orders
- `POST /api/orders` - Create order (mock payment)
- `GET /api/orders/{orderNumber}` - Get order details

### AI Assistant
- `POST /api/ai/chat` - Send message to AI
- `GET /api/ai/chat/history/{sessionId}` - Get chat history

---

## 🎬 Demo Scenarios

See [ARCHITECTURE.md - Section 9](./ARCHITECTURE.md#9-演示脚本) for detailed demo scripts.

**Quick Examples:**

1. **RAG**: "Aura Harmony 的蓝牙传输距离是多少？"
2. **Memory**: "我收到的这个颜色不对" (after mentioning an order)
3. **Function Calling**: "帮我看看订单状态"
4. **Complex Task**: "把订单送到新地址 XXX"

---

## 👥 Team

- **6 people**, **8-9 days**
- See [ARCHITECTURE.md - Section 8](./ARCHITECTURE.md#8-开发计划) for team division

---

## 🔐 Environment Variables

Create `.env` files:

**Backend** (`aura-backend/.env`):
```bash
OPENAI_API_KEY=sk-your-api-key-here
DB_PASSWORD=your-mysql-password
```

**Frontend** (`.env.local`):
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 🐛 Troubleshooting

### Backend won't start
- Check MySQL is running
- Verify `OPENAI_API_KEY` in `.env`
- Check port 8080 is available

### Frontend can't connect to backend
- Verify backend is running on port 8080
- Check CORS configuration in `application.yml`

### AI responses are slow
- Normal for first request (cold start)
- Check OpenAI API status
- Consider using `gpt-3.5-turbo` for faster responses

---

## 📝 License

MIT License - see [LICENSE](LICENSE) file

---

## 🙏 Acknowledgments

- [Spring AI](https://docs.spring.io/spring-ai/reference/)
- [OpenAI](https://platform.openai.com/)
- [React](https://react.dev/)

---

## 📞 Support

For questions or issues:
- Check [ARCHITECTURE.md](./ARCHITECTURE.md)
- Review [DEVELOPMENT_STANDARDS.md](./DEVELOPMENT_STANDARDS.md)
- Open an issue on GitHub

---

**Built with ❤️ by the Aura Team**
