# 个人相册管理系统

一个功能完整的个人相册管理系统，采用前后端分离架构开发。

## 项目简介

本系统是一个个人相册管理系统，支持用户注册登录、相册创建、照片上传与管理等功能。系统采用现代化的技术栈，界面美观，操作便捷。

## 主要功能

### 用户管理
- 用户注册与登录
- JWT Token认证
- 密码加密存储（BCrypt）
- 用户信息保护

### 相册管理
- 创建相册
- 编辑相册（名称、描述）
- 删除相册
- 相册列表展示

### 照片管理
- 上传照片（支持批量上传）
- 瀑布流布局展示照片墙
- 照片重命名
- 照片删除（支持批量删除）
- 照片移动到其他相册
- 照片复制到其他相册
- PhotoSwipe图片预览

## 技术栈

### 后端
- **Spring Boot 3.1.5** - 应用框架
- **Spring Security** - 安全认证
- **MyBatis-Plus 3.5.4** - ORM框架
- **MySQL 8.0** - 数据库
- **JWT** - Token认证

### 前端
- **Vue 3** - 前端框架
- **Vite 5** - 构建工具
- **Element Plus 2.4** - UI组件库
- **PhotoSwipe 5.4** - 图片预览
- **Pinia** - 状态管理
- **Vue Router** - 路由管理

## 项目结构

```
photoManagement/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/photo/album/
│   │   │   │   ├── config/    # 配置类
│   │   │   │   ├── controller/# 控制器
│   │   │   │   ├── dto/       # 数据传输对象
│   │   │   │   ├── entity/    # 实体类
│   │   │   │   ├── exception/ # 异常处理
│   │   │   │   ├── mapper/    # MyBatis Mapper
│   │   │   │   ├── security/  # 安全相关
│   │   │   │   ├── service/   # 服务层
│   │   │   │   └── utils/     # 工具类
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── pom.xml
│   └── uploads/               # 上传文件存储目录
├── frontend/                  # 前端项目
│   ├── src/
│   │   ├── api/              # API接口
│   │   ├── assets/           # 静态资源
│   │   ├── components/       # 公共组件
│   │   ├── router/           # 路由配置
│   │   ├── store/            # 状态管理
│   │   ├── utils/            # 工具函数
│   │   ├── views/            # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── database/                  # 数据库
│   └── init.sql              # 数据库初始化脚本
├── DEPLOYMENT.md             # 部署说明文档
└── README.md                 # 项目说明文档
```

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### 数据库配置

1. 创建数据库并导入初始数据：
```bash
mysql -u root -p < database/init.sql
```

2. 修改后端配置文件 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    username: root
    password: your_password  # 修改为你的数据库密码
```

### 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动。

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动。

## 页面说明

登录后系统提供三个主要菜单：

### 1. 全部照片
- 按上传时间倒序分页展示所有照片
- 支持照片的删除、重命名操作
- 支持批量删除
- 瀑布流布局展示

### 2. 我的相册
- 支持创建、编辑、删除相册
- 相册卡片展示
- 点击相册进入相册详情

### 3. 图片管理
- 支持上传照片
- 支持批量上传
- 支持拖拽上传
- 可选择上传到指定相册

## 安全特性

- 密码使用BCrypt加密存储
- JWT Token认证机制
- 文件类型和大小限制
- SQL注入防护
- XSS攻击防护
- CORS跨域配置

## 部署说明

详细的部署说明请查看 [DEPLOYMENT.md](./DEPLOYMENT.md)，包括：
- 开发环境运行
- 生产环境部署
- Docker部署
- Docker Compose一键部署
- 安全配置建议

## 许可证

本项目仅供学习和参考使用。
