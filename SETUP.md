# 快速启动指南

## 环境要求
- Java 17+
- Node.js 18+
- Maven 3.6+
- MySQL 8.0+ (可选，默认使用H2内存数据库)

## 启动步骤

### 1. 克隆项目
```bash
git clone https://github.com/your-username/ai-love-system.git
cd ai-love-system
```

### 2. 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 3. 启动前端服务
```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

### 4. 访问应用
- 前端应用: http://localhost:3000
- API文档: http://localhost:8080/api/swagger-ui.html

## 配置说明

### 智谱AI配置
在 `backend/src/main/resources/application-dev.yml` 中配置您的API密钥：
```yaml
app:
  ai:
    zhipu:
      api-key: your-zhipu-ai-api-key
```

### 数据库配置
默认使用H2内存数据库，如需使用MySQL，请修改配置文件。

## 常见问题

1. **端口冲突**: 确保8080和3000端口未被占用
2. **API密钥**: 确保智谱AI API密钥配置正确
3. **依赖安装**: 确保Maven和npm依赖安装完成

## 开发模式

项目支持热重载，修改代码后会自动重启服务。
