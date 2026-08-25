import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useProject } from '../context/ProjectContext';
import { Card } from '../components/common/Card';
import { Button as CommonButton } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { RECENT_PROJECTS } from '../data/project';
import { Sparkles, Code2, Bug, BrainCircuit, Rocket, ChevronRight, Folder } from 'lucide-react';
import './Home.css';

export const Home: React.FC = () => {
  const navigate = useNavigate();
  const { projectName, framework, buildStatus, runtimeError } = useProject();

  const handleShortcut = (type: string) => {
    switch (type) {
      case 'build':
        navigate('/ai');
        break;
      case 'fix':
        navigate('/terminal');
        break;
      case 'explain':
        navigate('/memory');
        break;
      case 'deploy':
        navigate('/deploy');
        break;
    }
  };

  return (
    <div className="home-screen-container font-ui">
      {/* Brand Hero Greeting */}
      <div className="home-hero-header">
        <div className="hero-brand">
          <Sparkles size={24} className="hero-sparkle-icon" />
          <h1 className="hero-app-title">VELOCITY-IDE</h1>
        </div>
        <p className="hero-tagline">Build software. Anywhere.</p>
        <p className="hero-greeting font-mono">Your AI development workspace on mobile.</p>
      </div>

      {/* Current Active Project Card */}
      <Card variant="glow" className="current-project-card">
        <div className="project-card-header">
          <div className="project-title-group">
            <span className="card-label">CURRENT PROJECT</span>
            <h3 className="project-name">{projectName}</h3>
            <span className="project-stack font-mono">{framework}</span>
          </div>
          {runtimeError ? (
            <Badge variant="error" dot>Error</Badge>
          ) : (
            <Badge variant="success" dot>Build passed</Badge>
          )}
        </div>

        <div className="project-stats-row font-mono">
          <span>12 files</span>
          <span>•</span>
          <span>3 changes</span>
          <span>•</span>
          <span className="text-success">● Live Preview Ready</span>
        </div>

        <div className="project-card-actions">
          <CommonButton
            variant="primary"
            fullWidth
            icon={<Code2 size={16} />}
            onClick={() => navigate('/workspace')}
          >
            Open Workspace
          </CommonButton>
        </div>
      </Card>

      {/* Build With AI Main Feature Banner */}
      <div className="build-ai-card animate-pulse-glow" onClick={() => navigate('/ai')}>
        <div className="build-ai-content">
          <div className="build-ai-icon-badge">
            <Sparkles size={20} className="text-accent" />
          </div>
          <div className="build-ai-text">
            <span className="build-ai-label font-mono">✦ BUILD WITH AI</span>
            <h3 className="build-ai-heading">Describe what you want. Velocity builds it.</h3>
          </div>
        </div>
        <CommonButton variant="primary" size="sm" icon={<ChevronRight size={16} />}>
          Start building
        </CommonButton>
      </div>

      {/* AI Shortcut Grid */}
      <div className="shortcuts-section">
        <span className="section-label font-mono">AI SHORTCUTS</span>
        <div className="shortcuts-grid">
          <div className="shortcut-card" onClick={() => handleShortcut('build')}>
            <Sparkles size={18} className="shortcut-icon text-accent" />
            <span className="shortcut-title">Build an app</span>
          </div>

          <div className="shortcut-card" onClick={() => handleShortcut('fix')}>
            <Bug size={18} className="shortcut-icon text-warning" />
            <span className="shortcut-title">Fix a bug</span>
          </div>

          <div className="shortcut-card" onClick={() => handleShortcut('explain')}>
            <BrainCircuit size={18} className="shortcut-icon text-info" />
            <span className="shortcut-title">Explain project</span>
          </div>

          <div className="shortcut-card" onClick={() => handleShortcut('deploy')}>
            <Rocket size={18} className="shortcut-icon text-success" />
            <span className="shortcut-title">Deploy project</span>
          </div>
        </div>
      </div>

      {/* Recent Projects List */}
      <div className="recents-section">
        <span className="section-label font-mono">RECENT PROJECTS</span>
        <div className="recents-list">
          {RECENT_PROJECTS.map((proj) => (
            <Card
              key={proj.name}
              interactive
              className={`recent-item ${proj.active ? 'active-recent' : ''}`}
              onClick={() => navigate('/workspace')}
            >
              <div className="recent-left">
                <Folder size={18} className={proj.active ? 'text-accent' : 'text-muted'} />
                <div className="recent-info">
                  <span className="recent-name">{proj.name}</span>
                  <span className="recent-stack font-mono">{proj.stack}</span>
                </div>
              </div>
              <div className="recent-right font-mono">
                <Badge variant={proj.active ? 'success' : 'neutral'}>{proj.status}</Badge>
                <span className="recent-time">{proj.time}</span>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
};
