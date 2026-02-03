# Aura Frontend

> React + TypeScript + Vite frontend application for Aura Quiet Living

## 📋 Tech Stack

- **React 19.2** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS (via CDN)

---

## 🚀 Quick Start

### 1. Install Dependencies

```bash
# 国内用户建议先设置镜像源
npm config set registry https://registry.npmmirror.com

# 安装依赖
npm install
```

### 2. Run Development Server

```bash
npm run dev
```

Frontend will be available at `http://localhost:5173`

### 3. Build for Production

```bash
npm run build
```

---

## 📁 Project Structure

```
aura-frontend/
├── components/           # React components
│   ├── Navbar.tsx
│   ├── Hero.tsx
│   ├── ProductGrid.tsx
│   ├── ProductDetail.tsx
│   ├── Cart.tsx
│   ├── Checkout.tsx
│   └── ...
│
├── services/            # API services
│   └── api.ts          # Backend API calls
│
├── types.ts            # TypeScript type definitions
├── constants.ts        # App constants
├── App.tsx             # Main app component
├── index.tsx           # Entry point
└── vite.config.ts      # Vite configuration
```

---

## 🔌 API Integration

The frontend connects to the backend via `services/api.ts`:

```typescript
import { productAPI, orderAPI, aiAPI } from './services/api';

// Get all products
const products = await productAPI.getAll();

// Create order
const order = await orderAPI.create(orderData);

// Chat with AI
const response = await aiAPI.chat(message, sessionId);
```

---

## 🎯 Key Features

- **Product Catalog** - Browse and search products
- **Shopping Cart** - Add/remove items
- **Checkout** - Create orders (mock payment)
- **AI Assistant** - Chat with AI for product recommendations

---

## 🛠️ Development

### Environment Variables

Create `.env.local`:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

### Available Scripts

- `npm run dev` - Start dev server
- `npm run build` - Build for production
- `npm run preview` - Preview production build

---

## 📝 Notes

- Product data is fetched from backend API (not hardcoded)
- AI chat requires backend to be running
- Mock payment always succeeds

---

**For backend setup, see [../aura-backend/README.md](../aura-backend/README.md)**
