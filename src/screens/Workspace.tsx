import React, { useState } from 'react';
import { useProject } from '../context/ProjectContext';
import { FileTree } from '../components/editor/FileTree';
import { CodeEditor } from '../components/editor/CodeEditor';
import { AISheet } from '../components/ai/AISheet';
import { AIAgent } from './AIAgent';
import { Folder, Code2, Sparkles } from 'lucide-react';
import './Workspace.css';

export const Workspace: React.FC = () => {
  const { files, selectedFile, setSelectedFile } = useProject();
  const [activeTab, setActiveTab] = useState<'Files' | 'Code' | 'AI'>('Files');
  const [showAISheet, setShowAISheet] = useState(false);

  const handleSelectFileFromTree = (path: string) => {
    setSelectedFile(path);
    setActiveTab('Code');
  };

  return (
    <div className="workspace-screen-container">
      {/* Workspace Tabs Header (Mobile) */}
      <div className="workspace-tabs-bar font-ui">
        <button
          className={`ws-tab-btn ${activeTab === 'Files' ? 'active' : ''}`}
          onClick={() => setActiveTab('Files')}
        >
          <Folder size={16} />
          <span>Files</span>
        </button>

        <button
          className={`ws-tab-btn ${activeTab === 'Code' ? 'active' : ''}`}
          onClick={() => setActiveTab('Code')}
        >
          <Code2 size={16} />
          <span>Code</span>
        </button>

        <button
          className={`ws-tab-btn ${activeTab === 'AI' ? 'active' : ''}`}
          onClick={() => setActiveTab('AI')}
        >
          <Sparkles size={16} className="text-accent" />
          <span>AI</span>
        </button>
      </div>

      {/* Main Workspace View Area */}
      <div className="workspace-main-content">
        {/* Mobile View Switching */}
        <div className={`ws-mobile-view ${activeTab === 'Files' ? 'show' : 'hide'}`}>
          <FileTree files={files} onSelectFile={handleSelectFileFromTree} />
        </div>

        <div className={`ws-mobile-view ${activeTab === 'Code' ? 'show' : 'hide'}`}>
          <CodeEditor onOpenAI={() => setShowAISheet(true)} />
        </div>

        <div className={`ws-mobile-view ${activeTab === 'AI' ? 'show' : 'hide'}`}>
          <AIAgent embedded />
        </div>
      </div>

      {/* Contextual AI Bottom Sheet for Editor */}
      <AISheet
        isOpen={showAISheet}
        onClose={() => setShowAISheet(false)}
        fileName={selectedFile}
      />
    </div>
  );
};
