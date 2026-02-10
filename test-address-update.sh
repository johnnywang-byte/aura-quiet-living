#!/bin/bash
# 测试订单地址修改功能
# Test Order Address Update Function

echo "=========================================="
echo "🧪 测试订单地址修改功能"
echo "=========================================="
echo ""

# 检查后端是否运行
if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "❌ 后端服务未运行，请先启动："
    echo "   cd aura-backend && mvn spring-boot:run"
    exit 1
fi

SESSION_ID=$(uuidgen)
echo "使用 Session ID: $SESSION_ID"
echo ""

# 测试1：修改不存在的订单
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "测试1: 修改不存在的订单"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "输入: Change address for order ORD-99999999999999-9999 to 456 Oak Ave"
echo ""
echo "预期: 明确说明订单不存在 + 提供格式参考 + 建议邮箱查询"
echo ""
echo "AI响应:"
curl -s -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\":\"$SESSION_ID\",\"message\":\"Change address for order ORD-99999999999999-9999 to 456 Oak Ave\"}" \
  | jq -r '.data.message' | fold -w 70 -s
echo ""

sleep 2

# 测试2：查询数据库中的一个PENDING订单
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "测试2: 修改PENDING状态的订单（应该成功）"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "输入: Change address for order ORD-20260206064823-1234 to 789 Pine St"
echo ""
echo "预期: 成功确认 + 显示新地址"
echo ""
echo "AI响应:"
curl -s -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\":\"$SESSION_ID\",\"message\":\"Change address for order ORD-20260206064823-1234 to 789 Pine St\"}" \
  | jq -r '.data.message' | fold -w 70 -s
echo ""

sleep 2

# 测试3：查看修改后的订单信息
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "测试3: 验证地址是否真的更新了"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "输入: Check order ORD-20260206064823-1234"
echo ""
echo "预期: 应该显示新地址 789 Pine St"
echo ""
echo "AI响应:"
curl -s -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\":\"$SESSION_ID\",\"message\":\"Check order ORD-20260206064823-1234\"}" \
  | jq -r '.data.message' | fold -w 70 -s
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ 测试完成"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "检查点："
echo "  - 测试1：应该明确说'找不到订单'而不是'系统错误'"
echo "  - 测试2：应该成功更新地址"
echo "  - 测试3：应该显示新地址"
echo ""
