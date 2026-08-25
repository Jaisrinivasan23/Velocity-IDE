import React, { useState, useEffect } from 'react';
import { BottomSheet } from '../layout/BottomSheet';
import { useProject } from '../../context/ProjectContext';
import { Button } from '../common/Button';
import { DEBUGGER_DIFF } from '../../data/code';
import { Check, Loader2, Bug, CheckCircle2 } from 'lucide-react';
import './AIDebuggerSheet.css';

interface AIDebuggerSheetProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AIDebuggerSheet: React.FC<AIDebuggerSheetProps> = ({ isOpen, onClose }) => {
  const { fixErrorWithAI, runtimeError } = useProject();
  
  const [debugStep, setDebugStep] = useState(0);
  const [isApplying, setIsApplying] = useState(false);

  const debugSteps = [
    'Analyzing stack trace',
    'Finding source',
    'Checking recent changes',
    'Identifying root cause',
    'Preparing fix',
    'Running tests'
  ];

  useEffect(() => {
    if (isOpen) {
      setDebugStep(0);
      const timer = setInterval(() => {
        setDebugStep(prev => {
          if (prev < debugSteps.length) return prev + 1;
          clearInterval(timer);
          return prev;
        });
      }, 400);
      return () => clearInterval(timer);
    }
  }, [isOpen]);

  const handleApplyFix = async () => {
    setIsApplying(true);
    await fixErrorWithAI();
    setIsApplying(false);
    onClose();
  };

  const isAnalysisComplete = debugStep >= debugSteps.length;

  return (
    <BottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title="AI Debugger"
      subtitle="Automated stack trace analysis & repair"
    >
      <div className="ai-debugger-body">
        {/* Error Badge */}
        <div className="debugger-error-box">
          <Bug size={18} className="text-error" />
          <div className="error-box-info">
            <span className="error-box-title font-mono">RUNTIME ERROR</span>
            <span className="error-box-detail font-mono">
              {runtimeError || 'TransactionList.jsx:42 - Cannot read properties of undefined'}
            </span>
          </div>
        </div>

        {/* Debugging Workflow Steps */}
        <div className="debugger-steps-list">
          {debugSteps.map((label, idx) => {
            const isDone = debugStep > idx;
            const isRunning = debugStep === idx;

            return (
              <div key={label} className={`debugger-step-item ${isDone ? 'done' : isRunning ? 'running' : 'pending'}`}>
                <span className="step-icon-wrap">
                  {isDone && <Check size={14} className="text-success" />}
                  {isRunning && <Loader2 size={14} className="animate-spin text-accent" />}
                  {!isDone && !isRunning && <span className="step-dot" />}
                </span>
                <span className="step-label">{label}</span>
              </div>
            );
          })}
        </div>

        {/* Diagnosis & Proposed Fix Diff */}
        {isAnalysisComplete && (
          <div className="debugger-solution-box animate-fade-in">
            <div className="solution-header font-mono">
              <CheckCircle2 size={16} className="text-success" />
              <span>Issue Found & Solution Prepared</span>
            </div>

            <p className="solution-text">
              <strong>transactions</strong> can be <code>undefined</code> during the initial render before data hydration.
            </p>

            <div className="diff-preview font-mono">
              <div className="diff-header">PROPOSED FIX (src/components/TransactionList.jsx:42)</div>
              {DEBUGGER_DIFF.split('\n').map((line, idx) => (
                <div 
                  key={idx} 
                  className={`diff-line ${line.startsWith('+') ? 'diff-add' : line.startsWith('-') ? 'diff-remove' : ''}`}
                >
                  {line}
                </div>
              ))}
            </div>

            {/* Action Buttons */}
            <div className="debugger-actions">
              <Button
                variant="success"
                fullWidth
                loading={isApplying}
                onClick={handleApplyFix}
                icon={<Check size={16} />}
              >
                Apply Fix
              </Button>
              <div className="secondary-actions">
                <Button variant="secondary" size="sm" onClick={onClose}>
                  Review Code
                </Button>
                <Button variant="ghost" size="sm" onClick={onClose}>
                  Reject
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </BottomSheet>
  );
};
