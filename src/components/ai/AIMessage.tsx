import React from 'react';
import { AIMessage as AIMessageType } from '../../types';
import { Sparkles, User } from 'lucide-react';
import './AIMessage.css';

interface AIMessageProps {
  message: AIMessageType;
}

export const AIMessage: React.FC<AIMessageProps> = ({ message }) => {
  const isUser = message.sender === 'user';

  return (
    <div className={`ai-message-row ${isUser ? 'user-row' : 'ai-row'}`}>
      <div className={`message-avatar ${isUser ? 'user-avatar' : 'ai-avatar'}`}>
        {isUser ? <User size={16} /> : <Sparkles size={16} />}
      </div>
      
      <div className="message-body">
        <div className="message-header">
          <span className="sender-name">{isUser ? 'You' : 'Velocity AI'}</span>
          <span className="message-time">{message.timestamp}</span>
        </div>
        <div className="message-text">
          {message.text.split('\n').map((paragraph, idx) => (
            <p key={idx}>{paragraph}</p>
          ))}
        </div>
      </div>
    </div>
  );
};
