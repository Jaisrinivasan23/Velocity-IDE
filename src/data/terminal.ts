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
    "added 142 packages, and audited 143 packages in 3s",
    "",
    "24 packages are looking for funding",
    "  run `npm fund` for details",
    "",
    "found 0 vulnerabilities",
    "[SUCCESS] All dependencies installed successfully."
  ],
  "npm run dev": [
    "$ npm run dev",
    "",
    "  VITE v7.0.0  ready in 218 ms",
    "",
    "  ➜  Local:   http://localhost:5173/",
    "[SUCCESS] Development server running."
  ],
  "npm test": [
    "$ vitest run",
    "",
    " RUN  v1.0.0 /home/project",
    "",
    " [OK] src/components/BalanceCard.test.jsx (4 tests)",
    " [OK] src/components/TransactionList.test.jsx (8 tests)",
    " [OK] src/App.test.jsx (6 tests)",
    "",
    "Test Files  3 passed (3)",
    "     Tests  18 passed (18)",
    "      Time  1.2s",
    "",
    "[SUCCESS] All 18 tests passed successfully."
  ],
  "npm run build": [
    "$ tsc && vite build",
    "vite v5.0.0 building for production...",
    "transforming (14) src/main.tsx",
    "rendering chunks (4)...",
    "computing gzip size (4)...",
    "dist/index.html                   0.46 kB │ gzip:  0.29 kB",
    "dist/assets/index-B_oM-0C2.css    12.4 kB │ gzip:  3.1 kB",
    "dist/assets/index-C3yV9zZ1.js    143.2 kB │ gzip: 46.1 kB",
    "[SUCCESS] 14 modules transformed.",
    "",
    "build time: 1.4s",
    "build output: dist/",
    "",
    "[SUCCESS] Build completed successfully in 1.4s"
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
