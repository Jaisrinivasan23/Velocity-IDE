import React from 'react';
import './Card.css';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'elevated' | 'glow' | 'glass';
  interactive?: boolean;
}

export const Card: React.FC<CardProps> = ({
  children,
  variant = 'default',
  interactive = false,
  className = '',
  ...props
}) => {
  return (
    <div
      className={`v-card v-card-${variant} ${interactive ? 'v-card-interactive' : ''} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};
