# 个人相册管理系统部署说明文档

## 项目概述

这是一个前后端分离的个人相册管理系统，包含用户注册登录、相册管理、照片上传与管理等功能。

### 技术栈

**后端：**
- Spring Boot 3.1.5
- Spring Security
- MyBatis-Plus 3.5.4
- MySQL 8.0
- JWT认证

**前端：**
- Vue 3
- Vite 5
- Element Plus 2.4
- PhotoSwipe 5.4
- Pinia状态管理

## 环境要求

### 后端环境
- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+

### 前端环境
- Node.js 16+ 
- npm 或 yarn

## 数据库配置

### 1. 创建数据库

执行 `database/init.sql` 文件创建数据库和表：

```bash
mysql -u root -p < database/init.sql
```

或者手动在MySQL客户端中执行SQL脚本。

### 2. 修改数据库配置

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/photo_album?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password  # 修改为你的数据库密码
```

### 3. 修改JWT密钥

为了安全，请修改JWT密钥：

```yaml
jwt:
  secret: YourSecretKeyForJWTTokenGenerationMustBeVeryLongAndSecure123456789  # 请修改为自己的密钥
```

## 后端部署

### 开发环境运行

1. 进入后端目录：
```bash
cd backend
```

2. 安装依赖：
```bash
mvn clean install
```

3. 运行项目：
```bash
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 生产环境部署

#### 方式一：JAR包部署

1. 打包项目：
```bash
cd backend
mvn clean package -DskipTests
```

2. 运行JAR包：
```bash
java -jar target/album-1.0.0.jar
```

3. 后台运行（Linux）：
```bash
nohup java -jar target/album-1.0.0.jar > app.log 2>&1 &
```

#### 方式二：Docker部署

1. 创建Dockerfile（在backend目录下）：

```dockerfile
FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY target/album-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

2. 构建镜像：
```bash
docker build -t photo-album-backend .
```

3. 运行容器：
```bash
docker run -d -p 8080:8080 --name photo-album \
  -v /path/to/uploads:/uploads \
  photo-album-backend
```

## 前端部署

### 开发环境运行

1. 进入前端目录：
```bash
cd frontend
```

2. 安装依赖：
```bash
npm install
```

3. 运行开发服务器：
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动。

### 生产环境部署

#### 方式一：静态文件部署

1. 构建生产版本：
```bash
cd frontend
npm run build
```

2. 构建完成后，将 `dist` 目录部署到Web服务器（如Nginx、Apache）。

#### Nginx配置示例：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /uploads {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

#### 方式二：Docker部署

1. 创建Dockerfile（在frontend目录下）：

```dockerfile
FROM node:16-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

2. 创建nginx.conf（在frontend目录下）：

```nginx
server {
    listen 80;
    
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /uploads {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

3. 构建镜像：
```bash
docker build -t photo-album-frontend .
```

4. 运行容器：
```bash
docker run -d -p 80:80 --name photo-album-web photo-album-frontend
```

## Docker Compose部署（推荐）

创建 `docker-compose.yml` 文件：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: photo-album-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: photo_album
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - photo-album-network

  backend:
    build: ./backend
    container_name: photo-album-backend
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/photo_album?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123
    ports:
      - "8080:8080"
    volumes:
      - upload-data:/uploads
    networks:
      - photo-album-network

  frontend:
    build: ./frontend
    container_name: photo-album-frontend
    depends_on:
      - backend
    ports:
      - "80:80"
    networks:
      - photo-album-network

volumes:
  mysql-data:
  upload-data:

networks:
  photo-album-network:
    driver: bridge
```

运行：
```bash
docker-compose up -d
```

## 安全配置建议

### 1. 数据库安全
- 使用强密码
- 限制数据库访问IP
- 定期备份数据库

### 2. 应用安全
- 修改默认JWT密钥为复杂字符串
- 启用HTTPS
- 配置防火墙规则
- 定期更新依赖包

### 3. 文件上传安全
- 已限制文件类型（jpg, jpeg, png, gif, bmp, webp）
- 已限制文件大小（10MB）
- 文件存储在服务器本地，建议配置定期备份

### 4. 用户隐私保护
- 密码使用BCrypt加密存储
- JWT Token过期时间为24小时
- 敏感信息不在前端存储

## 功能说明

### 用户功能
- 用户注册与登录
- JWT Token认证
- 用户信息管理

### 相册管理
- 创建相册
- 编辑相册（名称、描述）
- 删除相册（照片移至未分类）

### 照片管理
- 上传照片（支持批量上传）
- 照片展示（瀑布流布局）
- 照片重命名
- 照片删除（支持批量删除）
- 照片移动到其他相册
- 照片复制到其他相册
- PhotoSwipe图片预览

## 常见问题

### 1. 后端启动失败
- 检查数据库连接配置
- 确认MySQL服务已启动
- 检查端口8080是否被占用

### 2. 前端无法访问后端API
- 检查后端服务是否启动
- 确认CORS配置正确
- 检查代理配置（开发环境）

### 3. 文件上传失败
- 检查文件大小是否超过限制
- 确认文件格式是否支持
- 检查uploads目录权限

### 4. 登录后Token失效
- 检查JWT密钥配置
- 确认Token未过期
- 清除浏览器缓存重新登录

## 维护建议

1. **定期备份数据库**
   ```bash
   mysqldump -u root -p photo_album > backup_$(date +%Y%m%d).sql
   ```

2. **定期清理日志文件**
   - 配置日志轮转
   - 定期清理旧日志

3. **监控服务状态**
   - 使用监控工具（如Prometheus）
   - 配置告警机制

4. **定期更新依赖**
   ```bash
   # 后端
   cd backend
   mvn versions:display-dependency-updates
   
   # 前端
   cd frontend
   npm outdated
   ```

## 技术支持

如有问题，请查看项目源码或提交Issue。

## 许可证

本项目仅供学习和参考使用。
