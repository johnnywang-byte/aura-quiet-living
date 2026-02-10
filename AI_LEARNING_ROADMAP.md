# AI学习路线规划 - 从实习到AI Agent开发

**制定日期**: 2026-02-09  
**目标**: 短期找到AI相关实习，长期成为AI Agent开发专家  
**基础**: aura-quiet-living AI Agent项目经验

---

## 📋 目录

1. [短期目标：3个月找实习](#短期目标3个月找实习)
2. [中期目标：6-12个月深度提升](#中期目标6-12个月深度提升)
3. [长期目标：1-3年AI专家](#长期目标1-3年ai专家)
4. [学习资源整合](#学习资源整合)
5. [时间规划表](#时间规划表)
6. [里程碑检查点](#里程碑检查点)

---

## 🎯 短期目标：3个月找实习

### 月1：巩固项目 + LLM基础理论

#### Week 1-2：深化aura项目理解

**目标**：能流畅讲解项目15分钟 + Q&A

**任务清单**：
- [ ] 画出完整的AI调用流程图（从前端到后端）
- [ ] 整理项目亮点（量化数据）：
  - 支持多少种用户意图？
  - 响应时间是多少？
  - RAG检索准确率？
- [ ] 准备项目答辩PPT（15分钟版本）
- [ ] 写技术博客：《我如何设计AI客服系统》

**产出**：
```
aura-project-summary/
├── architecture-diagram.png     # 系统架构图
├── flow-diagram.png             # AI调用流程图
├── presentation.pptx            # 项目答辩PPT
└── blog-post.md                 # 技术博客
```

---

#### Week 3-4：Transformer核心原理

**学习资源**：RethinkFun深度学习课程

**必学章节**：
- ✅ **第15章：Transformer**（重点！5-8小时）
  - Self-Attention机制
  - Multi-Head Attention
  - Position Encoding
  - Feed Forward Network

- ✅ **第16章：BERT和GPT**（4-6小时）
  - 重点学习GPT部分
  - 理解预训练、微调、Prompt
  - 对比BERT和GPT的区别

**学习方法**：
```
Day 1-2：看课程视频 + 做笔记
Day 3-4：画图理解（手绘或draw.io）
Day 5-6：对比aura项目：
  - 为什么OpenAI能理解上下文？
  - Prompt Engineering的底层原理是什么？
Day 7：整理知识点，准备面试讲解
```

**产出**：
- [ ] Transformer结构图（带注释）
- [ ] Self-Attention计算示例（手算一遍）
- [ ] 学习笔记：《Transformer原理图解》
- [ ] 面试准备：15分钟讲解Transformer

---

### 月2：MiniMind实战 + 算法准备

#### Week 5-6：MiniMind快速上手

**目标**：理解LLM底层实现，不求完全写完

**学习路线**：
```
Day 1-2：环境准备 + Clone项目
  git clone https://github.com/jingyaogong/minimind
  理解项目结构，跑通demo

Day 3-5：理解核心模块（只读代码，不改）
  ✅ model/LMConfig.py - 模型配置
  ✅ model/model.py - Transformer实现
  ✅ 重点：attention机制如何实现

Day 6-7：手写核心部分
  ✅ 手写Self-Attention（不看代码）
  ✅ 理解Q、K、V的计算过程
  ✅ 对比：和RethinkFun课程内容
```

**产出**：
```
minimind-learning/
├── notes/
│   ├── day1-setup.md           # 环境搭建笔记
│   ├── day3-model-structure.md # 模型结构分析
│   └── day5-attention.md       # Attention实现
├── code/
│   └── self_attention.py       # 手写的Attention
└── README.md                   # 学习总结
```

---

#### Week 7-8：算法 + 面试准备

**LeetCode刷题**（每天1题）：
```
Week 7：数据结构基础
- HashMap/HashSet（5题）
- Array/List操作（5题）
- String处理（4题）

Week 8：常用算法
- 双指针（5题）
- 滑动窗口（5题）
- 二分查找（4题）
```

**技术面试准备**：
- [ ] Java基础：JVM、多线程、Spring Boot
- [ ] AI知识：能讲清楚Transformer、RAG、Agent
- [ ] 系统设计：画出aura系统架构，讲解设计决策

**行为面试准备**（STAR方法）：
```
Story 1：项目难点
- Situation：用户说"它"，如何识别？
- Task：实现代词识别功能
- Action：关键词映射 + OpenAI上下文理解
- Result：准确率提升XX%

Story 2：性能优化
- Situation：订单查询重复2次
- Task：优化查询性能
- Action：删除冗余查询，代码解耦
- Result：数据库查询减少50%

Story 3：团队协作
- Situation：前后端接口设计
- Task：统一返回格式
- Action：定义Response规范
- Result：开发效率提升
```

---

### 月3：简历优化 + 投递面试

#### Week 9-10：简历 + 作品集

**简历优化**：
```markdown
## 项目经验

### AI客服Agent系统 | Java + Spring AI + OpenAI
**项目时间**：2026.01 - 2026.02 | **角色**：核心开发

**项目描述**：
基于多Agent架构的智能客服系统，支持产品咨询、订单管理、库存查询等功能

**核心成果**：
• 设计Multi-Agent架构（Orchestrator、ProductExpert、CustomerService），
  实现意图识别准确率>90%
• 实现RAG检索增强，结合向量数据库和关键词映射，产品查询准确率提升50%
• 使用Function Calling集成7个业务函数，自动化处理订单、库存等操作
• 设计三层记忆系统（内存/SQL/向量库），支持上下文理解和多轮对话
• 优化数据库查询逻辑，通过代码解耦减少50%重复查询，提升系统性能

**技术栈**：
Java、Spring Boot、Spring AI、OpenAI API、MySQL、Vector Store、
Function Calling、RAG、Multi-Agent

**项目亮点**：
- 能够处理代词识别（"它"、"这个"等），通过上下文理解提升用户体验
- 产品关键词映射机制，将通用词（headphones）自动映射为产品名（Harmony）
- 完整的订单状态转换规则，防止业务逻辑错误

---

### MiniMind LLM学习项目 | PyTorch
**项目时间**：2026.02 | **角色**：个人学习

**项目描述**：
深入学习大语言模型底层实现，理解Transformer架构和训练原理

**学习成果**：
• 手写实现Self-Attention机制，深入理解Q、K、V的计算过程
• 分析MiniMind代码结构，理解从Tokenizer到推理的完整流程
• 对比商业API（OpenAI）和开源模型的架构差异
• 能够用通俗语言讲解Transformer原理（15分钟版本）

**技术栈**：PyTorch、Transformer、Tokenization、Self-Attention

**产出**：
- GitHub学习笔记：包含代码分析、原理图解
- 技术博客：《Transformer原理图解》
- 面试准备：能够白板讲解Attention机制
```

**作品集网站**：
```
personal-portfolio/
├── projects/
│   ├── aura-agent/          # aura项目展示
│   │   ├── demo.mp4         # 演示视频
│   │   ├── architecture.png # 架构图
│   │   └── README.md        # 项目说明
│   └── minimind-learning/   # MiniMind学习
│       ├── notes.md         # 学习笔记
│       └── attention.py     # 核心代码
└── blogs/
    ├── transformer-explained.md    # Transformer讲解
    └── rag-in-practice.md         # RAG实战
```

---

#### Week 11-12：投递 + 面试

**投递策略**：
```
目标公司（优先级）：
1. AI创业公司（字节、百度AI、商汤、旷视）
2. 大厂AI部门（阿里达摩院、腾讯AI Lab）
3. 外企（微软、谷歌、亚马逊）

岗位关键词：
- AI工程实习生
- 后端开发（AI方向）
- NLP算法实习
- AI Agent开发实习

投递渠道：
- 牛客网（每天关注实习内推）
- 实习僧（投递10家/周）
- BOSS直聘（主动联系HR）
- 校园招聘网（关注宣讲会）
- 学长学姐内推（最有效！）

投递量：
Week 11：30家公司
Week 12：20家公司（持续投递）
目标：拿到5-10个面试机会
```

**面试准备**：
- [ ] 每天模拟面试1小时（录视频）
- [ ] 整理高频面试题（AI + Java + 算法）
- [ ] 准备问面试官的问题（3-5个）
- [ ] 面试后复盘（记录问题，改进话术）

---

## 🚀 中期目标：6-12个月深度提升

### 阶段1：实习期深化（月4-6）

#### AI Agent进阶

**学习资源**：
1. **LangChain官方文档**（核心！）
   - Agents：ReAct、Plan-and-Execute
   - Memory：ConversationBuffer、VectorStore
   - Tools：Function Calling、Custom Tools
   - Chains：Sequential、Router Chain

2. **实战项目**（基于LangChain）：

**项目1：个人知识库助手**
```python
# 功能：
- 上传文档（PDF、Markdown）自动索引
- 问答系统（基于RAG）
- 记忆上下文（Memory Chain）
- 多文档对比

# 技术栈：
LangChain + FAISS + OpenAI API + Streamlit

# 时间：2周
```

**项目2：代码审查Agent**
```python
# 功能：
- 分析GitHub仓库
- 检测代码质量问题
- 生成优化建议
- 自动生成文档

# 技术栈：
LangChain + GitHub API + GPT-4

# 时间：3周
```

---

#### MiniMind深度实现

**完整实现计划**（月5-6）：

**Week 1-2：基础模块**
```python
Day 1-3：Tokenizer实现
- BPE分词算法
- 构建词表（vocab.json）
- 测试：中英文分词效果

Day 4-7：Embedding层
- Token Embedding
- Position Embedding
- 可视化：用t-SNE展示词向量
```

**Week 3-4：核心模块**
```python
Day 8-10：Self-Attention实现
- 手写矩阵运算（理解计算过程）
- Multi-Head Attention
- 注意力可视化

Day 11-14：Transformer Block
- Feed Forward Network
- Layer Norm + Residual
- 完整的Decoder实现
```

**Week 5-6：训练与优化**
```python
Day 15-18：训练循环
- 数据集准备（小说、对话）
- Loss计算（CrossEntropy）
- 梯度更新（Adam优化器）

Day 19-21：生成与评估
- 文本生成（Temperature采样）
- 困惑度（Perplexity）计算
- 对比实验（不同超参数）
```

**产出**：
- 完整的MiniMind实现（GitHub开源）
- 实验报告（模型效果对比）
- 技术博客系列（3-5篇）

---

#### RethinkFun系统学习

**补充章节**（实习空闲时间）：

**基础理论**（选修）：
- 第1章：初识深度学习（1-2小时快速过）
- 第2-4章：数学基础（遇到不懂再补）
- 第6章：初识PyTorch（如果要写代码）

**深度学习基础**（选修）：
- 第8章：神经网络（理解前向传播、反向传播）
- 第9章：优化算法（SGD、Adam、学习率调度）
- 第10-11章：CNN卷积网络（了解即可）

**NLP专项**（重点）：
- 第12章：自然语言处理基础
- 第13-14章：RNN和循环网络（了解历史）
- 第17章：Llama（开源模型生态）
- 第18章：DeepSeek（国产模型）

**学习策略**：
```
优先级1：第15-16章（必学）
优先级2：第17-18章（了解前沿）
优先级3：第12章（NLP基础）
优先级4：其他章节（按需学习）
```

---

### 阶段2：开源贡献（月7-9）

#### 参与开源项目

**推荐项目**（选1-2个）：
1. **LangChain**（Python）
   - 修复Issue（标签：good first issue）
   - 提交PR（文档、示例、Bug修复）
   - 参与讨论（GitHub Discussions）

2. **LlamaIndex**（Python）
   - 贡献数据连接器（Data Connector）
   - 优化查询引擎（Query Engine）
   - 写教程（Community Guides）

3. **Spring AI**（Java - 你熟悉！）
   - 改进文档（中文翻译）
   - 添加示例（Example Projects）
   - 报告Bug（基于aura项目经验）

**贡献流程**：
```
Week 1：熟悉项目（跑demo、读文档）
Week 2-3：找Issue（选择力所能及的）
Week 4-5：提交PR（代码 + 测试 + 文档）
Week 6：Code Review（根据反馈修改）
```

**收益**：
- ✅ GitHub贡献记录（绿色小方块）
- ✅ 简历亮点（开源贡献者）
- ✅ 技术提升（学习优秀代码）
- ✅ 人脉拓展（认识维护者）

---

### 阶段3：领域深化（月10-12）

#### 论文阅读计划

**核心论文**（每周1篇，精读）：

**Month 10：Transformer系列**
```
Week 1：Attention is All You Need（必读）
Week 2：BERT: Pre-training of Deep Bidirectional Transformers
Week 3：GPT-1/GPT-2/GPT-3论文对比阅读
Week 4：总结：Transformer发展脉络
```

**Month 11：Agent专题**
```
Week 1：ReAct: Synergizing Reasoning and Acting in Language Models
Week 2：Reflexion: Language Agents with Verbal Reinforcement Learning
Week 3：AutoGPT技术报告
Week 4：总结：Agent架构演进
```

**Month 12：优化技术**
```
Week 1：LoRA: Low-Rank Adaptation of Large Language Models
Week 2：QLoRA: Efficient Finetuning of Quantized LLMs
Week 3：RLHF（Reinforcement Learning from Human Feedback）
Week 4：总结：模型微调技术栈
```

**阅读方法**：
```
1. 快速浏览（30分钟）：Abstract、Introduction、Conclusion
2. 精读核心（2小时）：Method、Experiments
3. 代码实现（3小时）：找开源实现，跑一遍
4. 写总结（1小时）：博客/笔记，用自己的话讲
```

---

#### 高级项目实战

**项目3：aura-minimind集成**（重点！）

**目标**：用自己训练的模型替换OpenAI API

**实施步骤**：

**Phase 1：数据准备**（1周）
```python
# 1. 收集aura产品数据
products = [
    {"name": "Harmony", "description": "..."},
    {"name": "Pulse", "description": "..."},
    ...
]

# 2. 构造训练集
training_data = [
    {
        "instruction": "What is Harmony?",
        "output": "Harmony is wireless headphones..."
    },
    {
        "instruction": "How much does Pulse cost?",
        "output": "Pulse smartwatch is priced at $199.99"
    },
    ...
]

# 3. 生成50-100个训练样本
```

**Phase 2：模型微调**（2周）
```python
# 使用MiniMind在aura数据上微调
model = MiniMind(...)
model.finetune(
    dataset=aura_dataset,
    epochs=10,
    learning_rate=1e-4
)

# 评估效果
perplexity = evaluate(model, test_set)
```

**Phase 3：集成部署**（1周）
```java
// 修改aura项目，支持切换模型
public class AIService {
    // 原来
    private OpenAIClient openAI;
    
    // 新增
    private MiniMindClient miniMind;
    
    public String chat(String message) {
        if (useOpenAI) {
            return openAI.chat(message);
        } else {
            return miniMind.generate(message);
        }
    }
}
```

**Phase 4：效果对比**（1周）
```python
# 对比实验
metrics = {
    "OpenAI GPT-3.5": {
        "accuracy": 0.95,
        "latency": 500ms,
        "cost": $0.002/request
    },
    "MiniMind-Finetuned": {
        "accuracy": 0.75,  # 可能更低
        "latency": 200ms,  # 本地更快
        "cost": $0         # 免费！
    }
}
```

**产出**：
- 微调后的模型（可部署）
- 对比实验报告
- 技术博客：《我如何用开源模型替代OpenAI》
- 演示视频（展示效果对比）

---

## 🎓 长期目标：1-3年AI专家

### Year 1：工程能力成长

**技能树**：

**AI工程 (50%)**
```
├─ Prompt Engineering
│  ├─ Few-shot Learning
│  ├─ Chain-of-Thought
│  └─ Prompt优化技巧
│
├─ RAG系统设计
│  ├─ 文档分块策略
│  ├─ 向量数据库选型（FAISS、Pinecone、Milvus）
│  ├─ 混合检索（关键词+语义）
│  └─ 重排序（Reranking）
│
├─ Agent架构
│  ├─ Single Agent优化
│  ├─ Multi-Agent协作
│  ├─ Tool Use（Function Calling）
│  └─ Planning & Reasoning
│
└─ 模型部署
   ├─ 模型量化（GGUF、AWQ）
   ├─ 推理优化（vLLM、TensorRT）
   ├─ 容器化（Docker、K8s）
   └─ 云服务（AWS、阿里云）
```

**后端架构 (30%)**
```
├─ 分布式系统
│  ├─ Redis（缓存、分布式锁）
│  ├─ Kafka（消息队列）
│  └─ 分布式事务
│
├─ 数据库优化
│  ├─ MySQL调优（索引、查询优化）
│  ├─ 向量数据库（相似度检索）
│  └─ 数据库设计模式
│
└─ 微服务架构
   ├─ Spring Cloud
   ├─ 服务注册与发现
   └─ API网关
```

**DevOps (20%)**
```
├─ CI/CD
│  ├─ GitHub Actions
│  ├─ Jenkins
│  └─ 自动化测试
│
├─ 监控告警
│  ├─ Prometheus + Grafana
│  ├─ 日志分析（ELK）
│  └─ 性能监控（APM）
│
└─ 性能优化
   ├─ JVM调优
   ├─ 接口性能优化
   └─ 数据库慢查询优化
```

---

### Year 2：算法能力提升

**深度学习框架精通**
```
PyTorch深入：
├─ 自动微分（Autograd）原理
├─ 自定义层（Custom Layer）
├─ 分布式训练（DDP、FSDP）
├─ 模型导出（ONNX、TorchScript）
└─ 性能优化（混合精度、梯度累积）

训练大模型：
├─ 多GPU训练（DataParallel）
├─ 梯度检查点（Gradient Checkpointing）
├─ 内存优化（ZeRO、DeepSpeed）
└─ 训练监控（Weights & Biases）

模型压缩：
├─ 量化（INT8、FP16）
├─ 剪枝（Pruning）
├─ 知识蒸馏（Distillation）
└─ 低秩分解（LoRA、AdaLoRA）
```

**机器学习算法**
```
监督学习：
├─ 分类（SVM、决策树、XGBoost）
├─ 回归（线性回归、岭回归）
└─ 集成学习（Bagging、Boosting）

无监督学习：
├─ 聚类（K-means、DBSCAN）
├─ 降维（PCA、t-SNE）
└─ 异常检测

强化学习：
├─ Q-Learning
├─ DQN（Deep Q-Network）
├─ PPO（Proximal Policy Optimization）
└─ RLHF（用于LLM对齐）
```

**NLP专项深化**
```
任务专精：
├─ 文本分类（情感分析、意图识别）
├─ 序列标注（NER、词性标注）
├─ 信息抽取（实体关系抽取）
├─ 问答系统（RAG、检索式问答）
└─ 文本生成（摘要、翻译）

技术栈：
├─ Hugging Face Transformers
├─ spaCy（NLP工具库）
├─ NLTK（自然语言工具包）
└─ Sentence Transformers（语义相似度）

多模态：
├─ 图文理解（CLIP、BLIP）
├─ 视觉问答（VQA）
└─ 文生图（Stable Diffusion）
```

---

### Year 3：研究能力培养

**研究方向（选1-2个深耕）**

**方向1：Agent系统研究**
```
研究问题：
├─ Multi-Agent如何高效协作？
├─ 如何提升Agent的Planning能力？
├─ Tool Use的最佳实践是什么？
└─ 如何评估Agent系统的性能？

实践路径：
├─ 阅读顶会论文（NeurIPS、ICML、ACL）
├─ 复现SOTA方法
├─ 提出改进方案
├─ 实验验证（在真实场景）
└─ 发表论文/技术报告

产出目标：
├─ 1-2篇顶会论文（一作/二作）
├─ 开源Agent框架（GitHub 1000+ stars）
└─ 业界影响力（演讲、技术分享）
```

**方向2：模型优化工程**
```
研究问题：
├─ 如何在资源受限下部署大模型？
├─ 量化和剪枝的trade-off？
├─ 推理加速的最佳实践？
└─ 成本优化策略？

实践路径：
├─ 深入研究模型压缩技术
├─ 优化公司/团队AI系统性能
├─ 成本降低50%+（量化指标）
└─ 分享优化经验（博客、会议）

产出目标：
├─ 推理速度提升5-10倍
├─ 成本降低50%+
├─ 成为团队优化专家
└─ 技术影响力（KOL）
```

**方向3：垂直领域应用**
```
选择领域：
├─ 金融AI（风控、投顾）
├─ 医疗AI（诊断、问诊）
├─ 法律AI（合同审查、案例检索）
├─ 教育AI（智能辅导、作业批改）
└─ 电商AI（推荐、客服）← 基于aura经验

实践路径：
├─ 深入理解领域知识
├─ 构建领域数据集
├─ 微调领域模型
├─ 产品化（从0到1）
└─ 商业化（盈利模式）

产出目标：
├─ 领域AI产品（DAU 10万+）
├─ 商业化成功（盈利）
├─ 成为领域专家
└─ 行业影响力
```

---

## 📚 学习资源整合

### 在线课程

**入门必修**：
1. **吴恩达《Machine Learning》**（Coursera）
   - 时长：11周
   - 难度：⭐⭐⭐
   - 推荐度：⭐⭐⭐⭐⭐
   - 学习时间：实习前/期间

2. **Fast.ai《Practical Deep Learning》**
   - 时长：8周
   - 难度：⭐⭐⭐⭐
   - 推荐度：⭐⭐⭐⭐⭐
   - 学习时间：实习期间

3. **DeepLearning.AI《LangChain系列》**
   - 时长：每门课1-2小时
   - 难度：⭐⭐
   - 推荐度：⭐⭐⭐⭐⭐
   - 学习时间：实习期间（优先！）

**进阶选修**：
4. **Stanford CS224N（NLP）**
   - 时长：20讲
   - 难度：⭐⭐⭐⭐⭐
   - 推荐度：⭐⭐⭐⭐
   - 学习时间：Year 1

5. **Stanford CS25（Transformers）**
   - 时长：12讲
   - 难度：⭐⭐⭐⭐
   - 推荐度：⭐⭐⭐⭐⭐
   - 学习时间：实习后

---

### 书籍推荐

**必读书籍**：
1. **《动手学深度学习》**（李沐）
   - 适合：入门 + 实战
   - 推荐度：⭐⭐⭐⭐⭐
   - 配套：dive-into-deep-learning.org

2. **《深度学习》**（花书）
   - 适合：理论深入
   - 推荐度：⭐⭐⭐⭐
   - 难度：较高，作为参考书

3. **《LangChain实战》**
   - 适合：Agent开发
   - 推荐度：⭐⭐⭐⭐
   - 时间：实习期间

**进阶书籍**：
4. **《Speech and Language Processing》**（NLP圣经）
5. **《Reinforcement Learning》**（强化学习）
6. **《Natural Language Processing with Transformers》**

---

### 技术社区

**必关注**：
1. **Hugging Face**（https://huggingface.co）
   - 模型库、数据集、Demo
   - 每日论文（Papers）
   - 社区讨论（Forums）

2. **Papers with Code**（https://paperswithcode.com）
   - 论文+代码
   - SOTA排行榜
   - 趋势跟踪

3. **GitHub**
   - LangChain、LlamaIndex、AutoGPT
   - MiniMind、ChatGLM、Llama
   - Spring AI

**国内社区**：
4. **AI研习社**（https://ai.yanxishe.com）
5. **PaperWeekly**（公众号）
6. **机器之心**、**量子位**（资讯）

**B站UP主**：
7. **跟李沐学AI**（论文精读）
8. **小土堆**（PyTorch教程）
9. **Datawhale**（开源学习）

---

### GitHub资源

**学习路线**：
- https://github.com/ml-tooling/best-of-ml-python
- https://github.com/krahets/hello-algo（算法）
- https://github.com/datawhalechina/llm-universe（LLM教程）

**开源项目**：
- https://github.com/langchain-ai/langchain
- https://github.com/run-llama/llama_index
- https://github.com/spring-projects-experimental/spring-ai

**论文复现**：
- https://github.com/labmlai/annotated_deep_learning_paper_implementations

---

## ⏱️ 时间规划表

### 每日时间分配

**实习期间（工作日）**：
```
09:00-18:00  公司实习（8小时）
             ├─ 完成工作任务
             ├─ 学习团队代码
             └─ 参与技术分享

19:00-20:00  晚餐 + 休息

20:00-21:30  理论学习（1.5小时）
             ├─ RethinkFun课程
             ├─ 论文阅读
             └─ 在线课程

21:30-22:30  编程实践（1小时）
             ├─ LeetCode刷题
             ├─ MiniMind实现
             └─ 个人项目

22:30-23:00  总结复盘（30分钟）
             ├─ 写学习笔记
             ├─ 更新GitHub
             └─ 规划明天任务
```

**周末安排**：
```
周六上午（3小时）  深度学习理论
                   ├─ 系统学习课程
                   └─ 精读论文

周六下午（3小时）  项目实战
                   ├─ MiniMind实现
                   ├─ LangChain项目
                   └─ 开源贡献

周六晚上（2小时）  技术输出
                   ├─ 写技术博客
                   └─ 录制视频

周日上午（3小时）  自由学习
                   ├─ 探索新技术
                   └─ 看技术视频

周日下午（2小时）  复盘 + 规划
                   ├─ 周总结
                   └─ 下周计划

周日晚上          休息放松
```

---

### 月度学习计划

**Month 1（找实习前）**：
```
Week 1：aura项目深化 + 简历优化
Week 2：Transformer理论学习
Week 3：MiniMind上手 + LeetCode
Week 4：面试准备 + 模拟面试
```

**Month 2-3（面试季）**：
```
Week 5-6：持续投递 + 面试
Week 7-8：Offer选择 + 实习准备
```

**Month 4-6（实习期）**：
```
Week 9-12：工作 + LangChain学习
Week 13-16：MiniMind完整实现
Week 17-20：开源贡献 + 论文阅读
Week 21-24：高级项目（aura-minimind集成）
```

**Month 7-12（深度提升）**：
```
Week 25-36：系统学习RethinkFun课程
Week 37-48：论文精读 + 技术输出
Week 49-52：年终总结 + 转正准备
```

---

## 🎯 里程碑检查点

### Month 1：✅ 理论基础 + 项目准备
- [ ] 能流畅讲解aura项目（15分钟）
- [ ] 理解Transformer原理（能画图讲解）
- [ ] GitHub有学习笔记（10+ commits）
- [ ] 技术博客发布（1-2篇）

### Month 2：✅ 算法能力 + 面试准备
- [ ] LeetCode刷题100+（Medium为主）
- [ ] MiniMind代码理解（能讲核心模块）
- [ ] 简历优化完成（1页，亮点突出）
- [ ] 模拟面试5次+（有录像复盘）

### Month 3：🎯 拿到实习Offer
- [ ] 投递50+家公司
- [ ] 获得10+面试机会
- [ ] 拿到3+个Offer
- [ ] **成功入职AI相关实习** ⭐

### Month 6：✅ 工程能力成长
- [ ] 完成2个LangChain项目
- [ ] MiniMind完整实现（GitHub开源）
- [ ] 开源贡献（至少1个PR merged）
- [ ] 实习表现优秀（考虑转正）

### Month 9：✅ 深度理解LLM
- [ ] 精读10+篇核心论文
- [ ] 能从零训练小模型
- [ ] aura-minimind集成完成
- [ ] 技术博客10+篇（有阅读量）

### Month 12：🎯 成为AI工程师
- [ ] 实习转正/拿到全职Offer
- [ ] GitHub有影响力项目（100+ stars）
- [ ] 技术栈完整（AI + 后端 + DevOps）
- [ ] **成为团队AI技术骨干** ⭐

### Year 2：🚀 成为AI专家
- [ ] 独立负责AI项目（端到端）
- [ ] 开源项目维护者（500+ stars）
- [ ] 发表1-2篇技术文章（被广泛引用）
- [ ] 在会议/Meetup分享（3次+）

### Year 3：🏆 行业影响力
- [ ] 技术专家认证（公司/行业）
- [ ] 论文发表（顶会/期刊）
- [ ] 开源框架作者（1000+ stars）
- [ ] **成为AI领域KOL** ⭐

---

## 💡 核心建议

### 学习原则

**1. 实践>理论（70/30原则）**
```
70%时间：写代码、做项目、参与开源
30%时间：看论文、学理论、上课程
```

**2. 项目驱动学习**
```
✅ 好的学习方式：
- 学Transformer → 立刻实现MiniMind
- 学RAG → 立刻优化aura项目
- 学Agent → 立刻写LangChain项目

❌ 不好的方式：
- 只看视频不动手
- 学完理论就忘记
- 没有实际项目产出
```

**3. 持续输出**
```
每周输出：
├─ GitHub Commits（代码提交）
├─ 技术博客（知识总结）
├─ 学习笔记（Markdown文档）
└─ 社区讨论（帮助他人）

目标：
- 1年后GitHub全绿
- 50+篇技术博客
- 建立个人品牌
```

**4. 建立技术网络**
```
线上：
├─ 加入AI技术群（微信、Discord）
├─ 关注大V（Twitter、GitHub）
└─ 参与开源社区

线下：
├─ 参加Meetup（北京、上海AI聚会）
├─ 参加会议（AICon、QCon）
└─ 校友网络（学长学姐内推）
```

**5. 保持好奇心**
```
每周关注：
├─ Hugging Face每日论文
├─ GitHub Trending（AI分类）
├─ ArXiv最新论文
└─ 技术博客（Medium、知乎）

每月尝试：
├─ 一个新工具（新框架、新模型）
├─ 一个新领域（多模态、强化学习）
└─ 一个新项目（Weekend Project）
```

---

### 关键成功因素

**短期（找实习）**：
1. ✅ 项目经验（aura是你的最大优势！）
2. ✅ 理论基础（Transformer必须懂）
3. ✅ 算法能力（LeetCode 100+）
4. ✅ 沟通能力（能讲清楚技术方案）

**中期（实习表现）**：
1. ✅ 快速学习能力（新技术上手快）
2. ✅ 工程能力（写出高质量代码）
3. ✅ 解决问题能力（独立完成任务）
4. ✅ 团队协作（Code Review、文档）

**长期（AI专家）**：
1. ✅ 技术深度（某个方向专家）
2. ✅ 技术广度（全栈AI工程师）
3. ✅ 影响力（技术输出、开源贡献）
4. ✅ 业务理解（技术如何创造价值）

---

## 📌 行动清单

### 本周立即开始（Week 1）

**Day 1-2（今天和明天）**：
- [ ] 整理aura项目文档（README、架构图）
- [ ] 开始看RethinkFun第15章（Transformer）
- [ ] 创建GitHub学习仓库（ai-learning-roadmap）

**Day 3-4**：
- [ ] 继续学习Transformer（画图理解）
- [ ] 看李沐《Attention论文精读》视频
- [ ] 写学习笔记（Markdown）

**Day 5-6**：
- [ ] Clone MiniMind项目，跑通demo
- [ ] 开始LeetCode刷题（2题/天）
- [ ] 准备简历初稿

**Day 7（周末）**：
- [ ] 完成aura项目PPT（15分钟版本）
- [ ] 写技术博客：《我的AI Agent项目总结》
- [ ] 规划下周学习任务

---

### 本月关键任务（Month 1）

**Week 1**：
- [x] aura项目深化
- [ ] 简历初稿
- [ ] Transformer理论开始

**Week 2**：
- [ ] Transformer理论完成
- [ ] 技术博客1篇
- [ ] LeetCode 20题

**Week 3**：
- [ ] MiniMind上手
- [ ] 手写Self-Attention
- [ ] LeetCode 40题

**Week 4**：
- [ ] 面试准备（技术+行为）
- [ ] 模拟面试3次
- [ ] 简历定稿

---

### 三个月检查点（Quarter 1）

**Month 1完成**：
- [ ] 理论基础扎实（Transformer原理）
- [ ] 项目准备充分（aura讲解流畅）
- [ ] 算法能力达标（LeetCode 100题）
- [ ] 简历作品集完善

**Month 2完成**：
- [ ] 投递50+公司
- [ ] 面试10+次
- [ ] MiniMind核心理解

**Month 3完成**：
- [ ] 🎯 拿到实习Offer（最终目标！）
- [ ] GitHub有完整学习记录
- [ ] 技术博客3-5篇

---

## 🎓 学习资源汇总

### 在线资源链接

**课程平台**：
- Coursera：https://www.coursera.org
- Fast.ai：https://www.fast.ai
- DeepLearning.AI：https://www.deeplearning.ai

**技术社区**：
- Hugging Face：https://huggingface.co
- Papers with Code：https://paperswithcode.com
- GitHub：https://github.com

**中文资源**：
- 动手学深度学习：https://zh.d2l.ai
- AI研习社：https://ai.yanxishe.com
- Datawhale：https://github.com/datawhalechina

**视频学习**：
- B站搜索：跟李沐学AI
- B站搜索：小土堆PyTorch
- YouTube：Andrej Karpathy

---

## ✅ 最后总结

### 核心路径

```
Month 1-3：找实习
    ├─ 巩固aura项目
    ├─ 学习Transformer原理（RethinkFun 15-16章）
    ├─ 上手MiniMind（理解核心）
    └─ 算法+面试准备

Month 4-6：实习深化
    ├─ 工作表现优秀
    ├─ 学习LangChain（Agent开发）
    ├─ 完整实现MiniMind
    └─ 开源贡献

Month 7-12：能力跃升
    ├─ 论文精读（10+篇）
    ├─ 高级项目（aura-minimind集成）
    ├─ 系统学习RethinkFun
    └─ 实习转正

Year 2-3：成为专家
    ├─ 工程能力（全栈AI工程师）
    ├─ 算法能力（深度学习专家）
    ├─ 研究能力（论文发表）
    └─ 影响力（开源、分享）
```

---

### 关键记忆

**你已经有的优势**：
- ✅ aura-quiet-living项目（AI Agent实战经验）
- ✅ Multi-Agent架构理解
- ✅ RAG系统实现
- ✅ Function Calling应用
- ✅ 完整的技术文档

**你需要补充的**：
- 📚 Transformer底层原理（RethinkFun）
- 💻 手写代码实现（MiniMind）
- 📝 算法基础（LeetCode）
- 🎤 表达能力（面试讲解）

**最重要的事**：
1. **不要贪多！** 先完成短期目标（3个月找实习）
2. **项目驱动！** 每学一个概念，立刻用项目验证
3. **持续输出！** GitHub、博客、笔记一个都不能少
4. **保持节奏！** 长期坚持比短期冲刺更重要

---

**记住：你的aura项目是你最大的优势！把它讲好、写好、优化好，就能拿到好实习！然后在实习期间深入学习Transformer和MiniMind，长期成为AI Agent开发专家！** 🚀

**加油！3个月后，期待你拿到心仪的Offer！** 💪

---

**文档版本**: v1.0  
**创建日期**: 2026-02-09  
**最后更新**: 2026-02-09  
**下次更新**: 完成Month 1后（2026-03-09）

---
