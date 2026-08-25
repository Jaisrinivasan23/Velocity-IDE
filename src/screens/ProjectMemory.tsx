import React from 'react';
import { Card } from '../components/common/Card';
import { BrainCircuit, Cpu, Layers, History, Sparkles } from 'lucide-react';
import './ProjectMemory.css';

export const ProjectMemory: React.FC = () => {
  const memoryItems = [
    {
      title: 'Architecture Overview',
      icon: Layers,
      content: 'PocketLedger is built using standard React functional components. State management uses local React hooks with LocalStorage sync for transaction persistence.'
    },
    {
      title: 'Tech Stack & Dependencies',
      icon: Cpu,
      content: 'React 18.3.1, Vite 7.0.0, TypeScript, Lucide React icons, Vanilla CSS custom properties theme tokens.'
    },
    {
      title: 'Design System & Conventions',
      icon: Sparkles,
      content: 'Mobile-first 390px/412px layout grid. Indigo primary accent (#7C5CFF), dark surface backgrounds (#0D0F12), glassmorphic backdrop filters for modal cards.'
    },
    {
      title: 'AI Decision Memory',
      icon: History,
      content: 'AI has previously created initial project scaffold, added glassmorphic variant styling to TransactionList.jsx, and fixed runtime undefined array mapping error.'
    }
  ];

  return (
    <div className="memory-screen-container font-ui">
      <div className="memory-screen-header">
        <div className="memory-title-group">
          <BrainCircuit size={24} className="text-accent" />
          <h1 className="memory-screen-title">PROJECT MEMORY</h1>
        </div>
        <p className="memory-screen-sub font-mono">Architectural knowledge & Context Graph</p>
      </div>

      <div className="memory-cards-list">
        {memoryItems.map((item) => {
          const Icon = item.icon;
          return (
            <Card key={item.title} className="memory-card">
              <div className="memory-card-header font-mono">
                <Icon size={18} className="text-accent" />
                <span className="memory-card-title">{item.title}</span>
              </div>
              <p className="memory-card-body">{item.content}</p>
            </Card>
          );
        })}
      </div>
    </div>
  );
};
