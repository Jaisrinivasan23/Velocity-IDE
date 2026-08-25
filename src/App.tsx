import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ProjectProvider } from './context/ProjectContext';
import { TopBar } from './components/layout/TopBar';
import { BottomNav } from './components/layout/BottomNav';
import { ToastContainer } from './components/common/Toast';

import { Home } from './screens/Home';
import { Workspace } from './screens/Workspace';
import { AIAgent } from './screens/AIAgent';
import { Preview } from './screens/Preview';
import { TerminalScreen } from './screens/TerminalScreen';
import { Deploy } from './screens/Deploy';
import { ProjectDoctor } from './screens/ProjectDoctor';
import { ProjectMemory } from './screens/ProjectMemory';

export const App: React.FC = () => {
  return (
    <ProjectProvider>
      <BrowserRouter>
        <div className="v-mobile-shell">
          <TopBar />
          
          <main className="v-screen-viewport">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/workspace" element={<Workspace />} />
              <Route path="/ai" element={<AIAgent />} />
              <Route path="/preview" element={<Preview />} />
              <Route path="/terminal" element={<TerminalScreen />} />
              <Route path="/deploy" element={<Deploy />} />
              <Route path="/doctor" element={<ProjectDoctor />} />
              <Route path="/memory" element={<ProjectMemory />} />
            </Routes>
          </main>

          <BottomNav />
          <ToastContainer />
        </div>
      </BrowserRouter>
    </ProjectProvider>
  );
};

export default App;
