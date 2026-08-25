import React, { useState } from 'react';
import { useProject } from '../../context/ProjectContext';
import './PocketLedgerApp.css';

interface PocketLedgerAppProps {
  isInspectMode: boolean;
  onSelectCard: () => void;
}

export const PocketLedgerApp: React.FC<PocketLedgerAppProps> = ({
  isInspectMode,
  onSelectCard,
}) => {
  const { isGlassmorphic, inspectedElement } = useProject();
  const [activeTab, setActiveTab] = useState('home');

  const isCardSelected = inspectedElement === 'TransactionCard';

  return (
    <div className="pocket-preview-device">
      {/* Device Header */}
      <div className="pocket-header">
        <div className="app-branding">
          <span className="app-icon">⚡</span>
          <span className="app-name font-ui">PocketLedger</span>
        </div>
        <span className="offline-badge">● Local</span>
      </div>

      {/* Main Content */}
      <div className="pocket-content">
        {/* Balance Dashboard Card */}
        <div className="balance-card">
          <span className="balance-label">Total Balance</span>
          <h2 className="balance-amount font-mono">₹24,850</h2>

          <div className="balance-summary">
            <div className="summary-item income">
              <span className="summary-label">Income</span>
              <span className="summary-value font-mono">+₹42,000</span>
            </div>
            <div className="summary-item expense">
              <span className="summary-label">Expenses</span>
              <span className="summary-value font-mono">-₹17,150</span>
            </div>
          </div>
        </div>

        {/* Transactions List */}
        <div className="transactions-container">
          <div className="section-header">
            <h3 className="section-title">Transactions</h3>
            <span className="section-more">View all</span>
          </div>

          <div className="transactions-list">
            {/* Transaction Card 1 (Swiggy) - Primary Inspector Target */}
            <div
              className={`transaction-card ${isGlassmorphic ? 'glassmorphic-card' : ''} ${
                isInspectMode ? 'inspectable' : ''
              } ${isCardSelected ? 'inspected-highlight' : ''}`}
              onClick={() => {
                if (isInspectMode) onSelectCard();
              }}
            >
              {isInspectMode && <div className="inspect-tag font-mono">TransactionCard</div>}
              <div className="tx-left">
                <div className="tx-icon food">🍕</div>
                <div className="tx-info">
                  <span className="tx-name">Swiggy</span>
                  <span className="tx-category">Food & Dining</span>
                </div>
              </div>
              <span className="tx-amount expense font-mono">-₹420</span>
            </div>

            {/* Transaction Card 2 (Amazon) */}
            <div className={`transaction-card ${isGlassmorphic ? 'glassmorphic-card' : ''}`}>
              <div className="tx-left">
                <div className="tx-icon shop">📦</div>
                <div className="tx-info">
                  <span className="tx-name">Amazon</span>
                  <span className="tx-category">Shopping</span>
                </div>
              </div>
              <span className="tx-amount expense font-mono">-₹1,299</span>
            </div>

            {/* Transaction Card 3 (Salary) */}
            <div className={`transaction-card ${isGlassmorphic ? 'glassmorphic-card' : ''}`}>
              <div className="tx-left">
                <div className="tx-icon salary font-mono">💼</div>
                <div className="tx-info">
                  <span className="tx-name">Salary</span>
                  <span className="tx-category font-mono">Income</span>
                </div>
              </div>
              <span className="tx-amount income font-mono">+₹42,000</span>
            </div>

            {/* Transaction Card 4 (Uber) */}
            <div className={`transaction-card ${isGlassmorphic ? 'glassmorphic-card' : ''}`}>
              <div className="tx-left">
                <div className="tx-icon travel font-mono">🚗</div>
                <div className="tx-info">
                  <span className="tx-name font-mono">Uber</span>
                  <span className="tx-category font-mono">Travel</span>
                </div>
              </div>
              <span className="tx-amount expense font-mono">-₹280</span>
            </div>
          </div>
        </div>
      </div>

      {/* Pocket Bottom Navigation */}
      <div className="pocket-bottom-nav font-ui">
        {[
          { id: 'home', label: 'Home', icon: '🏠' },
          { id: 'stats', label: 'Stats', icon: '📊' },
          { id: 'profile', label: 'Profile', icon: '👤' },
        ].map((tab) => (
          <button
            key={tab.id}
            className={`pocket-nav-item ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            <span className="pocket-nav-icon">{tab.icon}</span>
            <span className="pocket-nav-label">{tab.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
};
