# Chess Asset & Theme Generation Guide

This guide details how to generate, configure, and register new **Board Themes** and **Piece Themes** in the Chess World ecosystem.

The system is architecturally decoupled: **Board Themes** and **Piece Themes** are configured independently. Any board can be paired with any piece set without touching or changing chess logic.

---

## 1. Master Generation Prompts

### Board Theme Master Prompt
Use this prompt template when generating a full 8×8 board illustration or textured board image:

> *"Create a premium 2D 8x8 chess board asset in [THEME NAME] style. Use an exact square 8x8 grid. Create distinct but harmonious light and dark squares with high piece-to-square contrast. Include subtle material texture, realistic depth, beveled edges, and soft cinematic lighting. No chess pieces. No text. No UI overlays. No perspective distortion (perfect orthogonal top-down view). Game-ready. High resolution (2048x2048 PNG)."*

---

### Piece Theme Master Prompt
Use this prompt template when generating a 12-piece chess set:

> *"Create a complete premium 2D chess piece set in [THEME NAME] style. Generate exactly 12 pieces: white king, white queen, white rook, white bishop, white knight, white pawn, black king, black queen, black rook, black bishop, black knight, black pawn. All pieces must share identical perspective, lighting direction, proportions, height hierarchy, and material finish. Transparent background (alpha channel PNG). Clean silhouettes and edges. Game-ready PNG assets. No board. No text. No UI."*

---

## 2. Specialized Style Prompt Library

### 1. Wood (Carved Boxwood & Rosewood)
- **Board:**
  > *"Top-down orthogonal 2D chess board crafted from natural dark walnut and light honey oak. Realistic wood grain lines running consistently across tiles. Hand-carved bevel frame with subtle recessed groove and brass corner inlay. Soft warm lamp illumination. 2048x2048 PNG, perfectly square, no pieces, no text."*
- **Pieces:**
  > *"Complete 12-piece chess set hand-turned from natural boxwood (warm cream) and dark rosewood (rich chocolate). Subtle lathe-turned ridges, tactile satin polish, gentle bottom contact shadow, clean transparent background. 12 separate high-res PNGs."*

### 2. Marble (Carrara & Obsidian)
- **Board:**
  > *"Top-down 2D chess board crafted from polished white Carrara marble with light grey veins and deep black obsidian with subtle gold flecks. Mirror-polished stone reflection, crisp mitered border frame. No pieces, no text, square orthogonal 2048x2048 PNG."*
- **Pieces:**
  > *"12 chess pieces sculpted from translucent Carrara marble and carved obsidian stone. Refined classical Staunton silhouette with polished highlights and realistic subsurface scattering. Transparent background, game-ready PNG."*

### 3. Metal (Forged Iron & Brushed Brass)
- **Board:**
  > *"Tabletop chess board fabricated with brushed brass light squares and anodized gunmetal dark squares. Industrial bevel rivets along the chamfered border. Ambient rim lighting, perfectly square 8x8 orthogonal, no pieces, no text."*
- **Pieces:**
  > *"12 solid metal chess pieces: warm brushed brass for White, heavy blackened cast iron for Black. Weighted solid bases, precision lathed details, tactile metallic sheen. Transparent background."*

### 4. Glass (Frosted & Smoked Crystal)
- **Board:**
  > *"Frosted sea-glass light squares alternating with smoked dark crystal squares. Soft refractive edge bevels, subtle internal caustic illumination. Completely flat top-down grid, no perspective distortion, no pieces."*
- **Pieces:**
  > *"12 chess pieces molded from optical crystal: frosted glass White pieces and smoky quartz Black pieces. Soft internal light glow, crisp refractions, transparent PNG."*

### 5. Paper & Ink (Vintage Etching & Parchment)
- **Board:**
  > *"Hand-drawn 2D chess board rendered in vintage parchment paper texture with sepia and iron gall ink hatched shading. Hand-lettered coordinate margins, deckled paper edges. High resolution, perfectly square."*
- **Pieces:**
  > *"12 hand-drawn storybook chess character pieces in vintage ink and watercolor wash. Expressive royal figures, tactile paper grain texture, transparent background."*

### 6. Forest Grove (Moss & Birch)
- **Board:**
  > *"Natural woodland chess board with pale birch light squares and deep evergreen moss dark squares. Weathered timber border with subtle botanical leaf accents along corners. Top-down, 8x8 square."*
- **Pieces:**
  > *"12 organic wood-spirit chess pieces carved from living birch and ancient bog oak. Subtle vine and leaf motifs integrated into piece crowns, transparent background."*

### 7. Ocean & Abyssal (Aquamarine & Coral)
- **Board:**
  > *"Nautical tabletop chess board with mother-of-pearl light squares and deep abyssal navy dark squares. Polished drift-wood frame with wave bevel lines. Orthogonal, no pieces."*
- **Pieces:**
  > *"12 ocean-themed chess pieces: pearlescent ivory White pieces and deep kraken-charcoal Black pieces with subtle marine contours. Transparent PNG."*

### 8. Royal Court (Ivory & Burgundy Velvet)
- **Board:**
  > *"Majestic palace chess board: polished cream ivory squares paired with deep imperial burgundy enameled squares. Gilded filigree border in antique gold leaf. Top-down, square."*
- **Pieces:**
  > *"12 heirloom royal chess pieces: crowned monarchs with detailed scepters, mitres with inlaid jewels, draped cloaks. Rich antique gold and silver trim. Transparent background."*

### 9. Fantasy Realm (Elven Silver & Dwarven Rune)
- **Board:**
  > *"Mythic chess board made of mithril silver light squares and runic dark basalt stone. Subtle glowing runes inscribed along the border frame. Top-down orthogonal 8x8, no pieces."*
- **Pieces:**
  > *"12 fantasy-inspired chess pieces: elegant high-elven knights and tall crowns for White, dwarven runic armor for Black. Clean transparent background."*

### 10. Cyber Horizon (Neon Grid & Carbon Fiber)
- **Board:**
  > *"Dark carbon-fiber textured chess board with luminescent cyan grid accents and deep matte graphite tiles. Subtle cybernetic edge chamfer. No pieces, orthogonal top-down."*
- **Pieces:**
  > *"12 minimalist holographic / cyber-sculpted chess pieces with sharp geometric facets and neon core lines (electric cyan vs hot amber). Transparent PNG."*

### 11. Minimal Nordic (Scandinavian Ash & Charcoal)
- **Board:**
  > *"Ultra-clean Scandinavian chess board with pale ash wood and matte charcoal slate squares. Zero ornamentation, pure proportions, subtle tactile grain. 2048x2048 square."*
- **Pieces:**
  > *"12 minimalist geometric chess pieces: clean Bauhaus-inspired silhouettes, matte finish, pure geometry. Transparent PNG."*

---

## 3. How to Add a New Theme to the Project

Follow this step-by-step checklist to register a new board or piece theme without modifying any chess engine code.

### Step 1: Prepare and Optimize Assets
1. **Board Theme:**
   - If using a full raster image, name it `board_<theme_id>.png` (lowercase, alphanumeric + underscores only).
   - Place into `app/src/main/res/drawable/`.
   - Ensure resolution is 1024×1024 or 2048×2048 square.
2. **Piece Theme:**
   - 12 transparent PNGs named:
     - `piece_<theme_id>_king_white.png`, `piece_<theme_id>_king_black.png`
     - `piece_<theme_id>_queen_white.png`, `piece_<theme_id>_queen_black.png`
     - `piece_<theme_id>_rook_white.png`, `piece_<theme_id>_rook_black.png`
     - `piece_<theme_id>_bishop_white.png`, `piece_<theme_id>_bishop_black.png`
     - `piece_<theme_id>_knight_white.png`, `piece_<theme_id>_knight_black.png`
     - `piece_<theme_id>_pawn_white.png`, `piece_<theme_id>_pawn_black.png`
   - Place into `app/src/main/res/drawable/`.

### Step 2: Register in `ThemeModels.kt`
Open `app/src/main/java/com/example/chessgame/theme/ThemeModels.kt`:

#### To add a Board Theme:
```kotlin
val MY_NEW_BOARD = BoardTheme(
    id = "my_board_id",
    name = "Celestial Marble",
    description = "Polished moonstone and midnight lapis with gold veins",
    lightSquare = Color(0xFFE8ECEF),
    darkSquare = Color(0xFF233245),
    border = Color(0xFF16202C),
    coordinateColorLight = Color(0xFF233245),
    coordinateColorDark = Color(0xFFE8ECEF),
    selectedSquareColor = Color(0x66E5A93C),
    legalMoveColor = Color(0x805B9A68),
    captureColor = Color(0xB3C93B2B),
    boardDrawableRes = R.drawable.board_celestial // Optional: omit for procedural tiles
)

// Add to BOARDS list:
val BOARDS = listOf(
    ARTISAN_WOOD,
    DARK_WALNUT,
    FOREST_GROVE,
    MY_NEW_BOARD, // <-- Added here
    ...
)
```

#### To add a Piece Theme:
```kotlin
val MY_NEW_PIECES = PieceTheme(
    id = "my_pieces_id",
    name = "Obsidian & Gold",
    description = "Solid brass and sculpted obsidian stone pieces",
    hasCustomDrawables = true,
    whiteKingRes = R.drawable.piece_obsidian_king_white,
    whiteQueenRes = R.drawable.piece_obsidian_queen_white,
    whiteRookRes = R.drawable.piece_obsidian_rook_white,
    whiteBishopRes = R.drawable.piece_obsidian_bishop_white,
    whiteKnightRes = R.drawable.piece_obsidian_knight_white,
    whitePawnRes = R.drawable.piece_obsidian_pawn_white,
    blackKingRes = R.drawable.piece_obsidian_king_black,
    blackQueenRes = R.drawable.piece_obsidian_queen_black,
    blackRookRes = R.drawable.piece_obsidian_rook_black,
    blackBishopRes = R.drawable.piece_obsidian_bishop_black,
    blackKnightRes = R.drawable.piece_obsidian_knight_black,
    blackPawnRes = R.drawable.piece_obsidian_pawn_black
)

// Add to PIECES list:
val PIECES = listOf(
    STORYBOOK_HANDCRAFTED,
    TOURNAMENT_STAUNTON,
    MY_NEW_PIECES, // <-- Added here
    ...
)
```

### Step 3: Verification Checklist
1. **Readability:** Open `THEME STUDIO` in the app and test in `THEME PREVIEW`. Verify that light and dark pieces are immediately distinguishable on all square shades.
2. **Move Indicators:** Tap a piece and ensure the sage move dots and terracotta capture rings stand out clearly against both square colors.
3. **Check Highlight:** Place King into check and verify the crimson/amber pulse is visible without obscuring the King piece artwork.
4. **Mix & Match:** Select the new board with existing piece sets, and select the new piece set with existing boards.

---
*Created for the Chess Game Tabletop Experience.*
