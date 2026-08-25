import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Folder, Clock, Star, MoreVertical, MessageSquare } from 'lucide-react';
import { Card } from '../components/common/Card';
import './Projects.css';

export const Projects: React.FC = () => {
  const navigate = useNavigate();

  const mockProjects = [
    { id: '1', name: 'PocketLedger', framework: 'React · Vite', lastActive: '2 mins ago', starred: true },
    { id: '2', name: 'VelocityDocs', framework: 'Next.js', lastActive: '2 days ago', starred: false },
    { id: '3', name: 'API Server', framework: 'Node.js · Express', lastActive: '1 week ago', starred: false },
  ];

  const mockChats = [
    { id: '1', title: 'Build modern expense tracker', time: '10 mins ago', messages: 14 },
    { id: '2', title: 'Fix CSS Grid layout', time: '1 day ago', messages: 5 },
    { id: '3', title: 'Setup auth middleware', time: '3 days ago', messages: 8 },
  ];

  return (
    <div className="projects-screen-container">
      <div className="projects-header">
        <h2 className="projects-title">Your Projects</h2>
        <p className="projects-subtitle">Manage and access your development workspaces</p>
      </div>

      <div className="projects-list">
        {mockProjects.map(project => (
          <Card 
            key={project.id} 
            interactive 
            className="project-card"
            onClick={() => navigate('/workspace')}
          >
            <div className="project-card-header">
              <div className="project-icon">
                <Folder size={20} className="text-accent" />
              </div>
              <div className="project-actions">
                <button className="project-action-btn">
                  <Star size={16} className={project.starred ? "text-warning fill-current" : "text-muted"} />
                </button>
                <button className="project-action-btn">
                  <MoreVertical size={16} className="text-muted" />
                </button>
              </div>
            </div>
            
            <div className="project-card-body">
              <h3 className="project-name">{project.name}</h3>
              <span className="project-framework font-mono">{project.framework}</span>
            </div>
            
            <div className="project-card-footer">
              <Clock size={12} />
              <span>{project.lastActive}</span>
            </div>
          </Card>
        ))}
      </div>

      <div className="projects-header" style={{ marginTop: '16px' }}>
        <h2 className="projects-title">Recent Chats</h2>
      </div>

      <div className="projects-list">
        {mockChats.map(chat => (
          <Card 
            key={chat.id} 
            interactive 
            className="project-card"
            onClick={() => navigate('/ai')}
          >
            <div className="project-card-header">
              <div className="project-icon" style={{ background: 'var(--bg-elevated)', borderColor: 'var(--accent-primary)' }}>
                <MessageSquare size={18} className="text-accent" />
              </div>
            </div>
            
            <div className="project-card-body">
              <h3 className="project-name">{chat.title}</h3>
              <span className="project-framework font-mono">{chat.messages} messages</span>
            </div>
            
            <div className="project-card-footer">
              <Clock size={12} />
              <span>{chat.time}</span>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};
