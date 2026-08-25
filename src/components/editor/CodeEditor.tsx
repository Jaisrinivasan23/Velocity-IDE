import React from 'react';
import { useProject } from '../../context/ProjectContext';
import { EditorToolbar } from './EditorToolbar';
import { Sparkles, FileCode, RotateCcw } from 'lucide-react';
import './CodeEditor.css';

interface CodeEditorProps {
  onOpenAI: () => void;
}

export const CodeEditor: React.FC<CodeEditorProps> = ({ onOpenAI }) => {
  const { 
    selectedFile, 
    codeState, 
    updateCode, 
    modifiedFiles, 
    isGlassmorphic, 
    rollbackAvailable, 
    rollbackAIChanges 
  } = useProject();

  const currentCode = codeState[selectedFile] || `// ${selectedFile}\nexport default function Component() {\n  return <div>Component</div>;\n}`;
  const isModified = modifiedFiles.includes(selectedFile);
  const lines = currentCode.split('\n');

  return (
    <div className="v-editor-container font-mono">
      {/* Editor File Tab Header */}
      <div className="v-editor-tab-header">
        <div className="v-editor-tab active">
          <FileCode size={14} className="text-accent" />
          <span>{selectedFile.split('/').pop()}</span>
          {isModified && <span className="modified-star">*</span>}
        </div>

        {rollbackAvailable && (
          <button 
            className="v-rollback-badge-btn" 
            onClick={rollbackAIChanges}
            title="Rollback AI changes"
          >
            <RotateCcw size={12} />
            <span>Rollback AI</span>
          </button>
        )}
      </div>

      {/* Main Code Textarea / Line Numbers area */}
      <div className="v-editor-body">
        <div className="v-line-numbers">
          {lines.map((_, i) => (
            <div key={i + 1} className="v-line-number">{i + 1}</div>
          ))}
        </div>

        <div className="v-code-content-wrapper">
          <textarea
            className="v-code-textarea"
            value={currentCode}
            onChange={(e) => updateCode(selectedFile, e.target.value)}
            spellCheck={false}
          />
        </div>
      </div>

      {/* AI Changed Indicator Floating Pill */}
      {isGlassmorphic && selectedFile.includes('TransactionList.jsx') && (
        <div className="v-editor-ai-pill animate-fade-in font-ui">
          <Sparkles size={14} className="text-accent" />
          <span>AI modified: Glassmorphism variant applied</span>
        </div>
      )}

      {/* Bottom Editor Toolbar */}
      <EditorToolbar onOpenAI={onOpenAI} />
    </div>
  );
};
