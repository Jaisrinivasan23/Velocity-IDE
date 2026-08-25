import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Command, Play, Sparkles } from 'lucide-react';
import './EditorToolbar.css';

interface EditorToolbarProps {
  onOpenAI: () => void;
}

export const EditorToolbar: React.FC<EditorToolbarProps> = ({ onOpenAI }) => {
  const navigate = useNavigate();

  return (
    <div className="v-editor-toolbar">
      <button className="v-toolbar-action" title="Search Code">
        <Search size={16} />
        <span>Search</span>
      </button>

      <button className="v-toolbar-action" title="Command Palette">
        <Command size={16} />
        <span>Command</span>
      </button>

      <button className="v-toolbar-action" onClick={() => navigate('/preview')} title="Run Preview">
        <Play size={16} className="text-success" />
        <span>Run</span>
      </button>

      <button className="v-toolbar-action v-toolbar-ai" onClick={onOpenAI} title="AI Assistant">
        <Sparkles size={16} className="text-accent" />
        <span>AI</span>
      </button>
    </div>
  );
};
