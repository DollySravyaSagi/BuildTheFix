# BuildTheFix — Java Spring Boot Edition

**BuildTheFix** is a marketplace platform converting real-world operational bottlenecks submitted by non-technical users into structured technical SaaS blueprints powered by the Google Gemini API, with duplicate detection, developer demo submissions, and paid solution repository access unlocked via Stripe.

---

## Technical Stack & Architecture

- **Language & Runtime:** Java 17
- **Framework:** Spring Boot 3.2.5
- **ORM & Persistence:** Spring Data JPA with PostgreSQL (H2 in-memory DB fallback for test/dev)
- **AI Integration:** Google Gemini API (`gemini-1.5-flash`) via `java.net.http.HttpClient`
- **Payment Gateway:** Stripe Java SDK (`stripe-java` v24.16.0)
- **Build Tool:** Apache Maven 3.9.6
- **Testing:** JUnit 5 and Mockito

---

## Features Preserved & Enhanced

1. **Problem Submissions:** Non-technical Problem Posters submit real-world bottlenecks in plain text.
2. **Gemini AI Reframing:** Google Gemini API analyzes plain-text problems and restructures them into formal SaaS blueprints (`formal_title`, `target_persona`, `tech_stack`, `core_features`, `roadmap`).
3. **Duplicate Detection:** Automatic Token Jaccard & Topic Overlap similarity engine flags duplicate problems and links posters to existing blueprints.
4. **Developer Demos:** Developers claim open problems and submit live deployment demo links.
5. **Stripe Unlocking:** Full access to developer solution source code repositories remains locked until payment is processed through Stripe (test mode).

---

## Getting Started & Local Setup

### Prerequisites
- **Java JDK 17** (or higher)
- **Git**
- Optional: PostgreSQL Database (defaults to H2 in-memory DB out of the box)

---

### Environment Variables

Configure environment variables in your terminal or create a `.env` file / application properties override:

| Environment Variable | Description | Default / Fallback |
| :--- | :--- | :--- |
| `PORT` | HTTP Server Port | `8080` |
| `GEMINI_API_KEY` | Google Gemini API Key | *(Optional — uses smart dynamic synthesizer if unprovided)* |
| `STRIPE_SECRET_KEY` | Stripe Secret Key (`sk_test_...`) | *(Optional — uses test simulation mode if unprovided)* |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC Connection URL | `jdbc:h2:mem:buildthefixdb` |
| `SPRING_DATASOURCE_USERNAME` | Database Username | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database Password | `""` |

---

### Building and Running the Application

1. **Compile the Project:**
   ```bash
   powershell -ExecutionPolicy Bypass -File .\run_maven.ps1 compile
   ```

2. **Run Unit & Integration Tests:**
   ```bash
   powershell -ExecutionPolicy Bypass -File .\run_maven.ps1 test
   ```

3. **Start the Spring Boot Application:**
   ```bash
   powershell -ExecutionPolicy Bypass -File .\run_maven.ps1 spring-boot:run
   ```
   The backend server will start at: `http://localhost:8080`

4. **Access Database Console (H2 Mode):**
   Navigate to `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:buildthefixdb`
   - User Name: `sa`
   - Password: *(leave empty)*

---

## API Endpoints Overview

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register new ProblemPoster or Developer |
| `POST` | `/api/auth/login` | Authenticate user and receive access token |
| `GET` | `/api/auth/me?email=...` | Retrieve user profile details |
| `GET` | `/api/problems` | List problems (supports `status` filter and `search` query) |
| `POST` | `/api/problems` | Submit new problem (triggers Gemini AI & Duplicate Detector) |
| `GET` | `/api/problems/{id}` | Get single problem and reframed blueprint |
| `PATCH` | `/api/problems/{id}/claim` | Developer claims an open problem statement |
| `PATCH` | `/api/problems/{id}/solve` | Developer submits live demo link & sets price |
| `POST` | `/api/payments/create-intent` | Generate Stripe PaymentIntent for solution purchase |
| `PATCH` | `/api/problems/{id}/unlock` | Confirm Stripe payment and unlock repository link |
| `GET` | `/api/stats` | Compute marketplace analytics & MRR |
| `POST` | `/api/seed` | Seed initial demo problems |

---

## Project Documentation
Detailed architectural blueprints, class interactions, and request flows are documented in [`docs/ARCHITECTURE.md`](file:///c:/Users/dolly/.gemini/antigravity-ide/scratch/BuildTheFix/docs/ARCHITECTURE.md).
