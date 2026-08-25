import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BottomSheet } from '../layout/BottomSheet';
import { useProject } from '../../context/ProjectContext';
import { Button } from '../common/Button';
import { Sparkles, Code2, Check, Loader2, FileCode } from 'lucide-react';
import './InspectorSheet.css';

interface InspectorSheetProps {
  isOpen: boolean;
  onClose: () => void;
}

export const InspectorSheet: React.FC<InspectorSheetProps> = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const { applyGlassmorphicChange, setSelectedFile } = useProject();

  const [mode, setMode] = useState<'info' | 'ai_prompt' | 'executing'>('info');
  const [execStep, setExecStep] = useState(0);

  const execSteps = [
    'Analyzing component',
    'Finding source',
    'Updating styles',
    'Rebuilding preview',
    'Verifying result'
  ];

  const handleChooseGlassmorphic = async () => {
    setMode('executing');
    setExecStep(0);

    for (let i = 0; i < execSteps.length; i++) {
      setExecStep(i);
      await new Promise(r => setTimeout(r, 350));
    }

    await applyGlassmorphicChange();
    setMode('info');
    onClose();
  };

  const handleViewSource = () => {
    setSelectedFile('src/components/TransactionList.jsx');
    onClose();
    navigate('/workspace');
  };

  return (
    <BottomSheet
      isOpen={isOpen}
      onClose={() => {
        setMode('info');
        onClose();
      }}
      title="UI Inspector"
      subtitle="Selected Element Details"
    >
      <div className="inspector-sheet-body">
        {mode === 'info' && (
          <div className="inspector-info-view">
            {/* Selected Element Header */}
            <div className="selected-element-card font-mono">
              <span className="element-meta-label">SELECTED ELEMENT</span>
              <h3 className="element-name">TransactionCard</h3>
              <div className="element-file-info">
                <FileCode size={14} className="text-accent" />
                <span>src/components/TransactionList.jsx</span>
                <span className="line-badge font-mono">Line 42</span>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="inspector-actions font-ui">
              <Button
                variant="primary"
                fullWidth
                icon={<Sparkles size={16} />}
                onClick={() => setMode('ai_prompt')}
              >
                Ask AI to Modify
              </Button>
              <div className="secondary-row">
                <Button
                  variant="secondary"
                  size="sm"
                  icon={<Code2 size={14} />}
                  onClick={handleViewSource}
                >
                  View Source
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => alert("TransactionCard is rendered inside TransactionList.jsx line 42 with props: transaction object.")}
                >
                  Explain
                </Button>
              </div>
            </div>
          </div>
        )}

        {mode === 'ai_prompt' && (
          <div className="inspector-prompt-view animate-fade-in">
            <h4 className="prompt-heading">What should I change for TransactionCard?</h4>

            <div className="suggestion-chips-column">
              <button className="change-chip featured" onClick={handleChooseGlassmorphic}>
                <Sparkles size={16} className="chip-icon" />
                <div className="chip-text font-ui">
                  <span className="chip-title">Make this card glassmorphic</span>
                  <span className="chip-sub">Translucent background, blur filter & subtle glow</span>
                </div>
              </button>

              <button className="change-chip" onClick={handleChooseGlassmorphic}>
                <Sparkles size={14} className="chip-icon" />
                <span>Add swipe-to-delete</span>
              </button>

              <button className="change-chip" onClick={handleChooseGlassmorphic}>
                <Sparkles size={14} className="chip-icon" />
                <span>Change spacing & padding</span>
              </button>

              <button className="change-chip" onClick={handleChooseGlassmorphic}>
                <Sparkles size={14} className="chip-icon" />
                <span>Improve micro-animations</span>
              </button>

              <button className="change-chip" onClick={handleChooseGlassmorphic}>
                <Sparkles size={14} className="chip-icon" />
                <span>Make it more compact</span>
              </button>
            </div>
          </div>
        )}

        {mode === 'executing' && (
          <div className="inspector-exec-view animate-fade-in font-mono">
            <div className="exec-title font-ui">
              <Sparkles size={18} className="text-accent animate-spin" />
              <span>Updating TransactionList.jsx...</span>
            </div>

            <div className="exec-steps-container">
              {execSteps.map((label, idx) => {
                const isDone = execStep > idx;
                const isRunning = execStep === idx;

                return (
                  <div key={label} className={`exec-step ${isDone ? 'done' : isRunning ? 'running' : 'pending'}`}>
                    <span className="step-status">
                      {isDone && <Check size={14} className="text-success" />}
                      {isRunning && <Loader2 size={14} className="animate-spin text-accent" />}
                      {!isDone && !isRunning && '○'}
                    </span>
                    <span className="step-label">{label}</span>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </BottomSheet>
  );
};
