# Grandmaster Arena — Asset Generation & Visual Art Guide

This guide details the creative art direction, aesthetic standards, and exact reusable prompt templates for generating game-ready visual assets for the **Grandmaster Arena / Tactical Monolith** mobile chess experience.

---

## 1. Visual Direction: "Tactical Monolith / Grandmaster Arena"

### Core Identity & Tone
- **Atmosphere:** High-stakes competitive game arena, tactical command monolith, futuristic deep-void aesthetic with obsidian, brushed titanium, deep slate, and vivid cybernetic energy accents.
- **Lighting & Materials:** Subtle metallic reflections, cold cinematic rim lighting, deep space ambient occlusion, and sharp glowing status highlights.
- **Color Hierarchy:**
  - **Void Abyss & Deep:** `#090D14`, `#0B111A` — infinite backdrop depth.
  - **Titanium Armor Surfaces:** `#131A24`, `#1A2433`, border `#2B3A4F` — tactile hardware panels.
  - **Cyber Cyan (Active/Focus):** `#00E5FF` — strategic highlights, hints, best moves.
  - **Solar Amber (Valor/Streak):** `#FFAB00` — master ranks, XP, puzzles, warnings.
  - **Crimson Alert (Threat/Combat):** `#FF3366` — in-check warnings, blunders, combat alerts.
  - **Emerald Victory (Trophy/Success):** `#00E676` — win states, confirmed moves, achievements.

---

## 2. Master Generation Prompts by Asset Category

### A. Opponent Avatars & Commander Dossiers
Used for the AI Setup roster, live in-game opponent HUD cards, and player profiles.

- **Art Style Specs:** Painterly digital concept art, stylized character portrait, dynamic dramatic key lighting, dark sci-fi/fantasy tactical uniform or commander armor, subtle glowing eye or crest accents, dark atmospheric background. Aspect ratio: 1:1 square. No borders, no text.

- **Level 1 — Cadet Valen (Recruit / Easy):**
  > *"Stylized digital portrait of a young eager chess commander cadet, Valen. Short brown hair, sleek high-collar cyber uniform with subtle blue trim, determined expression, confident slight smile. Tactical sci-fi academy command deck background with soft holographic blue lighting. High resolution character art, game avatar portrait."*

- **Level 2 — Strategist Lyra (Tactician / Medium):**
  > *"Stylized digital concept art portrait of a sharp, brilliant female chess tactician commander named Lyra. Sleek dark braided hair, futuristic cybernetic tactical visor over one eye with glowing amber-cyan data telemetry, sharp focused gaze, wearing sleek slate-grey command armor. High-tech battlefield command bridge background, cool cinematic rim lighting."*

- **Level 3 — Commander Voron (Warmaster / Hard):**
  > *"Stylized digital portrait of a formidable, battle-hardened chess warlord commander named Voron. Short silver hair, intense piercing eyes, subtle battle scar across cheek, wearing heavy dark titanium tactical armor with crimson energy crests. Moody dramatic red rim lighting, atmospheric battle smoke background, commanding intense game character portrait."*

- **Level 4 — Oracle Kairos (Grandmaster / Expert):**
  > *"Stylized epic digital art of an enigmatic cyber-grandmaster AI entity named Kairos. Ethereal holographic face with luminous cybernetic neural circuits, glowing cyan and gold eyes, wearing intricate celestial armor robe. Dark cosmic data matrix background, radiant energy aura, master game avatar portrait, ultra detailed."*

---

### B. Environment & Arena Backgrounds
Used for the global atmospheric scaffold (`bg_arena_monolith.jpg`).

- **Master Prompt:**
  > *"Futuristic grandmaster chess arena monolith environment. Cinematic wide angle view of an epic monolithic dark titanium and obsidian battle chamber. Glowing neon cyber-cyan and gold circuitry running through geometric pillars. Deep atmospheric fog, moody volumetric lighting, dramatic sci-fi architecture. Rich deep dark palette (#090D14 base). Cinematic digital game environment, 4k, no characters, no text, no interface overlays."*

---

### C. Insignias, Emblems & Victory Crests
Used for App Icon, Splash Screen, Main Lobby header, and Match Result Victory overlays.

- **Primary Game Monolith Crest (`logo_game_emblem.jpg`):**
  > *"Magnificent metallic 3D chess game logo emblem. A powerful geometric knight chess piece merged with a king's crown and a tactical shield. Crafted from brushed dark titanium and polished obsidian, glowing with electric cyan energy lines and gold accents. Centered, isolated on dark moody background, high-end mobile game app logo icon, dramatic lighting, 8k render."*

- **Victory Laurels & Honor Crest (`banner_victory_crest.jpg`):**
  > *"Radiant golden victory crest banner for a grandmaster chess game. Gilded laurel wreath framing crossed energy swords and a majestic royal crown, glowing with brilliant emerald and amber triumphant light. Dark dramatic cinematic background, sparkling victory particles, game achievement badge art, ultra high quality."*

---

### D. Achievement & Honor Badges
Used in the Commander Dossier (`PlayerProfileDialog.kt`).

- **Specs:** 1:1 square, metallic sculpted icon inside a hexagonal or circular bezel, vibrant glowing center, transparent or deep dark background.
- **Master Prompt Template:**
  > *"Tactical game achievement medal icon for [ACHIEVEMENT NAME: e.g. First Checkmate / Grandmaster Slayer / Tactical Prodigy]. Sleek hexagonal titanium frame with glowing [CYAN / GOLD / CRIMSON] energy core and a detailed relief carving of [SYMBOL: e.g. crowned king falling / shattered chess sword / glowing brain labyrinth]. High-end mobile game medal icon, isolated, 3D render."*

---

### E. Board Themes & Piece Sets

The theme system in `com.example.chessgame.theme.ThemeModels` supports modular, independent pairing of any Board Theme with any Piece Theme.

#### 1. Board Theme Prompt Template:
> *"Orthogonal top-down 2D chess board texture asset in [THEME NAME: e.g. Cyber Monolith / Obsidian Lapis / Brushed Damascus]. Exact 8x8 square grid. Crisp contrast between light and dark tiles. Subtle brushed metallic grain, beveled tile edges, recessed titanium outer frame. Perfect top-down bird's-eye view, no perspective distortion, no chess pieces, no text, no numbers. 2048x2048 PNG."*

#### 2. Piece Set Prompt Template (12 individual assets):
> *"Complete 12-piece chess set in [THEME NAME: e.g. Cybernetic Titanium] style. Rendered in transparent alpha PNG format:
> White pieces: King, Queen, Rook, Bishop, Knight, Pawn in polished silver titanium with cyan energy accents.
> Black pieces: King, Queen, Rook, Bishop, Knight, Pawn in matte obsidian with crimson energy accents.
> Consistent orthogonal side-angle perspective (30-degree tilt), identical lighting from top-left, clean silhouettes, no board, game-ready sprites."*

---

### F. Loading Animation & Motion Video Assets

If creating motion video loops for Splash / Boot sequences:
- **Format:** MP4 / WebM / WebP Animation (60 FPS, 1080x1920 portrait).
- **Duration:** 2.5 to 4.0 seconds (clean loopable cut).
- **Concept:**
  > *"Cinematic 3D animation of two giant stylized chess kings (one obsidian with crimson glow, one platinum with cyan glow) standing face to face on a floating geometric titanium board. Energy pulses surge through the floor tiles as glowing sparks swirl into an explosive crown insignia. Dark atmospheric sci-fi arena."*
- **Native Compose Fallback:** When video generation is not available or offline, the native Compose `ArenaBackgroundScaffold` with pulsating energy halos and multi-stage status telemetry (`BOOTING CHESS ENGINE... 100%`) provides an instantaneous, ultra-reliable 60 FPS boot sequence.

---

## 3. Registering New Assets into the Android Codebase

1. Place image files into `app/src/main/res/drawable/` (lowercase naming convention: `avatar_*`, `bg_*`, `icon_*`, `badge_*`).
2. Add colors and style tokens in `app/src/main/java/com/example/chessgame/theme/GameDesignSystem.kt`.
3. Register new boards or pieces in `app/src/main/java/com/example/chessgame/theme/ThemeModels.kt`.
4. Test in `THEME PREVIEW` and `AI SETUP` to verify readability, contrast, and performance.
