export const INITIAL_CODE: Record<string, string> = {
  "src/components/TransactionList.jsx": `import React from 'react';

const transactions = [
  {
    id: 1,
    name: "Swiggy",
    category: "Food",
    amount: -420,
    date: "Today, 2:15 PM"
  },
  {
    id: 2,
    name: "Amazon",
    category: "Shopping",
    amount: -1299,
    date: "Yesterday"
  },
  {
    id: 3,
    name: "Salary",
    category: "Income",
    amount: 42000,
    date: "1 Aug"
  },
  {
    id: 4,
    name: "Uber",
    category: "Travel",
    amount: -280,
    date: "31 Jul"
  }
];

export function TransactionList({ isGlass = false }) {
  return (
    <div className="transactions-container">
      <h3 className="section-title">Transactions</h3>
      <div className="transactions-list">
        {transactions.map((tx) => (
          <TransactionCard
            key={tx.id}
            transaction={tx}
            variant={isGlass ? "glass" : "default"}
          />
        ))}
      </div>
    </div>
  );
}

function TransactionCard({ transaction, variant }) {
  const isPositive = transaction.amount > 0;
  return (
    <div className={\`transaction-card \${variant === 'glass' ? 'glassmorphic-card' : ''}\`}>
      <div className="tx-info">
        <span className="tx-name">{transaction.name}</span>
        <span className="tx-category">{transaction.category}</span>
      </div>
      <span className={\`tx-amount \${isPositive ? 'income' : 'expense'}\`}>
        {isPositive ? '+' : ''}₹{Math.abs(transaction.amount).toLocaleString('en-IN')}
      </span>
    </div>
  );
}
`,

  "src/components/BalanceCard.jsx": `import React from 'react';

export function BalanceCard() {
  return (
    <div className="balance-card">
      <span className="balance-label">Total Balance</span>
      <h2 className="balance-amount">₹24,850</h2>
      
      <div className="balance-summary">
        <div className="summary-item income">
          <span className="summary-label">Income</span>
          <span className="summary-value">+₹42,000</span>
        </div>
        <div className="summary-item expense">
          <span className="summary-label">Expenses</span>
          <span className="summary-value">-₹17,150</span>
        </div>
      </div>
    </div>
  );
}
`,

  "src/components/BottomNav.jsx": `import React from 'react';

export function BottomNav({ activeTab, onSelect }) {
  const tabs = [
    { id: 'home', label: 'Home', icon: '🏠' },
    { id: 'stats', label: 'Stats', icon: '📊' },
    { id: 'profile', label: 'Profile', icon: '👤' },
  ];

  return (
    <div className="pocket-bottom-nav">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          className={\`pocket-nav-item \${activeTab === tab.id ? 'active' : ''}\`}
          onClick={() => onSelect(tab.id)}
        >
          <span className="pocket-nav-icon">{tab.icon}</span>
          <span className="pocket-nav-label">{tab.label}</span>
        </button>
      ))}
    </div>
  );
}
`,

  "src/App.jsx": `import React, { useState } from 'react';
import { BalanceCard } from './components/BalanceCard';
import { TransactionList } from './components/TransactionList';
import { BottomNav } from './components/BottomNav';
import './styles.css';

export default function App() {
  const [activeTab, setActiveTab] = useState('home');

  return (
    <div className="pocket-app">
      <header className="pocket-header">
        <div className="app-branding">
          <span className="app-icon">⚡</span>
          <h1 className="app-name">PocketLedger</h1>
        </div>
        <span className="offline-badge">● Local</span>
      </header>

      <main className="pocket-content">
        <BalanceCard />
        <TransactionList />
      </main>

      <BottomNav activeTab={activeTab} onSelect={setActiveTab} />
    </div>
  );
}
`,

  "src/main.jsx": `import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
`,

  "src/styles.css": `/* PocketLedger Mock Styles */
.pocket-app {
  background: #0E1015;
  color: #F5F7FA;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.pocket-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #1F242D;
}

.balance-card {
  background: linear-gradient(135deg, #171923 0%, #10121A 100%);
  border: 1px solid #252A35;
  border-radius: 16px;
  padding: 20px;
  margin: 16px;
}

.transaction-card {
  background: #151821;
  border: 1px solid #222733;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
`,

  "package.json": `{
  "name": "pocketledger",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "test": "vitest run"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0"
  }
}
`,

  "README.md": `# PocketLedger

A modern, mobile-first personal expense tracker built with React and Vite.

## Features
- Offline-first balance & income tracking
- Categorized transaction log
- Clean dark-mode UI with local persistence
`
};

export const GLASS_MODIFIED_CODE = `import React from 'react';

const transactions = [
  {
    id: 1,
    name: "Swiggy",
    category: "Food",
    amount: -420,
    date: "Today, 2:15 PM"
  },
  {
    id: 2,
    name: "Amazon",
    category: "Shopping",
    amount: -1299,
    date: "Yesterday"
  },
  {
    id: 3,
    name: "Salary",
    category: "Income",
    amount: 42000,
    date: "1 Aug"
  },
  {
    id: 4,
    name: "Uber",
    category: "Travel",
    amount: -280,
    date: "31 Jul"
  }
];

export function TransactionList() {
  return (
    <div className="transactions-container">
      <h3 className="section-title">Transactions</h3>
      <div className="transactions-list">
        {transactions.map((tx) => (
          <TransactionCard
            key={tx.id}
            transaction={tx}
            variant="glass"
          />
        ))}
      </div>
    </div>
  );
}

function TransactionCard({ transaction, variant }) {
  const isPositive = transaction.amount > 0;
  return (
    <div className={\`transaction-card \${variant === 'glass' ? 'glassmorphic-card' : ''}\`}>
      <div className="tx-info">
        <span className="tx-name">{transaction.name}</span>
        <span className="tx-category">{transaction.category}</span>
      </div>
      <span className={\`tx-amount \${isPositive ? 'income' : 'expense'}\`}>
        {isPositive ? '+' : ''}₹{Math.abs(transaction.amount).toLocaleString('en-IN')}
      </span>
    </div>
  );
}
`;

export const GLASS_DIFF = `- <TransactionCard transaction={transaction} />
+ <TransactionCard
+   transaction={transaction}
+   variant="glass"
+ />`;

export const FIXED_ERROR_CODE = `import React from 'react';

// Safe fallbacks applied
export function TransactionList({ transactions = [] }) {
  return (
    <div className="transactions-container">
      <h3 className="section-title">Transactions</h3>
      <div className="transactions-list">
        {transactions?.map((tx) => (
          <TransactionCard
            key={tx.id}
            transaction={tx}
          />
        ))}
      </div>
    </div>
  );
}
`;

export const DEBUGGER_DIFF = `- transactions.map((tx) => (
+ transactions?.map((tx) => (`;
