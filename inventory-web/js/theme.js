/* ============================================================
   Theme manager — persists choice, applies on load (no flash),
   and renders the toggle controls used in the topbar.
   ============================================================ */

const ThemeManager = (() => {
  const STORAGE_KEY = 'inv_theme';

  function getTheme() {
    return localStorage.getItem(STORAGE_KEY) || 'default';
  }

  function applyTheme(theme) {
    if (theme === 'tavern') {
      document.documentElement.setAttribute('data-theme', 'tavern');
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
    localStorage.setItem(STORAGE_KEY, theme);
  }

  function toggle() {
    const next = getTheme() === 'tavern' ? 'default' : 'tavern';
    applyTheme(next);
    if (typeof Audio2 !== 'undefined') Audio2.refreshAmbientForTheme();
    return next;
  }

  // Apply immediately on script parse (before paint) to avoid a flash
  // of the wrong theme on page load.
  applyTheme(getTheme());

  return { getTheme, applyTheme, toggle };
})();

function renderAvControls() {
  const theme = ThemeManager.getTheme();
  return `
    <button class="theme-toggle" id="themeToggleBtn" aria-label="Switch theme">
      ${theme === 'tavern' ? Icons.tavernMoon || moonIcon() : sunIcon()}
      <span>${theme === 'tavern' ? 'Boar Hat' : 'Daylight'}</span>
    </button>
    <button class="av-toggle ${Audio2.isSoundOn() ? 'active' : ''}" id="soundToggleBtn" aria-label="Toggle click sounds" title="Click sounds">
      ${Audio2.isSoundOn() ? speakerIcon() : speakerMuteIcon()}
    </button>
    <button class="av-toggle ${Audio2.isMusicOn() ? 'active' : ''}" id="musicToggleBtn" aria-label="Toggle ambient music" title="Ambient music">
      ${musicNoteIcon()}
    </button>
  `;
}

function wireAvControls() {
  const themeBtn = document.getElementById('themeToggleBtn');
  const soundBtn = document.getElementById('soundToggleBtn');
  const musicBtn = document.getElementById('musicToggleBtn');

  if (themeBtn) {
    themeBtn.addEventListener('click', () => {
      const next = ThemeManager.toggle();
      themeBtn.querySelector('span').textContent = next === 'tavern' ? 'Boar Hat' : 'Daylight';
      themeBtn.innerHTML = (next === 'tavern' ? moonIcon() : sunIcon()) + `<span>${next === 'tavern' ? 'Boar Hat' : 'Daylight'}</span>`;
    });
  }

  if (soundBtn) {
    soundBtn.addEventListener('click', () => {
      const newVal = !Audio2.isSoundOn();
      Audio2.setSoundOn(newVal);
      soundBtn.classList.toggle('active', newVal);
      soundBtn.innerHTML = newVal ? speakerIcon() : speakerMuteIcon();
      if (newVal) Audio2.playClick();
    });
  }

  if (musicBtn) {
    musicBtn.addEventListener('click', () => {
      Audio2.ensureContext();
      const newVal = !Audio2.isMusicOn();
      Audio2.setMusicOn(newVal);
      musicBtn.classList.toggle('active', newVal);
    });
  }
}

function sunIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/></svg>`;
}
function moonIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79Z"/></svg>`;
}
function speakerIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><path d="M15.54 8.46a5 5 0 0 1 0 7.07"/></svg>`;
}
function speakerMuteIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"/><line x1="23" y1="9" x2="17" y2="15"/><line x1="17" y1="9" x2="23" y2="15"/></svg>`;
}
function musicNoteIcon() {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/></svg>`;
}
