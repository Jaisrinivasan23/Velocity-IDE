import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Sparkles } from 'lucide-react';
import './FloatingAIButton.css';

export const FloatingAIButton: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  if (location.pathname === '/ai') {
    return null; // Don't show AI FAB inside AI screen
  }

  const getTooltipContext = () => {
    switch (location.pathname) {
      case '/workspace': return 'Ask AI about code';
      case '/preview': return 'Inspect & edit UI';
      case '/terminal': return 'Explain terminal output';
      case '/deploy': return 'Prepare deployment';
      default: return 'Build with AI';
    }
  };

  return (
    <div className="v-fab-wrapper">
      <button 
        className="v-fab-btn animate-pulse-glow"
        onClick={() => navigate('/ai')}
        aria-label="Open AI Agent"
      >
        <Sparkles size={22} className="v-fab-icon" />
      </button>
      <span className="v-fab-tooltip">{getTooltipContext()}</span>
    </div>
  );
};
