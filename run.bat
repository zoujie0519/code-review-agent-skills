@echo off
REM Windows 启动脚本
setlocal

REM 设置环境变量（如果未设置）
if not defined OPENAI_API_KEY (
    echo Warning: OPENAI_API_KEY is not set. Using default value.
    set OPENAI_API_KEY=your-api-key-here
)

echo ========================================
echo Starting Code Review Agent...
echo ========================================

java -jar build\libs\code-review-agent-skills.jar

endlocal
