#!/bin/bash
# 重新生成向量数据库脚本
# Rebuild Vector Store Script

echo "=========================================="
echo "🔄 重新生成向量数据库"
echo "=========================================="
echo ""

# 检查后端是否运行
echo "1️⃣ 检查后端服务状态..."
if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "❌ 后端服务未运行，请先启动后端服务："
    echo "   cd aura-backend && mvn spring-boot:run"
    exit 1
fi
echo "✅ 后端服务正在运行"
echo ""

# 查看当前向量数据库状态
echo "2️⃣ 查看当前向量数据库状态..."
curl -s http://localhost:8080/api/admin/vector-store/status | jq '.'
echo ""

# 确认是否继续
echo "=========================================="
read -p "⚠️  是否继续重新生成？这将删除旧数据 (y/n): " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ 操作已取消"
    exit 0
fi

# 重新生成向量数据库
echo ""
echo "3️⃣ 重新生成向量数据库..."
echo "   使用模型: text-embedding-3-large (3072维)"
echo "   分块大小: 800字符"
echo "   分块重叠: 100字符"
echo ""

RESPONSE=$(curl -s -X POST http://localhost:8080/api/admin/vector-store/rebuild)
echo "$RESPONSE" | jq '.'

# 检查结果
SUCCESS=$(echo "$RESPONSE" | jq -r '.success')
if [ "$SUCCESS" = "true" ]; then
    echo ""
    echo "=========================================="
    echo "✅ 向量数据库重新生成成功！"
    echo "=========================================="
    
    TOTAL_DOCS=$(echo "$RESPONSE" | jq -r '.total_documents')
    TOTAL_CHUNKS=$(echo "$RESPONSE" | jq -r '.total_chunks')
    
    echo "📊 统计信息："
    echo "   - 处理文档数: $TOTAL_DOCS"
    echo "   - 生成分块数: $TOTAL_CHUNKS"
    echo "   - 平均每文档: $((TOTAL_CHUNKS / TOTAL_DOCS)) 个分块"
    echo ""
    echo "💡 提示："
    echo "   - 新的向量数据库已保存到 ./data/vector-store.json"
    echo "   - 现在可以测试AI对话，上下文理解应该更准确了"
    echo ""
else
    echo ""
    echo "❌ 向量数据库重新生成失败"
    ERROR=$(echo "$RESPONSE" | jq -r '.error // "未知错误"')
    echo "   错误信息: $ERROR"
    exit 1
fi
