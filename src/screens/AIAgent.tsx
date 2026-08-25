import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useProject } from '../context/ProjectContext';
import { AIMessage } from '../components/ai/AIMessage';
import { ExecutionPlan } from '../components/ai/ExecutionPlan';
import { ToolExecution } from '../components/ai/ToolExecution';
import { Button } from '../components/common/Button';
import { Sparkles, Send, Plus, Mic, Play, CheckCircle2 } from 'lucide-react';
import './AIAgent.css';

interface AIAgentProps {
  embedded?: boolean;
}

export const AIAgent: React.FC<AIAgentProps> = ({ embedded = false }) => {
  const navigate = useNavigate();
  const { 
    aiMessages, 
    aiBuildPlan, 
    aiToolActivities, 
    runAIBuildWorkflow, 
    aiBusy,
    sendChatMessage,
    startNewChat
  } = useProject();

  const [inputPrompt, setInputPrompt] = useState('');

  const handleSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!inputPrompt.trim() || aiBusy) return;
    
    if (inputPrompt.toLowerCase().includes('build a modern expense tracker')) {
      runAIBuildWorkflow(inputPrompt.trim());
    } else {
      sendChatMessage(inputPrompt.trim());
    }
    setInputPrompt('');
  };

  const suggestions = [
    'Explain this file',
    'Fix errors',
    'Refactor',
    'Improve performance',
    'Add feature',
    'Write tests'
  ];

  const isBuildComplete = aiBuildPlan.length > 0 && aiBuildPlan.every(s => s.status === 'complete');

  return (
    <div className={`ai-agent-container font-ui ${embedded ? 'embedded' : ''}`}>
      {/* Header */}
      {!embedded && (
        <div className="ai-agent-header">
          <div className="agent-title-group">
            <Sparkles size={20} className="text-accent" />
            <h2 className="agent-title">AI Agent</h2>
          </div>
          <span className="agent-subtitle">Project-aware development agent</span>
        </div>
      )}

      {/* Main Conversation Stream */}
      <div className="ai-conversation-scroll">
        {aiMessages.map((msg) => (
          <AIMessage key={msg.id} message={msg} />
        ))}

        {/* Execution Plan Step Card */}
        <ExecutionPlan steps={aiBuildPlan} />

        {/* Tool Activity Logs */}
        <ToolExecution activities={aiToolActivities} />

        {/* App Ready Banner Call-to-Action */}
        {isBuildComplete && (
          <div className="app-ready-banner animate-fade-in">
            <div className="ready-text-wrap font-mono">
              <CheckCircle2 size={20} className="text-success" />
              <span>App ready! PocketLedger running on device.</span>
            </div>
            <Button
              variant="success"
              fullWidth
              icon={<Play size={16} />}
              onClick={() => navigate('/preview')}
            >
              Open Preview
            </Button>
          </div>
        )}
      </div>

      {/* Suggested Action Chips */}
      <div className="agent-suggestions-bar">
        <span className="suggestions-label font-mono">SUGGESTIONS:</span>
        <div className="suggestions-scroll font-ui">
          {suggestions.map(sugg => (
            <button
              key={sugg}
              className="agent-chip"
              onClick={() => setInputPrompt(sugg)}
            >
              <Sparkles size={12} className="text-accent" />
              <span>{sugg}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Bottom Input Area */}
      <form className="agent-input-container" onSubmit={handleSubmit}>
        <button type="button" className="input-icon-btn" title="New Chat" onClick={startNewChat}>
          <Plus size={18} />
        </button>
        <button type="button" className="input-icon-btn" title="Voice Input">
          <Mic size={18} />
        </button>
        <input
          type="text"
          className="agent-text-input"
          placeholder="Ask Velocity anything..."
          value={inputPrompt}
          onChange={(e) => setInputPrompt(e.target.value)}
        />
        <Button
          type="submit"
          size="sm"
          loading={aiBusy}
          icon={<Send size={14} />}
        >
          Send
        </Button>
      </form>
    </div>
  );
};
