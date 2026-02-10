# QA-Ai Agent - AI Agent技术讲解

本文件夹包含所有与AI Agent相关的技术讲解文档，帮助你理解系统的AI功能和实现细节。

---

## 📚 核心文档

### 1. AI调用流程
- **AI_CALL_FLOW_COMPLETE_GUIDE.md** (80KB) ⭐
  - 最详尽的AI调用流程文档
  - 从前端到后端的完整流程
  - 包含意图识别、增强查询、函数调用等核心机制
  - 适合全面理解AI系统架构

---

## 🧠 核心机制讲解

### 2. 记忆系统
- **MEMORY_SYSTEM.md** (28KB) ⭐
  - 三层记忆架构完整解析（内存、SQL、向量库）
  - 数据保存与读取流程
  - SQL数据调用时机
  - 向量存储自动保存机制
  - 会话ID持久化方案

### 3. Function与Agent关系
- **FUNCTION_REGISTRATION_AND_AGENTS.md** (22KB)
  - Function注册机制
  - Agent与Function的映射关系
  - Function Calling vs Direct Service Call

### 4. 增强查询机制
- **PRODUCT_KEYWORD_EXTRACTION_IMPROVEMENT.md** (13KB)
  - 如何处理代词（"它"、"那个"）
  - 产品关键词提取
  - 上下文理解原理

---

## 🔧 技术细节

### 5. RAG分块策略
- **PDF_CHUNKING_EXPLAINED.md** (18KB)
  - PDF产品手册分块策略
  - 为什么分1000字符
  - 如何优化检索准确度

---

## 🧪 测试文档

### 6. AI测试问题集
- **AI_TEST_QUESTIONS_COMPREHENSIVE.md** (10KB)
  - 简化版测试问题（11个核心问题）
  - 覆盖所有Agent和Function
  - 包含上下文依赖问题

### 7. 详细测试指南
- **AI_ASSISTANT_TEST_GUIDE.md** (24KB)
  - 完整的测试场景和预期结果
  - 数据准备说明
  - 测试检查清单

---

## 📖 阅读顺序建议

### 快速入门（30分钟）
1. AI_CALL_FLOW_COMPLETE_GUIDE.md（重点看序列图和流程部分）
2. FUNCTION_REGISTRATION_AND_AGENTS.md（理解Function机制）
3. AI_TEST_QUESTIONS_COMPREHENSIVE.md（尝试提问测试）

### 深度理解（2小时）
1. AI_CALL_FLOW_COMPLETE_GUIDE.md（完整阅读）
2. MEMORY_SYSTEM.md（理解三层记忆系统）
3. PRODUCT_KEYWORD_EXTRACTION_IMPROVEMENT.md（理解增强查询）
4. PDF_CHUNKING_EXPLAINED.md（理解RAG）

### 测试验证（1小时）
1. AI_ASSISTANT_TEST_GUIDE.md（完整测试指南）
2. AI_TEST_QUESTIONS_COMPREHENSIVE.md（执行测试）

---

## 🎯 按需查询

| 我想了解... | 推荐文档 |
|-----------|---------|
| AI是如何工作的？ | AI_CALL_FLOW_COMPLETE_GUIDE.md |
| AI如何记住对话？ | MEMORY_SYSTEM.md |
| 向量库如何自动保存？ | MEMORY_SYSTEM.md (向量存储章节) |
| AI如何调用Function？ | FUNCTION_REGISTRATION_AND_AGENTS.md |
| AI如何理解"它"、"那个"？ | PRODUCT_KEYWORD_EXTRACTION_IMPROVEMENT.md |
| PDF手册如何被索引？ | PDF_CHUNKING_EXPLAINED.md |
| 如何测试AI功能？ | AI_TEST_QUESTIONS_COMPREHENSIVE.md |
| 完整测试指南？ | AI_ASSISTANT_TEST_GUIDE.md |

---

## 💡 关键概念速查

### 核心组件
- **OrchestratorAgent**: 意图路由（判断用户想做什么）
- **ProductExpertAgent**: 产品咨询（增强查询、RAG检索）
- **CustomerServiceAgent**: 订单服务（Function Calling）
- **GeneralChatAgent**: 通用聊天（闲聊、问候）

### 关键技术
- **Intent Recognition**: OpenAI识别用户意图
- **Enhanced Query**: 解析代词、添加上下文
- **Function Calling**: OpenAI自动调用Java函数
- **RAG**: 检索产品手册相关内容
- **Memory Service**: 三层记忆（内存/SQL/向量库）

---

**文件夹创建日期**: 2026-02-09  
**最后更新**: 2026-02-10  
**文档总数**: 7个  
**总大小**: ~175KB

---
