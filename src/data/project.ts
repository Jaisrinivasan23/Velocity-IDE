import { DoctorIssue, ProjectFile } from '../types';

export const INITIAL_FILE_TREE: ProjectFile[] = [
  {
    name: 'src',
    path: 'src',
    type: 'folder',
    children: [
      {
        name: 'components',
        path: 'src/components',
        type: 'folder',
        children: [
          { name: 'BalanceCard.jsx', path: 'src/components/BalanceCard.jsx', type: 'file' },
          { name: 'TransactionList.jsx', path: 'src/components/TransactionList.jsx', type: 'file' },
          { name: 'BottomNav.jsx', path: 'src/components/BottomNav.jsx', type: 'file' }
        ]
      },
      { name: 'App.jsx', path: 'src/App.jsx', type: 'file' },
      { name: 'main.jsx', path: 'src/main.jsx', type: 'file' },
      { name: 'styles.css', path: 'src/styles.css', type: 'file' }
    ]
  },
  {
    name: 'public',
    path: 'public',
    type: 'folder',
    children: [
      { name: 'favicon.svg', path: 'public/favicon.svg', type: 'file' }
    ]
  },
  { name: 'package.json', path: 'package.json', type: 'file' },
  { name: 'README.md', path: 'README.md', type: 'file' }
];

export const INITIAL_DOCTOR_ISSUES: DoctorIssue[] = [
  {
    id: 'doc-1',
    title: 'Dependency update',
    severity: 'Medium',
    description: 'React 18.3.1 patch release available with rendering optimizations.',
    impact: 'Low performance impact during rapid DOM rerenders.',
    isFixed: false
  },
  {
    id: 'doc-2',
    title: 'Unused package',
    severity: 'Low',
    description: 'lodash-es declared in package.json but not imported in source code.',
    impact: 'Increases bundle size by ~14KB.',
    isFixed: false
  },
  {
    id: 'doc-3',
    title: 'Missing error boundary',
    severity: 'Medium',
    description: 'Top-level component app tree lacks an error boundary fallback wrapper.',
    impact: 'Unhandled component exceptions crash the screen.',
    isFixed: false
  }
];

export const RECENT_PROJECTS = [
  {
    name: 'PocketLedger',
    stack: 'React · Vite',
    status: 'Build passed',
    time: '2 min ago',
    active: true
  },
  {
    name: 'CampusFlow',
    stack: 'React · Vite',
    status: 'Build passed',
    time: 'Yesterday',
    active: false
  },
  {
    name: 'QuickShop',
    stack: 'Next.js',
    status: 'Deployed',
    time: '3 days ago',
    active: false
  }
];
