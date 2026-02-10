# PDF切片（Chunking）详解

**创建日期**: 2026-02-07  
**相关技术**: RAG、向量数据库、文本分割

---

## 🎯 什么是PDF切片？

PDF切片（Chunking）是将长文本分割成小块的过程，用于：
- ✅ 提高向量搜索的精确度
- ✅ 适应AI模型的上下文窗口限制
- ✅ 加快检索速度
- ✅ 减少嵌入（Embedding）成本

---

## 📋 当前配置

### application.yml（第69-71行）

```yaml
app:
  pdf:
    manuals-path: classpath:manuals/  # PDF手册存放路径
  vector:
    chunk-size: 800      # 每块最大字符数（之前是500）
    chunk-overlap: 100   # 块之间的重叠字符数（之前是50）
```

**参数说明**：

| 参数 | 当前值 | 旧值 | 说明 |
|------|--------|------|------|
| `chunk-size` | 800字符 | 500字符 | 每个文本块的最大长度 |
| `chunk-overlap` | 100字符 | 50字符 | 相邻块之间的重叠部分 |

**为什么增大了？**
- 使用了更好的嵌入模型（text-embedding-3-large）
- 更大的块保留更多上下文
- 更多的重叠提高连续性

---

## 🔄 完整切片流程

### 流程图

```
1. PDF文件
   ↓
2. 提取文本（PDFBox）
   ↓
3. 清理文本（去除噪音）
   ↓
4. 按句子分割
   ↓
5. 组合成固定大小的块（带重叠）
   ↓
6. 添加元数据
   ↓
7. 生成向量嵌入（OpenAI）
   ↓
8. 存储到向量数据库
```

---

## 📝 代码详解

### 1. 主流程：vectorizeProductManual()

**位置**: `PDFVectorizationService.java` (第109-148行)

```java
public int vectorizeProductManual(String productId, String pdfPath) {
    // 1. 验证PDF
    if (!PDFParser.isValidPDF(pdfPath)) {
        throw new IllegalArgumentException("Invalid PDF file");
    }
    
    // 2. 提取文本
    String rawText = extractTextFromPDF(pdfPath);
    
    // 3. 清理文本
    String cleanedText = PDFParser.cleanText(rawText);
    
    // 4. 切分成块
    List<String> chunks = splitIntoChunks(cleanedText, chunkSize);
    
    // 5. 为每个块创建Document（带元数据）
    List<Document> documents = new ArrayList<>();
    for (int i = 0; i < chunks.size(); i++) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("product_id", productId);        // 产品ID
        metadata.put("source", fileName);             // 文件名
        metadata.put("chunk_index", i);               // 块索引
        
        Document doc = new Document(chunks.get(i), metadata);
        documents.add(doc);
    }
    
    // 6. 添加到向量数据库（自动生成嵌入）
    vectorStore.add(documents);
    
    return chunks.size();
}
```

---

### 2. 文本提取：extractTextFromPDF()

**位置**: `PDFVectorizationService.java` (第153-163行)

```java
private String extractTextFromPDF(String pdfPath) {
    // 使用 Apache PDFBox 加载PDF
    try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        
        log.debug("Extracted {} characters from {}", text.length(), pdfPath);
        return text;
    } catch (IOException e) {
        throw new RuntimeException("Failed to extract text from PDF", e);
    }
}
```

**示例输出**：
```
原始PDF内容:
"Aura Harmony User Manual
Chapter 1: Getting Started
To set up your Aura Harmony headphones, follow these steps:
1. Charge the device for at least 2 hours
2. Press and hold the power button for 3 seconds
..."
```

---

### 3. 核心切片算法：splitIntoChunks()

**位置**: `PDFVectorizationService.java` (第168-209行)

#### 算法步骤

```java
private List<String> splitIntoChunks(String text, int chunkSize) {
    List<String> chunks = new ArrayList<>();
    
    // 1. 如果文本小于块大小，直接返回
    if (text.length() <= chunkSize) {
        chunks.add(text);
        return chunks;
    }
    
    // 2. 按句子分割（保留语义完整性）
    String[] sentences = text.split("(?<=[.!?])\\s+");
    //                        ↑ 正则表达式：在句号、感叹号、问号后面分割
    
    StringBuilder currentChunk = new StringBuilder();
    
    // 3. 逐句添加，直到达到块大小--800
    for (String sentence : sentences) {
        if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {// （检查：如果加上这个句子会超过800字符吗？）
            // 4. 当前块已满，保存它  （超过了！保存当前块，开始新块）
            chunks.add(currentChunk.toString().trim());
            
            // 5. 创建重叠部分（overlap）
            String overlap = "";
            String prevChunk = currentChunk.toString();
            if (prevChunk.length() > chunkOverlap) {
                // 取前一个块的最后100个字符作为重叠
                overlap = prevChunk.substring(prevChunk.length() - chunkOverlap);
            }
            
            // 6. 开始新块（包含重叠部分）
            currentChunk = new StringBuilder(overlap + " " + sentence);
        } else {
            // 继续添加到当前块
            if (currentChunk.length() > 0) {
                currentChunk.append(" ");
            }
            currentChunk.append(sentence);
        }
    }
    
    // 7. 添加最后一个块
    if (currentChunk.length() > 0) {
        chunks.add(currentChunk.toString().trim());
    }
    
    return chunks;
}
```

---

## 📊 切片示例

### 示例：Aura Harmony手册切片

**原始文本**（1500字符）：
```
Aura Harmony User Manual

Chapter 1: Getting Started
To set up your Aura Harmony headphones, follow these steps:
1. Charge the device for at least 2 hours before first use.
2. Press and hold the power button for 3 seconds to turn on.
3. The LED will flash blue indicating pairing mode.

Chapter 2: Pairing with Devices
For iOS devices: Go to Settings > Bluetooth > Select "Aura Harmony".
For Android devices: Open Bluetooth settings and tap "Aura Harmony".
The connection will be established within 5 seconds.

Chapter 3: Using Noise Cancellation
To activate noise cancellation, press the NC button once.
The LED will turn green indicating active noise cancellation.
Battery life with NC on is approximately 20 hours.

Chapter 4: Charging
Use the included USB-C cable to charge the device.
Charging time: 2 hours for full charge.
Battery indicator: Red while charging, Green when fully charged.
...
```

---

### 切片结果（chunk-size=800, overlap=100）

#### Chunk 0 (724字符)
```
Aura Harmony User Manual

Chapter 1: Getting Started
To set up your Aura Harmony headphones, follow these steps:
1. Charge the device for at least 2 hours before first use.
2. Press and hold the power button for 3 seconds to turn on.
3. The LED will flash blue indicating pairing mode.

Chapter 2: Pairing with Devices
For iOS devices: Go to Settings > Bluetooth > Select "Aura Harmony".
For Android devices: Open Bluetooth settings and tap "Aura Harmony".
The connection will be established within 5 seconds.

Chapter 3: Using Noise Cancellation
To activate noise cancellation, press the NC button once.
```

**元数据**:
```json
{
  "product_id": "aura-harmony",
  "source": "aura-harmony.pdf",
  "chunk_index": 0
}
```

---

#### Chunk 1 (启始有重叠) (623字符)
```
To activate noise cancellation, press the NC button once.  ← 重叠部分开始
The LED will turn green indicating active noise cancellation.
Battery life with NC on is approximately 20 hours.

Chapter 4: Charging
Use the included USB-C cable to charge the device.
Charging time: 2 hours for full charge.
Battery indicator: Red while charging, Green when fully charged.
...
```

**元数据**:
```json
{
  "product_id": "aura-harmony",
  "source": "aura-harmony.pdf",
  "chunk_index": 1
}
```

**注意**：Chunk 1的开头包含了Chunk 0末尾的100个字符（重叠部分）

---

## 🔍 为什么需要重叠（Overlap）？

### 问题：没有重叠的情况

**假设**：没有重叠，chunk-size=800

```
Chunk 0: "...press the NC button once."
Chunk 1: "The LED will turn green..."
```

**用户问题**：
```
👤: "What happens when I press the NC button?"
```

**向量搜索结果**：
- Chunk 0匹配："press the NC button once." ← 但没有说明结果！
- Chunk 1不匹配：开头没有提到"NC button"

**结果**：❌ AI无法完整回答，因为信息被切断了

---

### 解决：有重叠的情况

**有重叠**：overlap=100字符

```
Chunk 0: "...press the NC button once."
Chunk 1: "...press the NC button once. The LED will turn green..."
         ↑ 重叠部分保留了上下文
```

**用户问题**：
```
👤: "What happens when I press the NC button?"
```

**向量搜索结果**：
- Chunk 0匹配："press the NC button once."
- Chunk 1也匹配："press the NC button once. The LED will turn green..."

**结果**：✅ Chunk 1包含完整信息，AI能完整回答

---

## 📐 切片参数优化

### 参数对比

| 参数 | 旧值 | 新值 | 影响 |
|------|------|------|------|
| **chunk-size** | 500字符 | 800字符 | ↑ 更多上下文，更完整的信息 |
| **chunk-overlap** | 50字符 | 100字符 | ↑ 更好的连续性，减少信息断裂 |

---

### 为什么增大参数？

#### 原因1：使用了更好的嵌入模型

```yaml
# 旧模型
model: text-embedding-3-small
dimensions: 1536

# 新模型
model: text-embedding-3-large
dimensions: 3072
```

**text-embedding-3-large的优势**：
- ✅ 更高维度（3072维）→ 能处理更复杂的语义
- ✅ 更强的上下文理解 → 更大的块不影响质量
- ✅ 更好的长文本表达能力

---

#### 原因2：保留更多上下文

**小块问题**（500字符）：
```
Chunk: "Charging time: 2 hours for full charge."
```
- ❌ 缺少上下文："Charging"指什么设备？
- ❌ 用户问"How long does it take to charge?"可能匹配到其他产品

**大块优势**（800字符）：
```
Chunk: "Aura Harmony headphones...
        Chapter 4: Charging
        Use the included USB-C cable to charge the device.
        Charging time: 2 hours for full charge.
        Battery indicator: Red while charging..."
```
- ✅ 包含产品名称
- ✅ 包含章节标题
- ✅ 包含相关上下文

---

### 参数选择指南

| chunk-size | 适用场景 | 优点 | 缺点 |
|-----------|---------|------|------|
| 200-400 | 短文档、FAQ | 精确匹配 | 上下文少 |
| 500-800 | 产品手册、教程 | 平衡性好 | - |
| 1000+ | 技术文档、论文 | 上下文丰富 | 可能包含无关信息 |

| overlap | 适用场景 | 优点 | 缺点 |
|---------|---------|------|------|
| 0-50 | 独立段落 | 存储少 | 信息可能断裂 |
| 50-100 | 连续文本 | 平衡性好 | - |
| 100-200 | 复杂逻辑文档 | 连续性强 | 存储冗余 |

**当前选择**：`chunk-size=800`, `overlap=100` ← 适合产品手册 ✅

---

## 🎨 正则表达式详解

### 句子分割正则：`(?<=[.!?])\\s+`

**解析**：
```regex
(?<=[.!?])   ← Positive Lookbehind（正向后行断言）
             ← 意思：前面必须是 . ! ? 之一
\\s+         ← 一个或多个空白字符（空格、换行等）
```

**示例**：
```java
String text = "Hello world. How are you? I'm fine! Thanks.";
String[] sentences = text.split("(?<=[.!?])\\s+");

// 结果
sentences[0] = "Hello world."
sentences[1] = "How are you?"
sentences[2] = "I'm fine!"
sentences[3] = "Thanks."
```

**为什么不用 `\\.\\s+`？**
```java
// ❌ 错误的方式
text.split("\\.\\s+");

// 结果
"Hello world"   ← 句号被吃掉了！
"How are you? I'm fine! Thanks"  ← 只分割了句号
```

**正确的方式**（Lookbehind）：
```java
// ✅ 正确的方式
text.split("(?<=[.!?])\\s+");

// 结果
"Hello world."   ← 句号保留了！
"How are you?"   ← 问号保留了！
"I'm fine!"      ← 感叹号保留了！
```

---

## 🗄️ 向量存储结构

### 存储路径

```
./data/vector-store.json
```

### 数据结构（简化）

```json
{
  "documents": [
    {
      "id": "uuid-1",
      "content": "Aura Harmony User Manual Chapter 1: Getting Started...",
      "metadata": {
        "product_id": "aura-harmony",
        "source": "aura-harmony.pdf",
        "chunk_index": 0
      },
      "embedding": [0.123, -0.456, 0.789, ..., 0.321]  // 3072维向量
    },
    {
      "id": "uuid-2",
      "content": "To activate noise cancellation, press the NC button...",
      "metadata": {
        "product_id": "aura-harmony",
        "source": "aura-harmony.pdf",
        "chunk_index": 1
      },
      "embedding": [0.234, -0.567, 0.890, ..., 0.432]  // 3072维向量
    }
  ]
}
```

---

## 🔧 初始化流程

### @PostConstruct：initializeVectorStore()

**位置**: `PDFVectorizationService.java` (第42-102行)

**触发时机**：Spring Boot启动时自动执行

```java
@PostConstruct
public void initializeVectorStore() {
    // 1. 检查向量存储文件是否存在
    File vectorStoreFile = new File("./data/vector-store.json");
    
    if (vectorStoreFile.exists()) {
        log.info("Vector store loaded from disk");
        return;  // ← 已存在，直接加载，不重新处理
    }
    
    // 2. 从PDF手册目录读取所有PDF
    File directory = resourceLoader.getResource(manualsPath).getFile();
    File[] pdfFiles = directory.listFiles((dir, name) -> name.endsWith(".pdf"));
    
    // 3. 逐个处理PDF
    for (File pdfFile : pdfFiles) {
        String productId = pdfFile.getName().replace(".pdf", "");
        int chunks = vectorizeProductManual(productId, pdfFile.getAbsolutePath());
        totalChunks += chunks;
    }
    
    // 4. 保存到磁盘
    vectorStore.save(vectorStoreFile);
    
    log.info("✅ Vector store initialized: {} documents with {} chunks", 
             totalDocuments, totalChunks);
}
```

**日志输出示例**：
```
2026-02-07 10:30:15 - Initializing vector store from PDF manuals...
2026-02-07 10:30:16 - Vectorizing product manual: aura-harmony from /path/to/aura-harmony.pdf
2026-02-07 10:30:16 - Extracted 5420 characters from aura-harmony.pdf
2026-02-07 10:30:16 - Split text into 8 chunks (size: 800, overlap: 100)
2026-02-07 10:30:17 - ✅ Vectorized product aura-harmony with 8 chunks
2026-02-07 10:30:17 - Vectorizing product manual: aura-serenity from /path/to/aura-serenity.pdf
...
2026-02-07 10:30:20 - ✅ Vector store initialized: 3 documents with 24 chunks
```

---

## 📊 性能统计

### 实际数据（示例）

**假设有3个产品手册**：

| 产品 | PDF大小 | 文本字符数 | Chunks数量 | 处理时间 |
|------|---------|-----------|-----------|---------|
| Aura Harmony | 1.2MB | 5,420 | 8 | 1.2秒 |
| Aura Serenity | 900KB | 4,100 | 6 | 0.9秒 |
| Aura Tranquility | 1.5MB | 6,800 | 10 | 1.5秒 |
| **总计** | 3.6MB | 16,320 | 24 | 3.6秒 |

**向量存储文件大小**：
- 24 chunks × 3072 dimensions × 4 bytes = ~295KB（压缩后）

---

## 🔄 RAG查询流程

### 从切片到回答

```
1. 用户问题
   👤: "How do I charge Aura Harmony?"
   ↓
2. RAGService 生成问题嵌入
   embedding = openai.embed("How do I charge Aura Harmony?")
   ↓
3. 向量相似度搜索
   vectorStore.similaritySearch(embedding, topK=3)
   ↓
4. 返回最相关的3个chunks
   Chunk 7: "Chapter 4: Charging. Use the USB-C cable..."  (相似度: 0.92)
   Chunk 1: "...battery life approximately 20 hours..."    (相似度: 0.78)
   Chunk 3: "...LED indicator turns red when charging..."  (相似度: 0.75)
   ↓
5. 组合chunks作为上下文
   context = Chunk 7 + Chunk 1 + Chunk 3
   ↓
6. 发送给OpenAI生成回答
   prompt = "Based on: {context}\nAnswer: {question}"
   ↓
7. AI生成回答
   🤖: "To charge your Aura Harmony headphones, use the included 
        USB-C cable. Connect it to the device and a power source. 
        The LED indicator will turn red while charging and green when 
        fully charged. It takes approximately 2 hours for a full charge."
```

---

## 💡 优化建议

### 当前实现的改进空间

#### 1. 更智能的分割策略

**当前**：简单的句子分割
```java
String[] sentences = text.split("(?<=[.!?])\\s+");
```

**改进**：基于段落和标题分割
```java
// 识别章节标题
if (line.matches("^Chapter \\d+:")) {
    // 新的section开始，作为chunk边界
}

// 识别项目符号列表
if (line.matches("^\\d+\\.|^[-*]")) {
    // 保持列表项完整性
}
```

---

#### 2. 动态chunk大小

**当前**：固定800字符

**改进**：根据内容类型调整
```java
if (isTableOfContents(text)) {
    chunkSize = 1500;  // 目录需要更大的块
} else if (isTechnicalSpec(text)) {
    chunkSize = 600;   // 技术规格需要精确切分
} else {
    chunkSize = 800;   // 默认
}
```

---

#### 3. 添加语义边界检测

**当前**：只按字符数切分

**改进**：避免在重要内容中间切断
```java
// 检查是否在段落中间
if (isMiddleOfParagraph(currentPosition)) {
    // 延长到段落结束
    extendToNextParagraph();
}

// 检查是否在列表中间
if (isMiddleOfList(currentPosition)) {
    // 包含完整列表
    extendToListEnd();
}
```

---

## 🧪 测试和验证

### 验证切片质量

```bash
# 启动后端，查看日志
mvn spring-boot:run

# 查找切片日志
grep "Split text into" logs/app.log

# 输出示例
Split text into 8 chunks (size: 800, overlap: 100)
Split text into 6 chunks (size: 800, overlap: 100)
```

---

### 查看向量存储内容

```bash
# 查看vector-store.json（格式化）
cat ./data/vector-store.json | jq '.documents[] | {id, content: .content[0:100], metadata}'

# 输出示例
{
  "id": "uuid-1",
  "content": "Aura Harmony User Manual Chapter 1: Getting Started To set up your Aura Harmony headphones...",
  "metadata": {
    "product_id": "aura-harmony",
    "source": "aura-harmony.pdf",
    "chunk_index": 0
  }
}
```

---

## ✅ 总结

### 当前PDF切片实现

1. **切片大小**：800字符（比之前的500增加了60%）
2. **重叠大小**：100字符（比之前的50增加了100%）
3. **分割策略**：基于句子边界，保持语义完整
4. **存储格式**：JSON文件（./data/vector-store.json）
5. **嵌入模型**：text-embedding-3-large（3072维）

### 优势

- ✅ 保留更多上下文信息
- ✅ 重叠部分避免信息断裂
- ✅ 按句子分割保持语义完整
- ✅ 元数据支持精确的来源追踪

### 适用场景

- ✅ 产品手册
- ✅ 用户指南
- ✅ 技术文档
- ✅ FAQ文档

---

**相关文档**：
- `VECTOR_STORE_UPGRADE.md` - 向量存储升级指南
- `SYSTEM_ARCHITECTURE_CN.md` - 系统架构详解

---

**END**
