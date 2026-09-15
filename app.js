/* ================= APPLICATION STATE ================= */
let problems = [];
let activeTab = 'home';
let currentBlueprintId = null;

// Fallback problems for offline resilience
const DEFAULT_PROBLEMS = [
  {
    id: "mock-1",
    client_name: "Sarah Jenkins",
    client_email: "sarah@sweetdelights.com",
    raw_title: "WhatsApp Bakery Orders",
    raw_description: "I run a custom cake shop. Customers send me order details, reference pictures, and text requests on WhatsApp. I manually write them down on sticky notes and draw order calendar entries on a whiteboard. I constantly lose notes or misread handwriting, causing custom text spelling mistakes on cakes, and late deliveries.",
    status: "Solved",
    developer_name: "Leo Vance",
    developer_github: "leodev",
    solution_url: "https://sweetorders-bakery.vercel.app",
    is_unlocked: false,
    createdAt: new Date().toISOString(),
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
    createdAt: new Date().toISOString(),
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
    id: "mock-3",
    client_name: "Dr. Amanda Zhao",
    client_email: "amanda@veterinarycare.org",
    raw_title: "Manual Patient Appointment Reminders & Pet Med Schedules",
    raw_description: "Our veterinary clinic staff spends 3 hours every morning calling pet owners to confirm appointments and remind them about post-surgery medication schedules. Patients frequently forget medicine timings, and no-shows waste our surgeon's time.",
    status: "Open",
    developer_name: null,
    developer_github: null,
    solution_url: null,
    is_unlocked: false,
    createdAt: new Date().toISOString(),
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

/* ================= API CLIENT SERVICE ================= */
const API = {
  async getStats() {
    try {
      const res = await fetch('/api/stats');
      if (!res.ok) throw new Error('API Stats Error');
      return await res.json();
    } catch (err) {
      // Fallback calculation on client
      const total = problems.length;
      const solved = problems.filter(p => p.status === 'Solved').length;
      const inProgress = problems.filter(p => p.status === 'In Progress').length;
      const open = problems.filter(p => p.status === 'Open').length;
      return {
        total_problems: total,
        solved_problems: solved,
        in_progress: inProgress,
        open_problems: open,
        total_mrr: solved * 49
      };
    }
  },

  async getProblems(filterStatus = 'all', searchQuery = '') {
    try {
      let url = '/api/problems?';
      if (filterStatus && filterStatus !== 'all') {
        const statusMap = { 'open': 'Open', 'progress': 'In Progress', 'solved': 'Solved' };
        url += `status=${encodeURIComponent(statusMap[filterStatus] || filterStatus)}&`;
      }
      if (searchQuery) {
        url += `search=${encodeURIComponent(searchQuery)}&`;
      }

      const res = await fetch(url);
      if (!res.ok) throw new Error('API Problems Error');
      return await res.json();
    } catch (err) {
      console.warn('API unavailable, using local memory state:', err.message);
      return problems;
    }
  },

  async createProblem(data) {
    try {
      const res = await fetch('/api/problems', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      if (!res.ok) throw new Error('Failed to create problem on server');
      return await res.json();
    } catch (err) {
      console.warn('Falling back to local creation:', err.message);
      const fallbackProblem = {
        id: `local-${Date.now()}`,
        ...data,
        status: 'Open',
        developer_name: null,
        developer_github: null,
        solution_url: null,
        is_unlocked: false,
        createdAt: new Date().toISOString(),
        blueprint: {
          formal_title: "Automated Workflow Manager & SaaS Architecture",
          target_persona: "Digital Operations & Small Businesses",
          tech_stack: ["Next.js", "Express.js", "MongoDB Atlas"],
          core_features: ["Workflow Pipelines", "Automated Logs", "Team Reminders"],
          roadmap: ["Phase 1: Setup API", "Phase 2: Database Models"],
          duplicate_check: { is_duplicate: false, similar_problem_id: null, similarity_reason: "" }
        }
      };
      return fallbackProblem;
    }
  },

  async claimProblem(id, devData) {
    try {
      const res = await fetch(`/api/problems/${id}/claim`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(devData)
      });
      if (!res.ok) throw new Error('Failed to claim problem on server');
      return await res.json();
    } catch (err) {
      console.warn('Fallback local claim:', err.message);
      const prob = problems.find(p => p.id === id);
      if (prob) {
        prob.status = 'In Progress';
        prob.developer_name = devData.developer_name;
        prob.developer_github = devData.developer_github;
      }
      return prob;
    }
  },

  async solveProblem(id, solveData) {
    try {
      const res = await fetch(`/api/problems/${id}/solve`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(solveData)
      });
      if (!res.ok) throw new Error('Failed to submit solution on server');
      return await res.json();
    } catch (err) {
      console.warn('Fallback local solve:', err.message);
      const prob = problems.find(p => p.id === id);
      if (prob) {
        prob.status = 'Solved';
        prob.solution_url = solveData.solution_url;
      }
      return prob;
    }
  },

  async unlockProblem(id, unlockData) {
    try {
      const res = await fetch(`/api/problems/${id}/unlock`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(unlockData)
      });
      if (!res.ok) throw new Error('Failed to unlock problem on server');
      return await res.json();
    } catch (err) {
      console.warn('Fallback local unlock:', err.message);
      const prob = problems.find(p => p.id === id);
      if (prob) {
        prob.is_unlocked = true;
      }
      return prob;
    }
  }
};

/* ================= INITIALIZATION ================= */
document.addEventListener('DOMContentLoaded', async () => {
  // Load initial problems and stats from MongoDB / API
  await loadProblemsAndStats();

  // Setup DOM Event Listeners
  setupNavigation();
  setupFormHandlers();
  setupCardInteractions();
  setupModals();
  setupCardFormatting();
});

async function loadProblemsAndStats() {
  try {
    const fetched = await API.getProblems();
    if (fetched && fetched.length > 0) {
      problems = fetched;
    } else {
      const stored = localStorage.getItem('btf_problems');
      problems = stored ? JSON.parse(stored) : [...DEFAULT_PROBLEMS];
    }
  } catch (err) {
    problems = [...DEFAULT_PROBLEMS];
  }

  await updateStatistics();
  renderClientFeed();
  renderDeveloperFeed();
}

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
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  tabs.forEach(t => {
    t.btn.addEventListener('click', () => switchTab(t.name));
  });

  ctaClient.addEventListener('click', () => switchTab('client'));
  ctaDev.addEventListener('click', () => switchTab('dev'));
  logo.addEventListener('click', () => switchTab('home'));

  // Empty state CTA trigger
  document.addEventListener('click', (e) => {
    if (e.target && e.target.classList.contains('btn-action-post-now')) {
      document.getElementById('modal-post-problem').classList.add('show');
    }
  });
}

/* ================= GLOBAL STATISTICS COMPILER ================= */
async function updateStatistics() {
  const stats = await API.getStats();

  document.getElementById('stat-open').textContent = stats.open_problems ?? 0;
  document.getElementById('stat-progress').textContent = stats.in_progress ?? 0;
  document.getElementById('stat-solved').textContent = stats.solved_problems ?? 0;
  document.getElementById('stat-mrr').textContent = `$${(stats.total_mrr ?? 0).toLocaleString()} /mo`;

  // Update developer workspace active claimed counter
  const claimedCount = (stats.in_progress ?? 0) + (stats.solved_problems ?? 0);
  document.getElementById('dev-claimed-count').textContent = claimedCount;
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
    simulateAILoaderSteps(rawTitle, rawDesc, async () => {
      // Post problem to server MongoDB API
      const saved = await API.createProblem({
        client_name: clientName,
        client_email: clientEmail,
        raw_title: rawTitle,
        raw_description: rawDesc
      });

      // Insert at beginning of local array
      problems.unshift(saved);
      localStorage.setItem('btf_problems', JSON.stringify(problems));

      // Close loader
      modalLoading.classList.remove('show');

      // Refresh listings & stats from server
      await updateStatistics();
      renderClientFeed();
      renderDeveloperFeed();

      // Open details view modal of new problem instantly
      openBlueprintModal(saved.id || saved._id);
    });
  });
}

/* ================= SIMULATE AI BLUEPRINT PROCESSOR ================= */
function simulateAILoaderSteps(title, desc, onComplete) {
  const step1 = document.getElementById('ai-step-1');
  const step2 = document.getElementById('ai-step-2');
  const step3 = document.getElementById('ai-step-3');
  const step4 = document.getElementById('ai-step-4');

  // Reset states
  [step1, step2, step3, step4].forEach(s => {
    s.classList.remove('active', 'completed');
  });

  // Step 1: Parsing
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
          onComplete();
        }, 800);
      }, 800);
    }, 1000);
  }, 800);
}

/* ================= HELPER: FORMAT DATES ================= */
function formatDate(dateValue) {
  if (!dateValue) return 'Recently';
  try {
    const d = new Date(dateValue);
    if (isNaN(d.getTime())) return dateValue;
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  } catch {
    return 'Recently';
  }
}

/* ================= RENDER FEEDS ================= */

// 1. Client Feed Listing
function renderClientFeed() {
  const container = document.getElementById('client-problems-list');
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
    const probId = prob.id || prob._id;
    const card = document.createElement('div');
    card.className = "problem-card glass-panel";
    card.setAttribute('data-id', probId);

    const isDuplicate = prob.blueprint?.duplicate_check?.is_duplicate;
    const duplicateRibbon = isDuplicate 
      ? `<span class="duplicate-banner-ribbon">Duplicate Match</span>` 
      : '';

    const formattedDate = formatDate(prob.createdAt || prob.created_at);

    card.innerHTML = `
      ${duplicateRibbon}
      <div>
        <div class="card-header-row">
          <span class="status-badge ${prob.status.toLowerCase().replace(' ', '-')}">${prob.status}</span>
          <span class="card-meta">${formattedDate}</span>
        </div>
        <h4 class="card-title">${prob.blueprint?.formal_title || prob.raw_title}</h4>
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

    card.addEventListener('click', () => openBlueprintModal(probId));
    container.appendChild(card);
  });
}

// 2. Developer Feed Listing
function renderDeveloperFeed(filterStatus = 'all', searchQuery = '') {
  const container = document.getElementById('dev-problems-list');
  container.innerHTML = "";

  let filtered = [...problems];

  if (searchQuery.trim() !== "") {
    const q = searchQuery.toLowerCase();
    filtered = filtered.filter(p => {
      const matchStack = p.blueprint?.tech_stack?.some(s => s.toLowerCase().includes(q));
      const matchPersona = p.blueprint?.target_persona?.toLowerCase().includes(q);
      const matchTitle = p.blueprint?.formal_title?.toLowerCase().includes(q);
      const matchDesc = p.raw_description.toLowerCase().includes(q);
      return matchStack || matchPersona || matchTitle || matchDesc;
    });
  }

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
    const probId = prob.id || prob._id;
    const card = document.createElement('div');
    card.className = "problem-card glass-panel";
    card.setAttribute('data-id', probId);

    const tags = prob.blueprint?.tech_stack || [];
    const tagsHtml = tags.map(t => `<span class="tech-tag-sm">${t}</span>`).join('');
    const formattedDate = formatDate(prob.createdAt || prob.created_at);

    card.innerHTML = `
      <div>
        <div class="card-header-row">
          <span class="status-badge ${prob.status.toLowerCase().replace(' ', '-')}">${prob.status}</span>
          <span class="card-meta">${formattedDate}</span>
        </div>
        <h4 class="card-title">${prob.blueprint?.formal_title || prob.raw_title}</h4>
        <p class="card-description">${prob.raw_description}</p>
        
        <div class="card-tech-cloud">
          ${tagsHtml}
        </div>
      </div>
      
      <div class="card-footer-row">
        <span class="card-meta">Persona: ${prob.blueprint?.target_persona || 'Small Businesses'}</span>
        <span class="card-action-hint">
          <span>Explore Spec</span>
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
        </span>
      </div>
    `;

    card.addEventListener('click', () => openBlueprintModal(probId));
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
  const prob = problems.find(p => (p.id === id || p._id === id));
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
  title.textContent = prob.blueprint?.formal_title || prob.raw_title;
  rawDesc.textContent = prob.raw_description;
  persona.textContent = prob.blueprint?.target_persona || 'Small Business Operators';
  clientName.textContent = prob.client_name;

  // Render Features list
  const features = prob.blueprint?.core_features || [];
  featuresList.innerHTML = features.map(f => `<li>${f}</li>`).join('');
  
  // Render Roadmap list
  const roadmap = prob.blueprint?.roadmap || [];
  roadmapList.innerHTML = roadmap.map(r => `<li>${r}</li>`).join('');

  // Render Tech Tags
  const stack = prob.blueprint?.tech_stack || [];
  techTags.innerHTML = stack.map(t => `<span class="tech-tag">${t}</span>`).join('');

  // Setup Duplicate Warnings
  if (prob.blueprint?.duplicate_check?.is_duplicate) {
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

  // Dynamic footer action controls and paywall
  gateContainer.classList.add('hide');
  gateContainer.innerHTML = "";
  footer.innerHTML = "";

  if (activeTab === 'client') {
    // CLIENT VIEW
    if (prob.status === 'Open') {
      footer.innerHTML = `<span class="card-meta">Looking for developers to claim this blueprint...</span><button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
    } else if (prob.status === 'In Progress') {
      footer.innerHTML = `<span class="card-meta">🛠️ Project claimed by <strong>${prob.developer_name}</strong> and currently in progress...</span><button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
    } else if (prob.status === 'Solved') {
      gateContainer.classList.remove('hide');
      if (prob.is_unlocked) {
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
        gateContainer.innerHTML = `
          <div class="gate-title">🔒 Solution Built & Gated</div>
          <p class="gate-desc">Developer <strong>${prob.developer_name}</strong> has launched a custom solution! Unlock full lifetime subscription access to the host app for a small monthly SaaS fee.</p>
          <div class="gate-action-row">
            <button class="btn btn-primary" id="btn-trigger-checkout">Subscribe to Unlock ($15/mo)</button>
          </div>
        `;
        footer.innerHTML = `<button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>`;
        
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
      footer.innerHTML = `
        <button class="btn btn-secondary" onclick="closeModal('modal-blueprint-details')">Close</button>
        <button class="btn btn-success" id="btn-action-solve">Submit Live URL</button>
      `;
      document.getElementById('btn-action-solve').addEventListener('click', () => {
        closeModal('modal-blueprint-details');
        document.getElementById('modal-dev-solve').classList.add('show');
      });
    } else if (prob.status === 'Solved') {
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

  const openPostBtn = document.getElementById('btn-open-post-form');
  if (openPostBtn) {
    openPostBtn.addEventListener('click', () => {
      document.getElementById('modal-post-problem').classList.add('show');
    });
  }

  // Developer Claims form submit
  const claimForm = document.getElementById('form-claim-blueprint');
  claimForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const devNameVal = document.getElementById('claim-dev-name').value.trim();
    const devGithubVal = document.getElementById('claim-dev-github').value.trim();

    const updated = await API.claimProblem(currentBlueprintId, {
      developer_name: devNameVal,
      developer_github: devGithubVal
    });

    const idx = problems.findIndex(p => (p.id === currentBlueprintId || p._id === currentBlueprintId));
    if (idx !== -1 && updated) {
      problems[idx] = updated;
      localStorage.setItem('btf_problems', JSON.stringify(problems));
    }

    await updateStatistics();
    renderClientFeed();
    renderDeveloperFeed();

    closeModal('modal-dev-claim');
    claimForm.reset();
  });

  // Developer Solves form submit
  const solveForm = document.getElementById('form-solve-blueprint');
  solveForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const solveUrlVal = document.getElementById('solve-url').value.trim();

    const updated = await API.solveProblem(currentBlueprintId, {
      solution_url: solveUrlVal
    });

    const idx = problems.findIndex(p => (p.id === currentBlueprintId || p._id === currentBlueprintId));
    if (idx !== -1 && updated) {
      problems[idx] = updated;
      localStorage.setItem('btf_problems', JSON.stringify(problems));
    }

    await updateStatistics();
    renderClientFeed();
    renderDeveloperFeed();

    closeModal('modal-dev-solve');
    solveForm.reset();
  });
}

function closeModal(modalId) {
  const el = document.getElementById(modalId);
  if (el) el.classList.remove('show');
}

/* ================= STRIPE SANDBOX MONETIZATION GATE ================= */
function openCheckoutModal(prob) {
  document.getElementById('checkout-problem-title').textContent = prob.blueprint?.formal_title || prob.raw_title;
  document.getElementById('checkout-dev-name').textContent = prob.developer_name;
  document.getElementById('card-email').value = prob.client_email;

  const modal = document.getElementById('modal-checkout');
  const alertErr = document.getElementById('checkout-error-alert');
  alertErr.classList.add('hide');

  const paymentForm = document.getElementById('form-stripe-payment');
  
  paymentForm.onsubmit = (e) => {
    e.preventDefault();

    const cardNum = document.getElementById('card-number').value.replace(/\s/g, '');
    const cardExp = document.getElementById('card-expiry').value.trim();
    const cardCvc = document.getElementById('card-cvc').value.trim();
    const cardEmail = document.getElementById('card-email').value.trim();

    if (cardNum.length < 16 || cardExp.length < 5 || cardCvc.length < 3) {
      alertErr.classList.remove('hide');
      alertErr.textContent = "Payment validation failed. Please enter 16 digits, MM/YY, and CVC.";
      return;
    }

    alertErr.classList.add('hide');
    
    const payText = document.getElementById('btn-pay-text');
    const spinner = document.getElementById('btn-pay-spinner');
    
    payText.classList.add('hide');
    spinner.classList.remove('hide');

    setTimeout(async () => {
      const probId = prob.id || prob._id;
      const updated = await API.unlockProblem(probId, { user_email: cardEmail });

      const idx = problems.findIndex(p => (p.id === probId || p._id === probId));
      if (idx !== -1 && updated) {
        problems[idx] = updated;
        localStorage.setItem('btf_problems', JSON.stringify(problems));
      }

      await updateStatistics();
      renderClientFeed();
      renderDeveloperFeed();

      spinner.classList.add('hide');
      payText.classList.remove('hide');
      closeModal('modal-checkout');
      paymentForm.reset();

      // Reopen blueprint details unlocked
      openBlueprintModal(probId);
    }, 1800);
  };

  modal.classList.add('show');
}

/* ================= CARD FIELDS TYPING FORMATTER ================= */
function setupCardFormatting() {
  const cardNumInput = document.getElementById('card-number');
  const cardExpInput = document.getElementById('card-expiry');
  const cardCvcInput = document.getElementById('card-cvc');

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

  cardExpInput.addEventListener('input', (e) => {
    let value = e.target.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    if (value.length > 2) {
      e.target.value = value.substr(0, 2) + " / " + value.substr(2, 2);
    } else {
      e.target.value = value;
    }
  });

  cardCvcInput.addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/[^0-9]/gi, '');
  });
}
