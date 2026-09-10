# Chess World - Native Android 2D Chess Game

An artistic, immersive native Android 2D Chess game built with **Kotlin 2.2**, **Jetpack Compose Material 3**, and a custom **offline AI minimax engine**.

Designed around the vision:  
> *"Chess is not just a game. It's an atmosphere."*

---

## 🎨 Design Philosophy & "Chess World" Aesthetic

The game departs completely from generic black-and-gold mobile dashboards. It is crafted as a tactile, warm, environmental chess club experience:

- **Cozy Study Room & Tabletop Lighting**: Deep charcoal, warm cedar/walnut wood, aged parchment, cream, muted moss green, soft bronze, and amber candlelight accents.
- **Physical Tabletop Chessboard**: Frame bevels, recessed grooves, realistic ground-contact piece shadows, and smooth gliding animations along parabolic arcs.
- **Storybook Handcrafted Pieces**: Direct integration of the master 12-piece hand-drawn chess illustration pack (`chess-pack.zip`) featuring character-infused kings, queens, knights, rooks, bishops, and pawns.
- **Tournament Parchment Scorecards**: Player cards styled as physical scoresheets with fountain pen ink typography, live captured piece counts, material evaluation differentials, and wax-seal turn badges.
- **Illustrated Difficulty Environments**:
  - **Easy**: Cozy coffee table study (*"Relax and learn"*)
  - **Medium**: Tactician's lined chess notebook (*"Test your tactics"*)
  - **Hard**: Ticking tournament clock table (*"Think several moves ahead"*)
  - **Expert**: Grandmaster midnight library (*"Master the board"*)
- **Tactile Side Selection**: 3D elevated wooden plinths for White, Random, and Black piece selection with interactive lifts and realistic shadows.
- **Pass & Play Local Mode**: Tabletop cards with customizable player names and turn coordination.
- **Dramatic Match Conclusions**: High-impact checkmate scene with victor king standing tall, tilted defeated king, warm dramatic lighting, scorecard stats, and peaceful draw scenes.
- **Parchment Pause Overlay**: Notebook pause menu keeping the match visible under a soft vignette.
- **Illustrated Player's Handbook**: Illustrated rules guide covering basics, piece moves, castling, en passant, promotion, checkmate, and draws.

---

## 🏛️ Decoupled Theme System

Board themes and Piece themes are **100% decoupled** via `ThemeRegistry` in `com.example.chessgame.theme`:
Players can freely mix and match any board with any piece set.

### Board Themes (6 Built-In):
1. **Artisan Wood**: The hero hand-drawn wooden board from the master pack with rich timber texture and subtle bevel.
2. **Dark Walnut**: Warm mahogany and dark walnut tiles with brass corner inlay.
3. **Forest Grove**: Birch wood light squares with deep evergreen moss dark squares.
4. **Vintage Parchment**: Aged sepia paper texture with iron gall ink borders.
5. **Midnight Study**: Indigo midnight study tiles under a warm reading lamp.
6. **Royal Marble**: Polished Italian Carrara marble and graphite stone.

### Piece Themes (4 Built-In):
1. **Storybook Handcrafted**: The hero 12-piece illustrated raster set from `chess-pack.zip`.
2. **Tournament Staunton**: Weighted vector tournament pieces with specular highlights.
3. **Carved Boxwood**: Chiseled boxwood and dark ebony with satin bevels.
4. **Modern Slate**: Architectural minimalist silhouettes with Bauhaus balance.

### Theme Studio & Interactive 8×8 Preview:
- **Theme Studio**: 2-tab collection gallery with 2-column tactile preview cards and live swatches.
- **Theme Preview**: Fullscreen interactive 8×8 preview board with board rotation (`⟲`), move testing on sample layouts, and one-tap theme activation.

---

## ⚡ Complete Chess Engine & Offline Voice Announcer

All working chess functionality and tournament FIDE rules are strictly preserved:

- **Move Generation**: 100% legal moves (sliding raycasting, knight jumps, pawn moves, pinned piece checks).
- **Special Moves**: Kingside & Queenside castling with through-check validation, en passant with expiration, and 4-piece pawn promotion.
- **Draw Conditions**: Stalemate, threefold repetition, 50-move rule, insufficient material, and mutual draw agreement.
- **Offline AI (Minimax & Alpha-Beta Search)**: 4 distinct AI profiles (Easy, Medium, Hard, Expert).
- **Procedural Audio Synthesizer**: 100% offline PCM sound engine via Android `AudioTrack` (move, capture, check, castle, fanfare).
- **Offline Voice Announcer**: Android Text-to-Speech (`VoiceAnnouncer.kt`) announcing check, checkmate, castling, and promotions.

---

## 📝 Future Theme Generation Guide

See [`ASSET_GENERATION_GUIDE.md`](file:///d:/My%20Projects/Android%20app/Chess%20Game/ASSET_GENERATION_GUIDE.md) for master prompt templates and specialized prompts for:
- Wood, Marble, Metal, Glass, Paper, Forest, Ocean, Royal, Fantasy, Cyber, and Minimal sets.
- Step-by-step instructions on dropping new assets into `res/drawable` and registering them in `ThemeModels.kt` without changing any engine code.

---

## 📦 Debug APK & Installation

- **Root APK**: `ChessGame-debug.apk`
- **Gradle APK**: `app/build/outputs/apk/debug/app-debug.apk`

### Install via ADB:
```bash
adb install -r ChessGame-debug.apk
```

---

## 🛠️ Tech Stack & Requirements

- **Language**: Kotlin 2.2
- **UI Framework**: Android Jetpack Compose Material 3
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 16 (API 36)
- **Build System**: Gradle 9.1.0 / Android Gradle Plugin 8.9.0
- **Audio Engine**: Android AudioTrack PCM Synthesizer & Offline TTS Voice Announcer
