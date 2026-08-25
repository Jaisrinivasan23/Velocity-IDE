import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Home, Code2, Play, Terminal, Rocket } from 'lucide-react';
import { FloatingAIButton } from './FloatingAIButton';
import './BottomNav.css';

export const BottomNav: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const navItems = [
    { path: '/', label: 'Home', icon: Home },
    { path: '/workspace', label: 'Workspace', icon: Code2 },
    { path: '/preview', label: 'Preview', icon: Play },
    { path: '/terminal', label: 'Terminal', icon: Terminal },
    { path: '/deploy', label: 'Deploy', icon: Rocket },
  ];

  return (
    <div className="bottom-nav-container">
      <FloatingAIButton />

      <nav className="v-bottom-nav">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;

          return (
            <button
              key={item.path}
              className={`v-nav-item ${isActive ? 'active' : ''}`}
              onClick={() => navigate(item.path)}
            >
              <div className="v-nav-icon-wrapper">
                <Icon size={20} />
              </div>
              <span className="v-nav-label">{item.label}</span>
            </button>
          );
        })}
      </nav>
    </div>
  );
};
