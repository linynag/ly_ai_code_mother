#!/bin/bash

# MySQL Docker 启动脚本

set -e

echo "=== MySQL Docker 启动脚本 ==="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装或未启动"
    echo "请先安装并启动 Docker Desktop"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose 未安装"
    exit 1
fi

# 切换到脚本所在目录
cd "$(dirname "$0")"

echo "1. 清理旧的容器（如果存在）..."
docker-compose down -v 2>/dev/null || true

echo "2. 拉取最新的 MySQL 镜像..."
docker-compose pull mysql

echo "3. 启动 MySQL 容器..."
docker-compose up -d

echo "4. 等待 MySQL 启动（最多等待60秒）..."
timeout=60
counter=0
until docker exec mysql8 mysqladmin ping -h localhost -u root -p123456 --silent; do
    if [ $counter -ge $timeout ]; then
        echo "错误: MySQL 启动超时"
        docker-compose logs mysql
        exit 1
    fi
    echo "等待 MySQL 启动... ($counter/$timeout)"
    sleep 2
    counter=$((counter + 2))
done

echo "5. 检查数据库状态..."
docker exec mysql8 mysql -u root -p123456 -e "SELECT VERSION();"
docker exec mysql8 mysql -u root -p123py -e "SHOW DATABASES;"

echo ""
echo "=== MySQL 启动成功！==="
echo "连接信息："
echo "  主机: localhost"
echo "  端口: 3306"
echo "  数据库: ly_ai_code_mother"
echo "  用户名: myuser"
echo "  密码: 123456"
echo "  Root 密码: 123456"
echo ""
echo "常用命令："
echo "  查看日志: docker-compose logs mysql"
echo "  停止服务: docker-compose down"
echo "  连接数据库: docker exec -it mysql8 mysql -u root -p123456"
echo "  重启服务: docker-compose restart mysql"