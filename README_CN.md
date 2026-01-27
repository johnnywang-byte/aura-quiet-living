# Aura 电商平台

> 基于 Spring AI 的智能电商平台，展示 AI Agent 核心能力

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2-blue)](https://react.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 📋 项目概述

**Aura** 是一个演示型电商平台，展示 **10 大 Spring AI Agent 核心能力**：

1. ✅ **Prompt Engineering（提示工程）** - 品牌人格设定
2. ✅ **RAG（检索增强生成）** - 产品说明书问答（PDF）
3. ✅ **Memory（记忆管理）** - 多层对话记忆
4. ✅ **Function Calling（函数调用）** - 5 个核心函数
5. ✅ **Complex Task（复杂任务）** - 多步骤工作流
6. ✅ **Multi-Agent（多智能体）** - Agent 协作
7. ✅ **Planning（任务规划）** - 任务规划（ReAct）
8. ✅ **Tool Chain（工具链）** - 工具编排
9. ✅ **Error Handling（错误处理）** - 智能错误恢复
10. ✅ **Reflection（反思机制）** - 自我改进

---

## 🚀 快速开始

### 环境要求

- **Java 17+**
- **Node.js 18+**
- **MySQL 8.0+**
- **Maven 3.8+**
- **OpenAI API Key**

### 1. 克隆仓库

```bash
git clone <repository-url>
cd aura-quiet-living
```

### 2. 配置后端

```bash
cd aura-backend

# 配置环境变量
cp .env.example .env
# 编辑 .env 文件，添加 OPENAI_API_KEY 和 DB_PASSWORD

# 创建数据库
mysql -u root -p < src/main/resources/data.sql

# 运行后端
mvn spring-boot:run
```

后端将在 `http://localhost:8080` 启动

### 3. 配置前端

```bash
cd ..  # 返回项目根目录
npm install
npm run dev
```

前端将在 `http://localhost:5173` 启动

### 4. 验证

打开浏览器访问：`http://localhost:5173`

---

## 📁 项目结构

```
aura-quiet-living/
├── aura-backend/              # Spring Boot 后端
│   ├── src/main/java/com/aura/
│   │   ├── config/            # 配置类
│   │   ├── controller/        # REST API
│   │   ├── service/           # 业务逻辑
│   │   ├── repository/        # 数据访问
│   │   ├── model/             # 实体 & DTO
│   │   └── ai/                # AI agents & functions
│   ├── src/main/resources/
│   │   ├── application.yml    # 配置文件
│   │   ├── data.sql           # 数据库初始化
│   │   └── manuals/           # 产品 PDF
│   └── pom.xml
│
├── components/                # React 组件
├── services/                  # API 服务
├── types.ts                   # TypeScript 类型
│
├── 团队分工详细方案.md         # 详细分工表 ⭐
├── 团队分工与Git工作流.md      # Git 工作流
├── 立项与可行性分析.md         # 立项文档
├── 开发规范.md                # 中文开发规范
├── DEVELOPMENT_STANDARDS.md   # 英文开发规范
├── ARCHITECTURE.md            # 完整架构手册
└── README.md                  # 本文件
```

---

## 🛠️ 技术栈

### 后端
- **Spring Boot 3.2** - 应用框架
- **Spring AI** - AI 集成
- **OpenAI (gpt-4o-mini)** - 大语言模型
- **MySQL 8.0** - 数据库
- **SimpleVectorStore** - 向量存储（RAG）
- **Apache PDFBox** - PDF 处理

### 前端
- **React 19.2** - UI 框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Tailwind CSS** - 样式框架

---

## 📚 文档

- **[团队分工详细方案.md](./团队分工详细方案.md)** - 6 人团队详细分工 ⭐
- **[中国区Git使用指南.md](./中国区Git使用指南.md)** - 中国区成员网络加速方案 ⭐
- **[团队分工与Git工作流.md](./团队分工与Git工作流.md)** - Git 工作流和协作规范
- **[立项与可行性分析.md](./立项与可行性分析.md)** - 立项和可行性分析
- **[开发规范.md](./开发规范.md)** - 中文开发规范
- **[DEVELOPMENT_STANDARDS.md](./DEVELOPMENT_STANDARDS.md)** - 英文开发规范
- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - 完整架构手册（100+ 页）

---

## 🎯 核心功能

### 电商功能
- 产品目录（6 个高端产品）
- 购物车
- 订单管理
- 模拟支付（自动成功）

### AI Agent 功能
- **智能对话** - 自然对话，品牌人格
- **产品推荐** - 上下文感知推荐
- **订单协助** - 查询状态、修改地址
- **知识库** - 从产品说明书（PDF）回答问题
- **多步骤任务** - 自动处理复杂请求

---

## 👥 团队分工

**6 人团队，3-4 天开发周期**

详见 [团队分工详细方案.md](./团队分工详细方案.md)

| 成员 | 主要职责 | 类数量 |
|------|----------|--------|
| 成员 A | AI 核心架构（RAG + Multi-Agent） | 6 个类 |
| 成员 B | 业务逻辑 + ProductExpertAgent | 6 个类 |
| 成员 C | AI Functions（Function Calling） | 6 个类 |
| 成员 D | AI Agents + Memory | 6 个类 |
| 成员 E | Prompt 设计 + 实体类 | 5 个类 |
| 成员 F | 前端集成 + Controller + DTO | 6 个类 + 前端 |

---

## 🧪 测试

### 后端测试
```bash
cd aura-backend
mvn test
```

### 前端测试
```bash
npm test
```

---

## 📊 API 端点

### 产品
- `GET /api/products` - 获取所有产品
- `GET /api/products/{id}` - 获取产品详情
- `GET /api/products/search?q={keyword}` - 搜索产品

### 订单
- `POST /api/orders` - 创建订单（模拟支付）
- `GET /api/orders/{orderNumber}` - 获取订单详情

### AI 助手
- `POST /api/ai/chat` - 发送消息给 AI
- `GET /api/ai/chat/history/{sessionId}` - 获取聊天历史

---

## 🎬 演示场景

详见 [ARCHITECTURE.md - 第 9 节](./ARCHITECTURE.md#9-演示脚本)

**快速示例**：

1. **RAG**: "Aura Harmony 的蓝牙传输距离是多少？"
2. **Memory**: "我收到的这个颜色不对"（在提到订单后）
3. **Function Calling**: "帮我看看订单状态"
4. **Complex Task**: "把订单送到新地址 XXX"

---

## 🔐 环境变量

创建 `.env` 文件：

**后端** (`aura-backend/.env`)：
```bash
OPENAI_API_KEY=sk-your-api-key-here
DB_PASSWORD=your-mysql-password
```

**前端** (`.env.local`)：
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 🐛 故障排除

### 后端无法启动
- 检查 MySQL 是否运行
- 验证 `.env` 中的 `OPENAI_API_KEY`
- 检查端口 8080 是否可用

### 前端无法连接后端
- 验证后端是否在端口 8080 运行
- 检查 `application.yml` 中的 CORS 配置

### AI 响应慢
- 首次请求正常（冷启动）
- 检查 OpenAI API 状态
- 考虑使用 `gpt-3.5-turbo` 以获得更快响应

---

## 📝 开发规范

- **代码规范**：遵循 [开发规范.md](./开发规范.md)
- **Git 工作流**：遵循 [团队分工与Git工作流.md](./团队分工与Git工作流.md)
- **提交信息**：使用 Conventional Commits 格式

---

## 📞 支持

如有问题：
- 查看 [ARCHITECTURE.md](./ARCHITECTURE.md)
- 查看 [DEVELOPMENT_STANDARDS.md](./DEVELOPMENT_STANDARDS.md)
- 在 GitHub 上提交 Issue

---

**由 Aura 团队用 ❤️ 构建**

---

## 🌐 English Version

See [README.md](./README.md) for English documentation.
