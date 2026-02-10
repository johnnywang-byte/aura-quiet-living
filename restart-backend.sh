#!/bin/bash
# 重启后端服务脚本
# Restart Backend Service

echo "=========================================="
echo "🔄 重启后端服务"
echo "=========================================="
echo ""

# 1. 停止旧进程
echo "1️⃣ 停止旧的后端进程..."
PIDS=$(lsof -ti:8080 2>/dev/null)
if [ -z "$PIDS" ]; then
    echo "   没有发现运行中的后端进程"
else
    echo "   发现进程: $PIDS"
    echo "   正在停止..."
    kill $PIDS 2>/dev/null
    sleep 2
    
    # 强制杀死（如果还在运行）
    REMAINING=$(lsof -ti:8080 2>/dev/null)
    if [ ! -z "$REMAINING" ]; then
        echo "   强制停止..."
        kill -9 $REMAINING 2>/dev/null
    fi
    
    echo "   ✅ 已停止"
fi
echo ""

# 2. 编译最新代码
echo "2️⃣ 编译最新代码..."
cd /Users/johnnywang/Downloads/aura-quiet-living/aura-backend
mvn clean compile -q
if [ $? -eq 0 ]; then
    echo "   ✅ 编译成功"
else
    echo "   ❌ 编译失败"
    exit 1
fi
echo ""

# 3. 启动新服务
echo "3️⃣ 启动后端服务..."
echo "   使用新的代码修复："
echo "   - ✅ EntityNotFoundException 捕获（订单不存在）"
echo "   - ✅ 明确的错误消息"
echo "   - ✅ System Prompt 优化"
echo ""
echo "   后端将在新终端启动..."
echo "   按 Ctrl+C 可停止"
echo ""
echo "=========================================="
echo ""

# 启动服务
mvn spring-boot:run
