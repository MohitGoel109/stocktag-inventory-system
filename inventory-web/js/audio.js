/* ============================================================
   Audio engine — fully synthesized, no external audio files.
   Click SFX + an ambient drone loop, both generated in-browser
   via the Web Audio API. Tone/character changes with theme.
   ============================================================ */

const Audio2 = (() => {
  let ctx = null;
  let masterGain = null;
  let ambientNodes = null;
  let soundOn = JSON.parse(localStorage.getItem('inv_sound_on') ?? 'true');
  let musicOn = JSON.parse(localStorage.getItem('inv_music_on') ?? 'false');

  function ensureContext() {
    if (!ctx) {
      ctx = new (window.AudioContext || window.webkitAudioContext)();
      masterGain = ctx.createGain();
      masterGain.gain.value = 0.5;
      masterGain.connect(ctx.destination);
    }
    if (ctx.state === 'suspended') ctx.resume();
    return ctx;
  }

  function currentTheme() {
    return document.documentElement.getAttribute('data-theme') || 'default';
  }

  /* ---------- Click SFX ----------
     Default theme: short, bright, "tap" — two quick sine partials.
     Tavern theme: lower, woodier "thock" — filtered triangle + short noise burst,
     evoking a tankard set down on a wooden table. */
  function playClick() {
    if (!soundOn) return;
    const audioCtx = ensureContext();
    const now = audioCtx.currentTime;

    if (currentTheme() === 'tavern') {
      playTavernClick(audioCtx, now);
    } else {
      playDefaultClick(audioCtx, now);
    }
  }

  function playDefaultClick(audioCtx, now) {
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(920, now);
    osc.frequency.exponentialRampToValueAtTime(620, now + 0.06);
    gain.gain.setValueAtTime(0.0001, now);
    gain.gain.exponentialRampToValueAtTime(0.18, now + 0.008);
    gain.gain.exponentialRampToValueAtTime(0.0001, now + 0.09);
    osc.connect(gain).connect(masterGain);
    osc.start(now);
    osc.stop(now + 0.1);
  }

  function playTavernClick(audioCtx, now) {
    // Woody "thock": filtered triangle thump
    const osc = audioCtx.createOscillator();
    const filter = audioCtx.createBiquadFilter();
    const gain = audioCtx.createGain();
    osc.type = 'triangle';
    osc.frequency.setValueAtTime(180, now);
    osc.frequency.exponentialRampToValueAtTime(90, now + 0.1);
    filter.type = 'lowpass';
    filter.frequency.value = 700;
    gain.gain.setValueAtTime(0.0001, now);
    gain.gain.exponentialRampToValueAtTime(0.32, now + 0.006);
    gain.gain.exponentialRampToValueAtTime(0.0001, now + 0.16);
    osc.connect(filter).connect(gain).connect(masterGain);
    osc.start(now);
    osc.stop(now + 0.18);

    // Short noise burst for texture
    const bufferSize = audioCtx.sampleRate * 0.04;
    const buffer = audioCtx.createBuffer(1, bufferSize, audioCtx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) data[i] = (Math.random() * 2 - 1) * (1 - i / bufferSize);
    const noise = audioCtx.createBufferSource();
    noise.buffer = buffer;
    const noiseFilter = audioCtx.createBiquadFilter();
    noiseFilter.type = 'lowpass';
    noiseFilter.frequency.value = 1400;
    const noiseGain = audioCtx.createGain();
    noiseGain.gain.value = 0.08;
    noise.connect(noiseFilter).connect(noiseGain).connect(masterGain);
    noise.start(now);
  }

  /* ---------- Ambient loop ----------
     Default theme: soft, airy pad — slow detuned sine pair, gentle filter sweep.
     Tavern theme: lower drone with a slow tremolo, warmer/darker filter,
     evoking a low tavern hum rather than any copyrighted score. */
  function startAmbient() {
    if (ambientNodes) return;
    const audioCtx = ensureContext();
    const theme = currentTheme();

    const out = audioCtx.createGain();
    out.gain.value = 0.0001;
    out.connect(masterGain);
    out.gain.linearRampToValueAtTime(theme === 'tavern' ? 0.05 : 0.035, audioCtx.currentTime + 2);

    const baseFreq = theme === 'tavern' ? 98 : 220; // tavern: low G; default: airy A
    const detune = theme === 'tavern' ? 6 : 4;

    const osc1 = audioCtx.createOscillator();
    const osc2 = audioCtx.createOscillator();
    osc1.type = theme === 'tavern' ? 'sawtooth' : 'sine';
    osc2.type = 'sine';
    osc1.frequency.value = baseFreq;
    osc2.frequency.value = baseFreq * 1.5;
    osc1.detune.value = -detune;
    osc2.detune.value = detune;

    const filter = audioCtx.createBiquadFilter();
    filter.type = 'lowpass';
    filter.frequency.value = theme === 'tavern' ? 420 : 1200;
    filter.Q.value = 0.7;

    // Slow filter sweep LFO for a living, non-static texture
    const lfo = audioCtx.createOscillator();
    const lfoGain = audioCtx.createGain();
    lfo.frequency.value = 0.05;
    lfoGain.gain.value = theme === 'tavern' ? 80 : 200;
    lfo.connect(lfoGain).connect(filter.frequency);

    osc1.connect(filter);
    osc2.connect(filter);
    filter.connect(out);

    osc1.start();
    osc2.start();
    lfo.start();

    ambientNodes = { osc1, osc2, lfo, out };
  }

  function stopAmbient() {
    if (!ambientNodes) return;
    const audioCtx = ensureContext();
    const { osc1, osc2, lfo, out } = ambientNodes;
    out.gain.linearRampToValueAtTime(0.0001, audioCtx.currentTime + 0.8);
    setTimeout(() => {
      [osc1, osc2, lfo].forEach((n) => { try { n.stop(); } catch (e) {} });
      ambientNodes = null;
    }, 900);
  }

  function setSoundOn(value) {
    soundOn = value;
    localStorage.setItem('inv_sound_on', JSON.stringify(value));
  }

  function setMusicOn(value) {
    musicOn = value;
    localStorage.setItem('inv_music_on', JSON.stringify(value));
    if (value) startAmbient(); else stopAmbient();
  }

  function refreshAmbientForTheme() {
    if (musicOn) {
      stopAmbient();
      setTimeout(startAmbient, 950);
    }
  }

  function attachGlobalClickSfx() {
    document.addEventListener('click', (e) => {
      const target = e.target.closest('button, a.btn, .nav-item, .product-pick, .av-toggle, .theme-toggle');
      if (target) playClick();
    });
  }

  return {
    isSoundOn: () => soundOn,
    isMusicOn: () => musicOn,
    setSoundOn,
    setMusicOn,
    playClick,
    refreshAmbientForTheme,
    attachGlobalClickSfx,
    ensureContext,
  };
})();

document.addEventListener('DOMContentLoaded', () => {
  Audio2.attachGlobalClickSfx();
  // Ambient music needs a user gesture to start (browser autoplay policy);
  // if the user previously had it on, resume on first interaction.
  if (Audio2.isMusicOn()) {
    const resumeOnce = () => {
      Audio2.ensureContext();
      Audio2.setMusicOn(true);
      document.removeEventListener('click', resumeOnce);
    };
    document.addEventListener('click', resumeOnce, { once: true });
  }
});
