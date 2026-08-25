import React from 'react';
import { useProject } from '../../context/ProjectContext';
import { ShieldCheck, CheckCircle2, Wrench, Sparkles } from 'lucide-react';
import { Button } from '../common/Button';
import { Card } from '../common/Card';
import { Badge } from '../common/Badge';
import './DoctorView.css';

export const DoctorView: React.FC = () => {
  const { doctorIssues, fixDoctorIssue, fixAllDoctorIssues, projectHealthy, aiBusy } = useProject();

  const unfixedCount = doctorIssues.filter(i => !i.isFixed).length;

  return (
    <div className="v-doctor-container font-ui">
      {/* Hero Banner */}
      <div className="v-doctor-hero">
        <div className="hero-icon-wrap">
          {projectHealthy ? (
            <ShieldCheck size={32} className="text-success" />
          ) : (
            <Wrench size={32} className="text-warning" />
          )}
        </div>

        <div className="hero-text-wrap">
          <h2 className="hero-title">PROJECT HEALTH</h2>
          <p className="hero-sub font-mono">
            {projectHealthy 
              ? '✓ Project healthy · No critical issues' 
              : `${unfixedCount} improvements found`}
          </p>
        </div>
      </div>

      {/* Primary Action Button */}
      {!projectHealthy && (
        <Button
          variant="primary"
          fullWidth
          loading={aiBusy}
          icon={<Sparkles size={18} />}
          onClick={fixAllDoctorIssues}
          className="fix-all-btn"
        >
          Fix All Issues with AI
        </Button>
      )}

      {/* Issues List */}
      <div className="v-doctor-issues-list">
        {doctorIssues.map((issue) => (
          <Card key={issue.id} className={`issue-card ${issue.isFixed ? 'fixed' : ''}`}>
            <div className="issue-header">
              <div className="issue-title-group">
                <span className="issue-title">{issue.title}</span>
                <Badge
                  variant={
                    issue.severity === 'High' ? 'error' :
                    issue.severity === 'Medium' ? 'warning' : 'info'
                  }
                >
                  {issue.severity}
                </Badge>
              </div>

              {issue.isFixed ? (
                <span className="fixed-badge font-mono text-success">
                  <CheckCircle2 size={16} />
                  <span>Fixed</span>
                </span>
              ) : (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => fixDoctorIssue(issue.id)}
                >
                  Fix
                </Button>
              )}
            </div>

            <p className="issue-desc">{issue.description}</p>
            <span className="issue-impact font-mono">Impact: {issue.impact}</span>
          </Card>
        ))}
      </div>
    </div>
  );
};
