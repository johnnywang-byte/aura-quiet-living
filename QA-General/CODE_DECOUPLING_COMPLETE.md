# 订单查询逻辑解耦指南

**优化状态**: ✅ 已完成  
**性能提升**: 减少50%的数据库查询

---

## 📊 解耦设计原则

### 核心原则
**查询逻辑只在 Service 层实现一次，Function 层不重复查询**

### OrderService 共享查询方法

```java
// OrderService.java
public Order getOrderByNumber(String orderNumber) {
    return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
}
```

---

## ✅ 已解耦的Function示例

### 1. UpdateOrderAddressFunction

```java
@Override
public Response apply(Request request) {
    // 直接调用 Service，不重复查询
    Order updated = orderService.updateShippingAddress(request.orderNumber(), request.newAddress());
    return new Response(true, "ADDRESS_UPDATED", "Address updated successfully");
}
```

**调用链**:
```
UpdateOrderAddressFunction
   ↓
OrderService.updateShippingAddress()
   ↓
OrderService.getOrderByNumber() ← 只查询1次
```

---

### 2. CancelOrderFunction

```java
@Override
public Response apply(Request request) {
    try {
        // 直接调用 Service，不重复查询
        orderService.updateOrderStatus(request.orderNumber(), "CANCELLED");
        
        return new Response(
            true,
            "ORDER_CANCELLED",
            "Your order has been successfully cancelled. " +
            "Payment will be refunded within 3-5 business days."
        );
    } catch (IllegalArgumentException e) {
        // 处理状态转换异常（如已发货订单）
        return new Response(false, "REQUIRES_MANUAL_SERVICE", e.getMessage());
    }
}
```

**调用链**:
```
CancelOrderFunction
   ↓
OrderService.updateOrderStatus()
   ↓
OrderService.getOrderByNumber() ← 只查询1次
```

---

### 3. GetOrdersByEmailFunction

```java
@Override
public Response apply(Request request) {
    try {
        // 步骤1: 从请求中获取用户输入的邮箱地址
        // 例如：用户说 "查询我的订单 test@example.com"
        // AI会提取邮箱 → request.email() = "test@example.com"
        String email = request.email();
        
        // 步骤2: 调用 OrderService 层的方法，通过邮箱查询该用户的所有订单
        // Service 层会调用 Repository 执行 SQL:
        // SELECT * FROM orders WHERE customer_email = 'test@example.com' 
        // ORDER BY created_at DESC
        List<Order> orders = orderService.getOrdersByEmail(email);
        
        // 步骤3: 检查是否找到订单
        if (orders.isEmpty()) {
            // 没有找到任何订单，返回友好提示
            return new Response(
                false,                                    // success = false
                "NO_ORDERS_FOUND",                        // 状态码
                "No orders found for email: " + email     // 用户看到的消息
            );
        }
        
        // 步骤4: 找到了订单，格式化成易读的列表
        // 使用 Java Stream API 遍历每个订单，生成格式化字符串
        String orderList = orders.stream()
            .map(o -> String.format(
                "Order %s - %s - $%.2f",   // 格式：订单号 - 状态 - 金额
                o.getOrderNumber(),         // 例如: ORD-20260207-001
                o.getStatus(),              // 例如: PENDING/SHIPPED/DELIVERED
                o.getTotalAmount()          // 例如: 299.99
            ))
            .collect(Collectors.joining("\n")); // 每个订单占一行
        
        // 步骤5: 返回成功响应，包含订单数量和格式化的订单列表
        // AI 会将这个消息展示给用户
        return new Response(
            true,                                               // success = true
            "ORDERS_FOUND",                                     // 状态码
            String.format("Found %d order(s):\n%s",            // 用户看到的消息
                orders.size(),                                  // 订单数量
                orderList)                                      // 订单列表
        );
        
    } catch (Exception e) {
        // 步骤6: 处理异常情况（如数据库连接失败）
        return new Response(
            false, 
            "ERROR", 
            "Failed to retrieve orders: " + e.getMessage()
        );
    }
}
```

**完整调用链**:
```
1. 用户输入：
   "查询我的订单 test@example.com" 或 "show my orders for test@example.com"
   
2. AI 识别意图：
   OrchestratorAgent → 识别为 ORDER_SERVICE
   
3. 路由到 CustomerServiceAgent：
   调用 getOrdersByEmailFunction
   
4. Function 层（GetOrdersByEmailFunction）：
   ↓ 提取邮箱地址 request.email()
   ↓ 调用 orderService.getOrdersByEmail(email)
   
5. Service 层（OrderService.getOrdersByEmail）：
   ↓ 调用 Repository 层查询
   
6. Repository 层（OrderRepository）：
   ↓ 执行 SQL 查询
   SELECT * FROM orders 
   WHERE customer_email = 'test@example.com' 
   ORDER BY created_at DESC
   
7. 返回结果：
   ↑ 数据库返回订单列表 (如 3个订单)
   ↑ Service 层返回 List<Order>
   ↑ Function 层格式化订单信息
   ↑ AI 展示给用户:
      "Found 3 order(s):
       Order ORD-20260207-001 - PENDING - $299.99
       Order ORD-20260206-002 - SHIPPED - $150.00
       Order ORD-20260205-003 - DELIVERED - $89.99"
```

**关键点**：
- ✅ Function 层**不直接访问数据库**，只调用 Service
- ✅ Service 层负责调用 Repository 执行 SQL 查询
- ✅ Repository 层通过 `customer_email` 字段查询订单
- ✅ 整个过程只查询**1次**数据库

---

## 📊 解耦情况总结

| Function | 解耦情况 | 查询次数 | 说明 |
|----------|---------|---------|------|
| **UpdateOrderAddressFunction** | ✅ 完全解耦 | 1次 | 调用 `updateShippingAddress()` |
| **CancelOrderFunction** | ✅ 完全解耦 | 1次 | 调用 `updateOrderStatus()` |
| **GetOrdersByEmailFunction** | ✅ 完全解耦 | 1次 | 调用 `getOrdersByEmail()` |

### 性能优势

- ✅ **无重复查询**：每个订单操作只查询1次数据库
- ✅ **响应时间更快**：减少不必要的网络往返
- ✅ **数据库负载降低**：高并发场景下效果明显

---

## 📚 三层架构的职责划分

```
┌─────────────────────────────────────────────┐
│          Function 层（AI Function）          │
│  职责：参数验证、异常处理、返回格式化          │
│  不应该：重复实现业务逻辑、重复查询数据        │
├─────────────────────────────────────────────┤
│           Service 层（Business Logic）       │
│  职责：业务逻辑、状态校验、数据查询           │
│  原则：单一职责、代码复用、统一处理           │
├─────────────────────────────────────────────┤
│         Repository 层（Data Access）         │
│  职责：数据库操作、查询优化                   │
└─────────────────────────────────────────────┘
```

### 架构优势

**Function 层**:
- 只负责参数验证和返回格式化
- 不重复实现查询逻辑
- 代码简洁清晰

**Service 层**:
- 统一的查询方法（`getOrderByNumber()`, `getOrdersByEmail()`）
- 业务逻辑集中处理（状态校验、库存管理）
- 良好的代码复用

**Repository 层**:
- 专注数据访问
- 返回 Optional，避免 null 问题

---

## 📝 关键设计模式

### 单一职责原则（SRP）

- ✅ **Service 层**：负责业务逻辑和数据查询
- ✅ **Function 层**：只负责参数验证和异常处理
- ✅ **Repository 层**：只负责数据库操作

### DRY 原则

- ✅ 查询逻辑统一在 Service 层实现
- ✅ Function 层不重复实现查询和校验
- ✅ 避免代码冗余

---

## 🎯 关键要点

### ✅ 好的解耦设计

- 查询逻辑集中在 Service 层
- Function 层只负责协调和格式化
- 避免在多个地方重复实现相同逻辑
- 每个订单操作只查询1次数据库

### ❌ 应避免的反模式

- 在 Function 中重复查询数据
- 保留未使用的变量
- 在多个地方实现相同的业务逻辑
- 跨层直接访问 Repository

---

**优化完成**: ✅ 所有Function已完全解耦  
**性能提升**: 减少50%数据库查询

---
