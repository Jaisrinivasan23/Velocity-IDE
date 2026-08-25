import React from 'react';
import './Badge.css';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'success' | 'warning' | 'error' | 'info' | 'accent' | 'neutral';
  dot?: boolean;
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = 'neutral',
  dot = false
}) => {
  return (
    <span className={`v-badge v-badge-${variant}`}>
      {dot && <span className="v-badge-dot" />}
      {children}
    </span>
  );
};
