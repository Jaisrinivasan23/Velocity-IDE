import React from 'react';
import { ToolActivity } from '../../types';
import { Terminal, FileCode, CheckCircle2, Loader2 } from 'lucide-react';
import './ToolExecution.css';

interface ToolExecutionProps {
  activities: ToolActivity[];
}

export const ToolExecution: React.FC<ToolExecutionProps> = ({ activities }) => {
  return (
    <div className="v-tools-container">
      <h4 className="v-tools-heading">AGENT ACTIVITIES</h4>
      <div className="v-tools-list">
        {activities.map((act) => (
          <div key={act.id} className="v-tool-card">
            <div className="v-tool-left">
              {act.name.includes('Installing') || act.name.includes('Starting') ? (
                <Terminal size={16} className="v-tool-type-icon" />
              ) : (
                <FileCode size={16} className="v-tool-type-icon" />
              )}
              <div className="v-tool-info">
                <span className="v-tool-name">{act.name}</span>
                <span className="v-tool-detail font-mono">{act.detail}</span>
              </div>
            </div>

            <div className="v-tool-status">
              {act.status === 'complete' ? (
                <CheckCircle2 size={16} className="text-success" />
              ) : (
                <Loader2 size={16} className="animate-spin text-accent" />
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
