import React from 'react';
import { ExecutionStep } from '../../types';
import { Check, Loader2, Circle } from 'lucide-react';
import './ExecutionPlan.css';

interface ExecutionPlanProps {
  steps: ExecutionStep[];
}

export const ExecutionPlan: React.FC<ExecutionPlanProps> = ({ steps }) => {
  const completedCount = steps.filter(s => s.status === 'complete').length;

  return (
    <div className="v-plan-card">
      <div className="v-plan-header">
        <span className="v-plan-title">BUILD PLAN</span>
        <span className="v-plan-progress">{completedCount} / {steps.length}</span>
      </div>

      <div className="v-plan-steps">
        {steps.map((step) => (
          <div key={step.id} className={`v-step-item v-step-${step.status}`}>
            <span className="v-step-icon">
              {step.status === 'complete' && <Check size={14} className="text-success" />}
              {step.status === 'running' && <Loader2 size={14} className="animate-spin text-accent" />}
              {step.status === 'pending' && <Circle size={12} className="text-muted" />}
            </span>
            <span className="v-step-label">{step.label}</span>
          </div>
        ))}
      </div>
    </div>
  );
};
