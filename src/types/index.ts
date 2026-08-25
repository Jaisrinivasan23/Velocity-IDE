export type BuildStatus = "passed" | "building" | "failed";

export type ProjectFile = {
  name: string;
  path: string;
  type: "file" | "folder";
  content?: string;
  children?: ProjectFile[];
  isModified?: boolean;
};

export type AIMessage = {
  id: string;
  sender: "user" | "ai" | "system";
  text: string;
  timestamp: string;
  toolActivity?: ToolActivity[];
  executionPlan?: ExecutionStep[];
};

export type ExecutionStepStatus = "pending" | "running" | "complete";

export type ExecutionStep = {
  id: string;
  label: string;
  status: ExecutionStepStatus;
};

export type ToolActivityStatus = "running" | "complete";

export type ToolActivity = {
  id: string;
  name: string;
  detail: string;
  status: ToolActivityStatus;
};

export type DoctorIssue = {
  id: string;
  title: string;
  severity: "High" | "Medium" | "Low";
  description: string;
  impact: string;
  isFixed: boolean;
};

export type TerminalTab = "Terminal" | "Problems" | "Output";

export type ProjectState = {
  projectName: string;
  framework: string;
  selectedFile: string;
  files: ProjectFile[];
  modifiedFiles: string[];
  isGlassmorphic: boolean;
  codeState: Record<string, string>;
  previewRunning: boolean;
  inspectedElement: string | null;
  aiBusy: boolean;
  buildStatus: BuildStatus;
  runtimeError: string | null;
  testsPassed: boolean;
  projectHealthy: boolean;
  doctorIssues: DoctorIssue[];
  deploymentStatus: "idle" | "deploying" | "success";
  deploymentStep: number;
  deployedUrl: string;
  aiMessages: AIMessage[];
  aiBuildPlan: ExecutionStep[];
  aiToolActivities: ToolActivity[];
  terminalOutput: string[];
  selectedTerminalTab: TerminalTab;
  activeDeployTarget: string;
  rollbackAvailable: boolean;
  initialCodeState: Record<string, string>;
};
