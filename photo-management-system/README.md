# 个人相册管理系统

一个前后端分离的个人相册管理系统，支持用户注册登录、相册管理、照片上传、瀑布流展示等功能。

## 功能特性

### 用户模块
- 用户注册/登录（Spring Security + JWT）
- 用户信息管理

### 相册模块
- 创建、编辑、删除相册
- 相册列表展示
- 相册封面设置

### 照片模块
- 多张照片上传（支持 JPG、PNG、GIF、WEBP）
- 照片瀑布流展示
- 照片预览（支持左右切换）
- 照片重命名
- 照片移动/复制到不同相册
- 单张/批量删除照片
- 自动生成缩略图

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Security 6.x
- MyBatis-Plus 3.5.5
- MySQL 8.x
- JWT 0.12.3
- Thumbnailator（图片处理）

### 前端
- Vue 3.4
- Vite 5.x
- Element Plus 2.5
- Vue Router 4.x
- Pinia（状态管理）
- Axios
- Masonry Layout（瀑布流）
- PhotoSwipe（图片预览）

## 项目结构

```
photo-management-system/
├── backend/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/photomanagement/
│   │       ├── config/      # 配置类
│   │       ├── controller/  # 控制器
│   │       ├── dto/         # 数据传输对象
│   │       ├── entity/      # 实体类
│   │       ├── exception/   # 异常处理
│   │       ├── mapper/      # MyBatis映射器
│   │       ├── security/    # 安全配置
│   │       ├── service/     # 业务逻辑
│   │       └── util/        # 工具类
│   ├── src/main/resources/
│   │   └── application.yml  # 配置文件
│   └── pom.xml              # Maven配置
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── components/      # 组件
│   │   ├── layouts/         # 布局
│   │   ├── router/          # 路由
│   │   ├── stores/          # Pinia状态
│   │   ├── styles/          # 样式
│   │   ├── views/           # 页面视图
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
└── database/
    └── init.sql             # 数据库初始化脚本
```

## 部署说明

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 1. 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE photo_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p photo_management < database/init.sql
```

### 2. 后端部署

1. 修改数据库配置：
编辑 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/photo_management?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

2. 打包运行：
```bash
cd backend
mvn clean package
java -jar target/photo-management-backend-1.0.0.jar
```

后端服务将在 http://localhost:8080 启动

### 3. 前端部署

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 开发模式运行：
```bash
npm run dev
```

前端服务将在 http://localhost:5173 启动

3. 生产环境打包：
```bash
npm run build
```

打包后的文件在 `dist` 目录

### 4. 生产环境部署

#### 使用 Nginx 部署前端

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /uploads {
        proxy_pass http://localhost:8080;
    }
}
```

#### 后端服务守护进程（Systemd）

创建 `/etc/systemd/system/photo-management.service`：

```ini
[Unit]
Description=Photo Management Backend
After=syslog.target

[Service]
User=your_user
ExecStart=/usr/bin/java -jar /path/to/photo-management-backend-1.0.0.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
sudo systemctl enable photo-management
sudo systemctl start photo-management
```

## 默认账号

- 用户名：admin
- 密码：123456

## 配置说明

### 后端配置（application.yml）

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| server.port | 服务端口 | 8080 |
| spring.datasource.url | 数据库连接URL | - |
| spring.datasource.username | 数据库用户名 | root |
| spring.datasource.password | 数据库密码 | root |
| jwt.secret | JWT密钥 | photoManagementSecretKey... |
| jwt.expiration | Token有效期（毫秒） | 86400000 |
| upload.path | 文件上传路径 | ./uploads |
| upload.max-size | 最大文件大小（字节） | 20971520 |

### 前端配置（vite.config.js）

开发环境代理配置：
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## API 接口

### 认证接口
- POST `/api/auth/login` - 登录
- POST `/api/auth/register` - 注册
- GET `/api/auth/me` - 获取当前用户信息

### 相册接口
- GET `/api/albums` - 获取相册列表
- POST `/api/albums` - 创建相册
- PUT `/api/albums/{id}` - 更新相册
- DELETE `/api/albums/{id}` - 删除相册

### 照片接口
- GET `/api/photos` - 获取所有照片（分页）
- GET `/api/photos/album/{albumId}` - 获取相册照片（分页）
- POST `/api/photos/upload` - 上传照片
- PUT `/api/photos/{id}/rename` - 重命名照片
- PUT `/api/photos/{id}/move` - 移动照片
- POST `/api/photos/{id}/copy` - 复制照片
- DELETE `/api/photos/{id}` - 删除照片
- POST `/api/photos/batch-delete` - 批量删除照片

## 安全说明

1. 用户密码使用 BCrypt 加密存储
2. JWT Token 用于身份认证
3. 用户只能访问自己的相册和照片
4. 文件上传限制类型和大小
5. 自动生成缩略图保护原图

## 浏览器支持

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 许可证

MIT License
