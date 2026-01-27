# Aura Quiet Living - 团队分工与 Git 工作流

## 👥 团队配置（6 人）

> **核心理念**：让每个人都接触 AI Agents 开发，不设专门测试角色

### 角色分配（AI 优先）

| 成员 | 角色 | AI 相关职责 | 其他职责 |
|------|------|------------|----------|
| **成员 A** | AI 架构师 ⭐ | RAG + Multi-Agent 协调 | Spring AI 配置 |
| **成员 B** | AI Agent 开发 | ProductExpertAgent | 产品/订单 Service |
| **成员 C** | AI Function 开发 | 5 个 AI Functions | Memory Service |
| **成员 D** | AI Agent 开发 | OrchestratorAgent + CustomerServiceAgent | PDF 处理 |
| **成员 E** | AI 前端集成 | AI 聊天界面 + API 对接 | 前端集成 |
| **成员 F** | AI Prompt 工程师 | Prompt 设计 + 测试 | 数据库初始化 |

### 🎯 AI 学习目标

每个成员都会接触到：
- ✅ **Spring AI 框架**（成员 A-D, F）
- ✅ **AI Agent 开发**（成员 A, B, D）
- ✅ **Function Calling**（成员 C）
- ✅ **RAG 系统**（成员 A）
- ✅ **Prompt Engineering**（成员 F）
- ✅ **AI 前端集成**（成员 E）

### 🔍 当前项目状态

**前端（已完成 ✅）**：
- ✅ 12 个组件文件（Navbar、Hero、ProductGrid、ProductDetail、Cart、Checkout 等）
- ⚠️ **缺少**：`services/api.ts`、AI 聊天组件

**后端（骨架已创建 ⏳）**：
- ✅ 31 个 Java 类骨架
- ✅ `data.sql` 数据库初始化脚本（已完成）
- ⚠️ **需要实现**：所有方法的具体逻辑
- ⚠️ **需要创建**：产品 PDF 说明书

> **📋 详细分工方案**：查看 [团队分工详细方案.md](./团队分工详细方案.md) 了解每个成员负责的具体类和任务

---

## 📋 详细任务分工

### 🔵 成员 A - AI 架构师（核心 AI 能力）

**AI 相关任务（主要）**：
1. ✅ 实现 `RAGService.java`（检索增强生成）⭐
   - PDF 向量化逻辑
   - 语义检索实现
   - 与 SimpleVectorStore 集成
2. ✅ 实现 `MultiAgentService.java`（多 Agent 协调）⭐
   - Agent 路由逻辑（根据用户意图分配 Agent）
   - 任务分解和编排
   - Agent 间通信
3. ✅ 实现 `AIAgentService.java`（主编排器）⭐
   - 整合所有 AI 能力
   - ReAct 模式实现（思考-行动-观察）
4. ✅ 实现 `PDFVectorizationService.java`
   - PDF 解析（Apache PDFBox）
   - 文本分块（Chunking）
   - 向量化存储

**其他任务**：
5. ✅ 配置 Spring AI
   - `OpenAIConfig.java` 完善
   - `VectorStoreConfig.java` 完善
6. ✅ 编写单元测试（自己模块）

**Git 分支**：
- `feature/rag-service`
- `feature/multi-agent-service`
- `feature/ai-orchestrator`

**学习重点**：RAG、Multi-Agent、向量化  
**预计时间**：5-6 天

---

### 🔵 成员 B - AI Agent 开发（产品专家）

**AI 相关任务（主要）**：
1. ✅ 实现 `ProductExpertAgent.java`（产品专家 Agent）⭐
   - 产品推荐逻辑
   - 产品对比分析
   - 产品特性解释
   - 与 RAG 系统集成（查询产品说明书）
2. ✅ 设计产品专家的 Prompt
   - System Prompt（角色设定）
   - Few-shot Examples（示例对话）

**其他任务**：
3. ✅ 实现 `ProductService.java`
   - `getAllProducts()` - 查询所有产品
   - `getProductById()` - 根据 ID 查询
   - `searchProducts()` - 关键词搜索
   - `getProductsByCategory()` - 分类筛选
4. ✅ 实现 `OrderService.java`
   - `createOrder()` - 创建订单（模拟支付）
   - `getOrderByNumber()` - 查询订单
   - `updateShippingAddress()` - 修改配送地址
5. ✅ 编写单元测试（自己模块）

**Git 分支**：
- `feature/product-expert-agent`
- `feature/product-service`
- `feature/order-service`

**学习重点**：AI Agent 开发、Prompt Engineering  
**预计时间**：4-5 天

---

### 🔵 成员 C - AI Function 开发（Function Calling）

**AI 相关任务（主要）**：
1. ✅ 实现 5 个 AI Function 类（Function Calling）⭐
   - `GetOrderStatusFunction.java` - 查询订单状态
   - `UpdateOrderAddressFunction.java` - 更新配送地址
   - `SearchProductsFunction.java` - 搜索产品
   - `QueryProductManualFunction.java` - 查询说明书（调用 RAG）
   - `CheckInventoryFunction.java` - 检查库存
2. ✅ 实现 `MemoryService.java`（三层记忆系统）⭐
   - 会话级记忆（短期）- 当前对话上下文
   - 用户级记忆（中期）- 用户偏好和历史
   - 长期记忆存储（数据库）- 持久化
3. ✅ 设计 Function 的 JSON Schema
   - 定义每个 Function 的参数
   - 编写清晰的描述（让 AI 知道何时调用）

**其他任务**：
4. ✅ 实现 `AIController.java`
   - `POST /api/ai/chat` - 发送消息
   - `GET /api/ai/chat/history/{sessionId}` - 获取历史
5. ✅ 编写单元测试（自己模块）

**Git 分支**：
- `feature/ai-functions`
- `feature/memory-service`
- `feature/ai-controller`

**学习重点**：Function Calling、Memory 管理  
**预计时间**：4-5 天

---

### 🔵 成员 D - AI Agent 开发（协调者 + 客服）

**AI 相关任务（主要）**：
1. ✅ 实现 `OrchestratorAgent.java`（主协调者）⭐
   - 意图识别（用户想做什么？）
   - Agent 路由（分配给哪个 Agent？）
   - 结果整合
2. ✅ 实现 `CustomerServiceAgent.java`（客服专员）⭐
   - 订单查询和处理
   - 售后服务
   - 地址修改
3. ✅ 设计 Agent 的 Prompt
   - Orchestrator 的路由逻辑 Prompt
   - CustomerService 的服务话术 Prompt

**其他任务**：
4. ✅ 实现 `PDFParser.java`
   - PDF 文本提取（Apache PDFBox）
   - 文本清洗和格式化
5. ✅ 创建产品 PDF 说明书
   - 为 6 个产品创建简单的 PDF 文档
   - 放置到 `src/main/resources/manuals/` 目录
6. ✅ 编写单元测试（自己模块）

**Git 分支**：
- `feature/orchestrator-agent`
- `feature/customer-service-agent`
- `feature/pdf-parser`

**学习重点**：AI Agent 协调、意图识别  
**预计时间**：4-5 天

---

### 🟢 成员 E - AI 前端集成（聊天界面）

**AI 相关任务（主要）**：
1. ✅ 创建 AI 聊天组件 ⭐
   - `ChatPanel.tsx` - 聊天面板
   - `MessageBubble.tsx` - 消息气泡（区分用户/AI）
   - `ChatInput.tsx` - 输入框
   - 打字动画效果（AI 回复时）
2. ✅ 实现 AI 聊天逻辑
   - WebSocket 或轮询（实时对话）
   - 会话管理（sessionId）
   - 消息历史展示
3. ✅ AI 交互优化
   - 快捷问题按钮（"推荐产品"、"查询订单"）
   - Markdown 渲染（AI 回复支持格式化）
   - 代码高亮（如果 AI 返回代码）

**其他任务**：
4. ✅ 创建 API 服务层
   - `services/api.ts`（Axios 封装）
   - 产品/订单/AI Chat API 调用
5. ✅ 集成后端 API 到现有组件
   - 修改 `ProductGrid.tsx`（从 API 获取产品）
   - 修改 `Checkout.tsx`（调用订单 API）
6. ✅ 前端调试和优化
   - 错误处理、加载状态

**Git 分支**：
- `feature/ai-chat-ui`
- `feature/api-integration`

**学习重点**：AI 聊天界面、实时交互  
**预计时间**：3-4 天

---

### 🟣 成员 F - AI Prompt 工程师 + 数据准备

**AI 相关任务（主要）**：
1. ✅ 实现 `SystemPrompts.java`（Prompt 工程）⭐
   - 品牌人格 Prompt（Aura 的语气和风格）
   - ProductExpertAgent Prompt（产品专家角色）
   - CustomerServiceAgent Prompt（客服话术）
   - OrchestratorAgent Prompt（路由逻辑）
2. ✅ Prompt 测试和优化 ⭐
   - 测试不同场景下的 AI 回复
   - 优化 Prompt 提高准确性
   - Few-shot Examples 设计
3. ✅ AI 功能端到端测试
   - 测试完整的 AI 对话流程
   - 测试 Function Calling 是否正确触发
   - 测试 RAG 知识问答准确性

**其他任务**：
4. ✅ ~~创建数据库初始化脚本~~ （已完成）
5. ✅ 文档完善
   - API 文档（Swagger）
   - AI 能力演示脚本
   - README 更新

**Git 分支**：
- `feature/system-prompts`
- `feature/database-init`
- `feature/ai-testing`
- `docs/ai-documentation`

**学习重点**：Prompt Engineering、AI 测试  
**预计时间**：3-4 天

---

## 🔄 Git 工作流（详细步骤）

### 第一步：初始化仓库（成员 A 或项目负责人）

```bash
# 1. 创建 GitHub 仓库（在 GitHub 网站上操作）
# 仓库名：aura-quiet-living
# 可见性：Private（团队项目）

# 2. 本地初始化（如果还没有）
cd /Users/johnnywang/Downloads/aura-quiet-living
git init
git add .
git commit -m "chore: initial project setup"

# 3. 关联远程仓库
git remote add origin https://github.com/your-team/aura-quiet-living.git

# 4. 创建并推送 main 分支
git branch -M main
git push -u origin main

# 5. 创建 develop 分支（集成分支）
git checkout -b develop
git push -u origin develop

# 6. 设置 develop 为默认分支（在 GitHub Settings 中操作）
```

---

### 第二步：团队成员克隆仓库

```bash
# 每个成员执行
git clone https://github.com/your-team/aura-quiet-living.git
cd aura-quiet-living

# 切换到 develop 分支
git checkout develop
git pull origin develop
```

---

### 第三步：功能开发流程（每个成员）

#### 3.1 创建功能分支

```bash
# 从 develop 创建新分支
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name

# 示例：
# 成员 A: git checkout -b feature/spring-ai-config
# 成员 B: git checkout -b feature/database-schema
# 成员 D: git checkout -b feature/frontend-setup
```

#### 3.2 开发和提交

```bash
# 进行开发...

# 查看修改
git status

# 添加文件
git add .
# 或者添加特定文件
git add src/main/java/com/aura/config/AIConfig.java

# 提交（遵循 Conventional Commits）
git commit -m "feat(ai): add Spring AI configuration"

# 更多提交示例：
# git commit -m "feat(product): implement product CRUD API"
# git commit -m "fix(order): validate shipping address format"
# git commit -m "docs(readme): update setup instructions"
```

#### 3.3 推送到远程

```bash
# 首次推送
git push -u origin feature/your-feature-name

# 后续推送
git push
```

#### 3.4 创建 Pull Request（PR）

1. 访问 GitHub 仓库页面
2. 点击 "Pull requests" → "New pull request"
3. Base: `develop` ← Compare: `feature/your-feature-name`
4. 填写 PR 标题和描述：
   ```markdown
   ## 功能描述
   实现了 Spring AI 配置和基础集成
   
   ## 变更内容
   - 添加了 Spring AI 依赖
   - 配置了 OpenAI API
   - 创建了 AIConfig 配置类
   
   ## 测试
   - [x] 单元测试通过
   - [x] 本地运行正常
   
   ## 截图（如果是前端）
   （可选）
   ```
5. 指定审查者（Reviewers）：至少 1 人
6. 点击 "Create pull request"

#### 3.5 代码审查

**审查者（其他成员）**：
```bash
# 拉取分支进行本地测试
git fetch origin
git checkout feature/your-feature-name
git pull origin feature/your-feature-name

# 运行测试
cd aura-backend
mvn test  # 后端测试

cd ..
npm test  # 前端测试

# 在 GitHub 上进行代码审查
# - 检查代码质量
# - 提出改进建议
# - 批准或请求修改
```

#### 3.6 合并到 develop

**方式一：Squash and Merge（推荐）**
- 在 GitHub PR 页面点击 "Squash and merge"
- 所有提交会合并为一个提交
- 保持 develop 分支历史清晰

**方式二：命令行合并**
```bash
git checkout develop
git pull origin develop
git merge --squash feature/your-feature-name
git commit -m "feat(scope): feature description"
git push origin develop
```

#### 3.7 删除功能分支

```bash
# 删除本地分支
git branch -d feature/your-feature-name

# 删除远程分支
git push origin --delete feature/your-feature-name
```

---

### 第四步：同步 develop 分支

```bash
# 每天开始工作前，同步最新代码
git checkout develop
git pull origin develop

# 如果你的功能分支还在开发中，需要合并最新的 develop
git checkout feature/your-feature-name
git merge develop
# 解决冲突（如果有）
git add .
git commit -m "chore: merge develop into feature branch"
git push
```

---

### 第五步：发布到 main（项目完成后）

```bash
# 由项目负责人执行
git checkout main
git pull origin main
git merge develop
git push origin main

# 打标签
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

---

## 📅 开发时间线（8-9 天）

### Day 1-2：基础架构搭建

| 成员 | 任务 | 分支 |
|------|------|------|
| A | Spring AI 配置、项目结构 | `feature/spring-ai-config` |
| B | 数据库设计、实体类 | `feature/database-schema` |
| C | 基础 Function 接口定义 | `feature/ai-functions-base` |
| D | 前端项目初始化、路由配置 | `feature/frontend-setup` |
| E | API 服务封装 | `feature/api-integration` |
| F | 测试框架配置 | `feature/test-setup` |

**里程碑**：
- ✅ 后端项目可启动
- ✅ 前端项目可启动
- ✅ 数据库连接成功

---

### Day 3-4：核心功能开发

| 成员 | 任务 | 分支 |
|------|------|------|
| A | Multi-Agent 系统实现 | `feature/multi-agent-system` |
| B | 产品/订单 API 完成 | `feature/product-api`, `feature/order-api` |
| C | 8 个 Function 实现 | `feature/ai-functions` |
| D | 产品页面开发 | `feature/product-pages` |
| E | 购物车页面 | `feature/cart-page` |
| F | 单元测试编写 | `feature/backend-tests` |

**里程碑**：
- ✅ 产品 CRUD API 可用
- ✅ 订单 API 可用
- ✅ 基础 AI 对话可用

---

### Day 5-6：AI 能力集成

| 成员 | 任务 | 分支 |
|------|------|------|
| A | RAG 系统实现 | `feature/rag-service` |
| B | 订单高级功能（修改地址） | `feature/order-advanced` |
| C | Memory 管理实现 | `feature/memory-management` |
| D | 页面优化和响应式 | `feature/ui-polish` |
| E | AI 聊天界面完成 | `feature/chat-interface` |
| F | 集成测试 | `feature/integration-tests` |

**里程碑**：
- ✅ RAG 知识问答可用
- ✅ Memory 记忆功能可用
- ✅ AI 聊天界面完成

---

### Day 7：前后端联调

| 成员 | 任务 | 分支 |
|------|------|------|
| A | AI 功能调优 | `bugfix/ai-optimization` |
| B | API 性能优化 | `refactor/api-optimization` |
| C | 错误处理完善 | `feature/error-handling` |
| D+E | 前端联调和 Bug 修复 | `bugfix/frontend-integration` |
| F | 端到端测试 | `feature/e2e-tests` |

**里程碑**：
- ✅ 前后端完全打通
- ✅ 所有功能可演示

---

### Day 8：测试和优化

| 成员 | 任务 | 分支 |
|------|------|------|
| 全员 | Bug 修复 | `bugfix/*` |
| F | 测试覆盖率提升 | `test/coverage-improvement` |
| D+E | UI/UX 优化 | `feature/ui-ux-polish` |

**里程碑**：
- ✅ 测试覆盖率 > 70%
- ✅ 无严重 Bug

---

### Day 9：文档和演示准备

| 成员 | 任务 | 分支 |
|------|------|------|
| A | AI 能力文档 | `docs/ai-capabilities` |
| B | API 文档（Swagger） | `docs/api-documentation` |
| F | README 完善 | `docs/readme-update` |
| 全员 | 演示脚本准备 | - |

**里程碑**：
- ✅ 文档完整
- ✅ 演示准备就绪

---

## 🚨 常见问题和解决方案

### 1. 合并冲突

```bash
# 拉取最新 develop
git checkout develop
git pull origin develop

# 切回功能分支并合并
git checkout feature/your-feature-name
git merge develop

# 解决冲突
# 1. 打开冲突文件
# 2. 手动解决冲突标记（<<<<<<, ======, >>>>>>）
# 3. 保存文件

# 标记为已解决
git add .
git commit -m "chore: resolve merge conflicts"
git push
```

### 2. 误提交到错误分支

```bash
# 撤销最后一次提交（保留修改）
git reset --soft HEAD~1

# 切换到正确分支
git checkout correct-branch

# 重新提交
git add .
git commit -m "feat: your message"
```

### 3. 需要修改最后一次提交信息

```bash
# 修改最后一次提交信息
git commit --amend -m "feat(scope): corrected message"

# 强制推送（如果已经推送过）
git push --force
```

### 4. 拉取他人分支进行协作

```bash
# 拉取所有远程分支
git fetch origin

# 查看所有分支
git branch -a

# 切换到他人分支
git checkout feature/other-person-branch
git pull origin feature/other-person-branch

# 进行修改并推送
git add .
git commit -m "feat: collaborative work"
git push
```

---

## ✅ 每日站会（Daily Standup）

**时间**：每天上午 10:00（15 分钟）

**每人回答三个问题**：
1. 昨天完成了什么？
2. 今天计划做什么？
3. 有什么阻碍？

**示例**：
> **成员 A**：
> - 昨天：完成了 Spring AI 配置和 Multi-Agent 基础框架
> - 今天：实现 RAG 系统的 PDF 解析
> - 阻碍：需要成员 B 提供产品数据库表结构

---

## 📊 进度跟踪

使用 GitHub Projects 或简单的 Markdown 文件：

```markdown
## 后端进度
- [x] Spring AI 配置
- [x] 数据库设计
- [/] Multi-Agent 系统（进行中）
- [ ] RAG 系统
- [ ] Memory 管理

## 前端进度
- [x] 项目初始化
- [/] 组件库（进行中）
- [ ] 产品页面
- [ ] AI 聊天界面
```

---

## 🎯 成功标准

### 代码质量
- ✅ 所有 PR 必须经过至少 1 人审查
- ✅ 测试覆盖率 > 70%
- ✅ 无严重 Bug

### Git 规范
- ✅ 提交信息遵循 Conventional Commits
- ✅ 分支命名规范
- ✅ 定期同步 develop 分支

### 团队协作
- ✅ 每日站会参与
- ✅ 及时沟通阻碍
- ✅ 代码审查积极参与

---

**文档版本**: 1.0  
**创建日期**: 2026-01-27  
**适用项目**: Aura Quiet Living  
**团队规模**: 6 人
