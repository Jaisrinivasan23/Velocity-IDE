import React, { useState } from 'react';
import { useProject } from '../context/ProjectContext';
import { PocketLedgerApp } from '../components/preview/PocketLedgerApp';
import { InspectorSheet } from '../components/preview/InspectorSheet';
import { RotateCw, MousePointer, Maximize2, Sparkles, CheckCircle2 } from 'lucide-react';
import './Preview.css';

export const Preview: React.FC = () => {
  const { setInspectedElement, addToast } = useProject();
  
  const [isInspectMode, setIsInspectMode] = useState(false);
  const [showInspectorSheet, setShowInspectorSheet] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);

  const handleToggleInspect = () => {
    const nextState = !isInspectMode;
    setIsInspectMode(nextState);
    if (nextState) {
      addToast('Visual Inspect Mode enabled. Tap any card below.', 'info');
    }
  };

  const handleSelectCard = () => {
    setInspectedElement('TransactionCard');
    setShowInspectorSheet(true);
  };

  return (
    <div className={`preview-screen-container ${isFullscreen ? 'fullscreen-mode' : ''}`}>
      {/* Control Header Bar */}
      <div className="preview-control-bar font-ui">
        <div className="status-indicator-group">
          <span className="live-status-dot font-mono">
            <CheckCircle2 size={14} className="text-success" />
            <span>Local Dev Server</span>
          </span>
        </div>

        <div className="preview-action-group">
          <button
            className="preview-btn"
            onClick={() => addToast('Refreshed HMR frame', 'info')}
            title="Refresh Frame"
          >
            <RotateCw size={16} />
          </button>

          <button
            className={`preview-btn inspect-toggle ${isInspectMode ? 'active' : ''}`}
            onClick={handleToggleInspect}
            title="Toggle Visual Inspect Mode"
          >
            <MousePointer size={16} />
            <span>Inspect</span>
          </button>

          <button
            className="preview-btn"
            onClick={() => setIsFullscreen(!isFullscreen)}
            title="Toggle Fullscreen"
          >
            <Maximize2 size={16} />
          </button>
        </div>
      </div>

      {/* Visual Inspect Mode Active Banner */}
      {isInspectMode && (
        <div className="inspect-banner animate-fade-in font-ui">
          <Sparkles size={16} className="text-accent animate-spin" />
          <span>Tap <strong>TransactionCard</strong> to inspect code & ask AI modifications</span>
        </div>
      )}

      {/* Main App Device Container */}
      <div className="preview-device-wrapper">
        <PocketLedgerApp
          isInspectMode={isInspectMode}
          onSelectCard={handleSelectCard}
        />
      </div>

      {/* Inspector Bottom Sheet */}
      <InspectorSheet
        isOpen={showInspectorSheet}
        onClose={() => {
          setShowInspectorSheet(false);
          setInspectedElement(null);
        }}
      />
    </div>
  );
};
