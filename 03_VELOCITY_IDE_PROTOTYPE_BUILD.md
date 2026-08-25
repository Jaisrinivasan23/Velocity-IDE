# `03_VELOCITY_IDE_PROTOTYPE_BUILD.md`

````md
# VELOCITY-IDE
## React + Vite Frontend Prototype Build Specification

---

# 1. Objective

Build the complete **Velocity-IDE** frontend prototype using:

- React
- Vite
- TypeScript
- React Router
- Lucide React
- CSS

This is a **frontend-only interactive prototype** for a hackathon demonstration.

There must be:

- no backend
- no database
- no authentication
- no external AI API
- no real deployment
- no real terminal execution
- no real Git operations
- no real code compilation

All functionality should be simulated with local mock data and React state.

The prototype must nevertheless feel like a real AI-powered mobile development environment.

---

# 2. Primary Objective

The application must demonstrate this complete workflow:

```text
IDEA
  ↓
AI PLAN
  ↓
CREATE PROJECT
  ↓
EDIT CODE
  ↓
RUN APP
  ↓
INSPECT UI
  ↓
ASK AI
  ↓
MODIFY SOURCE
  ↓
LIVE UPDATE
  ↓
DEBUG
  ↓
TEST
  ↓
PROJECT HEALTH
  ↓
DEPLOY
  ↓
APP LIVE
````

The entire flow must work without a backend.

---

# 3. Product Name

Use:

**Velocity-IDE**

Do not use:

* Forge AI
* CodeOS
* DevForge
* generic "AI IDE"

The branding throughout the application must consistently say:

**VELOCITY-IDE**

Tagline:

**Build software. Anywhere.**

---

# 4. Technical Requirements

Create a Vite React application.

Recommended setup:

```bash
npm create vite@latest velocity-ide -- --template react-ts
cd velocity-ide
npm install
npm install react-router-dom lucide-react
npm run dev
```

Use TypeScript.

Use functional React components.

Use React hooks.

Keep application state local.

---

# 5. Project Structure

Use a clean architecture:

```text
src/
├── components/
│   ├── layout/
│   │   ├── TopBar.tsx
│   │   └── BottomNav.tsx
│   │
│   ├── ai/
│   │   ├── AIMessage.tsx
│   │   ├── AISheet.tsx
│   │   ├── ExecutionPlan.tsx
│   │   └── ToolExecution.tsx
│   │
│   ├── editor/
│   │   ├── FileTree.tsx
│   │   ├── CodeEditor.tsx
│   │   └── EditorToolbar.tsx
│   │
│   ├── preview/
│   │   ├── PreviewFrame.tsx
│   │   └── InspectorSheet.tsx
│   │
│   ├── terminal/
│   │   └── Terminal.tsx
│   │
│   ├── deployment/
│   │   └── DeploymentProgress.tsx
│   │
│   └── common/
│       ├── Button.tsx
│       ├── Card.tsx
│       ├── Badge.tsx
│       ├── IconButton.tsx
│       └── Toast.tsx
│
├── screens/
│   ├── Home.tsx
│   ├── Workspace.tsx
│   ├── AIAgent.tsx
│   ├── Preview.tsx
│   ├── TerminalScreen.tsx
│   ├── Deploy.tsx
│   ├── ProjectDoctor.tsx
│   └── ProjectMemory.tsx
│
├── data/
│   ├── project.ts
│   ├── code.ts
│   ├── terminal.ts
│   └── mockAI.ts
│
├── hooks/
│   ├── useAIWorkflow.ts
│   ├── useToast.ts
│   └── useProject.ts
│
├── styles/
│   ├── globals.css
│   └── theme.css
│
├── App.tsx
└── main.tsx
```

Keep components small and reusable.

Do not create one massive component containing the entire application.

---

# 6. Routing

Implement routes:

```text
/
 /workspace
 /ai
 /preview
 /terminal
 /deploy
 /doctor
 /memory
```

The application must never show a blank screen for a route.

Every route must render a complete UI.

---

# 7. Global State

Create a lightweight local application state.

The state should include:

```ts
type ProjectState = {
  projectName: string
  framework: string
  files: ProjectFile[]
  selectedFile: string
  buildStatus: "passed" | "building" | "failed"
  previewRunning: boolean
  testsPassed: boolean
  deployed: boolean
  inspectedElement: string | null
  aiBusy: boolean
}
```

You may use React Context or a small custom state hook.

Do not add Redux.

---

# 8. Mock Project

Use:

```text
Project:
PocketLedger

Framework:
React + Vite

Storage:
Local Storage

Files:
12
```

Project files:

```text
PocketLedger
├── src
│   ├── components
│   │   ├── BalanceCard.jsx
│   │   ├── TransactionList.jsx
│   │   └── BottomNav.jsx
│   ├── App.jsx
│   ├── main.jsx
│   └── styles.css
├── public
├── package.json
└── README.md
```

---

# 9. Home Screen

Implement the Home screen according to the UI specification.

Required content:

```text
VELOCITY-IDE

Good morning
Ready to build?
```

Current project:

```text
PocketLedger
React · Vite
● Build passed
12 files
3 changes
```

Button:

**Open Workspace**

Primary AI card:

```text
BUILD WITH AI

Describe what you want.
Velocity builds it.

Start building
```

Recent projects:

* PocketLedger
* CampusFlow
* QuickShop

AI shortcuts:

* Build an app
* Fix a bug
* Explain project
* Deploy project

---

# 10. Build With AI Interaction

When the user taps:

**Start building**

navigate to:

```text
/ai
```

Automatically prefill:

```text
Build a modern expense tracker with a dashboard, transaction history, categories and local storage.
```

The input must remain editable.

---

# 11. AI Build Workflow

When the user submits the prompt:

Add the user message to the conversation.

Then show the AI response:

```text
I can build that.

I'll create the project structure, implement the dashboard and transaction flow, add local persistence, then run the application and verify the main flow.
```

Then start a simulated workflow.

Execution steps:

```text
Understanding requirements
Planning architecture
Creating project structure
Creating dashboard
Creating transaction components
Adding local storage
Installing dependencies
Starting application
Running verification
```

Each step should animate.

State transitions:

```text
pending
→ running
→ completed
```

Use approximately 300–700ms per step.

Do not make the demo excessively slow.

At completion:

```text
App ready
```

Show:

**Open Preview**

button.

---

# 12. AI Agent Tool Activity

Display realistic tool execution.

Examples:

```text
Reading project
src/

Creating file
src/components/TransactionList.jsx

Updating file
src/App.jsx

Installing dependencies
npm install

Starting development server
npm run dev
```

Each activity must have:

* icon
* description
* status
* subtle animation

---

# 13. Workspace

Workspace route:

```text
/workspace
```

Top bar:

```text
PocketLedger
● Running
```

Tabs:

```text
Files
Code
AI
```

Default tab:

**Files**

---

# 14. File Explorer

Implement expandable folders.

Example:

```text
PocketLedger

⌄ src
   ⌄ components
      BalanceCard.jsx
      TransactionList.jsx
      BottomNav.jsx

   App.jsx
   main.jsx
   styles.css

⌄ public

package.json
README.md
```

Clicking a file:

* selects it
* opens Code view
* updates selected file state

---

# 15. Code Editor

Implement a realistic mobile code editor.

Use:

* monospace font
* line numbers
* syntax highlighting
* current line highlight
* scrollable code
* file name
* modified indicator

For `TransactionList.jsx`, use realistic React code.

Example:

```jsx
const transactions = [
  {
    name: "Swiggy",
    amount: -420,
    category: "Food",
  },
  {
    name: "Amazon",
    amount: -1299,
    category: "Shopping",
  },
];

export function TransactionList() {
  return (
    <div className="transactions">
      {transactions.map((transaction) => (
        <TransactionCard
          key={transaction.name}
          transaction={transaction}
        />
      ))}
    </div>
  );
}
```

The editor should visually show the changed line when AI modifies it.

---

# 16. Editor AI Actions

When the AI button is pressed from the editor, show a bottom sheet.

Actions:

```text
Explain this file
Fix errors
Refactor this component
Improve performance
Add feature
Write tests
```

Selecting an action should show a mock AI response.

---

# 17. Live Preview

Route:

```text
/preview
```

Top bar:

```text
Live Preview
● Running on device
```

Controls:

```text
Refresh
Inspect
Fullscreen
More
```

Render a polished PocketLedger application.

The preview should look like a real mobile app.

---

# 18. PocketLedger Preview

Render:

```text
PocketLedger

Total Balance
₹24,850
```

Cards:

```text
Income
₹42,000

Expenses
₹17,150
```

Transactions:

```text
Swiggy
Food
-₹420

Amazon
Shopping
-₹1,299

Salary
Income
+₹42,000

Uber
Travel
-₹280
```

Bottom navigation:

```text
Home
Stats
Profile
```

Use a refined visual design.

---

# 19. Preview Inspection

Implement an Inspect toggle.

When Inspect mode is enabled, interactive elements should become selectable.

When the user taps the TransactionCard:

Highlight the element.

Open an inspector bottom sheet.

Show:

```text
SELECTED ELEMENT

TransactionCard

src/components/TransactionList.jsx

Line 42
```

Buttons:

```text
Ask AI
View Source
Explain
```

---

# 20. Visual-to-Code Magic Interaction

This is a critical feature.

When the user taps:

**Ask AI**

show:

```text
What should I change?
```

Suggested action:

```text
Make this card glassmorphic
```

Also show:

```text
Add swipe-to-delete
Change spacing
Improve animation
Make it more compact
```

When the user selects:

**Make this card glassmorphic**

start:

```text
Analyzing component
Finding source
Updating styles
Rebuilding preview
Verifying result
```

After completion:

```text
✓ Updated TransactionList.jsx
```

Then visibly change the transaction card styling in the preview.

This interaction must work.

---

# 21. Source Update Simulation

When the visual modification completes, update the mock source code.

For example:

Before:

```jsx
<TransactionCard transaction={transaction} />
```

After:

```jsx
<TransactionCard
  transaction={transaction}
  variant="glass"
/>
```

Show a diff view.

Example:

```diff
- <TransactionCard transaction={transaction} />
+ <TransactionCard
+   transaction={transaction}
+   variant="glass"
+ />
```

Do not actually compile the code.

The preview can be updated directly through React state.

---

# 22. Terminal

Route:

```text
/terminal
```

Tabs:

```text
Terminal
Problems
Output
```

Initial output:

```text
$ npm run dev

> pocketledger@1.0.0 dev
> vite

VITE v7.0.0 ready

Local: http://localhost:5173

✓ ready in 412ms
```

Quick actions:

```text
npm install
npm run dev
npm test
npm run build
git status
```

Clicking a command appends predefined output.

---

# 23. Terminal Mock Commands

## npm install

Show:

```text
Installing dependencies...

added 148 packages

✓ completed
```

## npm run dev

Show:

```text
VITE v7.0.0 ready

Local: http://localhost:5173

✓ ready in 412ms
```

## npm test

Show:

```text
Running tests...

Test Suites: 4 passed
Tests: 18 passed
Time: 1.8s

✓ All tests passed
```

## npm run build

Show:

```text
Building production bundle...

✓ Compiled successfully
✓ Assets optimized
✓ dist/ generated

Build completed successfully
```

## git status

Show:

```text
On branch main

Changes not staged:
  modified:
    src/App.jsx
    src/components/TransactionList.jsx

3 changes
```

---

# 24. Simulated Runtime Error

Add a visible action:

**Simulate Error**

When clicked, show:

```text
RUNTIME ERROR

TransactionList.jsx:42

Cannot read properties of undefined
```

Set project build status to:

```text
failed
```

Show an error badge.

---

# 25. AI Debugger

Add:

**Fix with AI**

When clicked, show an AI debugging workflow:

```text
Analyzing stack trace
Finding source
Checking recent changes
Identifying root cause
Preparing fix
Running tests
```

Then show:

```text
Issue found

transactions can be undefined
during the initial render.

Suggested fix:
Add a safe fallback before mapping.
```

Show diff:

```diff
- transactions.map(...)
+ transactions?.map(...)
```

Buttons:

```text
Apply Fix
Review
Reject
```

---

# 26. Apply Fix

When:

**Apply Fix**

is clicked:

* remove error state
* set build status to passed
* show success toast
* update terminal output
* update code view
* show:

```text
✓ Bug fixed
✓ Tests passed
```

---

# 27. Project Doctor

Route:

```text
/doctor
```

Show:

```text
PROJECT HEALTH

3 improvements found
```

Issues:

```text
Dependency update
Medium

Unused package
Low

Missing error boundary
Medium
```

Each issue has:

**Fix**

Primary action:

**Fix All**

When Fix All is clicked:

Animate each issue.

Final:

```text
✓ All issues resolved
```

Set project health to:

**Healthy**

---

# 28. Project Memory

Route:

```text
/memory
```

Show:

```text
PROJECT MEMORY

What Velocity knows about this project
```

Cards:

```text
Architecture
React + Vite

Storage
Local persistence

Design System
Dark UI · Indigo accent · Rounded cards

Important Decision
Offline-first transaction storage

Known Issue
API integration not connected

Recent Changes
Transaction card redesigned
```

This screen should feel like AI project context.

---

# 29. Deploy

Route:

```text
/deploy
```

Show:

```text
PocketLedger

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

Targets:

```text
Vercel
Netlify
GitHub Pages
Device
```

Use selectable deployment cards.

---

# 30. Deployment Simulation

When the user clicks:

**Deploy Project**

run a sequence:

```text
Preparing build
Running tests
Creating artifact
Uploading
Health check
```

Each step should take approximately 400–700ms.

After completion:

```text
✓ Deployment successful

YOUR APP IS LIVE

pocketledger-forge.app
```

Actions:

```text
Open App
Copy URL
View Logs
```

The URL can be copied to clipboard.

No actual deployment occurs.

---

# 31. Toast System

Implement reusable toast notifications.

Examples:

```text
✓ Changes applied
```

```text
✓ Build completed
```

```text
✓ Bug fixed
```

```text
✓ Deployment successful
```

```text
↶ AI changes rolled back
```

Toasts should disappear automatically.

---

# 32. AI Workflow State Machine

Implement AI workflow state locally.

Example:

```ts
type AIWorkflowState =
  | "idle"
  | "thinking"
  | "planning"
  | "executing"
  | "verifying"
  | "complete";
```

Use state to control UI.

Never rely only on `setTimeout` chains scattered throughout components.

Create a reusable workflow hook.

---

# 33. Demo State

The application should have a deterministic demo state.

A fresh page load should always have:

```text
PocketLedger
Build passed
Preview running
18 tests available
Project ready to deploy
```

The demo should not depend on random data.

---

# 34. Demo Reset

Add an option in settings or overflow menu:

**Reset Demo**

Reset:

* AI workflow
* preview changes
* terminal
* errors
* deployment state
* project health

This allows the hackathon presentation to restart cleanly.

---

# 35. Mobile Navigation

Use a persistent bottom navigation on primary screens:

```text
Home
Workspace
Preview
Terminal
Deploy
```

Do not show bottom navigation over:

* full-screen editor
* bottom sheets
* deployment success modal

Use safe-area padding.

---

# 36. Bottom Sheets

Use reusable bottom-sheet behavior for:

* AI contextual actions
* preview inspector
* debugging
* project actions

Requirements:

* drag-like visual handle
* rounded top corners
* dark elevated surface
* backdrop
* smooth entrance
* close interaction

---

# 37. Animations

Use subtle animations.

Recommended durations:

```text
Fast:
150ms

Normal:
200–250ms

Workflow:
300–700ms
```

Animate:

* page transitions
* cards
* AI steps
* terminal output
* progress
* success states
* inspector selection
* bottom sheets

Do not make the UI slow.

---

# 38. Icons

Use Lucide React.

Recommended icons:

```text
Sparkles
Code2
Folder
Terminal
Play
Rocket
Bug
ShieldCheck
GitBranch
Search
Settings
ChevronRight
ArrowLeft
RefreshCw
Maximize
Send
Mic
Paperclip
Check
AlertTriangle
X
Undo2
```

Use consistent icon sizes.

---

# 39. Buttons

Primary button:

* accent background
* white text
* medium weight
* 44–48px minimum height

Secondary:

* elevated surface
* subtle border

Ghost:

* transparent
* muted text
* accent on active

Danger:

* red only when destructive

---

# 40. Cards

Cards should use:

```text
background: elevated surface
border: subtle
border-radius: 14–18px
```

Avoid excessive shadows.

Use accent borders only for selected/active states.

---

# 41. Responsive Design

The primary breakpoint is mobile.

At approximately:

```text
768px+
```

allow expanded layouts.

Desktop workspace can become:

```text
┌────────────┬──────────────────────┬──────────────┐
│ File Tree  │ Editor / Preview     │ AI Agent     │
│            │                      │              │
└────────────┴──────────────────────┴──────────────┘
```

But do not compromise the mobile UI.

---

# 42. Accessibility

Implement:

* semantic buttons
* keyboard focus states
* sufficient contrast
* readable font sizes
* minimum touch target around 44px
* labels for icon-only buttons
* `aria-label` where necessary

---

# 43. Performance

Keep the prototype lightweight.

Avoid:

* large unnecessary dependencies
* complex state libraries
* heavy editor frameworks
* unnecessary re-renders

The prototype should load quickly.

---

# 44. No Backend

Do not implement:

* API server
* database
* authentication
* WebSocket
* cloud functions

All data should live in:

* mock files
* React state
* localStorage where useful

---

# 45. No External AI API

Do not use:

* OpenAI API
* Gemini API
* Anthropic API
* any API key

AI behavior must be mocked.

The demo must work offline after installation.

---

# 46. No Real Deployment

Deployment must be simulated.

Do not send files to Vercel, Netlify, GitHub, or another provider.

The final deployment URL is mock data.

---

# 47. No Real Terminal Execution

Do not execute shell commands from the browser.

Terminal output is mock data.

---

# 48. Demo-Critical Interactions

These interactions MUST work:

```text
Home
 ↓
Build with AI
 ↓
AI execution
 ↓
Open Preview
 ↓
Inspect transaction card
 ↓
Ask AI
 ↓
Make card glassmorphic
 ↓
Preview changes
 ↓
Open Workspace
 ↓
View changed source
 ↓
Open Terminal
 ↓
Run tests
 ↓
Project Doctor
 ↓
Deploy
 ↓
Deployment success
```

Also ensure:

```text
Preview
 ↓
Simulate Error
 ↓
Fix with AI
 ↓
Apply Fix
 ↓
Error disappears
 ↓
Tests pass
```

---

# 49. Visual Demo Details

The prototype should contain enough realistic detail that screenshots look like a real product.

Include:

* realistic project names
* realistic code
* realistic terminal output
* realistic file paths
* realistic status information
* realistic AI messages
* realistic deployment logs
* realistic timestamps
* realistic mock URLs

Avoid placeholder text such as:

```text
Lorem ipsum
Test
Sample text
Coming soon
Feature 1
```

Every visible element should feel intentional.

---

# 50. Empty and Error States

Create polished empty states where required.

Example:

```text
No project selected

Open a project or start building with AI.
```

Error:

```text
Something went wrong

Velocity couldn't complete this action.

Try again
```

These are fallback states only.

The main demo should use successful states.

---

# 51. Final Home Hero

The Home screen should communicate the product immediately:

```text
VELOCITY-IDE

Build software.
Anywhere.

Your AI development workspace,
running on your phone.
```

Primary action:

**Build with AI**

Secondary:

**Open Workspace**

---

# 52. Final Demo Scenario

The prototype must support this exact presentation:

## Opening

Show Velocity-IDE Home.

Say:

> "This is Velocity-IDE, an AI-powered development workspace designed to let developers build and ship software directly from their phone."

Tap:

**Build with AI**

---

## AI Build

Prompt:

> Build a modern expense tracker with a dashboard, transaction history, categories and local storage.

Show AI planning and execution.

Tap:

**Open Preview**

---

## Live Application

Show PocketLedger running.

Say:

> "The important part is that the AI understands not only the code, but also the application running on the device."

Tap a transaction card.

Show:

```text
TransactionCard
src/components/TransactionList.jsx
```

Tap:

**Ask AI**

Choose:

**Make this card glassmorphic**

Show AI execution.

Preview updates.

---

## Source Connection

Open Workspace.

Show:

```text
TransactionList.jsx
```

Show the changed code/diff.

Say:

> "The UI change was translated back into the actual source component."

---

## Debugging

Open Terminal.

Tap:

**Simulate Error**

Show runtime error.

Tap:

**Fix with AI**

Show:

```text
Analyzing
Finding source
Preparing fix
Running tests
```

Apply fix.

Show:

```text
✓ Bug fixed
✓ Tests passed
```

---

## Project Health

Open Project Doctor.

Show:

```text
No critical issues
```

---

## Deployment

Open Deploy.

Tap:

**Deploy Project**

Show:

```text
Build ✓
Tests ✓
Artifact ✓
Upload ✓
Health Check ✓
```

Final:

```text
YOUR APP IS LIVE

pocketledger-forge.app
```

---

# 53. Final Acceptance Criteria

The implementation is complete only when:

* the application starts with `npm run dev`
* all routes work
* there are no broken imports
* there are no blank screens
* the mobile layout works at 390px
* the bottom navigation works
* the AI workflow works
* the preview works
* the inspector works
* the visual-to-code interaction works
* the source diff appears
* the terminal works with mock commands
* the simulated error works
* the AI debugger works
* Project Doctor works
* Project Memory works
* deployment animation works
* deployment success works
* toasts work
* bottom sheets work
* the demo can be completed without an API key
* the demo can be reset

---

# 54. Final Quality Bar

Do not stop at functional screens.

The final result must feel:

> **Fast. Premium. Mobile-native. Technical. Intelligent.**

A judge should understand the product within the first 30 seconds.

Within 3–5 minutes they should see:

```text
Idea
 ↓
AI
 ↓
Code
 ↓
Live App
 ↓
Visual Editing
 ↓
Debugging
 ↓
Testing
 ↓
Deployment
```

The final impression should be:

> **"This isn't an AI chatbot inside a code editor. This is an AI development environment designed around the phone."**

---

# 55. Final Product Identity

## VELOCITY-IDE

### Build software. Anywhere.

**AI-powered development. On your phone.**

```
```
