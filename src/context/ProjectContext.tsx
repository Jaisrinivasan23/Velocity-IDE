import React, { createContext, useContext, useState, useCallback } from 'react';
import { ProjectState, DoctorIssue, ExecutionStep, ToolActivity, AIMessage } from '../types';
import { INITIAL_CODE, GLASS_MODIFIED_CODE, FIXED_ERROR_CODE } from '../data/code';
import { INITIAL_FILE_TREE, INITIAL_DOCTOR_ISSUES } from '../data/project';
import { INITIAL_TERMINAL_OUTPUT, MOCK_COMMAND_OUTPUTS } from '../data/terminal';
import { INITIAL_BUILD_STEPS, MOCK_TOOL_ACTIVITIES, INITIAL_AI_CONVERSATION } from '../data/mockAI';

interface ToastMessage {
  id: string;
  type: 'success' | 'info' | 'error' | 'warning';
  text: string;
}

interface ProjectContextType extends ProjectState {
  setSelectedFile: (path: string) => void;
  updateCode: (path: string, newCode: string) => void;
  setInspectedElement: (element: string | null) => void;
  applyGlassmorphicChange: () => Promise<void>;
  simulateError: () => void;
  fixErrorWithAI: () => Promise<void>;
  runTerminalCommand: (command: string) => void;
  fixDoctorIssue: (issueId: string) => void;
  fixAllDoctorIssues: () => Promise<void>;
  startDeployment: () => Promise<void>;
  runAIBuildWorkflow: (promptText?: string) => Promise<void>;
  rollbackAIChanges: () => void;
  resetDemo: () => void;
  toasts: ToastMessage[];
  addToast: (text: string, type?: 'success' | 'info' | 'error' | 'warning') => void;
  removeToast: (id: string) => void;
  activeDeployTarget: string;
  setActiveDeployTarget: (target: string) => void;
}

const ProjectContext = createContext<ProjectContextType | undefined>(undefined);

const createInitialState = (): ProjectState => ({
  projectName: 'PocketLedger',
  framework: 'React · Vite',
  selectedFile: 'src/components/TransactionList.jsx',
  files: INITIAL_FILE_TREE,
  modifiedFiles: ['src/App.jsx', 'src/components/TransactionList.jsx'],
  isGlassmorphic: false,
  codeState: { ...INITIAL_CODE },
  initialCodeState: { ...INITIAL_CODE },
  previewRunning: true,
  inspectedElement: null,
  aiBusy: false,
  buildStatus: 'passed',
  runtimeError: null,
  testsPassed: true,
  projectHealthy: false,
  doctorIssues: INITIAL_DOCTOR_ISSUES.map(i => ({ ...i })),
  deploymentStatus: 'idle',
  deploymentStep: 0,
  deployedUrl: 'pocketledger-forge.app',
  aiMessages: [...INITIAL_AI_CONVERSATION],
  aiBuildPlan: [...INITIAL_BUILD_STEPS],
  aiToolActivities: [...MOCK_TOOL_ACTIVITIES],
  terminalOutput: [...INITIAL_TERMINAL_OUTPUT],
  selectedTerminalTab: 'Terminal',
  activeDeployTarget: 'Vercel',
  rollbackAvailable: false,
});

export const ProjectProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<ProjectState>(createInitialState);
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const addToast = useCallback((text: string, type: 'success' | 'info' | 'error' | 'warning' = 'success') => {
    const id = Math.random().toString(36).substring(2, 9);
    setToasts(prev => [...prev, { id, text, type }]);
    setTimeout(() => {
      removeToast(id);
    }, 3500);
  }, []);

  const removeToast = useCallback((id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  }, []);

  const setSelectedFile = (path: string) => {
    setState(prev => ({ ...prev, selectedFile: path }));
  };

  const updateCode = (path: string, newCode: string) => {
    setState(prev => ({
      ...prev,
      codeState: { ...prev.codeState, [path]: newCode },
      modifiedFiles: prev.modifiedFiles.includes(path) ? prev.modifiedFiles : [...prev.modifiedFiles, path]
    }));
  };

  const setInspectedElement = (element: string | null) => {
    setState(prev => ({ ...prev, inspectedElement: element }));
  };

  const setActiveDeployTarget = (target: string) => {
    setState(prev => ({ ...prev, activeDeployTarget: target }));
  };

  // Hero Flow 1: AI Build Workflow
  const runAIBuildWorkflow = async (promptText?: string) => {
    const userPrompt = promptText || "Build a modern expense tracker with a dashboard, transaction history, categories and local storage.";
    
    setState(prev => ({
      ...prev,
      aiBusy: true,
      aiMessages: [
        ...prev.aiMessages,
        {
          id: Date.now().toString(),
          sender: 'user',
          text: userPrompt,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        },
        {
          id: (Date.now() + 1).toString(),
          sender: 'ai',
          text: "I can build that.\n\nI'll create the project structure, implement the dashboard and transaction flow, add local persistence, then run the application and verify the main flow.",
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }
      ],
      aiBuildPlan: prev.aiBuildPlan.map(s => ({ ...s, status: 'pending' }))
    }));

    // Step through the plan
    const stepsCount = state.aiBuildPlan.length;
    for (let i = 0; i < stepsCount; i++) {
      setState(prev => ({
        ...prev,
        aiBuildPlan: prev.aiBuildPlan.map((s, idx) => {
          if (idx === i) return { ...s, status: 'running' };
          if (idx < i) return { ...s, status: 'complete' };
          return s;
        })
      }));
      await new Promise(r => setTimeout(r, 400));
    }

    setState(prev => ({
      ...prev,
      aiBusy: false,
      aiBuildPlan: prev.aiBuildPlan.map(s => ({ ...s, status: 'complete' })),
      previewRunning: true
    }));
    addToast('App ready! Preview updated.', 'success');
  };

  // Hero Flow 2: Glassmorphic UI -> Source modification
  const applyGlassmorphicChange = async () => {
    setState(prev => ({ ...prev, aiBusy: true }));
    await new Promise(r => setTimeout(r, 800));

    setState(prev => ({
      ...prev,
      aiBusy: false,
      isGlassmorphic: true,
      codeState: {
        ...prev.codeState,
        'src/components/TransactionList.jsx': GLASS_MODIFIED_CODE
      },
      modifiedFiles: prev.modifiedFiles.includes('src/components/TransactionList.jsx')
        ? prev.modifiedFiles
        : [...prev.modifiedFiles, 'src/components/TransactionList.jsx'],
      rollbackAvailable: true
    }));

    addToast('✓ Updated TransactionList.jsx', 'success');
  };

  // Hero Flow 3: Simulate Error
  const simulateError = () => {
    setState(prev => ({
      ...prev,
      buildStatus: 'failed',
      runtimeError: 'TransactionList.jsx:42 - Cannot read properties of undefined',
      testsPassed: false,
      terminalOutput: [
        ...prev.terminalOutput,
        "",
        "🔴 RUNTIME ERROR in src/components/TransactionList.jsx:42",
        "TypeError: Cannot read properties of undefined (reading 'map')",
        "    at TransactionList (TransactionList.jsx:42:25)",
        "    at App (App.jsx:16:9)"
      ]
    }));
    addToast('Runtime Error simulated', 'error');
  };

  // Hero Flow 3: AI Debugger Fix
  const fixErrorWithAI = async () => {
    setState(prev => ({ ...prev, aiBusy: true }));
    await new Promise(r => setTimeout(r, 1200));

    setState(prev => ({
      ...prev,
      aiBusy: false,
      buildStatus: 'passed',
      runtimeError: null,
      testsPassed: true,
      codeState: {
        ...prev.codeState,
        'src/components/TransactionList.jsx': FIXED_ERROR_CODE
      },
      terminalOutput: [
        ...prev.terminalOutput,
        "",
        "✓ AI Fix Applied to TransactionList.jsx",
        "✓ Fast HMR update applied",
        "✓ All runtime checks passed"
      ]
    }));

    addToast('✓ Bug fixed', 'success');
    addToast('✓ Tests passed', 'success');
  };

  // Hero Flow 4: Terminal Commands
  const runTerminalCommand = (command: string) => {
    const outputs = MOCK_COMMAND_OUTPUTS[command] || [`$ ${command}`, "Command executed successfully."];
    setState(prev => {
      let testsPassed = prev.testsPassed;
      let buildStatus = prev.buildStatus;

      if (command === 'npm test') {
        testsPassed = true;
      }
      if (command === 'npm run build' && prev.runtimeError === null) {
        buildStatus = 'passed';
      }

      return {
        ...prev,
        testsPassed,
        buildStatus,
        terminalOutput: [...prev.terminalOutput, "", ...outputs]
      };
    });

    if (command === 'npm test') {
      addToast('✓ All 18 tests passed', 'success');
    } else if (command === 'npm run build') {
      addToast('✓ Build completed successfully', 'success');
    }
  };

  // Hero Flow 5: Project Doctor
  const fixDoctorIssue = (issueId: string) => {
    setState(prev => {
      const updatedIssues = prev.doctorIssues.map(issue =>
        issue.id === issueId ? { ...issue, isFixed: true } : issue
      );
      const allFixed = updatedIssues.every(i => i.isFixed);
      return {
        ...prev,
        doctorIssues: updatedIssues,
        projectHealthy: allFixed
      };
    });
    addToast('✓ Issue resolved', 'success');
  };

  const fixAllDoctorIssues = async () => {
    setState(prev => ({ ...prev, aiBusy: true }));
    await new Promise(r => setTimeout(r, 1000));
    setState(prev => ({
      ...prev,
      aiBusy: false,
      doctorIssues: prev.doctorIssues.map(i => ({ ...i, isFixed: true })),
      projectHealthy: true
    }));
    addToast('✓ All 3 issues resolved', 'success');
  };

  // Hero Flow 7: Deploy Simulation
  const startDeployment = async () => {
    setState(prev => ({ ...prev, deploymentStatus: 'deploying', deploymentStep: 1 }));
    
    // Step 1: Preparing build (500ms)
    await new Promise(r => setTimeout(r, 500));
    setState(prev => ({ ...prev, deploymentStep: 2 }));

    // Step 2: Running tests (600ms)
    await new Promise(r => setTimeout(r, 600));
    setState(prev => ({ ...prev, deploymentStep: 3 }));

    // Step 3: Creating artifact (500ms)
    await new Promise(r => setTimeout(r, 500));
    setState(prev => ({ ...prev, deploymentStep: 4 }));

    // Step 4: Uploading (600ms)
    await new Promise(r => setTimeout(r, 600));
    setState(prev => ({ ...prev, deploymentStep: 5 }));

    // Step 5: Health check (500ms)
    await new Promise(r => setTimeout(r, 500));
    setState(prev => ({
      ...prev,
      deploymentStatus: 'success',
      deploymentStep: 5,
      deployed: true
    }));
    addToast('✓ Deployment successful!', 'success');
  };

  // Rollback AI changes
  const rollbackAIChanges = () => {
    setState(prev => ({
      ...prev,
      isGlassmorphic: false,
      codeState: {
        ...prev.codeState,
        'src/components/TransactionList.jsx': INITIAL_CODE['src/components/TransactionList.jsx']
      },
      rollbackAvailable: false
    }));
    addToast('↶ AI changes rolled back', 'info');
  };

  // Reset Demo to initial pristine state
  const resetDemo = () => {
    setState(createInitialState());
    addToast('Demo state reset', 'info');
  };

  return (
    <ProjectContext.Provider
      value={{
        ...state,
        setSelectedFile,
        updateCode,
        setInspectedElement,
        applyGlassmorphicChange,
        simulateError,
        fixErrorWithAI,
        runTerminalCommand,
        fixDoctorIssue,
        fixAllDoctorIssues,
        startDeployment,
        runAIBuildWorkflow,
        rollbackAIChanges,
        resetDemo,
        toasts,
        addToast,
        removeToast,
        setActiveDeployTarget
      }}
    >
      {children}
    </ProjectContext.Provider>
  );
};

export const useProject = () => {
  const context = useContext(ProjectContext);
  if (!context) {
    throw new Error('useProject must be used within a ProjectProvider');
  }
  return context;
};
