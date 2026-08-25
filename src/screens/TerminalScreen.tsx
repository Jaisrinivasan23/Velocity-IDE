import React, { useState } from 'react';
import { TerminalView } from '../components/terminal/TerminalView';
import { AIDebuggerSheet } from '../components/ai/AIDebuggerSheet';
import { useProject } from '../context/ProjectContext';

export const TerminalScreen: React.FC = () => {
  const { runtimeError } = useProject();
  const [showDebuggerSheet, setShowDebuggerSheet] = useState(false);

  return (
    <div style={{ height: '100%', width: '100%' }}>
      <TerminalView />
      <AIDebuggerSheet
        isOpen={showDebuggerSheet || Boolean(runtimeError && false)} 
        onClose={() => setShowDebuggerSheet(false)}
      />
    </div>
  );
};
