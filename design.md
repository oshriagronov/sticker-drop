# Design System: Vibrant Noir (StickerDrop)

The design system for **StickerDrop** (Google Stitch project: **StickerDrop**) is built on a foundation of **Corporate Modernism** infused with a **High-Contrast Dark Energy**. It targets a creative, mobile-first audience that values both efficiency and aesthetic pleasure. The visual style is professional yet playful—mimicking the "premium" feel of high-end developer tools while maintaining the approachability of a creative sticker creator.

---

## Colors

This design system utilizes a high-performance dark palette.

- **Primary (`#00F5A0` / Spring Mint):** Used for critical actions, highlights, primary buttons, and active indicators.
- **Background (`#051424` / Deep Navy-Charcoal):** Main screen canvas base color.
- **Surface Container (`#122131`):** Sub-surface elements, drawers, and secondary containers.
- **Surface Container High (`#1C2B3C`):** Higher elevation surfaces, hovered states, and dialog inputs.
- **Card Surface (`#2A3439`):** Primary card container background for sticker packs.
- **On Surface (`#D4E4FA`):** Main text color with crisp contrast.
- **On Surface Variant (`#B9CBBD`):** Subtitles, secondary text, and descriptions.
- **Outline / Border (`#849588` / `#3B4A40`):** Low-contrast borders for cards and containers.
- **Error (`#FFB4AB`):** Destructive actions, delete icons, and error alerts.

### Color Tokens Reference (Hex)
```yaml
surface: '#051424'
surface-dim: '#051424'
surface-bright: '#2C3A4C'
surface-container-lowest: '#010F1F'
surface-container-low: '#0D1C2D'
surface-container: '#122131'
surface-container-high: '#1C2B3C'
surface-container-highest: '#273647'
card-surface: '#2A3439'
on-surface: '#D4E4FA'
on-surface-variant: '#B9CBBD'
outline: '#849588'
outline-variant: '#3B4A40'
primary: '#CDFFDE'
primary-container: '#00F5A0'
on-primary-container: '#003921'
secondary: '#C1C7CB'
secondary-container: '#444A4D'
error: '#FFB4AB'
error-container: '#93000A'
```

---

## Typography

The typography strategy pairs **Plus Jakarta Sans** for modern geometric UI headers and body text with **JetBrains Mono** for technical labels, status indicators, and counters.

### Type Scale
- **Display Large (`display-lg`):** Plus Jakarta Sans, 40sp / Line Height 48sp, Weight 800 (Extrabold), Letter Spacing -0.02em
- **Headline Medium (`headline-md`):** Plus Jakarta Sans, 24sp / Line Height 32sp, Weight 700 (Bold)
- **Headline Small (`headline-sm`):** Plus Jakarta Sans, 20sp / Line Height 28sp, Weight 600 (SemiBold)
- **Body Large (`body-lg`):** Plus Jakarta Sans, 16sp / Line Height 24sp, Weight 400 (Regular)
- **Body Small (`body-sm`):** Plus Jakarta Sans, 14sp / Line Height 20sp, Weight 400 (Regular)
- **Label Medium (`label-md`):** JetBrains Mono, 12sp / Line Height 16sp, Weight 500 (Medium), Letter Spacing 0.05em

---

## Layout & Spacing

- **Base Unit:** 8dp rhythm.
- **Container Padding:** 24dp for screen edges.
- **Card Gap:** 20dp between bento grid items.
- **Gutter:** 16dp.
- **Interactive Minimum Target:** Touch targets must maintain a minimum height of 48dp.

---

## Elevation & Depth

Depth is created via **Tonal Layering** and **Soft Mint Glow Shadows**:
- **Level 0 (Background):** Base color `#051424`
- **Level 1 (Cards):** Card surface `#2A3439` with subtle 1px `#3B4A40` outline stroke.
- **Level 2 (Dialogs/Menus):** Surface container `#1C2B3C` with glass edge stroke.
- **Mint Glow:** Primary CTA buttons feature a soft mint glow shadow `rgba(0, 245, 160, 0.15)`.

---

## Shapes & Radii

- **Cards:** 24dp rounded corners (`rounded-[24px]`).
- **Input Fields & Secondary Buttons:** 12dp to 16dp rounded corners.
- **Sticker Thumbnails:** 12dp rounded corners.
- **Floating Action Buttons & Navigation Pills:** Fully pill-shaped (`RoundedCornerShape(50)`).

---

## Component Guidelines

1. **Sticker Pack Cards:**
   - Background `#2A3439` with 24dp corner radius.
   - Includes circular pack tray icon thumbnail, title, author name, sticker count pill (`X / 30 Stickers`), preview thumbnail row, delete action, and CTAs ("Add Sticker" & "Add to WhatsApp").
2. **Buttons:**
   - **Primary Action:** Solid `#00F5A0` fill with dark text `#003921`, bold font, and soft mint glow shadow.
   - **Secondary Action:** Outlined 2dp stroke `#3B4A40` with mint text `#00E293`.
3. **Pills & Badges:**
   - JetBrains Mono text in `#00E293` enclosed in rounded pill containers with a subtle border.
4. **Bottom Navigation Bar:**
   - Floating pill nav bar with rounded full shape, dark container `#0D1C2D`, active pill indicator in `#444A4D` / `#00F5A0`.
