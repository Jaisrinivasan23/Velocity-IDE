import { ExecutionStep, ToolActivity } from '../types';

export const INITIAL_BUILD_STEPS: ExecutionStep[] = [
  { id: 'step-1', label: 'Understanding requirements', status: 'pending' },
  { id: 'step-2', label: 'Planning architecture', status: 'pending' },
  { id: 'step-3', label: 'Creating project structure', status: 'pending' },
  { id: 'step-4', label: 'Creating dashboard', status: 'pending' },
  { id: 'step-5', label: 'Creating transaction components', status: 'pending' },
  { id: 'step-6', label: 'Adding local storage', status: 'pending' },
  { id: 'step-7', label: 'Installing dependencies', status: 'pending' },
  { id: 'step-8', label: 'Starting application', status: 'pending' },
  { id: 'step-9', label: 'Running verification', status: 'pending' }
];

export const MOCK_TOOL_ACTIVITIES: ToolActivity[] = [
  { id: 'tool-1', name: 'Reading project', detail: 'src/', status: 'complete' },
  { id: 'tool-2', name: 'Creating App.jsx', detail: 'src/App.jsx', status: 'complete' },
  { id: 'tool-3', name: 'Creating TransactionList.jsx', detail: 'src/components/TransactionList.jsx', status: 'complete' },
  { id: 'tool-4', name: 'Installing dependencies', detail: 'npm install', status: 'complete' },
  { id: 'tool-5', name: 'Starting development server', detail: 'npm run dev', status: 'complete' }
];

export const INITIAL_AI_CONVERSATION = [
  {
    id: 'msg-1',
    sender: 'user' as const,
    text: 'Build a modern expense tracker with a dashboard, transaction history, categories and local storage.',
    timestamp: '10:42 AM'
  },
  {
    id: 'msg-2',
    sender: 'ai' as const,
    text: "I can build that.\n\nI'll create the project structure, implement the dashboard and transaction flow, add local persistence, then run the application and verify the main flow.",
    timestamp: '10:42 AM'
  }
];
