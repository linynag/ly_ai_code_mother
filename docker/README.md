# MySQL Docker 镜像配置

这是一个完整的 MySQL 8.0 Docker 镜像配置，支持开箱即用的数据库服务。

## 文件结构

```
docker/
├── docker-compose.yml     # Docker Compose 配置文件
├── my.cnf                # MySQL 配置文件
├── start.sh              # 启动脚本
├── init/                 # 初始化脚本目录
│   └── 01_create_tables.sql  # 数据库初始化脚本
└── README.md             # 本文档
```

## 快速开始

### 方法一：使用启动脚本（推荐）

```bash
cd docker/
./start.sh
```

### 方法二：手动启动

```bash
cd docker/
docker-compose up -d
# 等待 MySQL 启动完成
docker-compose logs -f mysql
```

## 连接信息

- **主机**: localhost
- **端口**: 3306
- **数据库**: ly_ai_code_mother
- **用户名**: myuser
- **密码**: 123456
- **Root 密码**: 123456

## 常用命令

### 查看状态
```bash
# 查看容器状态
docker-compose ps

# 查看 MySQL 日志
docker-compose logs mysql

# 查看 MySQL 进程
docker exec mysql8 ps aux
```

### 连接数据库
```bash
# 使用 root 用户连接
docker exec -it mysql8 mysql -u root -p123456

# 使用普通用户连接
docker exec -it mysql8 mysql -u myuser -p123456 -D ly_ai_code_mother
```

### 管理服务
```bash
# 启动服务
docker-compose up -d

# 停止服务（保留数据卷）
docker-compose down

# 停止服务（删除数据卷）
docker-compose down -v

# 重启服务
docker-compose restart mysql

# 重新构建镜像
docker-compose up -d --build
```

### 数据备份与恢复
```bash
# 备份数据库
docker exec mysql8 mysqldump -u root -p123456 ly_ai_code_mother > backup.sql

# 恢复数据库
docker exec -i mysql8 mysql -u root -p123456 ly_ai_code_mother < backup.sql
```

## 配置说明

### 环境变量
- `MYSQL_ROOT_PASSWORD`: Root 用户密码
- `MYSQL_DATABASE`: 初始化时创建的数据库名
- `MYSQL_USER`: 普通用户名
- `MYSQL_PASSWORD`: 普通用户密码

### 数据持久化
- 数据存储在 Docker 数据卷 `mysql_data` 中
- 数据在容器重启后不会丢失

### 字符集配置
- 默认字符集: utf8mb4
- 排序规则: utf8mb4_unicode_ci
- 支持完整的 Unicode 字符集

### 性能优化
- 配置了适当的缓冲池大小
- 启用查询缓存
- 配置了慢查询日志
- 优化了连接参数

## 自定义配置

### 修改密码
编辑 `docker-compose.yml` 中的环境变量：
```yaml
environment:
  MYSQL_ROOT_PASSWORD: your_new_password
  MYSQL_PASSWORD: your_new_password
```

### 添加自定义初始化脚本
在 `init/` 目录下添加 `.sql` 文件，文件名按字母顺序执行。

### 自定义 MySQL 配置
编辑 `my.cnf` 文件，然后重启服务：
```bash
docker-compose restart mysql
```

## 故障排除

### 端口冲突
如果 3306 端口被占用，修改 `docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "3307:3306"  # 使用 3307 端口访问
```

### 容器无法启动
```bash
# 查看详细日志
docker-compose logs mysql

# 检查磁盘空间
docker system df
```

### 数据库连接失败
```bash
# 检查容器状态
docker-compose ps

# 检查 MySQL 是否就绪
docker exec mysql8 mysqladmin ping -h localhost -u root -p123456
```

### 清理环境
```bash
# 完全清理（删除所有相关资源）
docker-compose down -v
docker volume prune
docker system prune -f
```

## 安全注意事项

1. **生产环境请修改默认密码**
2. **生产环境请配置防火墙规则**
3. **定期备份数据库**
4. **监控数据库访问日志**
5. **不要将 Root 密码暴露在配置文件中**

## 扩展功能

### 添加 phpMyAdmin
在 `docker-compose.yml` 中添加：
```yaml
phpmyadmin:
  image: phpmyadmin/phpmyadmin
  environment:
    PMA_HOST: mysql
    PMA_PORT: 3306
  ports:
    - "8080:80"
  depends_on:
    - mysql
```

### 添加 Redis 缓存
```yaml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
```

## 许可证

本配置仅供学习和开发使用。