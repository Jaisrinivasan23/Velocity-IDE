import React from 'react';
import { X } from 'lucide-react';
import './BottomSheet.css';

interface BottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  subtitle?: string;
  children: React.ReactNode;
}

export const BottomSheet: React.FC<BottomSheetProps> = ({
  isOpen,
  onClose,
  title,
  subtitle,
  children
}) => {
  if (!isOpen) return null;

  return (
    <div className="v-sheet-overlay" onClick={onClose}>
      <div 
        className="v-sheet-container animate-slide-up"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="v-sheet-handle" />

        {(title || subtitle) && (
          <div className="v-sheet-header">
            <div className="v-sheet-title-group">
              {title && <h3 className="v-sheet-title">{title}</h3>}
              {subtitle && <p className="v-sheet-subtitle">{subtitle}</p>}
            </div>
            <button className="v-sheet-close" onClick={onClose} aria-label="Close sheet">
              <X size={18} />
            </button>
          </div>
        )}

        <div className="v-sheet-content">
          {children}
        </div>
      </div>
    </div>
  );
};
