# 个人相册管理系统部署说明

## 项目概述

个人相册管理系统是一个前后端分离的Web应用，提供用户注册登录、相册管理、照片上传和管理功能。

- **前端技术栈**: Vue 3 + Vite + Element Plus + PhotoSwipe
- **后端技术栈**: Spring Boot + Spring Security + MyBatis-Plus
- **数据库**: MySQL 8.0+

## 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

## 部署步骤

### 1. 数据库初始化

1. 登录MySQL数据库：
```bash
mysql -u root -p
```

2. 执行初始化脚本：
```bash
mysql -u root -p < sql/photo_management_init.sql
```

3. 默认用户账号：
   - 用户名: `admin`
   - 密码: `123456`

### 2. 后端部署

1. 修改数据库配置文件 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/photo_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

2. 修改文件上传路径配置：
```yaml
file:
  upload-path: D:/project/photoManagement/uploads/
  access-path: /uploads/
```

3. 编译并运行后端项目：
```bash
cd backend
mvn clean package
java -jar target/photo-management-1.0.0.jar
```

后端服务将在 `http://localhost:8080/api` 启动。

### 3. 前端部署

#### 开发环境

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动。

#### 生产环境

1. 构建生产版本：
```bash
cd frontend
npm run build
```

2. 将 `dist` 目录部署到Nginx或其他Web服务器。

3. Nginx配置示例：
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /uploads {
        proxy_pass http://localhost:8080/api/uploads;
    }
}
```

## 项目结构

```
photoManagement/
├── sql/                    # 数据库脚本
│   └── photo_management_init.sql
├── backend/                # Spring Boot后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/photo/
│   │       │   ├── config/         # 配置类
│   │       │   ├── controller/     # 控制器
│   │       │   ├── dto/            # 数据传输对象
│   │       │   ├── entity/         # 实体类
│   │       │   ├── mapper/         # MyBatis Mapper
│   │       │   ├── security/       # 安全相关
│   │       │   ├── service/        # 业务逻辑
│   │       │   ├── utils/          # 工具类
│   │       │   └── common/         # 公共类
│   │       └── resources/
│   └── pom.xml
├── frontend/               # Vue3前端
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # Pinia状态管理
│   │   └── utils/          # 工具函数
│   ├── package.json
│   └── vite.config.js
└── DEPLOY.md               # 部署说明
```

## 功能说明

### 用户模块
- 用户注册：支持用户名、密码、邮箱、昵称注册
- 用户登录：使用Spring Security进行身份认证，JWT令牌授权
- 密码加密：使用BCrypt加密存储

### 相册管理
- 创建相册
- 编辑相册信息
- 删除相册
- 相册列表展示

### 照片管理
- 多图上传：支持批量上传，限制格式和大小
- 照片展示：网格布局展示，支持点击放大预览
- 照片重命名
- 照片移动：将照片移动到其他相册
- 照片复制：将照片复制到其他相册
- 批量删除：支持多选删除
- 单张删除
- 分页加载

## 安全说明

1. 用户密码使用BCrypt加密存储，保障用户信息安全
2. 使用JWT进行接口认证，防止未授权访问
3. 图片上传进行格式和大小验证，防止恶意文件上传
4. 所有操作都进行用户身份验证，确保数据隐私

## 常见问题

### 1. 跨域问题
后端已配置CORS允许跨域，前端通过Vite代理解决开发环境跨域问题。

### 2. 图片无法访问
请确保 `application.yml` 中的 `upload-path` 目录存在，并且有读写权限。

### 3. 数据库连接失败
- 检查MySQL服务是否启动
- 检查用户名密码是否正确
- 检查数据库是否已创建
