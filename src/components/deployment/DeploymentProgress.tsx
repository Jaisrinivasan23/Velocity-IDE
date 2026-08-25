import React from 'react';
import { useProject } from '../../context/ProjectContext';
import { Check, Loader2, Rocket, ExternalLink, Copy, Terminal } from 'lucide-react';
import { Button } from '../common/Button';
import './DeploymentProgress.css';

export const DeploymentProgress: React.FC = () => {
  const { 
    deploymentStep, 
    deploymentStatus, 
    deployedUrl, 
    addToast 
  } = useProject();

  const deploySteps = [
    'Preparing build',
    'Running tests',
    'Creating artifact',
    'Uploading to edge network',
    'Running health check'
  ];

  const handleCopyUrl = () => {
    navigator.clipboard.writeText(`https://${deployedUrl}`);
    addToast('URL copied to clipboard!', 'success');
  };

  if (deploymentStatus === 'success') {
    return (
      <div className="v-deploy-success-card animate-fade-in font-ui">
        <div className="success-icon-badge">
          <Check size={36} className="text-success" />
        </div>

        <h2 className="success-heading">YOUR APP IS LIVE</h2>
        <p className="success-sub">Deployed via Vercel Edge Engine</p>

        <div className="deployed-url-box font-mono">
          <span className="url-protocol">https://</span>
          <span className="url-domain">{deployedUrl}</span>
        </div>

        <div className="success-action-buttons">
          <Button
            variant="primary"
            fullWidth
            icon={<ExternalLink size={16} />}
            onClick={() => window.open(`https://${deployedUrl}`, '_blank')}
          >
            Open App
          </Button>

          <div className="success-secondary-row">
            <Button
              variant="secondary"
              size="sm"
              icon={<Copy size={14} />}
              onClick={handleCopyUrl}
            >
              Copy URL
            </Button>

            <Button
              variant="ghost"
              size="sm"
              icon={<Terminal size={14} />}
              onClick={() => alert("Deployment Logs:\n- Build output: 142.8 KB\n- CDN Edge nodes: 24 active\n- SSL Certificate: Active")}
            >
              View Logs
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="v-deploy-progress-card font-mono">
      <div className="progress-header font-ui">
        <Rocket size={20} className="text-accent animate-spin" />
        <div>
          <h3 className="progress-title">Deploying PocketLedger</h3>
          <p className="progress-sub">Step {deploymentStep} of {deploySteps.length}</p>
        </div>
      </div>

      <div className="deploy-steps-list">
        {deploySteps.map((stepLabel, idx) => {
          const stepNum = idx + 1;
          const isDone = deploymentStep > stepNum;
          const isRunning = deploymentStep === stepNum;

          return (
            <div key={stepLabel} className={`deploy-step-row ${isDone ? 'done' : isRunning ? 'running' : 'pending'}`}>
              <span className="step-icon-wrap">
                {isDone && <Check size={14} className="text-success" />}
                {isRunning && <Loader2 size={14} className="animate-spin text-accent" />}
                {!isDone && !isRunning && '○'}
              </span>
              <span className="step-label">{stepLabel}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
};
