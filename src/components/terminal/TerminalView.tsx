import React, { useState, useRef, useEffect } from 'react';
import { useProject } from '../../context/ProjectContext';
import { TerminalTab } from '../../types';
import { Terminal as TerminalIcon, AlertCircle, CheckCircle2, Play, AlertOctagon } from 'lucide-react';
import './TerminalView.css';

export const TerminalView: React.FC = () => {
  const { 
    terminalOutput, 
    runTerminalCommand, 
    runtimeError, 
    simulateError, 
    fixErrorWithAI 
  } = useProject();

  const [activeTab, setActiveTab] = useState<TerminalTab>('Terminal');
  const [commandInput, setCommandInput] = useState('');
  const terminalEndRef = useRef<HTMLDivElement>(null);

  const quickCommands = ['npm install', 'npm run dev', 'npm test', 'npm run build', 'git status'];

  useEffect(() => {
    terminalEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [terminalOutput]);

  const handleCommandSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!commandInput.trim()) return;
    runTerminalCommand(commandInput.trim());
    setCommandInput('');
  };

  return (
    <div className="v-terminal-wrapper font-mono">
      {/* Terminal Tabs Header */}
      <div className="v-terminal-tabs">
        {(['Terminal', 'Problems', 'Output'] as TerminalTab[]).map(tab => (
          <button
            key={tab}
            className={`v-term-tab ${activeTab === tab ? 'active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab === 'Terminal' && <TerminalIcon size={14} />}
            {tab === 'Problems' && (
              <AlertCircle size={14} className={runtimeError ? 'text-error' : ''} />
            )}
            {tab === 'Output' && <Play size={14} />}
            <span>{tab}</span>
            {tab === 'Problems' && runtimeError && (
              <span className="problems-badge">1</span>
            )}
          </button>
        ))}

        <button 
          className="v-term-simulate-btn font-ui" 
          onClick={simulateError}
          title="Trigger simulated runtime exception"
        >
          <AlertOctagon size={14} />
          <span>Simulate Error</span>
        </button>
      </div>

      {/* Main Terminal Window */}
      <div className="v-terminal-body">
        {activeTab === 'Terminal' && (
          <div className="v-terminal-console">
            {terminalOutput.map((line, idx) => (
              <div 
                key={idx} 
                className={`v-term-line ${
                  line.startsWith('$') ? 'v-term-cmd' : 
                  line.includes('ERROR') ? 'v-term-err' : 
                  line.includes('✓') ? 'v-term-success' : ''
                }`}
              >
                {line}
              </div>
            ))}
            <div ref={terminalEndRef} />
          </div>
        )}

        {activeTab === 'Problems' && (
          <div className="v-terminal-problems">
            {runtimeError ? (
              <div className="v-problem-card">
                <div className="problem-header">
                  <AlertCircle size={16} className="text-error" />
                  <span className="problem-title">RUNTIME ERROR in src/components/TransactionList.jsx:42</span>
                </div>
                <p className="problem-msg">{runtimeError}</p>
                <div className="problem-action font-ui">
                  <button className="v-fix-ai-btn" onClick={fixErrorWithAI}>
                    Fix with AI
                  </button>
                </div>
              </div>
            ) : (
              <div className="v-no-problems">
                <CheckCircle2 size={24} className="text-success" />
                <span>No problems detected in workspace</span>
              </div>
            )}
          </div>
        )}

        {activeTab === 'Output' && (
          <div className="v-terminal-console">
            <div className="v-term-line">[Vite HMR] connected.</div>
            <div className="v-term-line">[Vitest] watch mode initialized.</div>
            <div className="v-term-line">[Deploy Engine] Vercel target pre-flight check ready.</div>
          </div>
        )}
      </div>

      {/* Quick Commands & Command Line Bar */}
      <div className="v-terminal-controls font-ui">
        <div className="v-quick-chips font-mono">
          {quickCommands.map(cmd => (
            <button
              key={cmd}
              className="v-quick-chip"
              onClick={() => runTerminalCommand(cmd)}
            >
              {cmd}
            </button>
          ))}
        </div>

        <form className="v-term-input-form font-mono" onSubmit={handleCommandSubmit}>
          <span className="term-prompt-symbol">$</span>
          <input
            type="text"
            className="v-term-input"
            placeholder="Type npm command or bash prompt..."
            value={commandInput}
            onChange={(e) => setCommandInput(e.target.value)}
          />
        </form>
      </div>
    </div>
  );
};
