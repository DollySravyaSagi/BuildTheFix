require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const path = require('path');
const Problem = require('./models/Problem');

const app = express();
const PORT = process.env.PORT || 5000;
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://127.0.0.1:27017/buildthefix';

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Serve frontend static assets
app.use(express.static(path.join(__dirname)));

/* ================= DEFAULT SEED DATA ================= */
const DEFAULT_PROBLEMS = [
  {
    client_name: "Sarah Jenkins",
    client_email: "sarah@sweetdelights.com",
    raw_title: "WhatsApp Bakery Orders",
    raw_description: "I run a custom cake shop. Customers send me order details, reference pictures, and text requests on WhatsApp. I manually write them down on sticky notes and draw order calendar entries on a whiteboard. I constantly lose notes or misread handwriting, causing custom text spelling mistakes on cakes, and late deliveries.",
    status: "Solved",
    developer_name: "Leo Vance",
    developer_github: "leodev",
    solution_url: "https://sweetorders-bakery.vercel.app",
    is_unlocked: false,
    blueprint: {
      formal_title: "SaaS Order Calendar & Recipe Tracker",
      target_persona: "Boutique Bakery Owners & Custom Cake Designers",
      tech_stack: ["React.js", "Node.js", "Express", "MongoDB", "Cloudinary (Images)"],
      core_features: [
        "Interactive Order Calendar: Visual tracking of booking limits per day.",
        "Visual Order Builder: Form mapping cake size, flavors, text inscriptions, and reference image uploads.",
        "Automated WhatsApp Confirmation: Generate templated receipts to send back to clients for confirmation."
      ],
      roadmap: [
        "Phase 1: Setup React SPA with drag-and-drop calendar UI.",
        "Phase 2: Add MongoDB schema mapping orders with image references.",
        "Phase 3: Integrate Cloudinary for reference photo uploads.",
        "Phase 4: Design shareable order confirmation pages."
      ],
      duplicate_check: {
        is_duplicate: false,
        similar_problem_id: null,
        similarity_reason: ""
      }
    }
  },
  {
    client_name: "Marcus Brodie",
    client_email: "marcus@brodiebuilt.com",
    raw_title: "Losing track of contractor timesheets",
    raw_description: "I manage a small home renovation crew. My subcontractors text me their daily hours, or tell me in person, and I compile them into a master Excel file at the end of the month. I constantly misplace texts, forget oral reports, and contractors argue over hours, causing invoice delays and trust issues.",
    status: "In Progress",
    developer_name: "Elena Rostova",
    developer_github: "elenarost",
    solution_url: null,
    is_unlocked: false,
    blueprint: {
      formal_title: "Micro-SaaS Billable Hours Tracker",
      target_persona: "Construction Foremen & Crew Subcontractors",
      tech_stack: ["Vite + React", "Firebase Auth & Firestore", "Tailwind CSS"],
      core_features: [
        "One-Tap Clock-in/Clock-out: GPS-stamped start and end times for renovation jobs.",
        "Real-Time Coordinator Board: Dashboard for foreman to view live crew logins.",
        "Dispute-Free PDF Invoicing: Export timesheets signed digitally at end of shift."
      ],
      roadmap: [
        "Phase 1: Build mobile-friendly shift punch cards.",
        "Phase 2: Implement contractor role-based authentication.",
        "Phase 3: Connect PDF export engine for payroll approval."
      ],
      duplicate_check: {
        is_duplicate: false,
        similar_problem_id: null,
        similarity_reason: ""
      }
    }
  },
  {
    client_name: "Dr. Amanda Zhao",
    client_email: "amanda@veterinarycare.org",
    raw_title: "Manual Patient Appointment Reminders & Pet Med Schedules",
    raw_description: "Our veterinary clinic staff spends 3 hours every morning calling pet owners to confirm appointments and remind them about post-surgery medication schedules. Patients frequently forget medicine timings, and no-shows waste our surgeon's time.",
    status: "Open",
    developer_name: null,
    developer_github: null,
    solution_url: null,
    is_unlocked: false,
    blueprint: {
      formal_title: "Automated Pet Care & SMS Appointment Cadence Engine",
      target_persona: "Veterinary Clinics, Animal Hospitals & Pet Parents",
      tech_stack: ["Next.js", "Node.js", "Twilio API", "MongoDB Atlas"],
      core_features: [
        "Two-Way SMS Confirmations: Automated text alerts that update calendar on reply.",
        "Medication Regimen Portal: Timed reminders sent to pet parents with dosage instructions.",
        "No-Show Predictive Scoring: Flags high-risk appointment slots for staff follow-up."
      ],
      roadmap: [
        "Phase 1: Build schedule calendar sync module.",
        "Phase 2: Integrate Twilio Programmable Messaging webhooks.",
        "Phase 3: Create prescription reminder timeline widget."
      ],
      duplicate_check: {
        is_duplicate: false,
        similar_problem_id: null,
        similarity_reason: ""
      }
    }
  }
];

/* ================= AI BLUEPRINT GENERATOR ================= */
const AI_BLUEPRINTS = {
  invoice: {
    title: "AI-Powered Invoice Tracker & OCR Parser",
    persona: "Freelancers, Contractors & Boutique Agencies",
    stack: ["React.js", "Tesseract.js (OCR)", "Node.js Express", "MongoDB"],
    features: [
      "Mobile Receipt Snap: Upload receipt photos directly from mobile devices.",
      "OCR Content Parsing: Instantly read total sums, tax entries, and vendor names using on-device OCR.",
      "Categorized Expense Logs: Automatic tagging into Tax Deductible buckets.",
      "Quick Export: Excel / PDF report compiler for tax filing seasons."
    ],
    roadmap: [
      "Phase 1: Implement canvas upload flow with Tesseract.js image-to-text parsers.",
      "Phase 2: Write regex filters to isolate currency metrics and vendor identifiers.",
      "Phase 3: Construct local expense ledger databases.",
      "Phase 4: Design PDF summary generators."
    ]
  },
  cake: {
    title: "SaaS Order Calendar & Recipe Tracker",
    persona: "Small-batch Bakers & Custom Confectioners",
    stack: ["Next.js", "Tailwind CSS", "MongoDB", "Resend (Email APIs)"],
    features: [
      "Visual Order Builder: Form specifying size, shapes, flavors, toppings, and photo references.",
      "Client Dashboard: Portal for clients to track baking progress (Staged: Received, Mixing, Baked, Decorating).",
      "Interactive Delivery Calendar: Block out date slots once daily capacity is reached.",
      "Automated Email Alerts: Confirm receipt details to reduce misspellings."
    ],
    roadmap: [
      "Phase 1: Construct the multi-step order booking funnel with reference uploads.",
      "Phase 2: Integrate calendar capacity blocking parameters.",
      "Phase 3: Configure Resend trigger templates for receipt verification.",
      "Phase 4: Setup real-time order state indicators."
    ]
  },
  timesheet: {
    title: "Micro-SaaS Billable Hours & Shift Tracker",
    persona: "Construction Foremen & Crew Subcontractors",
    stack: ["Vite + React", "Node.js + Express", "MongoDB", "Tailwind CSS"],
    features: [
      "One-Tap Clock-in/Clock-out: GPS-stamped start and end times for renovation jobs.",
      "Real-Time Coordinator Board: Dashboard for foreman to view live crew logins.",
      "Dispute-Free PDF Invoicing: Export timesheets signed digitally at end of shift."
    ],
    roadmap: [
      "Phase 1: Build mobile-friendly shift punch cards.",
      "Phase 2: Implement contractor role-based authentication.",
      "Phase 3: Connect PDF export engine for payroll approval."
    ]
  },
  booking: {
    title: "Automated Booking & Client Reminder System",
    persona: "Solo Service Providers, Consultants & Clinics",
    stack: ["React", "Express.js", "MongoDB", "Twilio / SendGrid"],
    features: [
      "Self-Serve Appointment Funnel: Client selects service, date, and inputs details.",
      "Automated WhatsApp & Email Notifications: Instant confirmations and reminder intervals.",
      "Deposit Collection Gate: Collect initial prepayments before confirming schedule.",
      "Client History CRM: Log past visits, notes, and preferences."
    ],
    roadmap: [
      "Phase 1: Build booking calendar widget with availability settings.",
      "Phase 2: Connect notification triggers with messaging APIs.",
      "Phase 3: Add deposit gateway support.",
      "Phase 4: Construct customer relationship database."
    ]
  },
  inventory: {
    title: "Barcode Inventory & Reorder Intelligence Hub",
    persona: "Independent Retailers & Warehouse Managers",
    stack: ["Next.js", "Node.js", "MongoDB", "HTML5 QR/Barcode Scanner"],
    features: [
      "Mobile Barcode Scan: Scan barcodes using device camera to increment or decrement stock.",
      "Low Stock Threshold Alerts: Automatic alerts when inventory dips below minimum levels.",
      "Supplier Purchase Order Generator: 1-click PO email drafts for low stock items.",
      "Stock Valuation Analytics: Real-time inventory value and turnover rate."
    ],
    roadmap: [
      "Phase 1: Implement web camera barcode scanning listener.",
      "Phase 2: Design stock quantity thresholds and automated email triggers.",
      "Phase 3: Build vendor catalog mapping tables.",
      "Phase 4: Construct turnover analytics dashboard."
    ]
  },
  default: {
    title: "Automated Workflow Manager & Domain Tracker",
    persona: "Small Business Operators & Digital Teams",
    stack: ["Vite + React", "Node.js + Express", "MongoDB", "Vanilla CSS"],
    features: [
      "Custom Workflow Boards: Drag-and-drop boards to map project pipelines.",
      "Database Tables: Flexible grid entries to track invoices, items, or records.",
      "System Activity Logs: Records changes made to documents.",
      "Notifications Centre: Alert teammates when records change states."
    ],
    roadmap: [
      "Phase 1: Assemble layout cards with drag-and-drop mechanics.",
      "Phase 2: Write flexible column model databases.",
      "Phase 3: Implement internal notification dispatchers."
    ]
  }
};

function generateAIBlueprint(title, description, existingProblems = []) {
  const combined = (title + ' ' + description).toLowerCase();

  let matchedConfig = AI_BLUEPRINTS.default;
  if (combined.includes('invoice') || combined.includes('receipt') || combined.includes('expense') || combined.includes('bill')) {
    matchedConfig = AI_BLUEPRINTS.invoice;
  } else if (combined.includes('cake') || combined.includes('bakery') || combined.includes('food') || combined.includes('order')) {
    matchedConfig = AI_BLUEPRINTS.cake;
  } else if (combined.includes('timesheet') || combined.includes('contractor') || combined.includes('hours') || combined.includes('clock')) {
    matchedConfig = AI_BLUEPRINTS.timesheet;
  } else if (combined.includes('book') || combined.includes('appointment') || combined.includes('reminder') || combined.includes('patient')) {
    matchedConfig = AI_BLUEPRINTS.booking;
  } else if (combined.includes('inventory') || combined.includes('stock') || combined.includes('barcode') || combined.includes('warehouse')) {
    matchedConfig = AI_BLUEPRINTS.inventory;
  }

  // Duplicate Check against existing database
  let isDuplicate = false;
  let similarId = null;
  let similarityReason = "";

  for (const item of existingProblems) {
    const itemText = (item.raw_title + ' ' + item.raw_description).toLowerCase();
    const commonKeywords = ['invoice', 'receipt', 'bakery', 'cake', 'timesheet', 'contractor', 'appointment', 'inventory', 'stock'];
    for (const kw of commonKeywords) {
      if (combined.includes(kw) && itemText.includes(kw)) {
        isDuplicate = true;
        similarId = item.id || item._id.toString();
        similarityReason = `High concept overlap detected with problem: "${item.raw_title}" (Keywords: ${kw}).`;
        break;
      }
    }
    if (isDuplicate) break;
  }

  return {
    formal_title: matchedConfig.title,
    target_persona: matchedConfig.persona,
    tech_stack: matchedConfig.stack,
    core_features: matchedConfig.features,
    roadmap: matchedConfig.roadmap,
    duplicate_check: {
      is_duplicate: isDuplicate,
      similar_problem_id: similarId,
      similarity_reason: similarityReason
    }
  };
}

/* ================= REST API ROUTES ================= */

// Health & Status
app.get('/api/health', (req, res) => {
  const isConnected = mongoose.connection.readyState === 1;
  res.json({
    status: 'ok',
    database: isConnected ? 'connected' : 'disconnected',
    database_host: isConnected ? mongoose.connection.host : null,
    uptime_seconds: process.uptime()
  });
});

// Platform Statistics
app.get('/api/stats', async (req, res) => {
  try {
    const total = await Problem.countDocuments();
    const solved = await Problem.countDocuments({ status: 'Solved' });
    const inProgress = await Problem.countDocuments({ status: 'In Progress' });
    const open = await Problem.countDocuments({ status: 'Open' });
    const unlocked = await Problem.countDocuments({ is_unlocked: true });

    // Calculate simulated MRR ($49 / month per solved solution)
    const mrr = solved * 49;

    res.json({
      total_problems: total,
      solved_problems: solved,
      in_progress: inProgress,
      open_problems: open,
      unlocked_count: unlocked,
      total_mrr: mrr
    });
  } catch (error) {
    res.status(500).json({ error: 'Failed to compute platform stats', details: error.message });
  }
});

// Get Problems (supports status filter and search query)
app.get('/api/problems', async (req, res) => {
  try {
    const { status, search } = req.query;
    let query = {};

    if (status && status !== 'all') {
      query.status = status;
    }

    if (search) {
      const searchRegex = new RegExp(search, 'i');
      query.$or = [
        { raw_title: searchRegex },
        { raw_description: searchRegex },
        { 'blueprint.formal_title': searchRegex },
        { 'blueprint.target_persona': searchRegex }
      ];
    }

    const problems = await Problem.find(query).sort({ createdAt: -1 });
    res.json(problems);
  } catch (error) {
    res.status(500).json({ error: 'Failed to retrieve problems', details: error.message });
  }
});

// Get Single Problem by ID
app.get('/api/problems/:id', async (req, res) => {
  try {
    const problem = await Problem.findById(req.params.id);
    if (!problem) {
      return res.status(404).json({ error: 'Problem not found' });
    }
    res.json(problem);
  } catch (error) {
    res.status(500).json({ error: 'Failed to retrieve problem', details: error.message });
  }
});

// Create New Problem (AI Blueprint Transformation & Duplicate Check)
app.post('/api/problems', async (req, res) => {
  try {
    const { client_name, client_email, raw_title, raw_description } = req.body;

    if (!client_name || !client_email || !raw_title || !raw_description) {
      return res.status(400).json({ error: 'All fields are required' });
    }

    // Retrieve recent problems to check for duplicates
    const recentProblems = await Problem.find({}).limit(50);
    const blueprint = generateAIBlueprint(raw_title, raw_description, recentProblems);

    const newProblem = new Problem({
      client_name,
      client_email,
      raw_title,
      raw_description,
      status: 'Open',
      blueprint
    });

    const savedProblem = await newProblem.save();
    res.status(201).json(savedProblem);
  } catch (error) {
    res.status(500).json({ error: 'Failed to create problem', details: error.message });
  }
});

// Claim Problem (Developer Action)
app.patch('/api/problems/:id/claim', async (req, res) => {
  try {
    const { developer_name, developer_github } = req.body;
    if (!developer_name || !developer_github) {
      return res.status(400).json({ error: 'Developer name and GitHub username are required' });
    }

    const problem = await Problem.findById(req.params.id);
    if (!problem) {
      return res.status(404).json({ error: 'Problem not found' });
    }

    if (problem.status !== 'Open') {
      return res.status(400).json({ error: `Cannot claim a problem that is already ${problem.status}` });
    }

    problem.status = 'In Progress';
    problem.developer_name = developer_name;
    problem.developer_github = developer_github;

    const updated = await problem.save();
    res.json(updated);
  } catch (error) {
    res.status(500).json({ error: 'Failed to claim problem', details: error.message });
  }
});

// Submit Solution (Developer Action)
app.patch('/api/problems/:id/solve', async (req, res) => {
  try {
    const { solution_url } = req.body;
    if (!solution_url) {
      return res.status(400).json({ error: 'Solution deployment URL is required' });
    }

    const problem = await Problem.findById(req.params.id);
    if (!problem) {
      return res.status(404).json({ error: 'Problem not found' });
    }

    problem.status = 'Solved';
    problem.solution_url = solution_url;

    const updated = await problem.save();
    res.json(updated);
  } catch (error) {
    res.status(500).json({ error: 'Failed to mark problem as solved', details: error.message });
  }
});

// Unlock Solution / Simulate Stripe Subscription
app.patch('/api/problems/:id/unlock', async (req, res) => {
  try {
    const { user_email } = req.body;
    const problem = await Problem.findById(req.params.id);
    if (!problem) {
      return res.status(404).json({ error: 'Problem not found' });
    }

    problem.is_unlocked = true;
    if (user_email && !problem.unlocked_by.includes(user_email)) {
      problem.unlocked_by.push(user_email);
    }

    const updated = await problem.save();
    res.json(updated);
  } catch (error) {
    res.status(500).json({ error: 'Failed to unlock solution', details: error.message });
  }
});

// Seed Initial Data Endpoint
app.post('/api/seed', async (req, res) => {
  try {
    await Problem.deleteMany({});
    const inserted = await Problem.insertMany(DEFAULT_PROBLEMS);
    res.json({ message: 'Database seeded successfully', count: inserted.length, items: inserted });
  } catch (error) {
    res.status(500).json({ error: 'Failed to seed database', details: error.message });
  }
});

// Catch-all route to serve SPA
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

/* ================= DATABASE INITIALIZATION & SERVER START ================= */
async function startServer() {
  app.listen(PORT, () => {
    console.log(`🚀 BuildTheFix Full-Stack Server running at: http://localhost:${PORT}`);
  });

  try {
    console.log(`📡 Connecting to MongoDB at: ${MONGODB_URI}`);
    await mongoose.connect(MONGODB_URI, {
      serverSelectionTimeoutMS: 3000
    });
    console.log('✅ Connected to MongoDB successfully.');

    // Check if initial problems exist; if empty, seed defaults
    const count = await Problem.countDocuments();
    if (count === 0) {
      console.log('🌱 Database is empty. Seeding initial demo problems...');
      await Problem.insertMany(DEFAULT_PROBLEMS);
      console.log('✅ Database seeded with default problems.');
    } else {
      console.log(`📊 Found ${count} existing problems in database.`);
    }
  } catch (err) {
    console.error('⚠️ MongoDB Notice:', err.message);
    console.log('💡 Tip: Make sure MongoDB is running locally (mongodb://127.0.0.1:27017) or set MONGODB_URI in .env to your MongoDB Atlas connection string.');
  }
}

startServer();
