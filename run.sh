#!/bin/bash

# Code Review Agent 启动脚本

# 设置环境变量（如果未设置）
if [ -z "$OPENAI_API_KEY" ]; then
    echo "Warning: OPENAI_API_KEY is not set. Using default value."
    export OPENAI_API_KEY="your-api-key-here"
fi

echo "========================================"
echo "Starting Code Review Agent..."
echo "========================================"

# 运行应用
java -jar build/libs/code-review-agent-skills.jar
