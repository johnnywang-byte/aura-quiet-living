#!/bin/bash
# 启动后端并重新生成向量数据库
# Start backend and rebuild vector store

echo "=========================================="
echo "🚀 启动后端服务并重新生成向量数据库"
echo "=========================================="
echo ""

# 检查旧的向量数据库文件
if [ -f "./data/vector-store.json" ]; then
    echo "⚠️  发现旧的向量数据库文件"
    echo "   文件路径: ./data/vector-store.json"
    echo "   文件大小: $(du -h ./data/vector-store.json | cut -f1)"
    echo ""
    read -p "是否删除并重新生成？(y/n): " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        rm -f ./data/vector-store.json
        echo "✅ 已删除旧文件"
    fi
    echo ""
fi

echo "📋 升级配置："
echo "   - 向量模型: text-embedding-3-large (3072维)"
echo "   - 分块大小: 800 字符"
echo "   - 分块重叠: 100 字符"
echo ""

echo "🔄 启动后端服务..."
echo "   后端将自动扫描 PDF 文件并生成新的向量数据库"
echo "   预计耗时: 30-60秒"
echo ""

# 启动后端
cd aura-backend
mvn spring-boot:run
