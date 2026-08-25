import React, { useState } from 'react';
import { BottomSheet } from '../layout/BottomSheet';
import { useProject } from '../../context/ProjectContext';
import { Button } from '../common/Button';
import { Sparkles, Send, FileCode } from 'lucide-react';
import './AISheet.css';

interface AISheetProps {
  isOpen: boolean;
  onClose: () => void;
  fileName: string;
}

export const AISheet: React.FC<AISheetProps> = ({ isOpen, onClose, fileName }) => {
  const { addToast } = useProject();
  const [prompt, setPrompt] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [aiResponse, setAiResponse] = useState<string | null>(null);

  const suggestions = [
    'Explain this file',
    'Fix errors',
    'Refactor component',
    'Improve performance',
    'Add feature',
    'Write tests'
  ];

  const handleAction = async (text: string) => {
    setIsProcessing(true);
    setAiResponse(null);
    await new Promise(r => setTimeout(r, 800));

    if (text.includes('Explain')) {
      setAiResponse(`This file (${fileName}) renders the list of financial transactions. It formats amounts with currency symbols and handles positive income vs negative expense variants.`);
    } else if (text.includes('Refactor')) {
      setAiResponse(`Refactored ${fileName} to extract TransactionCard into a memoized functional component with typed prop definitions.`);
    } else if (text.includes('Tests')) {
      setAiResponse(`Generated 4 new Vitest unit test cases for ${fileName} covering empty list states and negative amount formatting.`);
    } else {
      setAiResponse(`Analyzed ${fileName}. Applied optimization pass and updated component structure.`);
    }

    setIsProcessing(false);
    addToast(`AI Action completed for ${fileName}`, 'success');
  };

  return (
    <BottomSheet
      isOpen={isOpen}
      onClose={onClose}
      title={`AI Context for ${fileName}`}
      subtitle="Select an action or ask Velocity AI"
    >
      <div className="ai-sheet-body">
        <div className="ai-sheet-file-badge font-mono">
          <FileCode size={14} />
          <span>{fileName}</span>
        </div>

        {aiResponse && (
          <div className="ai-sheet-response animate-fade-in">
            <div className="response-header font-mono">
              <Sparkles size={14} className="text-accent" />
              <span>Velocity AI Insights</span>
            </div>
            <p>{aiResponse}</p>
          </div>
        )}

        <div className="ai-sheet-suggestions">
          <span className="suggestions-label">SUGGESTED ACTIONS</span>
          <div className="chips-grid">
            {suggestions.map((item) => (
              <button
                key={item}
                className="ai-suggestion-chip"
                onClick={() => handleAction(item)}
                disabled={isProcessing}
              >
                <Sparkles size={12} className="chip-sparkle" />
                <span>{item}</span>
              </button>
            ))}
          </div>
        </div>

        <div className="ai-sheet-input-row">
          <input
            type="text"
            className="ai-sheet-input"
            placeholder="Ask anything about this code..."
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && prompt.trim()) {
                handleAction(prompt);
                setPrompt('');
              }
            }}
          />
          <Button
            size="sm"
            onClick={() => {
              if (prompt.trim()) {
                handleAction(prompt);
                setPrompt('');
              }
            }}
            loading={isProcessing}
            icon={<Send size={14} />}
          >
            Ask
          </Button>
        </div>
      </div>
    </BottomSheet>
  );
};
