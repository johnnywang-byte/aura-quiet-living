# GitHub上传指南

本文档提供将Aura项目上传到GitHub的完整步骤和检查清单。

---

## ✅ 准备工作（已完成）

以下文件已经为您准备好：

- [x] `.gitignore` - 完整的忽略规则（前后端、数据、IDE）
- [x] `LICENSE` - MIT许可证
- [x] `.env.example` - 环境变量模板（前后端）
- [x] `CONTRIBUTING.md` - 贡献指南
- [x] `SECURITY.md` - 安全策略
- [x] `.github/` 目录 - Issue和PR模板

---

## 📋 上传前检查清单

### 1️⃣ 安全检查

```bash
# 检查是否有敏感文件被追踪
cd /Users/johnnywang/Downloads/aura-quiet-living
git ls-files | grep -E '\.env$|secret|password|key|credentials'

# 应该返回空（没有结果）
```

**确认项**：
- [ ] 没有 `.env` 文件被追踪
- [ ] `application.yml` 使用环境变量（不含实际密钥）
- [ ] 没有数据库密码明文
- [ ] 没有OpenAI API密钥明文

### 2️⃣ 数据文件检查

```bash
# 检查大文件
find . -type f -size +10M -not -path "*/node_modules/*" -not -path "*/.git/*"

# data/vector-store.json (1.8MB) 应该被忽略
git status | grep "vector-store.json"
# 应该没有输出
```

**确认项**：
- [ ] `data/vector-store.json` 不在git追踪中
- [ ] 没有超大文件（>50MB）
- [ ] PDF文件在 `aura-backend/src/main/resources/manuals/` 中（应该追踪）

### 3️⃣ 文档检查

**确认项**：
- [ ] `README.md` 完整且最新
- [ ] `LICENSE` 文件存在
- [ ] `.env.example` 文件存在并有说明
- [ ] `CONTRIBUTING.md` 存在
- [ ] `QA-Ai Agent/` 文档完整（包括新的MEMORY_SYSTEM.md）

---

## 🚀 上传步骤

### Step 1: 整理当前Git状态

```bash
cd /Users/johnnywang/Downloads/aura-quiet-living

# 查看当前状态
git status

# 查看未追踪的文件
git status -u
```

### Step 2: 提交所有准备好的文件

```bash
# 添加新创建的GitHub相关文件
git add .gitignore LICENSE .env.example CONTRIBUTING.md SECURITY.md
git add aura-backend/.env.example
git add .github/

# 添加QA文档更新
git add "QA-Ai Agent/"

# 提交
git commit -m "chore: 准备GitHub上传

- 更新.gitignore (完整的前后端忽略规则)
- 添加LICENSE (MIT)
- 添加.env.example模板文件
- 添加CONTRIBUTING.md和SECURITY.md
- 添加GitHub Issue和PR模板
- 整合QA-Ai Agent文档 (新增MEMORY_SYSTEM.md)
"
```

### Step 3: 提交代码更改

```bash
# 查看代码更改
git diff --stat

# 逐个添加或全部添加
git add aura-backend/
git add aura-frontend/
git add documents/

# 提交代码更改
git commit -m "feat: 完善AI Agent功能和文档

- 优化记忆系统实现
- 改进Function Calling机制
- 更新Agent System Prompts
- 完善错误处理
- 添加详细的技术文档
"
```

### Step 4: 在GitHub上创建仓库

1. 访问 https://github.com/new
2. 填写信息：
   - **Repository name**: `aura-quiet-living` 或 `aura-ai-ecommerce`
   - **Description**: Spring AI powered e-commerce platform with intelligent agent capabilities
   - **Public/Private**: 选择 Public（展示用）
   - **不要**勾选 "Initialize this repository with a README"（我们已有）

3. 点击 "Create repository"

### Step 5: 推送到GitHub

```bash
# 添加远程仓库（替换YOUR_USERNAME）
git remote add origin https://github.com/YOUR_USERNAME/aura-quiet-living.git

# 推送master分支
git push -u origin master

# 或者如果使用main分支
git branch -M main
git push -u origin main
```

### Step 6: 配置GitHub仓库设置

在GitHub仓库页面：

1. **Settings → General**
   - Features: 启用 Issues, Projects（可选）
   - Pull Requests: 启用 "Allow merge commits"

2. **Settings → Secrets and variables → Actions**（如果使用CI/CD）
   - 添加 `OPENAI_API_KEY` secret（用于测试，可选）

3. **About（仓库右上角）**
   - 添加描述
   - 添加网站（如果部署了）
   - 添加标签: `spring-ai`, `react`, `ecommerce`, `ai-agent`, `openai`, `rag`

---

## 🎯 上传后的工作

### 1️⃣ 完善仓库README

在GitHub上查看README渲染效果，确保：
- [ ] 所有链接正常工作
- [ ] 图片/徽章正确显示
- [ ] 代码块格式正确
- [ ] 目录链接有效

### 2️⃣ 创建Release（可选）

```bash
# 创建标签
git tag -a v1.0.0 -m "Release v1.0.0 - Initial public release"
git push origin v1.0.0
```

然后在GitHub上：
1. 进入 "Releases"
2. 点击 "Create a new release"
3. 选择 v1.0.0 标签
4. 添加Release notes

### 3️⃣ 添加Topics

在仓库页面点击设置图标（About部分），添加topics：
```
spring-ai, spring-boot, react, typescript, openai, gpt-4, ai-agent, 
multi-agent, rag, vector-database, ecommerce, java, mysql, vite
```

### 4️⃣ 更新README中的占位符

需要在README.md中替换：
- `<repository-url>` → 实际的GitHub仓库URL
- SECURITY.md中的 `[Your Email Address Here]` → 实际联系邮箱

---

## ⚠️ 重要提醒

### ❌ 不要做的事

1. **不要提交 .env 文件**
2. **不要提交 data/vector-store.json**（1.8MB，可重新生成）
3. **不要提交任何包含API密钥的文件**
4. **不要提交node_modules/或target/目录**
5. **不要提交IDE配置文件**（除非有特殊需要）

### ✅ 应该做的事

1. **定期检查依赖安全更新**
2. **保持文档最新**
3. **回应Issues和Pull Requests**
4. **遵循语义化版本规范**
5. **编写清晰的commit信息**

---

## 📊 项目亮点（用于展示）

在项目介绍中可以强调：

1. **完整的Spring AI实现** - 10个核心能力全覆盖
2. **多Agent协作** - OrchestratorAgent + 专业Agent
3. **三层记忆系统** - 内存+SQL+向量库
4. **RAG检索增强** - PDF产品手册智能问答
5. **Function Calling** - 8个核心业务函数
6. **详细文档** - 7个QA文档 + 完整架构手册
7. **生产级代码** - 规范的项目结构和错误处理

---

## 🎓 学习价值

适合：
- Spring AI初学者
- AI Agent开发者
- 全栈开发学习者
- 毕业设计参考

包含：
- 完整的前后端代码
- 详细的实现文档
- 测试用例和指南
- 架构设计说明
- 团队协作方案

---

## 📞 问题排查

### 推送失败

**问题**: `rejected - non-fast-forward`

**解决**:
```bash
git pull origin master --rebase
git push origin master
```

### 文件过大

**问题**: `file is over 100MB`

**解决**:
```bash
# 从历史中移除大文件
git rm --cached path/to/large/file
git commit --amend
git push -f origin master
```

### 敏感信息已提交

**问题**: 不小心提交了API密钥

**解决**:
1. 立即在OpenAI平台撤销该密钥
2. 使用 `git filter-branch` 或 BFG Repo-Cleaner 清除历史
3. 强制推送: `git push -f origin master`

---

## ✅ 最终检查清单

上传前最后确认：

- [ ] 所有敏感信息已移除
- [ ] .gitignore 配置正确
- [ ] LICENSE文件存在
- [ ] README.md完整且准确
- [ ] .env.example提供了模板
- [ ] 代码可以正常运行
- [ ] 文档链接都有效
- [ ] commit信息清晰
- [ ] 没有超大文件
- [ ] GitHub仓库设置完成

---

## 🎉 完成！

恭喜！您的项目现在可以在GitHub上展示了。

**接下来可以做的事**：
1. 在LinkedIn/Twitter上分享
2. 添加到个人简历/作品集
3. 申请GitHub Star
4. 寻求社区反馈
5. 持续改进和维护

---

**祝您的项目获得关注！** ⭐
