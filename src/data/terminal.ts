export const INITIAL_TERMINAL_OUTPUT = [
  "$ npm run dev",
  "",
  "  VITE v7.0.0  ready in 412 ms",
  "",
  "  ➜  Local:   http://localhost:5173/",
  "  ➜  Network: use --host to expose",
  "  ➜  press h + enter to show help",
  ""
];

export const MOCK_COMMAND_OUTPUTS: Record<string, string[]> = {
  "npm install": [
    "$ npm install",
    "Installing dependencies for PocketLedger...",
    "[1/4] Resolving packages...",
    "[2/4] Fetching packages...",
    "[3/4] Linking dependencies...",
    "[4/4] Building fresh packages...",
    "added 148 packages in 2.1s",
    "✓ All dependencies installed successfully."
  ],
  "npm run dev": [
    "$ npm run dev",
    "",
    "  VITE v7.0.0  ready in 320 ms",
    "  ➜  Local:   http://localhost:5173/",
    "✓ Development server running."
  ],
  "npm test": [
    "$ npm test",
    "RUNNING Vitest v1.3.1 in /workspace/pocketledger",
    "",
    " ✓ src/components/BalanceCard.test.jsx (4 tests)",
    " ✓ src/components/TransactionList.test.jsx (8 tests)",
    " ✓ src/App.test.jsx (6 tests)",
    "",
    " Test Suites: 3 passed, 3 total",
    " Tests:       18 passed, 18 total",
    " Snapshots:   0 total",
    " Time:        1.8s",
    "",
    "✓ All 18 tests passed successfully."
  ],
  "npm run build": [
    "$ npm run build",
    "> pocketledger@1.0.0 build",
    "> vite build",
    "",
    "vite v7.0.0 building for production...",
    "transforming...",
    "✓ 14 modules transformed.",
    "dist/index.html                   0.45 kB │ gzip:  0.29 kB",
    "dist/assets/index-Dk8z2x1.css     4.12 kB │ gzip:  1.32 kB",
    "dist/assets/index-B7y1a9X.js     142.8 kB │ gzip: 44.10 kB",
    "",
    "✓ Build completed successfully in 1.4s"
  ],
  "git status": [
    "$ git status",
    "On branch main",
    "Your branch is up to date with 'origin/main'.",
    "",
    "Changes not staged for commit:",
    "  (use \"git add <file>...\" to update what will be committed)",
    "  (use \"git restore <file>...\" to discard changes in working directory)",
    "	modified:   src/App.jsx",
    "	modified:   src/components/TransactionList.jsx",
    "",
    "3 changes untracked/modified."
  ]
};
