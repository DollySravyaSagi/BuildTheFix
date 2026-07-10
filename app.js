/* ================= APPLICATION STATE ================= */
let problems = [];
let activeTab = 'home';
let currentBlueprintId = null;

// Mock database defaults to populate the platform with realistic samples
const DEFAULT_PROBLEMS = [
  {
    id: "mock-1",
    client_name: "Sarah Jenkins",
    client_email: "sarah@sweetdelights.com",
    raw_title: " WhatsApp Bakery Orders",
    raw_description: "I run a custom cake shop. Customers send me order details, reference pictures, and text requests on WhatsApp. I manually write them down on sticky notes and draw order calendar entries on a whiteboard. I constantly lose notes or misread handwriting, causing custom text spelling mistakes on cakes, and late deliveries.",
    status: "Solved",
    developer_name: "Leo Vance",
    developer_github: "leodev",
    solution_url: "https://sweetorders-bakery.vercel.app",
    is_unlocked: false,
    created_at: "July 08, 2026",
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
    id: "mock-2",
    client_name: "Marcus Brodie",
    client_email: "marcus@brodiebuilt.com",
    raw_title: "Losing track of contractor timesheets",
    raw_description: "I manage a small home renovation crew. My subcontractors text me their daily hours, or tell me in person, and I compile them into a master Excel file at the end of the month. I constantly misplace texts, forget oral reports, and contractors argue over hours, causing invoice delays and trust issues.",
    status: "In Progress",
    developer_name: "Elena Rostova",
    developer_github: "elenarost",
    solution_url: null,
    is_unlocked: false,
    created_at: "July 09, 2026",
    blueprint: {
      formal_title: "Micro-SaaS Billable Hours Tracker",
      target_persona: "Construction Foremen & Crew Subcontractors",
      tech_stack: ["Vite + React", "Firebase Auth & Firestore", "Tailwind CSS"],
      core_features: [
        "One-Tap Clock-in/Clock-out: GPS-stamped start and end times for renovation jobs.",
        "Real-Time Coordinator Board: Dashboard for foreman to view live crew logins.",
        "Excel/CSV Monthly Export: Automatic calculations and report generation."
      ],
      roadmap: [
        "Phase 1: Configure Firebase backend and Firestore tables.",
        "Phase 2: Construct responsive subcontractor clock-in UI with Geolocation API.",
        "Phase 3: Build supervisor audit interface to edit or approve hours.",
        "Phase 4: Setup CSV parsing engines for exports."
      ],
      duplicate_check: {
        is_duplicate: false,
        similar_problem_id: null,
        similarity_reason: ""
      }
    }
  },
  {
    id: "mock-3",
    client_name: "Diana Prince",
    client_email: "diana@antiquities.org",
    raw_title: "Ancient artifacts library inventory tagging",
    raw_description: "We catalog historical artifacts. The museum staff write tag details on cards. When tags fall off, we don't know where the artifact belongs, or what box it corresponds to. We need a simple barcode/QR inventory checklist system to instantly check box contents using a phone camera.",
    status: "Open",
    developer_name: null,
    developer_github: null,
    solution_url: null,
    is_unlocked: false,
    created_at: "July 10, 2026",
    blueprint: {
      formal_title: "Real-time Stock Alert & Inventory Planner",
      target_persona: "Museum Curators & Archive Registrars",
      tech_stack: ["HTML5 QR Scanner", "Vanilla JS", "PocketBase Database"],
      core_features: [
        "Camera QR scanner widget: Mobile web camera QR scanning without app installs.",
        "Box Database: Searchable catalog representing box-to-artifact relationships.",
        "Missing Tag Recovery Tool: Visual list of unidentified items with pictures to re-match."
      ],
      roadmap: [
        "Phase 1: Setup HTML5 QR reading library components.",
        "Phase 2: Construct inventory database matching boxes to items.",
        "Phase 3: Design mobile-responsive UI suited for tablet/handheld browser use."
      ],
      duplicate_check: {
        is_duplicate: false,
        similar_problem_id: null,
        similarity_reason: ""
      }
    }
  }
];

/* ================= SIMULATED AI CONFIGS ================= */
const AI_BLUEPRINTS = {
  invoice: {
    title: "AI-Powered Invoice Tracker & OCR Parser",
    persona: "Freelancers, Contractors & Boutique Agencies",
    stack: ["React.js", "Tesseract.js (OCR)", "Node.js Express", "SQLite"],
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
    stack: ["Next.js", "Tailwind CSS", "Supabase", "Resend (Email APIs)"],
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
  client: {
    title: "Sleek Micro-CRM for Independent Contractors",
    persona: "Independent Plumbers, Electricians, Painters & Cleaners",
    stack: ["HTML5", "Vanilla CSS & JS", "SQLite (Backend)", "Chart.js"],
    features: [
      "Contact Cards: Store client address, phone logs, and historical jobs details.",
      "In-field Quotes Creator: Estimate costs, select standard labor fees, and email direct estimates on the spot.",
      "Job Cards Dashboard: Track active jobs, invoice values, and pending follow-ups.",
      "Simple Analytics: Visual reports of weekly revenue streams."
    ],
    roadmap: [
      "Phase 1: Setup relational database mapping clients, jobs, and invoices.",
      "Phase 2: Assemble estimate sheet builder with local draft saving capabilities.",
      "Phase 3: Write client mailing modules for fast quoting.",
      "Phase 4: Render revenue graphs via Chart.js integration."
    ]
  },
  inventory: {
    title: "Real-time Stock Alert & Inventory Planner",
    persona: "Small Warehouses & Retail Shop Managers",
    stack: ["Vite + React", "Express.js", "MongoDB", "Twilio (SMS Alerts)"],
    features: [
      "Bar/QR Code Scanner: Fast scanner using integrated webcams.",
      "Minimum Level Warnings: Configure alerts when items hit reorder numbers.",
      "Supplier Order Planners: Auto-generate order request drafts.",
      "Reconciliation logs: Audit trail to track who added or subtracted stock."
    ],
    roadmap: [
      "Phase 1: Implement client side camera scanner integrations.",
      "Phase 2: Build MongoDB collections for items, stocks, and logs.",
      "Phase 3: Integrate SMS alert triggers using Twilio endpoints.",
      "Phase 4: Build restocking proposal modules."
    ]
  },
  schedule: {
    title: "Frictionless Appointment Scheduler for Creators",
    persona: "Online Tutors, Consultants, Coachings & Creatives",
    stack: ["SvelteKit", "Node.js", "PostgreSQL", "Google Calendar API"],
    features: [
      "Public Booking Board: Shareable link listing slots open to client selection.",
      "Google Calendar sync: Live check of existing event blocks to avoid overlaps.",
      "Meeting Link integration: Automatically create Google Meet/Zoom channels.",
      "SMS Reminders: Twilio scheduling to prompt users 1 hour before slots."
    ],
    roadmap: [
      "Phase 1: Authenticate Google OAuth and map calendar sync parameters.",
      "Phase 2: Build public calendar slotting widgets.",
      "Phase 3: Integrate API connections to Zoom/Meet links.",
      "Phase 4: Construct reminder Cron workflows."
    ]
  },
  time: {
    title: "Micro-SaaS Billable Hours Tracker",
    persona: "Freelance Developers, Designers & Writers",
    stack: ["React.js", "Express", "MongoDB", "PDFKit (Invoicing)"],
    features: [
      "Floating Timer Widget: Easily play/pause clock from a floating toolbar.",
      "Project Breakdown logs: Associate hours with specific projects or tasks.",
      "Instant Invoice Compiler: Turn logged hours into professional invoices with one click.",
      "Idle Time alerts: Browser triggers if timer runs but no keyboard/mouse hits occur."
    ],
    roadmap: [
      "Phase 1: Setup React local clock state and local cache syncs.",
      "Phase 2: Develop task selector widgets and database integrations.",
      "Phase 3: Construct billing invoice generation modules.",
      "Phase 4: Write idle tracking scripts using window event loops."
    ]
  },
  default: {
    title: "Automated Workflow Manager & Domain Tracker",
    persona: "Small Business Operators & Digital Teams",
    stack: ["Vite + React", "Supabase Database & Storage", "Vanilla CSS"],
    features: [
      "Custom Workflow Boards: Drag-and-drop boards to maps project pipelines.",
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

/* ================= INITIALIZATION ================= */
document.addEventListener('DOMContentLoaded', () => {
  // Load from local storage, fallback to defaults if empty
  const stored = localStorage.getItem('btf_problems');
  if (stored) {
    problems = JSON.parse(stored);
  } else {
    problems = [...DEFAULT_PROBLEMS];
    localStorage.setItem('btf_problems', JSON.stringify(problems));
  }

  // Initial render
  updateStatistics();
  renderClientFeed();
  renderDeveloperFeed();

  // Setup DOM Event Listeners
  setupNavigation();
  setupFormHandlers();
  setupCardInteractions();
  setupModals();
  setupCardFormatting();
});

/* ================= ROUTING & NAVIGATION ================= */
function setupNavigation() {
  const navHome = document.getElementById('btn-nav-home');
  const navClient = document.getElementById('btn-nav-client');
  const navDev = document.getElementById('btn-nav-dev');

  const ctaClient = document.getElementById('btn-cta-client');
  const ctaDev = document.getElementById('btn-cta-dev');
  const logo = document.getElementById('nav-logo');

  const tabs = [
    { btn: navHome, section: document.getElementById('sec-home'), name: 'home' },
    { btn: navClient, section: document.getElementById('sec-client'), name: 'client' },
    { btn: navDev, section: document.getElementById('sec-dev'), name: 'dev' }
  ];

  function switchTab(targetName) {
    activeTab = targetName;
    tabs.forEach(t => {
      if (t.name === targetName) {
        t.btn.classList.add('active');
        t.section.classList.add('active');
      } else {
        t.btn.classList.remove('active');
        t.section.classList.remove('active');
      }
    });
    // Scroll to top of window
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  tabs.forEach(t => {
    t.btn.addEventListener('click', () => switchTab(t.name));
  });

  ctaClient.addEventListener('click', () => switchTab('client'));
  ctaDev.addEventListener('click', () => switchTab('dev'));
  logo.addEventListener('click', () => switchTab('home'));

  // Register empty state action trigger
  document.addEventListener('click', (e) => {
    if (e.target && e.target.classList.contains('btn-action-post-now')) {
      document.getElementById('modal-post-problem').classList.add('show');
    }
  });
}

/* ================= GLOBAL STATISTICS COMPILER ================= */
function updateStatistics() {
  const openCount = problems.filter(p => p.status === 'Open').length;
  const progressCount = problems.filter(p => p.status === 'In Progress').length;
  const solvedCount = problems.filter(p => p.status === 'Solved').length;

  // Calculate MRR (15$ per active unlocked solution)
  // Let's count unlocked mock items plus any unlocked user submissions
  const unlockedCount = problems.filter(p => p.status === 'Solved' && p.is_unlocked).length;
  const mrrVal = unlockedCount * 15;
  const annualARR = mrrVal * 12;

  document.getElementById('stat-open').textContent = openCount;
  document.getElementById('stat-progress').textContent = progressCount;
  document.getElementById('stat-solved').textContent = solvedCount;
  document.getElementById('stat-mrr').textContent = `$${mrrVal.toLocaleString()} /mo`;

  // Update developer workspace active claimed counter
  document.getElementById('dev-claimed-count').textContent = progressCount + solvedCount;
}

/* ================= INGESTION & FORM SUBMISSION ================= */
function setupFormHandlers() {
  const form = document.getElementById('form-post-problem');
  const modalPost = document.getElementById('modal-post-problem');
  const modalLoading = document.getElementById('modal-ai-loading');

  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const clientName = document.getElementById('post-client-name').value.trim();
    const clientEmail = document.getElementById('post-client-email').value.trim();
    const rawTitle = document.getElementById('post-title').value.trim();
    const rawDesc = document.getElementById('post-description').value.trim();

    // Close form and open loader
    modalPost.classList.remove('show');
    modalLoading.classList.add('show');
    form.reset();

    // Begin Loading Step Simulation
    simulateAILoaderSteps(rawTitle, rawDesc, (aiResult) => {
      // Create new problem record
      const newProblem = {
        id: `prob-${Date.now()}`,
        client_name: clientName,
        client_email: clientEmail,
        raw_title: rawTitle,
        raw_description: rawDesc,
        status: "Open",
        developer_name: null,
        developer_github: null,
        solution_url: null,
        is_unlocked: false,
        created_at: new Date().toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' }),
        blueprint: aiResult
      };

      // Add to array, save to localStorage
      problems.unshift(newProblem);
      localStorage.setItem('btf_problems', JSON.stringify(problems));

      // Close loader
      modalLoading.classList.remove('show');

      // Refresh listings
      updateStatistics();
      renderClientFeed();
      renderDeveloperFeed();

      // Open details view modal of new problem instantly
      openBlueprintModal(newProblem.id);
    });
  });
}

/* ================= SIMULATE AI BLUEPRINT PROCESSOR ================= */
function simulateAILoaderSteps(title, desc, onComplete) {
  const step1 = document.getElementById('shimmer-step-1');
  const step2 = document.getElementById('shimmer-step-2');
  const step3 = document.getElementById('shimmer-step-3');
  const step4 = document.getElementById('shimmer-step-4');

  // Reset statuses
  const steps = [step1, step2, step3, step4];
  steps.forEach(s => {
    s.classList.remove('active', 'completed');
  });

  step1.classList.add('active');

  setTimeout(() => {
    step1.classList.remove('active');
    step1.classList.add('completed');
    step2.classList.add('active');

    setTimeout(() => {
      step2.classList.remove('active');
      step2.classList.add('completed');
      step3.classList.add('active');

      setTimeout(() => {
        step3.classList.remove('active');
        step3.classList.add('completed');
        step4.classList.add('active');

        setTimeout(() => {
          step4.classList.remove('active');
          step4.classList.add('completed');

          // Process description to generate specifications
          const blueprint = runClientAISimulator(title, desc);
          onComplete(blueprint);
        }, 800);
      }, 800);
    }, 1000); // Spend longer checking duplicates for authenticity
  }, 800);
}

// Client Side Mock AI Generator
function runClientAISimulator(title, desc) {
  const cleanTitle = title.toLowerCase() + " " + desc.toLowerCase();
  let categoryKey = 'default';

  if (cleanTitle.match(/(invoice|receipt|billing|expense|ocr|tax)/)) {
    categoryKey = 'invoice';
  } else if (cleanTitle.match(/(cake|bakery|bake|food|cookie|restaurant)/)) {
    categoryKey = 'cake';
  } else if (cleanTitle.match(/(client|customer|lead|crm|contact|agent)/)) {
    categoryKey = 'client';
  } else if (cleanTitle.match(/(inventory|stock|warehouse|product|box|scan)/)) {
    categoryKey = 'inventory';
  } else if (cleanTitle.match(/(schedule|appoint|calendar|meet|booking|time slot)/)) {
    categoryKey = 'schedule';
  } else if (cleanTitle.match(/(time|clock|tracker|hour|billable)/)) {
    categoryKey = 'time';
  }

  const modelTemplate = AI_BLUEPRINTS[categoryKey];

  // Semantic Duplicate Check:
  // Scans existing problems in DB for shared keyword category or title similarity
  let isDuplicate = false;
  let similarProblemId = null;
  let similarityReason = "";

  for (let prob of problems) {
    const existingClean = prob.raw_title.toLowerCase() + " " + prob.raw_description.toLowerCase();
    
    // Check keyword alignment
    let matchesCategory = false;
    if (categoryKey !== 'default') {
      if (categoryKey === 'invoice' && existingClean.match(/(invoice|receipt|billing|expense)/)) matchesCategory = true;
      if (categoryKey === 'cake' && existingClean.match(/(cake|bakery|bake|food)/)) matchesCategory = true;
      if (categoryKey === 'client' && existingClean.match(/(client|customer|lead|crm)/)) matchesCategory = true;
      if (categoryKey === 'inventory' && existingClean.match(/(inventory|stock|warehouse|product|box)/)) matchesCategory = true;
      if (categoryKey === 'schedule' && existingClean.match(/(schedule|appoint|calendar|meet|booking)/)) matchesCategory = true;
      if (categoryKey === 'time' && existingClean.match(/(time|clock|tracker|hour)/)) matchesCategory = true;
    }

    if (matchesCategory) {
      isDuplicate = true;
      similarProblemId = prob.id;
      similarityReason = `This problem overlaps with '${prob.blueprint.formal_title}' (submitted by ${prob.client_name}). Both describe operational gaps inside managing ${categoryKey === 'cake' ? 'baking and cake schedules' : categoryKey + ' tracking workflows'}.`;
      break;
    }
  }

  return {
    formal_title: modelTemplate.title,
    target_persona: modelTemplate.persona,
    tech_stack: [...modelTemplate.stack],
    core_features: [...modelTemplate.features],
    roadmap: [...modelTemplate.roadmap],
    duplicate_check: {
      is_duplicate: isDuplicate,
      similar_problem_id: similarProblemId,
      similarity_reason: similarityReason
    }
  };
}

/* ================= RENDER FEEDS ================= */

// 1. Client Feed Listing
function renderClientFeed() {
  const container = document.getElementById('client-problems-list');
  const userEmail = "john@example.com"; // Simulation mock email filter

  // Clear list
  container.innerHTML = "";

  if (problems.length === 0) {
    container.innerHTML = `
      <div class="empty-state glass-panel">
        <span class="empty-icon">📁</span>
        <h4>No Problems Posted Yet</h4>
        <p>Describe your manual daily task bottleneck to get started.</p>
        <button class="btn btn-secondary btn-sm btn-action-post-now">Create First Post</button>
      </div>
    `;
    return;
  }

  problems.forEach(prob => {
    const card = document.createElement('div');
    card.className = "problem-card glass-panel";
    card.setAttribute('data-id', prob.id);

    // Duplicate ribbon alert
    const duplicateRibbon = prob.blueprint.duplicate_check.is_duplicate 
      ? `<span class="duplicate-banner-ribbon">Duplicate Match</span>` 
      : '';

    card.innerHTML = `
      ${duplicateRibbon}
      <div>
        <div class="card-header-row">
          <span class="status-badge ${prob.status.toLowerCase().replace(' ', '-')}">${prob.status}</span>
          <span class="card-meta">${prob.created_at}</span>
        </div>
        <h4 class="card-title">${prob.blueprint.formal_title}</h4>
        <p class="card-description">${prob.raw_description}</p>
      </div>
      
      <div class="card-footer-row">
        <span class="card-meta">By ${prob.client_name}</span>
        <span class="card-action-hint">
          <span>View Blueprint</span>
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
        </span>
      </div>
    `;

    card.addEventListener('click', () => openBlueprintModal(prob.id));
    container.appendChild(card);
  });
}

// 2. Developer Feed Listing
function renderDeveloperFeed(filterStatus = 'all', searchQuery = '') {
  const container = document.getElementById('dev-problems-list');
  container.innerHTML = "";

  let filtered = [...problems];

  // Apply search query
  if (searchQuery.trim() !== "") {
    const q = searchQuery.toLowerCase();
    filtered = filtered.filter(p => {
      const matchStack = p.blueprint.tech_stack.some(s => s.toLowerCase().includes(q));
      const matchPersona = p.blueprint.target_persona.toLowerCase().includes(q);
      const matchTitle = p.blueprint.formal_title.toLowerCase().includes(q);
      const matchDesc = p.raw_description.toLowerCase().includes(q);
      return matchStack || matchPersona || matchTitle || matchDesc;
    });
  }

  // Apply tab state status filters
  if (filterStatus !== 'all') {
    const statusMap = {
      'open': 'Open',
      'progress': 'In Progress',
      'solved': 'Solved'
    };
    filtered = filtered.filter(p => p.status === statusMap[filterStatus]);
  }

  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="empty-state glass-panel" style="grid-column: 1 / -1;">
        <span class="empty-icon">🔍</span>
        <h4>No Match Specs Found</h4>
        <p>Try clearing your search query or looking at different filters.</p>
      </div>
    `;
    return;
  }

  filtered.forEach(prob => {
    const card = document.createElement('div');
    card.className = "problem-card glass-panel";
    card.setAttribute('data-id', prob.id);

    // Build stack tags string
    const tagsHtml = prob.blueprint.tech_stack.map(t => `<span class="tech-tag-sm">${t}</span>`).join('');

    card.innerHTML = `
      <div>
        <div class="card-header-row">
          <span class="status-badge ${prob.status.toLowerCase().replace(' ', '-')}">${prob.status}</span>
          <span class="card-meta">${prob.created_at}</span>
        </div>
        <h4 class="card-title">${prob.blueprint.formal_title}</h4>
        <p class="card-description">${prob.raw_description}</p>
        
        <div class="card-tech-cloud">
          ${tagsHtml}
        </div>
      </div>
      
      <div class="card-footer-row">
        <span class="card-meta">Persona: ${prob.blueprint.target_persona}</span>
        <span class="card-action-hint">
          <span>Explore Spec</span>
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
        </span>
      </div>
    `;

    card.addEventListener('click', () => openBlueprintModal(prob.id));
    container.appendChild(card);
  });
}

function setupCardInteractions() {
  const searchInput = document.getElementById('dev-search-input');
  const chips = document.querySelectorAll('.filter-chip');

  searchInput.addEventListener('input', () => {
    const activeChip = document.querySelector('.filter-chip.active');
    const filter = activeChip ? activeChip.getAttribute('data-filter') : 'all';
    renderDeveloperFeed(filter, searchInput.value);
  });

  chips.forEach(chip => {
    chip.addEventListener('click', () => {
      chips.forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      const filter = chip.getAttribute('data-filter');
      renderDeveloperFeed(filter, searchInput.value);
    });
  });
}

/* ================= DETAIL VIEW & BLUEPRINT MODAL ================= */
function openBlueprintModal(id) {
  currentBlueprintId = id;
  const prob = problems.find(p => p.id === id);
  if (!prob) return;

  const modal = document.getElementById('modal-blueprint-details');
  const statusBadge = document.getElementById('modal-blueprint-status');
  const title = document.getElementById('modal-blueprint-title');
  const rawDesc = document.getElementById('modal-raw-description');
  const featuresList = document.getElementById('modal-features-list');
  const roadmapList = document.getElementById('modal-roadmap-list');
  const persona = document.getElementById('modal-persona');
  const clientName = document.getElementById('modal-client-name');
  const techTags = document.getElementById('modal-tech-tags');
  
  const duplicateAlert = document.getElementById('modal-duplicate-alert');
  const duplicateReason = document.getElementById('modal-duplicate-reason');
  const devInfoSection = document.getElementById('modal-dev-info-section');
  const devName = document.getElementById('modal-dev-name');
  const devGithub = document.getElementById('modal-dev-github');

  const gateContainer = document.getElementById('modal-gate-container');
  const footer = document.getElementById('modal-details-footer');

  // Setup basics
  statusBadge.className = `status-badge ${prob.status.toLowerCase().replace(' ', '-')}`;
  statusBadge.textContent = prob.status;
  title.textContent = prob.blueprint.formal_title;
  rawDesc.textContent = prob.raw_description;
  persona.textContent = prob.blueprint.target_persona;
  clientName.textContent = prob.client_name;

  // Render Features list
  featuresList.innerHTML = prob.blueprint.core_features.map(f => `<li>${f}</li>`).join('');
  
  // Render Roadmap list
  roadmapList.innerHTML = prob.blueprint.roadmap.map(r => `<li>${r}</li>`).join('');

  // Render Tech Tags
  techTags.innerHTML = prob.blueprint.tech_stack.map(t => `<span class="tech-tag">${t}</span>`).join('');

  // Setup Duplicate Warnings
  if (prob.blueprint.duplicate_check.is_duplicate) {
    duplicateAlert.classList.remove('hide');
    duplicateReason.textContent = prob.blueprint.duplicate_check.similarity_reason;
  } else {
    duplicateAlert.classList.add('hide');
  }

  // Setup Developer meta logs
  if (prob.developer_name) {
    devInfoSection.classList.remove('hide');
    devName.textContent = prob.developer_name;
    devGithub.textContent = `@${prob.developer_github}`;
    devGithub.href = `https://github.com/${prob.developer_github}`;
  } else {
    devInfoSection.classList.add('hide');
  }

  // Setup dynamic footer action controls and paywall overlays based on roles
  gateContainer.classList.add('hide');
  gateContainer.innerHTML = "";
  footer.innerHTML = "";

  // View Routing: is the user checking via Client Hub tab vs Developer tab?
  if (activeTab === 'client') {
    // CLIENT VIEW
    if (prob.status === 'Open') {
      footer.innerHTML = `<span class="card-meta">Looking for developers to claim this blueprint...</span><button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
    } else if (prob.status === 'In Progress') {
      footer.innerHTML = `<span class="card-meta">🛠️ Project claimed by <strong>${prob.developer_name}</strong> and currently in progress...</span><button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
    } else if (prob.status === 'Solved') {
      // SOLVED LINK STATE: Locked or Unlocked?
      gateContainer.classList.remove('hide');
      if (prob.is_unlocked) {
        // Solution revealed
        gateContainer.innerHTML = `
          <div class="gate-revealed-link-box">
            <div class="gate-revealed-title">🎉 Solution Unlocked & Ready</div>
            <p class="gate-desc">You have an active subscription for this SaaS tool. Open and log in to use your solution.</p>
            <div class="gate-url-text">${prob.solution_url}</div>
            <div class="gate-action-row">
              <a href="${prob.solution_url}" target="_blank" class="btn btn-success btn-sm">Launch Application</a>
            </div>
          </div>
        `;
        footer.innerHTML = `<button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
      } else {
        // Gated: Show Paywall Banner
        gateContainer.innerHTML = `
          <div class="gate-title">🔒 Solution Built & Gated</div>
          <p class="gate-desc">Developer <strong>${prob.developer_name}</strong> has launched a custom solution! Unlock full lifetime subscription access to the host app for a small monthly SaaS fee.</p>
          <div class="gate-action-row">
            <button class="btn btn-primary" id="btn-trigger-checkout">Subscribe to Unlock ($15/mo)</button>
          </div>
        `;
        footer.innerHTML = `<button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
        
        // Connect paywall trigger
        document.getElementById('btn-trigger-checkout').addEventListener('click', () => {
          closeModal('modal-blueprint-details');
          openCheckoutModal(prob);
        });
      }
    }
  } else {
    // DEVELOPER VIEW
    if (prob.status === 'Open') {
      footer.innerHTML = `
        <button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>
        <button class="btn btn-primary" id="btn-action-claim">Claim Problem Spec</button>
      `;
      document.getElementById('btn-action-claim').addEventListener('click', () => {
        closeModal('modal-blueprint-details');
        document.getElementById('modal-dev-claim').classList.add('show');
      });
    } else if (prob.status === 'In Progress') {
      // Allow developer to submit solution URL
      footer.innerHTML = `
        <button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>
        <button class="btn btn-success" id="btn-action-solve">Submit Live URL</button>
      `;
      document.getElementById('btn-action-solve').addEventListener('click', () => {
        closeModal('modal-blueprint-details');
        document.getElementById('modal-dev-solve').classList.add('show');
      });
    } else if (prob.status === 'Solved') {
      // Solved: inform developer of SaaS parameters
      gateContainer.classList.remove('hide');
      gateContainer.innerHTML = `
        <div class="gate-title" style="color: var(--color-success);">✅ SaaS Product Active</div>
        <p class="gate-desc">Your solution is deployed and earning MRR. If the client subscribes, payment logs verify payouts.</p>
        <div class="gate-url-text">${prob.solution_url}</div>
      `;
      footer.innerHTML = `<button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
    }
  }

  modal.classList.add('show');
}

/* ================= MODAL STATE CONTROLLER ================= */
function setupModals() {
  const closeBtns = [
    { btn: document.getElementById('btn-close-post-modal'), modal: 'modal-post-problem' },
    { btn: document.getElementById('btn-cancel-post'), modal: 'modal-post-problem' },
    { btn: document.getElementById('btn-close-details-modal'), modal: 'modal-blueprint-details' },
    { btn: document.getElementById('btn-close-claim-modal'), modal: 'modal-dev-claim' },
    { btn: document.getElementById('btn-cancel-claim'), modal: 'modal-dev-claim' },
    { btn: document.getElementById('btn-close-solve-modal'), modal: 'modal-dev-solve' },
    { btn: document.getElementById('btn-cancel-solve'), modal: 'modal-dev-solve' },
    { btn: document.getElementById('btn-close-checkout-modal'), modal: 'modal-checkout' }
  ];

  closeBtns.forEach(item => {
    if (item.btn) {
      item.btn.addEventListener('click', () => {
        closeModal(item.modal);
      });
    }
  });

  // Action Button to post problem
  const openPostBtn = document.getElementById('btn-open-post-form');
  if (openPostBtn) {
    openPostBtn.addEventListener('click', () => {
      document.getElementById('modal-post-problem').classList.add('show');
    });
  }

  // Developer Claims form submit
  const claimForm = document.getElementById('form-claim-blueprint');
  claimForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const devNameVal = document.getElementById('claim-dev-name').value.trim();
    const devGithubVal = document.getElementById('claim-dev-github').value.trim();

    const idx = problems.findIndex(p => p.id === currentBlueprintId);
    if (idx !== -1) {
      problems[idx].status = 'In Progress';
      problems[idx].developer_name = devNameVal;
      problems[idx].developer_github = devGithubVal;
      localStorage.setItem('btf_problems', JSON.stringify(problems));

      updateStatistics();
      renderClientFeed();
      renderDeveloperFeed();
    }

    closeModal('modal-dev-claim');
    claimForm.reset();
  });

  // Developer Solves form submit
  const solveForm = document.getElementById('form-solve-blueprint');
  solveForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const solveUrlVal = document.getElementById('solve-url').value.trim();

    const idx = problems.findIndex(p => p.id === currentBlueprintId);
    if (idx !== -1) {
      problems[idx].status = 'Solved';
      problems[idx].solution_url = solveUrlVal;
      localStorage.setItem('btf_problems', JSON.stringify(problems));

      updateStatistics();
      renderClientFeed();
      renderDeveloperFeed();
    }

    closeModal('modal-dev-solve');
    solveForm.reset();
  });
}

function closeModal(modalId) {
  document.getElementById(modalId).classList.remove('show');
}

/* ================= STRIPE SANDBOX MONETIZATION GATE ================= */
function openCheckoutModal(prob) {
  document.getElementById('checkout-problem-title').textContent = prob.blueprint.formal_title;
  document.getElementById('checkout-dev-name').textContent = prob.developer_name;
  
  // Set default client billing email
  document.getElementById('card-email').value = prob.client_email;

  const modal = document.getElementById('modal-checkout');
  const alertErr = document.getElementById('checkout-error-alert');
  alertErr.classList.add('hide');

  // Handle Checkout submission
  const paymentForm = document.getElementById('form-stripe-payment');
  
  // Re-bind to ensure single execution
  paymentForm.onsubmit = (e) => {
    e.preventDefault();

    const cardNum = document.getElementById('card-number').value.replace(/\s/g, '');
    const cardExp = document.getElementById('card-expiry').value.trim();
    const cardCvc = document.getElementById('card-cvc').value.trim();

    // Check validity logic (simple mock regex validation)
    if (cardNum.length < 16 || cardExp.length < 5 || cardCvc.length < 3) {
      alertErr.classList.remove('hide');
      alertErr.textContent = "Payment validation failed. Please check inputs.";
      return;
    }

    alertErr.classList.add('hide');
    
    // Simulate payment submission state
    const payText = document.getElementById('btn-pay-text');
    const spinner = document.getElementById('btn-pay-spinner');
    
    payText.classList.add('hide');
    spinner.classList.remove('hide');

    setTimeout(() => {
      // Complete mock payment state updates
      const idx = problems.findIndex(p => p.id === prob.id);
      if (idx !== -1) {
        problems[idx].is_unlocked = true;
        localStorage.setItem('btf_problems', JSON.stringify(problems));
        
        updateStatistics();
        renderClientFeed();
        renderDeveloperFeed();
      }

      // Close modal
      spinner.classList.add('hide');
      payText.classList.remove('hide');
      closeModal('modal-checkout');
      paymentForm.reset();

      // Notify and reopen blueprint details unlocked
      openBlueprintModal(prob.id);
    }, 1800);
  };

  modal.classList.add('show');
}

/* ================= CARD FIELDS TYPING FORMATTER ================= */
function setupCardFormatting() {
  const cardNumInput = document.getElementById('card-number');
  const cardExpInput = document.getElementById('card-expiry');
  const cardCvcInput = document.getElementById('card-cvc');

  // Formats card number: 1234 5678 1234 5678
  cardNumInput.addEventListener('input', (e) => {
    let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    let formatted = "";
    for (let i = 0; i < value.length; i++) {
      if (i > 0 && i % 4 === 0) {
        formatted += " ";
      }
      formatted += value[i];
    }
    e.target.value = formatted;
  });

  // Formats expiry MM / YY
  cardExpInput.addEventListener('input', (e) => {
    let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    if (value.length > 2) {
      e.target.value = value.substr(0, 2) + " / " + value.substr(2, 2);
    } else {
      e.target.value = value;
    }
  });

  // Numbers only for CVC
  cardCvcInput.addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/[^0-9]/gi, '');
  });
}
