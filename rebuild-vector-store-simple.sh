#!/bin/bash
# 简化版重新生成向量数据库脚本（无需确认）
# Simple Rebuild Vector Store Script (No Confirmation)

echo "🔄 重新生成向量数据库..."
echo "模型: text-embedding-3-large (3072维)"
echo ""

# 调用API重新生成
curl -s -X POST http://localhost:8080/api/admin/vector-store/rebuild | jq '.'

echo ""
echo "✅ 完成！"
