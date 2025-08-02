# AI恋爱系统

基于智谱AI的智能对话系统，支持自定义AI角色和情感分析。

## 🌟 功能特性

- 🔐 **用户认证系统** - 安全的注册登录机制
- 🤖 **AI角色管理** - 创建和定制个性化AI角色
- 💬 **智能对话** - 基于智谱AI的自然语言交互
- 😊 **情感分析** - 实时分析对话情感状态
- 📚 **对话历史** - 完整的对话记录和管理
- 📊 **数据统计** - 详细的使用统计和分析

## 🛠 技术栈

### 后端技术
- **Spring Boot 3.2** - 现代化Java应用框架
- **Spring Security** - 安全认证和授权
- **Spring Data JPA** - 数据持久化
- **JWT** - 无状态身份验证
- **智谱AI API** - 大语言模型集成
- **H2/MySQL** - 数据库支持
- **Maven** - 项目构建管理

### 前端技术
- **Vue 3** - 渐进式JavaScript框架
- **TypeScript** - 类型安全的JavaScript
- **Vite** - 快速构建工具
- **Ant Design Vue** - 企业级UI组件库
- **Pinia** - 现代状态管理
- **Vue Router** - 单页应用路由

## 🚀 快速开始

### 环境要求
- ☕ Java 17+
- 📦 Node.js 18+
- 🔧 Maven 3.6+

### 启动步骤

#### 后端服务
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 前端服务
```bash
cd frontend
npm install
npm run dev
```

### 访问应用
- 🌐 **前端应用**: http://localhost:3000
- 🔧 **后端API**: http://localhost:8080/api
- 📖 **API文档**: http://localhost:8080/api/swagger-ui.html
- 🗄️ **数据库控制台**: http://localhost:8080/api/h2-console

## ⚙️ 配置说明

### 智谱AI配置
在 `backend/src/main/resources/application.yml` 中配置：
```yaml
app:
  ai:
    zhipu:
      api-key: your-zhipu-ai-api-key
      model: glm-4-flash
      max-tokens: 2048
      temperature: 0.7
```

### 数据库配置
开发环境默认使用H2内存数据库，生产环境建议使用MySQL：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_love_system
    username: your-username
    password: your-password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### JWT配置
```yaml
app:
  jwt:
    secret: your-jwt-secret-key
    expiration: 86400000  # 24小时
```

## 📁 项目结构

```
ai-love-system/
├── 📂 backend/                    # Spring Boot后端
│   ├── 📂 src/main/java/com/ai/love/
│   │   ├── 📂 controller/         # REST控制器
│   │   ├── 📂 service/           # 业务逻辑层
│   │   ├── 📂 repository/        # 数据访问层
│   │   ├── 📂 entity/            # JPA实体类
│   │   ├── 📂 dto/               # 数据传输对象
│   │   ├── 📂 config/            # 配置类
│   │   ├── 📂 security/          # 安全相关
│   │   ├── 📂 util/              # 工具类
│   │   └── 📂 exception/         # 异常处理
│   ├── 📂 src/main/resources/
│   │   ├── 📄 application.yml    # 应用配置
│   │   └── 📄 application-dev.yml # 开发环境配置

├── 📂 frontend/                   # Vue3前端
│   ├── 📂 src/
│   │   ├── 📂 views/             # 页面组件
│   │   ├── 📂 components/        # 通用组件
│   │   ├── 📂 api/               # API接口封装
│   │   ├── 📂 stores/            # Pinia状态管理
│   │   ├── 📂 types/             # TypeScript类型
│   │   ├── 📂 utils/             # 工具函数
│   │   └── 📂 assets/            # 静态资源
│   ├── 📄 package.json           # 依赖配置
│   └── 📄 vite.config.ts         # Vite配置
├── 📂 database/                  # 数据库脚本
├── � issues/                    # 开发计划和问题
└── 📄 README.md                  # 项目说明
```

## 🧪 测试

### 运行测试
```bash
# 后端测试
cd backend
mvn test

# 前端测试
cd frontend
npm run test
```

## 📚 API文档

### 认证相关
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/auth/register` | 用户注册 |
| POST | `/auth/login` | 用户登录 |
| GET | `/auth/profile` | 获取用户信息 |
| PUT | `/auth/profile` | 更新用户信息 |
| PUT | `/auth/password` | 修改密码 |

### 角色管理
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/characters` | 创建AI角色 |
| GET | `/characters` | 获取角色列表 |
| GET | `/characters/{id}` | 获取角色详情 |
| PUT | `/characters/{id}` | 更新角色 |
| DELETE | `/characters/{id}` | 删除角色 |
| POST | `/characters/{id}/test` | 测试角色 |
| POST | `/characters/{id}/clone` | 复制角色 |

### 对话交互
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/chat/conversations` | 创建对话 |
| POST | `/chat/conversations/{id}/messages` | 发送消息 |
| GET | `/chat/conversations/{id}/messages` | 获取消息列表 |
| GET | `/conversations` | 获取对话列表 |
| GET | `/conversations/{id}` | 获取对话详情 |
| PUT | `/conversations/{id}` | 更新对话 |
| DELETE | `/conversations/{id}` | 删除对话 |

### 情感分析
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/emotions/analyze/{messageId}` | 分析消息情感 |
| GET | `/emotions/conversations/{id}` | 获取对话情感 |
| GET | `/emotions/trends` | 获取情感趋势 |
| GET | `/emotions/stats` | 获取情感统计 |

## 🚀 部署指南

### Docker部署
```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 生产环境部署
1. **数据库配置**
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://your-db-host:3306/ai_love_system
       username: ${DB_USERNAME}
       password: ${DB_PASSWORD}
   ```

2. **环境变量设置**
   ```bash
   export ZHIPU_AI_API_KEY=your-api-key
   export JWT_SECRET=your-jwt-secret
   export DB_USERNAME=your-db-username
   export DB_PASSWORD=your-db-password
   ```

3. **构建生产版本**
   ```bash
   # 后端
   cd backend
   mvn clean package -Pprod

   # 前端
   cd frontend
   npm run build
   ```

4. **启动生产服务**
   ```bash
   java -jar backend/target/ai-love-system-1.0.0.jar --spring.profiles.active=prod
   ```

## 🔧 开发指南

### 代码规范
- 后端遵循阿里巴巴Java开发手册
- 前端遵循Vue官方风格指南
- 使用ESLint和Prettier进行代码格式化

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 分支管理
- `main` - 主分支，用于生产环境
- `develop` - 开发分支，用于集成测试
- `feature/*` - 功能分支
- `hotfix/*` - 热修复分支

## 🐛 常见问题

### Q: 启动时提示端口被占用
A: 检查8080和3000端口是否被其他程序占用，或修改配置文件中的端口设置。

### Q: 智谱AI API调用失败
A: 检查API密钥是否正确配置，网络是否正常，API额度是否充足。

### Q: 数据库连接失败
A: 检查数据库配置是否正确，数据库服务是否启动。

### Q: 前端页面空白
A: 检查控制台错误信息，确认后端服务是否正常启动。

## 🤝 贡献指南

1. **Fork项目** - 点击右上角Fork按钮
2. **创建分支** - `git checkout -b feature/your-feature`
3. **提交更改** - `git commit -am 'Add some feature'`
4. **推送分支** - `git push origin feature/your-feature`
5. **创建PR** - 在GitHub上创建Pull Request

### 贡献者
感谢所有为项目做出贡献的开发者！

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源协议。

## 📞 联系方式

- 📧 **邮箱**: support@ai-love-system.com
- 🐛 **问题反馈**: [GitHub Issues](https://github.com/your-repo/ai-love-system/issues)
- 💬 **讨论交流**: [GitHub Discussions](https://github.com/your-repo/ai-love-system/discussions)

---

⭐ 如果这个项目对你有帮助，请给我们一个Star！
