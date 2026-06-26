/* ============================================================
   App shell — sidebar / topbar / mobile tab bar
   Shared across all authenticated pages.
   ============================================================ */

const NAV_ITEMS = [
  { id: 'dashboard', label: 'Dashboard', href: 'dashboard.html', icon: Icons.dashboard, roles: ['ADMIN', 'STAFF'] },
  { id: 'products', label: 'Products', href: 'products.html', icon: Icons.box, roles: ['ADMIN', 'STAFF'] },
  { id: 'categories', label: 'Categories', href: 'categories.html', icon: Icons.tag, roles: ['ADMIN', 'STAFF'] },
  { id: 'customers', label: 'Customers', href: 'customers.html', icon: Icons.users, roles: ['ADMIN', 'STAFF'] },
  { id: 'orders', label: 'Orders', href: 'orders.html', icon: Icons.receipt, roles: ['ADMIN', 'STAFF'] },
  { id: 'new-order', label: 'New order', href: 'new-order.html', icon: Icons.cart, roles: ['ADMIN', 'STAFF'] },
  { id: 'users', label: 'Team', href: 'users.html', icon: Icons.userCog, roles: ['ADMIN'] },
];

// Mobile bottom tab bar only has room for ~5 items — prioritized subset.
const TABBAR_ITEMS = ['dashboard', 'products', 'new-order', 'orders', 'categories'];

function renderShell({ activeId, pageTitle, crumb }) {
  const user = API.getCurrentUser();
  if (!user) {
    location.href = 'index.html';
    return;
  }

  const visibleNav = NAV_ITEMS.filter((item) => item.roles.includes(user.role));
  const tabbarNav = visibleNav.filter((item) => TABBAR_ITEMS.includes(item.id));

  const initials = user.name
    .split(' ')
    .map((p) => p[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();

  const navHtml = (items, asTabbar) =>
    items
      .map(
        (item) => `
        <a href="${item.href}" class="nav-item ${item.id === activeId ? 'active' : ''}">
          ${item.icon}<span>${item.label}</span>
        </a>`
      )
      .join('');

  document.body.insertAdjacentHTML(
    'afterbegin',
    `
    <div class="app-shell">
      <aside class="sidebar">
        <div class="sidebar-brand">
          <div class="mark">S</div>
          <div class="name">StockTag<span>Inventory OS</span></div>
        </div>
        <nav class="sidebar-nav">
          ${navHtml(visibleNav.filter((i) => i.id !== 'users'))}
          ${visibleNav.some((i) => i.id === 'users') ? `<div class="nav-section-label">Administration</div>${navHtml(visibleNav.filter((i) => i.id === 'users'))}` : ''}
        </nav>
        <div class="sidebar-footer">
          <div class="user-chip">
            <div class="avatar">${initials}</div>
            <div class="meta">
              <div class="uname">${escapeHtml(user.name)}</div>
              <div class="urole">${user.role.toLowerCase()}</div>
            </div>
          </div>
          <button class="logout-btn" id="logoutBtn">${Icons.logout} Sign out</button>
        </div>
      </aside>

      <div class="main-area">
        <header class="topbar">
          <div class="topbar-title">
            ${crumb ? `<div class="crumb">${escapeHtml(crumb)}</div>` : ''}
            <h1>${escapeHtml(pageTitle)}</h1>
          </div>
          <div style="display:flex; align-items:center; gap:10px; flex-wrap:wrap;">
            <div id="avControls" style="display:flex; align-items:center; gap:8px;"></div>
            <div id="topbarActions" style="display:flex; align-items:center; gap:10px;"></div>
          </div>
        </header>
        <main class="content" id="pageContent"></main>
      </div>

      <nav class="tabbar">
        ${navHtml(tabbarNav, true)}
      </nav>
    </div>
  `
  );

  document.getElementById('logoutBtn').addEventListener('click', () => {
    API.clearSession();
    location.href = 'index.html';
  });

  // Theme / sound / music controls
  if (typeof renderAvControls === 'function') {
    document.getElementById('avControls').innerHTML = renderAvControls();
    wireAvControls();
  }
}

function setTopbarActions(html) {
  const el = document.getElementById('topbarActions');
  if (el) el.innerHTML = html;
}

function pageRoot() {
  return document.getElementById('pageContent');
}
