# Professional IDE UI Skills & Guidelines

## 1. Color Palette
- **Primary Background**: `#2b2b2b`
- **Secondary Background**: `#1e1e1e` (or slightly darker/lighter for contrast, e.g. `#333333`)
- **Accent Color**: `#FD5E02` (Orange)
- **Text Primary**: `#ffffff` or `#e0e0e0`
- **Text Secondary**: `#a0a0a0`
- **Borders**: `#404040` (Subtle and sharp)

## 2. Typography
- **UI Font**: Professional sans-serif like `Inter`, `Roboto`, or system fonts (`-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif`).
- **Mono Font**: Classic IDE font like `JetBrains Mono`, `Fira Code`, `Consolas`, or `Menlo`.

## 3. UI Elements (Boxes & Containers)
- **Border Radius**: Use minimal rounding (`2px` or `4px` maximum) or sharp corners (`0px`) to match professional IDEs like VS Code or IntelliJ. Do not use overly rounded pill shapes (`99px`) or large radii (`20px`).
- **Shadows**: Minimal to no shadows. Use border lines for separation instead of heavy drop shadows or glows.
- **Glassmorphism**: Completely remove all glassmorphism, blur effects, or translucent backgrounds. Use solid colors.

## 4. Iconography and Text
- **Emojis**: Absolutely NO emojis in the UI text, tooltips, or placeholders. Use standard Lucide React icons instead if visual indicators are needed.
- **Tone**: Keep text concise, professional, and standard. Avoid conversational or overly enthusiastic phrasing.
