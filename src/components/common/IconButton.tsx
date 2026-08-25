import React from 'react';
import './IconButton.css';

interface IconButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  icon: React.ReactNode;
  variant?: 'default' | 'accent' | 'ghost' | 'active';
  size?: 'sm' | 'md' | 'lg';
  label?: string;
}

export const IconButton: React.FC<IconButtonProps> = ({
  icon,
  variant = 'default',
  size = 'md',
  label,
  className = '',
  ...props
}) => {
  return (
    <button
      className={`v-icon-btn v-icon-btn-${variant} v-icon-btn-${size} ${className}`}
      aria-label={label}
      title={label}
      {...props}
    >
      {icon}
    </button>
  );
};
