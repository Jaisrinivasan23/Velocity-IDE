import React from 'react';
import { DoctorView } from '../components/doctor/DoctorView';

export const ProjectDoctor: React.FC = () => {
  return (
    <div style={{ height: '100%', overflowY: 'auto' }}>
      <DoctorView />
    </div>
  );
};
