# `02_VELOCITY_IDE_UI_DESIGN.md`

````md
# VELOCITY-IDE
## Mobile-First UI/UX Design Specification

---

## 1. Design Objective

Build a **premium, mobile-first developer application** for Velocity-IDE.

The UI must feel like a serious next-generation Android development environment.

It should combine the visual language of:

- a modern mobile IDE
- an AI agent workspace
- a terminal
- a live app preview
- a deployment dashboard

without feeling like five separate applications.

The primary experience is mobile.

Target devices:

- 390 × 844
- 412 × 915

Desktop support is secondary.

---

# 2. Design Personality

Velocity-IDE should feel:

- premium
- technical
- futuristic
- fast
- minimal
- confident
- developer-focused

The interface should communicate:

> **Power without clutter.**

Avoid:

- generic SaaS dashboards
- excessive gradients
- cartoon illustrations
- AI robot imagery
- giant hero sections
- excessive glassmorphism
- unnecessary decorative elements
- desktop IDE layouts squeezed onto a phone

---

# 3. Visual System

## Background

Primary:

```text
#08090B
````

Secondary:

```text
#0D0F12
```

Elevated surface:

```text
#13161B
```

Higher elevated surface:

```text
#181C22
```

---

## Borders

Primary border:

```text
#252A32
```

Subtle border:

```text
rgba(255,255,255,0.06)
```

---

## Text

Primary:

```text
#F5F7FA
```

Secondary:

```text
#9AA2AF
```

Muted:

```text
#656D79
```

---

## Accent

Use a premium electric violet/indigo accent.

Suggested:

```text
#7C5CFF
```

Accent glow:

```text
rgba(124,92,255,0.18)
```

Do not use the accent everywhere.

Use it for:

* active navigation
* primary buttons
* AI state
* selected elements
* important highlights
* progress
* focus states

---

## Status colors

Success:

```text
#35D07F
```

Warning:

```text
#F5B94C
```

Error:

```text
#FF5C68
```

Info:

```text
#5BA7FF
```

---

# 4. Typography

Use:

## UI

**Inter**

Weights:

* 400
* 500
* 600
* 700

## Code

**JetBrains Mono**

Use for:

* code
* terminal
* file names where appropriate
* command output
* technical metadata

---

# 5. Typography Scale

Mobile:

```text
Display: 28px / 700
Heading: 22px / 700
Section: 16px / 600
Body: 14px / 400
Small: 12px / 500
Caption: 11px / 500
Code: 12–14px
```

Do not make text excessively large.

This is a developer tool, not a marketing website.

---

# 6. Spacing System

Use an 8px-based spacing system.

```text
4px
8px
12px
16px
20px
24px
32px
40px
48px
```

Primary mobile screen padding:

```text
16px
```

Large sections:

```text
24px
```

---

# 7. Corner Radius

Use consistent rounded surfaces.

```text
Small controls: 8px
Cards: 14px
Large panels: 18px
Bottom sheets: 24px
Buttons: 10–12px
```

Avoid extremely rounded "pill everything" styling.

---

# 8. Shadows

Use subtle shadows only.

Example:

```text
0 10px 40px rgba(0,0,0,0.28)
```

Cards should mostly be separated through:

* surface contrast
* borders
* elevation

rather than huge shadows.

---

# 9. Global Mobile Layout

Use:

```text
┌─────────────────────────────┐
│          Top Bar            │
├─────────────────────────────┤
│                             │
│                             │
│        Screen Content       │
│                             │
│                             │
├─────────────────────────────┤
│       Bottom Navigation     │
└─────────────────────────────┘
```

The bottom navigation should remain accessible.

Never place critical content behind it.

Use safe-area padding.

---

# 10. Bottom Navigation

Five destinations:

```text
Home
Workspace
Preview
Terminal
Deploy
```

Use icons plus small labels.

Active item:

* accent icon
* accent label
* subtle active background

Inactive:

* muted icon
* muted label

Navigation height:

Approximately:

```text
72px
```

---

# 11. Floating AI Button

The AI button is a central product element.

Place it slightly above the bottom navigation.

Appearance:

* circular
* accent background
* subtle glow
* AI/spark icon

Tap opens the AI Agent.

On relevant screens, the button can change context.

Examples:

Workspace:

> Ask about this code

Preview:

> Ask about this UI

Terminal:

> Explain this error

Deploy:

> Prepare deployment

The AI button should feel contextual rather than decorative.

---

# 12. Global Top Bar

Standard mobile top bar:

```text
←   VELOCITY-IDE                 ⋮
```

For project screens:

```text
←   PocketLedger             ● Running
```

Use:

* 52–60px height
* subtle bottom border
* compact controls

Do not overcrowd the top bar.

---

# 13. HOME SCREEN

## Purpose

The Home screen should communicate the entire product within seconds.

It should feel like the developer's command center.

---

## Header

Display:

```text
VELOCITY-IDE
```

Below:

```text
Good morning
```

Small secondary text:

```text
Ready to build?
```

Right side:

* notification icon
* settings icon

---

# 14. Home Hero Card

Large featured project card.

```text
┌──────────────────────────────┐
│  CURRENT PROJECT              │
│                              │
│  PocketLedger                │
│  React · Vite                │
│                              │
│  ● Build passed              │
│                              │
│  12 files     3 changes      │
│                              │
│  [ Open Workspace ]          │
└──────────────────────────────┘
```

Use subtle accent glow.

Do not make it overly decorative.

---

# 15. Build With AI Card

Below the current project:

```text
┌──────────────────────────────┐
│ ✦ BUILD WITH AI              │
│                              │
│ Describe what you want.      │
│ Velocity builds it.          │
│                              │
│ [ Start building ]           │
└──────────────────────────────┘
```

When tapped:

Open AI Agent.

Prefill:

```text
Build a modern expense tracker with
a dashboard, transaction history,
categories and local storage.
```

---

# 16. AI Shortcut Grid

Use compact cards.

```text
┌──────────────┐ ┌──────────────┐
│ ✦ Build      │ │ ⚡ Fix bug   │
│ App          │ │              │
└──────────────┘ └──────────────┘

┌──────────────┐ ┌──────────────┐
│ ◉ Explain    │ │ ↑ Deploy     │
│ Project      │ │              │
└──────────────┘ └──────────────┘
```

Keep icons simple.

---

# 17. Recent Projects

List:

```text
PocketLedger
React · Vite
Edited 2 min ago

CampusFlow
React · Vite
Edited yesterday

QuickShop
Next.js
Edited 3 days ago
```

Each project should have:

* project icon
* name
* stack
* status
* last modified time

---

# 18. WORKSPACE SCREEN

Workspace is the main IDE.

Header:

```text
←  PocketLedger                 ●
```

Below:

```text
Files       Code       AI
```

---

# 19. Files Tab

Display a clean project tree.

```text
PocketLedger

⌄ src
   ⌄ components
      BalanceCard.jsx
      TransactionList.jsx
      BottomNav.jsx

   App.jsx
   main.jsx

⌄ public

package.json
README.md
```

Use indentation clearly.

Folders should expand/collapse.

Tap a file to open it.

---

# 20. Code Editor

The editor should look realistic.

Header:

```text
TransactionList.jsx              ⋮
```

Tabs can show:

```text
App.jsx
TransactionList.jsx
```

Editor:

```text
38  const transactions = [
39    {
40      name: "Swiggy",
41      amount: -420,
42      category: "Food"
43    }
44  ];
```

Requirements:

* line numbers
* syntax highlighting
* monospace font
* current line highlight
* horizontal scrolling
* vertical scrolling
* modified indicator
* cursor appearance

Do not use an actual heavy IDE library unless needed.

A realistic visual editor is sufficient for the prototype.

---

# 21. Editor Bottom Toolbar

```text
⌕ Search
⌘ Command
▶ Run
✦ AI
```

Large touch targets.

The AI button should open contextual AI actions.

---

# 22. CONTEXTUAL AI SHEET

When the developer taps AI from the editor, show a bottom sheet.

Title:

```text
AI for TransactionList.jsx
```

Suggestions:

```text
Explain this file
Fix errors
Refactor this component
Improve performance
Add feature
Write tests
```

Input:

```text
Ask anything about this code...
```

The sheet should feel native to Android.

---

# 23. AI AGENT SCREEN

This is one of the most important screens.

Header:

```text
← AI Agent
```

Subtitle:

```text
Project-aware development agent
```

---

# 24. AI Conversation

User message:

```text
Build a modern expense tracker with
a dashboard, transaction history,
categories and local storage.
```

AI response:

```text
I can build that.

I'll create the project structure,
implement the dashboard and transaction
flow, add local persistence, then run
the application and verify the main flow.
```

---

# 25. AI Execution Plan

Show an expandable card.

```text
BUILD PLAN

✓ Understand requirements
✓ Plan architecture
✓ Create project structure
✓ Create dashboard
✓ Create transaction components
✓ Add local storage
○ Run verification
○ Final polish
```

Each step should animate from:

```text
○ pending
```

to:

```text
◌ running
```

to:

```text
✓ completed
```

---

# 26. AI Tool Activity

Show compact activity cards.

Examples:

```text
◉ Reading project
src/

✓ Creating App.jsx

✓ Updating TransactionList.jsx

✓ Installing dependencies

◌ Starting development server
```

This makes the AI feel like an actual agent.

---

# 27. AI Input

Bottom input:

```text
┌──────────────────────────────┐
│ Ask Velocity anything...     │
│                              │
│ +     🎙                ↑    │
└──────────────────────────────┘
```

Do not use excessive rounded styling.

---

# 28. LIVE PREVIEW SCREEN

This should be the most visually impressive screen.

Header:

```text
← Live Preview
```

Status:

```text
● Running on device
```

Toolbar:

```text
↻    Inspect    ⛶    ⋮
```

---

# 29. Device Preview

Render a polished PocketLedger app.

The preview itself should have:

* rounded device frame
* realistic mobile UI
* app status/header
* balance
* transaction list
* category chips
* bottom navigation

Example:

```text
┌──────────────────────────────┐
│ PocketLedger                 │
│                              │
│ Total Balance                │
│ ₹24,850                      │
│                              │
│ ┌────────────┐ ┌───────────┐ │
│ │ Income     │ │ Expenses  │ │
│ │ ₹42,000    │ │ ₹17,150   │ │
│ └────────────┘ └───────────┘ │
│                              │
│ Transactions                 │
│                              │
│ Swiggy             -₹420     │
│ Amazon           -₹1,299     │
│ Salary          +₹42,000     │
│ Uber               -₹280     │
│                              │
│ Home   Stats   Profile       │
└──────────────────────────────┘
```

Make this preview look like a real shipped application.

---

# 30. Preview Inspector

When Inspect mode is enabled:

User taps an element.

Show a subtle selection border.

Bottom sheet:

```text
SELECTED ELEMENT

TransactionCard

src/components/TransactionList.jsx

Line 42
```

Actions:

```text
Ask AI
View source
Explain
```

---

# 31. Preview → AI Interaction

When user selects:

**Ask AI**

Show:

```text
What should I change?
```

Suggested action:

```text
Make this card glassmorphic
```

When selected:

```text
Analyzing component       ✓
Finding source            ✓
Updating styles           ✓
Rebuilding preview        ◌
```

Then:

```text
✓ Updated TransactionList.jsx
```

The preview must visibly change.

This is the hero interaction of the prototype.

---

# 32. TERMINAL SCREEN

Header:

```text
← Terminal
```

Tabs:

```text
Terminal
Problems
Output
```

Terminal should use:

* black/dark surface
* JetBrains Mono
* green/white/muted output
* realistic spacing

Example:

```text
$ npm run dev

> pocketledger@1.0.0 dev
> vite

VITE v7.0.0 ready

Local: http://localhost:5173

✓ ready in 412ms
```

---

# 33. Terminal Quick Actions

Below the terminal:

```text
npm install
npm run dev
npm test
npm run build
git status
```

Buttons should populate the terminal with realistic mock output.

---

# 34. ERROR STATE

Add:

**Simulate Error**

After clicking:

```text
RUNTIME ERROR

TransactionList.jsx:42

Cannot read properties of undefined
```

Show:

```text
AI can investigate this error.
```

Button:

**Fix with AI**

---

# 35. AI DEBUGGING SHEET

Display:

```text
Analyzing stack trace
✓

Finding source
✓

Checking recent changes
✓

Preparing fix
✓

Running tests
◌
```

Then:

```text
Issue found

transactions can be undefined
during the initial render.

Suggested fix:
Add a safe fallback before mapping.
```

Show diff.

Buttons:

```text
Apply Fix
Review
Reject
```

---

# 36. DEPLOY SCREEN

Header:

```text
← Deploy
```

Project:

```text
PocketLedger
```

Status:

```text
Ready to deploy
```

Checklist:

```text
✓ Dependencies verified
✓ Production build
✓ Environment checked
✓ Tests passed
✓ Git changes reviewed
```

---

# 37. Deployment Targets

Cards:

```text
Vercel
Deploy web application

Netlify
Deploy web application

GitHub Pages
Static deployment

Device
Install local build
```

Selected target gets accent border.

---

# 38. Deployment Progress

When user taps Deploy:

```text
Preparing build             ✓
Running tests               ✓
Creating artifact           ✓
Uploading                   ✓
Health check                ✓
```

Use animated progress.

Do not make the animation slow.

Target:

Approximately 2–4 seconds.

---

# 39. Deployment Success

Large success state:

```text
✓

YOUR APP IS LIVE

pocketledger-forge.app
```

Actions:

```text
Open App
Copy URL
View Logs
```

Use a subtle success animation.

---

# 40. PROJECT DOCTOR SCREEN

Header:

```text
← Project Doctor
```

Hero:

```text
PROJECT HEALTH

3 improvements found
```

Cards:

```text
Dependency update
Medium

Unused package
Low

Missing error boundary
Medium
```

Each card:

```text
Why this matters
Fix
```

Primary button:

**Fix all**

After fixing:

```text
✓ All issues resolved
```

---

# 41. PROJECT MEMORY SCREEN

Header:

```text
← Project Memory
```

Subtitle:

```text
What Velocity knows about this project
```

Sections:

```text
Architecture
React + Vite

Storage
Local persistence

Design System
Dark UI · Indigo accent

Important Decision
Offline-first transaction storage

Known Issue
API integration not connected

Recent Changes
Transaction card redesigned
```

Use compact cards.

---

# 42. Micro-Interactions

Implement subtle motion for:

* page transitions
* card entrance
* AI thinking
* tool execution
* terminal output
* deployment progress
* success states
* selected preview elements
* bottom sheets
* button feedback

Animations should be fast.

Recommended:

```text
150–250ms
```

AI workflows can use slightly longer transitions.

Avoid excessive animation.

---

# 43. Loading States

Use realistic loading states.

Examples:

```text
Analyzing project...
Finding relevant files...
Planning changes...
Updating code...
Running application...
Verifying result...
```

Use:

* animated dots
* progress indicators
* skeletons
* status icons

---

# 44. Toast Notifications

Use compact bottom toast notifications.

Examples:

```text
✓ Changes applied
```

```text
✓ Build completed
```

```text
✓ Project deployed
```

```text
↶ AI changes rolled back
```

Toasts should disappear automatically.

---

# 45. Touch Interaction

All interactive controls must have at least approximately:

```text
44px × 44px
```

Do not use tiny desktop controls.

Provide:

* clear pressed states
* active states
* disabled states
* loading states
* error states

---

# 46. Responsive Desktop Mode

Desktop is secondary.

If the viewport becomes wider:

Use an expanded developer layout:

```text
┌──────────┬──────────────────────┬──────────────┐
│ Files    │ Code / Preview       │ AI Agent     │
│          │                      │              │
│          │                      │              │
└──────────┴──────────────────────┴──────────────┘
```

However, the mobile experience remains the primary design.

Do not redesign the entire application for desktop.

---

# 47. Component Style

Create reusable UI components:

```text
TopBar
BottomNav
GlassCard
StatusBadge
PrimaryButton
IconButton
AIMessage
ToolExecution
ExecutionPlan
FileTree
CodeEditor
Terminal
PreviewFrame
InspectorSheet
AISheet
ProgressSteps
Toast
ProjectCard
IssueCard
DeploymentCard
```

Keep the visual language consistent.

---

# 48. Final Visual Goal

The first impression should be:

> **This looks like a real mobile development environment.**

The second impression:

> **The AI actually operates the development environment.**

The final impression:

> **I can build and ship software from my phone.**

The UI must support that story visually at every step.

---

# 49. Final Demo Sequence

The interface should make this sequence effortless:

```text
HOME
 ↓
BUILD WITH AI
 ↓
AI PLAN
 ↓
PROJECT CREATED
 ↓
LIVE PREVIEW
 ↓
SELECT UI ELEMENT
 ↓
ASK AI
 ↓
SOURCE UPDATED
 ↓
PREVIEW UPDATED
 ↓
TERMINAL
 ↓
TEST
 ↓
PROJECT DOCTOR
 ↓
DEPLOY
 ↓
APP LIVE
```

This sequence is the heart of the Velocity-IDE prototype.

```
```
