# BuildTheFix ⚡

**A Full-Stack AI-Driven Marketplace Connecting Real-World Problems with Developer Solutions**

BuildTheFix is a full-stack, high-fidelity marketplace application designed to bridge the gap between non-technical everyday users facing workflow inefficiencies (demand) and student/entry-level developers looking to build high-impact micro-SaaS products (supply).

---

## 🚀 Key Features

1. **Phase 1: Problem Ingestion (Client Side)**
   - Clients post daily bottlenecks (e.g. tracking orders, messy invoicing, scheduling) using natural, emotional, everyday language.

2. **Phase 2: AI Transformation & Intelligence Engine**
   - **AI Problem Architect**: Parses raw descriptions and compiles a structured Technical Blueprint including suggested stacks, target personas, core features, and MVP roadmaps.
   - **AI Duplicate Detector**: Scans the MongoDB database to flag duplicate descriptions sharing overlapping concepts, protecting high-value project feeds.

3. **Phase 3: Development & State Tracking (Developer Side)**
   - Developers explore active blueprints, filter by status or tags, and click **"Claim Problem"** to transition project state from **Open** to **In Progress**.
   - Developers submit their deployed application URL, changing the status to **Solved**.

4. **Phase 4: Monetization & Resolution (The Value Loop)**
   - Once solved, the deployment link is gated behind a monetization wall.
   - Clients can unlock the live application by subscribing via a **Simulated Stripe Gateway** (complete with card input formatting and validation feedback).
   - Unlocked links reveal a launch CTA, generating real-time MRR logs.

---

## 🛠️ Architecture & Tech Stack

- **Backend**: Node.js & Express.js REST API (`server.js`).
- **Database**: MongoDB with Mongoose ODM (`models/Problem.js`). Supports Local MongoDB and Cloud MongoDB Atlas.
- **Frontend SPA**: Semantic HTML5, pure ES6 JavaScript client (`app.js`).
- **Styling**: Custom Glassmorphism UI in Vanilla CSS (`styles.css`) with animated glow orbs and responsive grid layouts.

---

## 💻 Setup & Run Locally

### 1. Install Dependencies
```bash
npm install
```

### 2. Configure Environment (.env)
Create or edit your `.env` file:
```env
PORT=5000

# For Local MongoDB (Default):
MONGODB_URI=mongodb://127.0.0.1:27017/buildthefix

# OR For MongoDB Atlas Cloud:
# MONGODB_URI=mongodb+srv://<username>:<password>@cluster.mongodb.net/buildthefix?retryWrites=true&w=majority
```

### 3. Start the Server
```bash
# Production start
npm start

# OR Development mode (auto-reload on save)
npm run dev
```

Open your browser and navigate to:
```
http://localhost:5000
```

---

## 📡 REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/health` | Check server and MongoDB connection status |
| `GET` | `/api/stats` | Retrieve platform statistics (Open, In Progress, Solved, MRR) |
| `GET` | `/api/problems` | List problems (supports `?status=Open` and `?search=keyword`) |
| `GET` | `/api/problems/:id` | Get details for a specific problem blueprint |
| `POST` | `/api/problems` | Post a new problem with AI blueprint generation & duplicate analysis |
| `PATCH` | `/api/problems/:id/claim` | Developer claims a problem (`In Progress`) |
| `PATCH` | `/api/problems/:id/solve` | Developer submits solution URL (`Solved`) |
| `PATCH` | `/api/problems/:id/unlock` | Client unlocks solution access via subscription |
| `POST` | `/api/seed` | Seed/reset initial sample problems |

---

## 🐙 Push to GitHub

To commit and push updates to your GitHub repository:
```bash
git add .
git commit -m "feat: integrate full-stack Node.js, Express, and MongoDB backend"
git push origin main
```
