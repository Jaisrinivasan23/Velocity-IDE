---
name: professional-frontend-styling
description: Velocity Design System - premium minimal dark IDE styling for React web files, Jetpack Compose files, and UI specs inside this repository.
---

# Velocity Design System

Apply these guidelines to all frontend components (React web files, Jetpack Compose, or UI specs) built inside this repository.

## 1. Color Palette

**Surfaces** (dark, premium, minimal):
- Primary Background: `#0E0F11`
- Elevated Surface (headers, cards): `#141519`
- Interactive Surface (inputs, dialogs, active tabs): `#1C1E24`
- Highest overlay (dropdowns): `#23262D`
- Terminal surface: `#0A0B0C`

**Borders**:
- Hairline standard: `#2A2D35`
- Subtle divider: rgba(255, 255, 255, 0.08)

**Text**:
- Primary: `#F2F3F5`
- Secondary: `#9AA1AC`
- Muted (placeholders/disabled): `#6B7280`

**Accent (brand orange - retained)**:
- Accent: `#FD5E02`
- Accent pressed: `#FF7A2A`
- Accent soft container (14% alpha): rgba(253, 94, 2, 0.14)

**Status**:
- Success: `#35D07F` (soft container 13% alpha)
- Warning: `#F5B94C`
- Error: `#FF5C68` (soft container 13% alpha)
- Info: `#5BA7FF`

## 2. Typography

- **UI Font**: Professional system sans-serif (`-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif` on web; default system UI on Android).
- **Mono Font**: `JetBrains Mono`, `Fira Code`, or `Consolas` for code, terminal, file names, and technical metadata.
- **Scale**: Display 24/700, Heading 20/700, Title 18/600, Section 16/600, Body 14/400, Small 12/500, Caption 11/500. Tight letter-spacing on large headings; keep text compact — this is a developer tool.

## 3. Boxes and Containers

- **Border Radius**: Controls 8dp, Cards 12dp, Sheets 16dp, chips/pills fully rounded. No aggressive pill-everything styling.
- **Shadows**: None or extremely subtle. Use hairline borders and surface contrast for separation.
- **Flat treatment**: Solid fills only — no heavy gradients, no blurry glass surfaces. Subtle 1dp borders are the primary separator.

## 4. Spacing & Layout

- 4dp spacing grid: 4, 8, 12, 16, 20, 24, 32.
- Primary screen padding: 16dp. Section gaps: 24dp.
- Minimum touch target: 48 x 48dp on Android; 44px+ on web.
- Mobile-first; the phone layout is the primary experience.

## 5. Motion

- Fast micro-interactions, 150–250ms, Material easing curves.
- Use motion for status changes, streaming content, sheet/dialog entrances, and pressed feedback. Avoid continuous decorative animation.

## 6. Iconography & Copy Tone

- **Emojis**: Absolutely NO emojis in UI labels, placeholders, or headers.
- **Copy Tone**: Clear, professional, standard. Avoid enthusiastic or conversational phrasings.
- **Honesty**: Never show a fake success state for a capability that isn't wired. Use muted/neutral states with quiet, accurate copy instead.

## 7. Compose Implementation

- All colors/radii come from `com.velocity.ide.ui.theme.AppTokens` — never inline hex values in screens.
- `VelocityTheme` in `ui/theme/Theme.kt` maps tokens into the Material3 dark scheme, typography, and shapes.
- Keep composables presentation-only: no business logic, no state writes beyond local UI state.