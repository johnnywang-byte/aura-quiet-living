# AI 对话测试问题集 - 完整功能覆盖

**版本**: v1.0  
**日期**: 2026-02-07  
**目的**: 快速测试所有Agent和Function功能

---

## 📋 测试覆盖范围

### 4个 AI Agents
1. **GeneralChatAgent** - 一般对话、品牌介绍
2. **ProductExpertAgent** - 产品咨询、RAG检索
3. **CustomerServiceAgent** - 订单管理、客户服务
4. **OrchestratorAgent** - 智能路由（自动）

### 7个 Functions
1. `getOrderStatusFunction` - 查询订单状态
2. `updateOrderAddressFunction` - 修改订单地址
3. `getOrdersByEmailFunction` - 通过邮箱查询订单
4. `cancelOrderFunction` - 取消订单
5. `checkInventoryFunction` - 检查库存
6. `queryProductManualFunction` - 查询产品手册 (RAG)
7. `searchProductsFunction` - 搜索产品

---

## 🎯 10个核心测试问题

### 问题 1: 通用对话 (GeneralChatAgent)
```
你好，介绍一下Aura Quiet Living这个品牌
```

**测试目标**:
- ✅ 触发 GeneralChatAgent
- ✅ 品牌介绍能力
- ✅ 友好的对话语气

**预期回答**:
- 介绍品牌理念（宁静生活、科技与自然结合）
- 提到产品类型（音频、穿戴、智能家居）
- 语气专业且温暖

---

### 问题 2: 产品搜索 (ProductExpertAgent + searchProductsFunction)
```
有什么耳机产品？
```

**测试目标**:
- ✅ 触发 ProductExpertAgent
- ✅ 直接调用 `productService.searchProducts("耳机")`
- ✅ 返回产品列表

**预期回答**:
- 推荐 Aura Harmony 无线降噪耳机
- 包含价格、特点（蓝牙5.0、30小时续航、主动降噪）
- 库存状态

---

### 问题 3: 产品手册查询 (ProductExpertAgent + queryProductManualFunction/RAG)
```
Aura Harmony的降噪效果怎么样？支持什么蓝牙协议？
```

**测试目标**:
- ✅ 触发 ProductExpertAgent
- ✅ 调用 RAG 从产品手册向量库检索
- ✅ 返回详细技术信息

**预期回答**:
- 从PDF手册中检索降噪技术细节
- 蓝牙5.0协议信息
- 专业且具体的技术参数

---

### 问题 4: 上下文理解 (ProductExpertAgent - 关键词提取)
```
第一句: "Aura Harmony多少钱？"
第二句: "它支持快充吗？"  ← 测试对"它"的理解
```

**测试目标**:
- ✅ 测试 `extractProductFromHistory()` 方法
- ✅ 从历史对话提取 "harmony" 关键词
- ✅ AI理解"它"指代的产品

**预期回答**:
- 第一句: 回答价格 $429
- 第二句: 正确理解"它"="Aura Harmony"，回答充电功能

---

### 问题 5: 库存查询 (CustomerServiceAgent + checkInventoryFunction)
```
Aura Echo智能音箱还有货吗？
```

**测试目标**:
- ✅ 触发 CustomerServiceAgent
- ✅ 调用 `checkInventoryFunction`
- ✅ 返回实时库存状态

**预期回答**:
- 调用库存查询函数
- 显示当前库存数量
- 如果有货，提示可以下单

---

### 问题 6: 查询订单状态 (CustomerServiceAgent + getOrderStatusFunction)
```
我的订单 ORD-20260205210016-9102 现在是什么状态？
```

**测试目标**:
- ✅ 触发 CustomerServiceAgent
- ✅ 调用 `getOrderStatusFunction`
- ✅ 返回订单详细信息

**预期回答**:
- 订单状态 (PENDING/SHIPPED/DELIVERED)
- 配送地址
- 订单商品列表
- 总金额

**注意**: 请替换为你数据库中实际存在的订单号！

---

### 问题 7: 通过邮箱查询订单 (CustomerServiceAgent + getOrdersByEmailFunction)
```
帮我查一下邮箱 daimazhumeng@gmail.com 的所有订单
```

**测试目标**:
- ✅ 触发 CustomerServiceAgent
- ✅ 调用 `getOrdersByEmailFunction`
- ✅ 返回该邮箱的所有订单

**预期回答**:
- 订单列表（订单号、日期、状态）
- 如果没有订单，提示未找到
- 如果有多个订单，逐一列出

**注意**: 请替换为你数据库中实际存在的客户邮箱！

---

### 问题 8: 修改订单地址 (CustomerServiceAgent + updateOrderAddressFunction)
```
我想把订单 ORD-20260205210016-9102 的地址改成 "北京市朝阳区建国路88号"
```

**测试目标**:
- ✅ 触发 CustomerServiceAgent
- ✅ 调用 `updateOrderAddressFunction`
- ✅ 验证订单状态（只有PENDING可修改）

**预期回答**:
- 如果订单状态为 PENDING: 成功修改地址并确认
- 如果订单已发货/送达: 提示无法修改，联系客服

**注意**: 
- 请替换为你数据库中实际存在的订单号
- 测试前确认订单状态是 PENDING

---

### 问题 9: 取消订单 - 成功场景 (CustomerServiceAgent + cancelOrderFunction)
```
我想取消订单 ORD-20260205210016-9102
```

**测试目标**:
- ✅ 触发 CustomerServiceAgent
- ✅ 调用 `cancelOrderFunction`
- ✅ 验证业务逻辑（PENDING订单可取消）

**预期回答**:
- 如果订单状态为 PENDING: 
  - ✅ 成功取消
  - ✅ 确认退款3-5个工作日到账
  - ✅ 库存已释放
  
**注意**: 确保测试订单状态为 PENDING

---

### 问题 10: 取消订单 - 需要人工服务 (CustomerServiceAgent + cancelOrderFunction)
```
我想取消订单 ORD-20260206081552-5284  ← 假设这个订单已发货
```

**测试目标**:
- ✅ 测试已发货/已送达订单的取消逻辑
- ✅ 验证错误处理和客服引导

**预期回答**:
- 如果订单状态为 SHIPPED: 
  - ❌ 无法直接取消
  - ✅ 提示订单已发货
  - ✅ 引导联系人工客服 (support@auraquietliving.com / 400-xxx-xxxx)
  
- 如果订单状态为 DELIVERED:
  - ❌ 无法取消
  - ✅ 提示订单已送达
  - ✅ 引导办理退货流程

**注意**: 测试前需要有状态为 SHIPPED 或 DELIVERED 的订单

---

## 🔄 额外测试：多轮对话与上下文

### 测试 11: 复杂多轮对话
```
第1轮: "你们有什么智能家居产品？"
第2轮: "Aura Echo的价格是多少？"
第3轮: "它支持哪些音乐平台？"  ← 测试对"它"的理解
第4轮: "帮我查一下库存"  ← 测试对"Aura Echo"的记忆
```

**测试目标**:
- ✅ Agent切换流畅（ProductExpertAgent → CustomerServiceAgent）
- ✅ 上下文记忆
- ✅ 代词理解

---

## 📊 测试检查表

### Agent 路由测试
- [ ] GeneralChatAgent - 通用对话
- [ ] ProductExpertAgent - 产品咨询
- [ ] CustomerServiceAgent - 订单管理
- [ ] Agent之间的切换流畅

### Function 调用测试
- [ ] `searchProductsFunction` - 产品搜索
- [ ] `queryProductManualFunction` (RAG) - 手册检索
- [ ] `checkInventoryFunction` - 库存查询
- [ ] `getOrderStatusFunction` - 订单状态
- [ ] `getOrdersByEmailFunction` - 邮箱查订单
- [ ] `updateOrderAddressFunction` - 修改地址
- [ ] `cancelOrderFunction` - 取消订单

### 特殊能力测试
- [ ] 上下文理解（代词"它"）
- [ ] 关键词提取（产品名称记忆）
- [ ] 业务规则验证（订单状态限制）
- [ ] 错误处理（无效订单号、邮箱）

---

## 🔧 测试前准备

### 1. 确认测试数据

在MySQL中运行以下查询，获取实际数据：

```sql
-- 获取可用的订单号
SELECT order_number, status, customer_email 
FROM orders 
ORDER BY created_at DESC 
LIMIT 5;

-- 获取不同状态的订单
SELECT status, COUNT(*) as count 
FROM orders 
GROUP BY status;

-- 获取产品库存
SELECT id, name, stock 
FROM products 
WHERE stock > 0;
```

### 2. 更新测试问题

将上述SQL查询结果中的**真实订单号**和**邮箱**替换到测试问题中。

---

## 📝 测试结果记录模板

| 问题# | 测试内容 | Agent | Function | 结果 | 备注 |
|-------|---------|-------|----------|------|------|
| 1 | 品牌介绍 | General | - | ✅/❌ | |
| 2 | 产品搜索 | Product | searchProducts | ✅/❌ | |
| 3 | 手册查询 | Product | RAG | ✅/❌ | |
| 4 | 上下文理解 | Product | - | ✅/❌ | |
| 5 | 库存查询 | Customer | checkInventory | ✅/❌ | |
| 6 | 订单状态 | Customer | getOrderStatus | ✅/❌ | |
| 7 | 邮箱查询 | Customer | getOrdersByEmail | ✅/❌ | |
| 8 | 修改地址 | Customer | updateAddress | ✅/❌ | |
| 9 | 取消订单(成功) | Customer | cancelOrder | ✅/❌ | |
| 10 | 取消订单(人工) | Customer | cancelOrder | ✅/❌ | |
| 11 | 多轮对话 | 多个 | 多个 | ✅/❌ | |

---

## 🎓 高级测试场景（可选）

### 测试 12: 边界情况 - 无效输入
```
查询订单 ORD-INVALID-123
```
**预期**: AI提示订单号格式错误或订单不存在

---

### 测试 13: 混合意图
```
Aura Harmony多少钱？顺便帮我查一下我的订单状态
```
**预期**: AI识别多个意图，逐一回答或引导用户分开询问

---

### 测试 14: 情感对话
```
我很沮丧，工作压力很大
```
**预期**: GeneralChatAgent给予共情回应，引导产品推荐（助眠产品）

---

### 测试 15: 跨语言测试（如果支持）
```
中文: "你好，有什么耳机推荐？"
英文: "Hello, what headphones do you recommend?"
```
**预期**: 根据输入语言回应

---

## 📚 相关文档

- [AI_ASSISTANT_TEST_GUIDE.md](./AI_ASSISTANT_TEST_GUIDE.md) - 详细测试指南
- [FUNCTION_REGISTRATION_AND_AGENTS.md](./FUNCTION_REGISTRATION_AND_AGENTS.md) - Function与Agent详解
- [MEMORY_SYSTEM.md](./MEMORY_SYSTEM.md) - 三层记忆系统详解
- [AI_CALL_FLOW_COMPLETE_GUIDE.md](./AI_CALL_FLOW_COMPLETE_GUIDE.md) - AI调用流程完整解析

---

## 💡 测试小贴士

1. **按顺序测试**: 先测基础功能（1-3），再测复杂功能（8-10）
2. **记录失败**: 详细记录任何异常行为或错误消息
3. **测试真实数据**: 使用数据库中实际存在的订单号和邮箱
4. **清除会话**: 每个独立测试前可刷新页面清除上下文
5. **多轮对话**: 测试完单个问题后，尝试连续对话测试上下文记忆

---

## 🚀 快速测试脚本

复制以下问题，逐个粘贴到AI助手中测试：

```
1. 你好，介绍一下Aura Quiet Living这个品牌
2. 有什么耳机产品？
3. Aura Harmony的降噪效果怎么样？支持什么蓝牙协议？
4. Aura Harmony多少钱？
5. 它支持快充吗？
6. Aura Echo智能音箱还有货吗？
7. 我的订单 [替换为真实订单号] 现在是什么状态？
8. 帮我查一下邮箱 [替换为真实邮箱] 的所有订单
9. 我想把订单 [替换为PENDING订单号] 的地址改成 "北京市朝阳区建国路88号"
10. 我想取消订单 [替换为PENDING订单号]
11. 我想取消订单 [替换为SHIPPED订单号]
```

**注意**: 测试前务必替换方括号内容为真实数据！

---

**祝测试顺利！** 🎉

如有问题或发现bug，请记录详细信息并反馈。
