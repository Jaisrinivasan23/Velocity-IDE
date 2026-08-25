import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useProject } from '../../context/ProjectContext';
import { Badge } from '../common/Badge';
import { 
  ArrowLeft, 
  MoreVertical, 
  ShieldCheck, 
  AlertOctagon, 
  RotateCcw, 
  Activity, 
  BrainCircuit, 
  Sparkles,
  Folder
} from 'lucide-react';
import './TopBar.css';

export const TopBar: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { 
    projectName, 
    buildStatus, 
    runtimeError, 
    simulateError, 
    resetDemo, 
    projectHealthy 
  } = useProject();
  
  const [showMenu, setShowMenu] = useState(false);

  const isHome = location.pathname === '/';
  const showBack = !isHome;

  const getScreenTitle = () => {
    switch (location.pathname) {
      case '/': return 'VELOCITY-IDE';
      case '/workspace': return projectName;
      case '/ai': return 'AI Agent';
      case '/preview': return 'Live Preview';
      case '/projects': return 'Projects';
      case '/terminal': return 'Terminal';
      case '/deploy': return 'Deploy';
      case '/doctor': return 'Project Doctor';
      case '/memory': return 'Project Memory';
      default: return 'VELOCITY-IDE';
    }
  };

  return (
    <header className="v-topbar">
      <div className="v-topbar-left">
        {showBack ? (
          <button className="v-topbar-back" onClick={() => navigate(-1)} aria-label="Go back">
            <ArrowLeft size={20} />
          </button>
        ) : (
          <div className="v-topbar-logo">
            <Sparkles size={18} className="v-topbar-sparkle" />
          </div>
        )}
        <div className="v-topbar-title-wrap">
          <span className="v-topbar-title">{getScreenTitle()}</span>
          {!isHome && (
            <span className="v-topbar-subtitle">
              {runtimeError ? '● Error' : buildStatus === 'passed' ? '● Running' : '● Building'}
            </span>
          )}
        </div>
      </div>

      <div className="v-topbar-right">
        {/* Projects Shortcut */}
        <button 
          className="v-topbar-action-icon"
          onClick={() => navigate('/projects')}
          title="Projects"
        >
          <Folder size={18} />
        </button>

        {/* Project Health Doctor Icon Shortcut */}
        <button 
          className="v-topbar-action-icon"
          onClick={() => navigate('/doctor')}
          title="Project Doctor"
        >
          <ShieldCheck size={18} className={projectHealthy ? 'text-success' : 'text-warning'} />
        </button>

        {/* Memory Icon Shortcut */}
        <button 
          className="v-topbar-action-icon"
          onClick={() => navigate('/memory')}
          title="Project Memory"
        >
          <BrainCircuit size={18} />
        </button>

        {/* Status Badge */}
        {runtimeError ? (
          <Badge variant="error" dot>Failed</Badge>
        ) : (
          <Badge variant="success" dot>Passed</Badge>
        )}

        {/* Overflow Menu */}
        <div className="v-topbar-menu-wrapper">
          <button 
            className="v-topbar-menu-btn"
            onClick={() => setShowMenu(!showMenu)}
            aria-label="More options"
          >
            <MoreVertical size={20} />
          </button>

          {showMenu && (
            <div className="v-topbar-dropdown animate-fade-in">
              <button 
                onClick={() => {
                  setShowMenu(false);
                  simulateError();
                }}
                className="v-dropdown-item text-error"
              >
                <AlertOctagon size={16} />
                <span>Simulate Error</span>
              </button>

              <button 
                onClick={() => {
                  setShowMenu(false);
                  navigate('/doctor');
                }}
                className="v-dropdown-item"
              >
                <Activity size={16} />
                <span>Project Doctor</span>
              </button>

              <button 
                onClick={() => {
                  setShowMenu(false);
                  resetDemo();
                }}
                className="v-dropdown-item"
              >
                <RotateCcw size={16} />
                <span>Reset Demo State</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
