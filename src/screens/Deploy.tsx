import React from 'react';
import { useProject } from '../context/ProjectContext';
import { DeploymentProgress } from '../components/deployment/DeploymentProgress';
import { Button } from '../components/common/Button';
import { Card } from '../components/common/Card';
import { Check, Rocket, ShieldCheck, Globe, Server } from 'lucide-react';
import './Deploy.css';

export const Deploy: React.FC = () => {
  const { 
    deploymentStatus, 
    startDeployment, 
    activeDeployTarget, 
    setActiveDeployTarget,
    testsPassed,
    projectHealthy
  } = useProject();

  const deployTargets = [
    { id: 'Vercel', name: 'Vercel Edge Network', desc: 'Global serverless distribution' },
    { id: 'Netlify', name: 'Netlify Cloud', desc: 'Static CDN & function hosting' },
    { id: 'GitHub', name: 'GitHub Pages', desc: 'Repository static gh-pages' },
    { id: 'Device', name: 'Local Device PWA', desc: 'Standalone offline installer' }
  ];

  const checklistItems = [
    { label: 'Dependencies verified', ok: true },
    { label: 'Production bundle verified', ok: true },
    { label: 'Environment variables set', ok: true },
    { label: `Unit tests passing (${testsPassed ? '18/18' : 'Failed'})`, ok: testsPassed },
    { label: `Project Doctor checks (${projectHealthy ? 'Clean' : '3 warnings'})`, ok: projectHealthy }
  ];

  const isReady = testsPassed;

  return (
    <div className="deploy-screen-container font-ui">
      {/* Header */}
      <div className="deploy-screen-header">
        <div className="deploy-title-group">
          <Rocket size={24} className="text-accent" />
          <h1 className="deploy-screen-title">DEPLOYMENT</h1>
        </div>
        <p className="deploy-screen-sub font-mono">Ship PocketLedger to edge production</p>
      </div>

      {deploymentStatus !== 'idle' ? (
        <DeploymentProgress />
      ) : (
        <>
          {/* Pre-flight Checklist Card */}
          <Card className="preflight-card">
            <div className="preflight-header font-mono">
              <ShieldCheck size={16} className="text-success" />
              <span>PRE-FLIGHT CHECKLIST</span>
            </div>

            <div className="checklist-items-list">
              {checklistItems.map(item => (
                <div key={item.label} className="checklist-item-row">
                  <span className={`check-icon ${item.ok ? 'ok' : 'warn'}`}>
                    <Check size={14} />
                  </span>
                  <span className="checklist-label">{item.label}</span>
                </div>
              ))}
            </div>
          </Card>

          {/* Target Selector */}
          <div className="target-selector-section">
            <span className="section-label font-mono">DEPLOYMENT TARGET</span>
            <div className="target-cards-grid">
              {deployTargets.map(t => (
                <Card
                  key={t.id}
                  interactive
                  className={`target-card ${activeDeployTarget === t.id ? 'active-target' : ''}`}
                  onClick={() => setActiveDeployTarget(t.id)}
                >
                  <div className="target-card-top">
                    {t.id === 'Vercel' ? <Globe size={18} className="text-accent" /> : <Server size={18} />}
                    <span className="target-name">{t.name}</span>
                  </div>
                  <span className="target-desc font-mono">{t.desc}</span>
                </Card>
              ))}
            </div>
          </div>

          {/* Primary Action Button */}
          <Button
            variant="primary"
            size="lg"
            fullWidth
            disabled={!isReady}
            icon={<Rocket size={20} />}
            onClick={startDeployment}
          >
            Deploy to {activeDeployTarget}
          </Button>
        </>
      )}
    </div>
  );
};
