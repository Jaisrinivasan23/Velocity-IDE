# VELOCITY-IDE — REAL AUTONOMOUS AI CODING AGENT

## STRICT IMPLEMENTATION PROMPT

You are implementing the **real AI coding agent** inside the native Kotlin Android application **Velocity-IDE**.

The existing application is being developed as a real Android IDE.

The objective is NOT to create a chatbot.

The objective is to build an **Antigravity-style autonomous software engineering agent for mobile**, where the AI can inspect a project, reason about it, dynamically select tools, create directories, create files, edit files, search code, execute commands through the runtime layer, inspect output, diagnose errors, retry, and verify its work.

The user must be able to configure the AI provider from:

```text
Settings
 → AI
```

Supported providers:

```text
Gemini
Claude
OpenAI
OpenRouter
Local LLM
```

The selected provider must actually power the agent.

---

# 1. ABSOLUTE RULES

These rules are mandatory.

## DO NOT BUILD

* fake AI responses
* hardcoded conversations
* mock tool execution
* simulated file creation
* fake terminal output
* fake build success
* fake agent progress
* hardcoded project context
* predetermined tool sequences
* provider-specific logic scattered throughout the application

## BUILD

A real:

```text
LLM
 ↓
Agent
 ↓
Tool Selection
 ↓
Tool Execution
 ↓
Observation
 ↓
LLM
 ↓
Next Action
 ↓
Tool Execution
 ↓
Verification
```

The agent must decide what tool to use based on the actual project state.

---

# 2. CORE PRODUCT BEHAVIOR

The user should be able to type:

```text
Build a complete expense tracker application.
```

The agent should autonomously:

```text
1. Inspect project
2. Understand project structure
3. Determine framework
4. Inspect relevant files
5. Create a plan
6. Create directories
7. Create files
8. Edit existing files
9. Install dependencies if required
10. Run the project
11. Read terminal output
12. Detect errors
13. Inspect relevant source
14. Fix errors
15. Run again
16. Verify
17. Report completion
```

The exact sequence MUST NOT be hardcoded.

The LLM chooses the sequence.

---

# 3. ARCHITECTURE

Implement:

```text
AI Layer
│
├── LlmProvider
│
├── ProviderRegistry
│
├── AgentEngine
│
├── AgentSession
│
├── AgentState
│
├── AgentPlanner
│
├── ToolRegistry
│
├── ToolExecutor
│
├── ContextEngine
│
├── ProjectMemory
│
├── ConversationStore
│
├── DiffEngine
│
├── CheckpointManager
│
└── ExecutionHistory
```

Keep these independent.

---

# 4. LLM PROVIDER ABSTRACTION

Create:

```kotlin
interface LlmProvider {

    suspend fun stream(
        request: LlmRequest
    ): Flow<LlmEvent>

    suspend fun complete(
        request: LlmRequest
    ): LlmResponse
}
```

Create provider implementations:

```text
GeminiProvider
OpenAIProvider
AnthropicProvider
OpenRouterProvider
LocalLlmProvider
```

Do not expose provider-specific APIs to the AgentEngine.

The agent must communicate only through `LlmProvider`.

---

# 5. PROVIDER REGISTRY

Create:

```text
ProviderRegistry
```

Responsibilities:

```text
register provider
get provider
remove provider
test provider
list providers
```

Configuration:

```text
provider
model
endpoint
API key
enabled
```

Allow multiple configured providers.

Example:

```text
Gemini
 └── gemini-2.x model

OpenAI
 └── selected model

Claude
 └── selected model

OpenRouter
 └── selected model

Local
 └── selected local model
```

---

# 6. SETTINGS UI

Create:

```text
Settings
 → AI
```

Sections:

## Provider

```text
○ Gemini
○ Claude
○ OpenAI
○ OpenRouter
○ Local LLM
```

## Model

Dynamic model field/list.

## API Key

Secure input.

## Endpoint

Only display when applicable.

## Agent permissions

```text
Ask before editing files
Ask before running commands
Ask before installing packages
Ask before deleting files
Ask before deployment
```

## Agent mode

```text
Ask
Edit
Agent
```

---

# 7. SECURE API KEY STORAGE

Never store API keys in:

```text
SharedPreferences plaintext
Room plaintext
source code
BuildConfig
Git
project files
logs
```

Use Android secure storage backed by:

```text
Android Keystore
```

or a properly encrypted credential storage implementation.

API keys must never appear in terminal output or AI prompts.

---

# 8. AGENT ENGINE

Create:

```text
AgentEngine
```

Responsibilities:

```text
startSession()
processUserMessage()
executeAgentLoop()
cancel()
pause()
resume()
```

The agent loop must be dynamic.

Pseudo architecture:

```text
User Message
      ↓
Build Context
      ↓
LLM
      ↓
Response
      ↓
Tool Call?
   /       \
 No         Yes
 │           │
Answer    Execute Tool
             ↓
          Tool Result
             ↓
        Update Context
             ↓
             LLM
             ↓
           repeat
```

Continue until:

```text
completed
waiting for user
cancelled
fatal error
```

---

# 9. DO NOT HARDCODE TOOL ORDER

This is extremely important.

Do NOT implement:

```text
always list_files
then read_file
then create_file
then run_command
```

The model must decide.

For example, if the user asks:

```text
Fix the error in LoginScreen.kt
```

the agent might:

```text
read_file(LoginScreen.kt)
 ↓
read_file(ViewModel)
 ↓
run tests
 ↓
fix file
 ↓
run tests
```

For:

```text
Add dark mode
```

it might:

```text
search_files(theme)
 ↓
read theme files
 ↓
edit theme
 ↓
run build
```

The sequence must emerge from model reasoning.

---

# 10. TOOL REGISTRY

Create:

```text
ToolRegistry
```

Tools must expose machine-readable schemas.

Example:

```kotlin
interface AgentTool {

    val name: String
    val description: String
    val inputSchema: JsonObject

    suspend fun execute(
        input: JsonObject,
        context: ToolContext
    ): ToolResult
}
```

The LLM receives these tool definitions.

---

# 11. CORE FILE TOOLS

Implement real tools.

## list_files

```text
list_files(path)
```

Returns:

```text
directories
files
sizes
```

Support recursive listing with limits.

---

## read_file

```text
read_file(
    path,
    startLine?,
    endLine?
)
```

Do NOT automatically read giant files completely.

Support ranges.

---

## create_file

```text
create_file(
    path,
    content
)
```

Actually create the file.

---

## create_directory

```text
create_directory(path)
```

Actually create the directory.

---

## write_file

```text
write_file(
    path,
    content
)
```

---

## edit_file

Implement structured edits.

Prefer:

```text
patch
```

over blind complete-file replacement.

---

## delete_file

Require confirmation according to user settings.

---

## rename_file

Actually rename.

---

## move_file

Actually move.

---

# 12. SEARCH TOOLS

The agent needs powerful repository search.

Implement:

```text
search_files
grep
find_files
```

Example:

```text
search_files(
    query="useState",
    path="src/"
)
```

Return:

```text
file
line
column
matching text
```

Implement efficient search.

Do not load every file into memory unnecessarily.

If an appropriate local search engine is used, make it cancellable and bounded.

---

# 13. SYMBOL / STRUCTURE INSPECTION

Where technically possible, expose:

```text
find_symbol
find_references
```

The purpose is to allow the agent to answer:

```text
Where is LoginScreen used?
```

without reading the entire project.

Architecture should allow language-specific indexers later.

---

# 14. PROJECT INSPECTION TOOL

Create:

```text
inspect_project
```

It should return useful metadata:

```text
project name
project type
framework
language
package manager
entry point
build system
important configuration files
runtime availability
```

Example:

```text
React
Vite
TypeScript
npm
src/main.tsx
package.json
```

---

# 15. PROJECT CONTEXT ENGINE

Create:

```text
ProjectContextEngine
```

It should build context dynamically.

Context sources:

```text
current project
file tree
current file
selected code
relevant files
search results
recent changes
terminal output
diagnostics
project memory
conversation history
runtime information
```

Do NOT send the entire repository to the model every time.

Use relevance-based context.

---

# 16. CONTEXT BUDGET

Implement limits.

For example:

```text
max files
max bytes
max tokens
max terminal output
max search results
```

If context exceeds limits:

```text
summarize
truncate
prioritize
```

Do not crash because a project is large.

---

# 17. DYNAMIC TOOL DISCOVERY

The agent should have a core tool set.

But architecture must support dynamic tools.

Create:

```text
ToolRegistry
```

with:

```text
register()
unregister()
enable()
disable()
getAvailableTools()
```

Future tools can be added by runtime/plugins.

Example:

```text
Core
 ├── filesystem
 ├── search
 ├── terminal

Runtime
 ├── npm
 ├── pip
 ├── python
 └── node

Development
 ├── build
 ├── test
 └── diagnostics

Git
 ├── status
 ├── diff
 └── commit
```

The model receives only tools currently available.

---

# 18. TERMINAL TOOL

Create:

```text
run_command
```

Input:

```text
command
workingDirectory
timeout
```

The tool MUST use the application's real:

```text
ProcessManager
```

and:

```text
RuntimeManager
```

It must NOT fake output.

Return:

```text
stdout
stderr
exitCode
duration
processId
```

---

# 19. PROCESS OBSERVATION

Create:

```text
get_process_output
stop_process
```

The agent should be able to start a long-running process and later inspect it.

Example:

```text
npm run dev
```

returns:

```text
processId = 123
```

Then:

```text
get_process_output(123)
```

returns new output.

---

# 20. REAL-TIME STREAMING

AI responses should stream into the UI.

Tool execution should also stream status.

Example:

```text
AI
Inspecting project...

Tool
list_files(src)

Result
12 files found

AI
I found the main application entry point.

Tool
read_file(src/App.tsx)
```

Do not wait until the entire agent run completes before displaying progress.

---

# 21. TOOL EXECUTION EVENTS

Create a unified event stream.

Example:

```kotlin
sealed class AgentEvent {

    data class Text(...)

    data class ToolStarted(...)

    data class ToolOutput(...)

    data class ToolCompleted(...)

    data class FileChanged(...)

    data class ProcessStarted(...)

    data class ProcessOutput(...)

    data class Error(...)

    data class Completed(...)
}
```

The UI observes this stream.

---

# 22. AGENT STATE

Implement:

```text
IDLE
PLANNING
INSPECTING
EXECUTING_TOOL
WAITING_FOR_TOOL
EDITING
RUNNING
TESTING
DEBUGGING
WAITING_FOR_APPROVAL
COMPLETED
FAILED
CANCELLED
```

The state must represent actual execution.

---

# 23. AGENT UI

The AI screen must look like an engineering agent, not ChatGPT clone.

Display:

```text
User request

Agent plan

Current action

Tool execution

Files changed

Terminal output

Errors

Diff

Final result
```

Example:

```text
BUILD FEATURE

✓ Inspect project
✓ Identify architecture
✓ Create components
● Running tests
○ Verify preview

TOOLS

read_file
create_file
edit_file
run_command

FILES

+ src/components/Login.tsx
M src/App.tsx
```

---

# 24. LIVE FILE TREE

When AI creates:

```text
src/features/auth/
```

the UI must immediately show it.

When AI creates:

```text
LoginScreen.tsx
```

the file must appear in the explorer.

No refresh button should be required.

Emit filesystem events:

```text
FileCreated
FileModified
FileDeleted
DirectoryCreated
Renamed
Moved
```

and update UI state.

---

# 25. OPEN FILE AUTOMATICALLY

When the AI creates or modifies an important file:

allow the UI to display:

```text
Open
Review
```

If user opens it, editor displays the actual filesystem content.

---

# 26. DIFF ENGINE

Every AI modification should produce:

```text
before
after
diff
```

Show:

```text
Added
Removed
Modified
```

Allow:

```text
Apply
Reject
Undo
```

---

# 27. CHECKPOINT SYSTEM

Before significant autonomous modifications:

```text
create_checkpoint
```

Store enough information to restore the project state.

If agent fails:

```text
Rollback
```

must actually restore the project.

---

# 28. AUTONOMOUS DEBUGGING

The agent must be able to fix its own mistakes.

Example:

```text
AI edits file
 ↓
run_command
 ↓
exitCode = 1
 ↓
read stderr
 ↓
identify source
 ↓
read relevant file
 ↓
edit file
 ↓
run again
```

Do NOT hardcode specific errors.

The LLM must interpret actual errors.

---

# 29. RETRY POLICY

Allow the agent to retry.

But enforce:

```text
maximum iterations
maximum runtime
maximum tool calls
```

Example configurable defaults:

```text
maxIterations = 30
maxToolCalls = 100
commandTimeout = 120 seconds
```

Do not allow infinite loops.

---

# 30. COMMAND APPROVAL

Before executing a potentially dangerous command, evaluate it.

Examples:

```text
rm
rm -rf
git reset
git clean
delete project
deployment
```

Require user confirmation.

Safe commands can optionally run automatically.

Allow the user to configure the policy.

---

# 31. FILE APPROVAL

Settings:

```text
Always ask
Ask for destructive changes only
Auto-approve workspace edits
```

The agent must respect the setting.

---

# 32. AI CHAT HISTORY

Persist:

```text
Project
 → Chat
    → Messages
       → Tool calls
       → Tool results
       → File changes
       → Agent runs
```

A previous conversation must remain available after application restart.

---

# 33. AGENT RUN HISTORY

Persist each autonomous task.

Example:

```text
Agent Run #42

Request:
Add transaction filtering

Started:
14:32

Files changed:
5

Commands:
npm install
npm test

Result:
Completed

Duration:
2m 41s
```

---

# 34. PROJECT MEMORY

Implement:

```text
ProjectMemoryService
```

Memory should contain useful durable facts:

```text
framework
architecture
important files
commands
conventions
dependencies
design decisions
known issues
```

The AI may update memory.

Do not blindly save entire conversations as memory.

---

# 35. MEMORY TOOL

Expose:

```text
get_project_memory
update_project_memory
```

Only store durable information.

---

# 36. AI EDIT MODES

Implement three modes.

## ASK

AI only explains.

No tools that modify files.

---

## EDIT

AI can inspect and modify files.

Command execution according to permission settings.

---

## AGENT

Full autonomous workflow:

```text
inspect
plan
edit
run
test
debug
verify
```

---

# 37. MODEL-SPECIFIC ADAPTERS

Gemini must use the correct native API/tool/function-calling mechanism for the selected Gemini model.

OpenAI must use its supported tool/function mechanism.

Claude must use its supported tool mechanism.

OpenRouter should support models exposed through its API.

Do not force all providers through incompatible request formats.

Normalize provider events internally.

---

# 38. STREAMING NORMALIZATION

Convert provider-specific responses into:

```text
LlmEvent.TextDelta
LlmEvent.ToolCall
LlmEvent.ToolCallResult
LlmEvent.Completed
LlmEvent.Error
```

AgentEngine should remain provider-independent.

---

# 39. LOCAL LLM

Implement the abstraction now:

```text
LocalLlmProvider
```

but do not claim local support until an actual Android-compatible local inference engine is integrated.

Possible future implementation:

```text
GGUF
llama.cpp
MediaPipe-compatible models
other Android inference runtime
```

The Settings UI must accurately display:

```text
Local LLM
Not configured
```

until it is genuinely usable.

---

# 40. NETWORK FAILURE

If AI provider fails:

```text
network error
timeout
rate limit
invalid API key
model unavailable
```

show an actionable error.

Do not lose the conversation.

Allow:

```text
Retry
Change provider
Change model
Cancel
```

---

# 41. PROVIDER FALLBACK

Architect for optional fallback.

Example:

```text
Primary:
Gemini

Fallback:
OpenRouter
```

Do not automatically switch providers unless the user enables fallback.

---

# 42. COST CONTROL

Track approximate:

```text
input tokens
output tokens
tool calls
```

where provider APIs expose them.

Display:

```text
Agent run
Tokens
Duration
Tool calls
```

This helps prevent accidental runaway agents.

---

# 43. LOGGING

Create structured agent logs.

Never log:

```text
API keys
passwords
credentials
authorization headers
```

Log:

```text
agent state
tool name
duration
success/failure
error type
```

---

# 44. CANCELLATION

The user must be able to stop an agent.

Example:

```text
[Stop Agent]
```

Cancellation must propagate:

```text
UI
 ↓
AgentEngine
 ↓
LLM request
 ↓
Tool execution
 ↓
Process
```

Kill running processes where appropriate.

---

# 45. PAUSE / RESUME

Architecture should support:

```text
Pause
Resume
```

For example:

```text
AI waiting for permission
```

then:

```text
Approve
```

and execution continues.

---

# 46. REAL-TIME UI SYNCHRONIZATION

The following must update immediately:

```text
file explorer
editor
terminal
AI chat
agent state
preview
diagnostics
```

Use:

```text
StateFlow
SharedFlow
Coroutines
```

where appropriate.

---

# 47. PREVIEW INTEGRATION

After the agent runs a development server:

```text
ProcessManager
 ↓
detect port
 ↓
PreviewService
 ↓
WebView
```

The AI should receive preview status:

```text
server running
port
URL
```

The preview should use the actual running project.

---

# 48. ERROR → AI PIPELINE

Connect:

```text
Runtime
 ↓
stderr
 ↓
Diagnostics
 ↓
AgentContext
 ↓
LLM
```

The AI should be able to see actual errors without the user manually copying them.

---

# 49. NATURAL LANGUAGE DEVELOPMENT

The user should be able to say:

```text
Make the dashboard responsive.
```

or:

```text
Find why the app crashes.
```

or:

```text
Add authentication.
```

or:

```text
Create a new settings page.
```

The agent determines the required operations.

---

# 50. NATURAL LANGUAGE TERMINAL

The user should be able to ask:

```text
Install the dependencies required for this project.
```

The AI determines the correct command.

For example:

```text
npm install
```

or:

```text
pip install -r requirements.txt
```

The command must be executed through RuntimeManager.

---

# 51. TOOL RESULT FORMAT

Every tool must return structured data.

Example:

```json
{
  "success": true,
  "tool": "read_file",
  "data": {
    "path": "src/App.tsx",
    "content": "..."
  }
}
```

Errors:

```json
{
  "success": false,
  "tool": "run_command",
  "error": {
    "type": "PROCESS_FAILED",
    "message": "...",
    "exitCode": 1
  }
}
```

Never force the LLM to parse arbitrary UI strings.

---

# 52. TOOL OUTPUT LIMITS

Prevent huge outputs.

For:

```text
grep
terminal
file reads
```

implement:

```text
max bytes
max lines
pagination
truncation metadata
```

Example:

```text
Output truncated.
Showing 500 of 18,421 lines.
```

The AI can request additional ranges.

---

# 53. TOOL PERMISSIONS

Every tool declares:

```text
read-only
write
execute
destructive
network
```

Example:

```text
read_file
READ

create_file
WRITE

run_command
EXECUTE

delete_file
DESTRUCTIVE
```

The permission system uses this metadata.

---

# 54. TOOL AUDIT LOG

Every tool execution records:

```text
timestamp
agentRun
tool
arguments
result
duration
approval
```

Do not expose secrets in logs.

---

# 55. NO HIDDEN MODIFICATIONS

Every file changed by AI must be traceable.

The UI should be able to answer:

```text
Which AI run changed this file?
```

---

# 56. MULTIPLE PROJECTS

AI sessions must be isolated per project.

Never allow:

```text
Project A AI
```

to accidentally edit:

```text
Project B
```

Agent context must contain:

```text
projectId
projectRoot
```

for every operation.

---

# 57. PROJECT LOCK

While an agent performs a large operation, coordinate concurrent edits.

Prevent:

```text
user edits file
+
AI overwrites same file
```

without resolution.

If conflict occurs:

```text
detect
notify
merge/review
```

---

# 58. USER EDIT DURING AGENT RUN

If the user modifies a file while the agent is working:

```text
Agent must re-read the file before applying its next modification.
```

Never blindly overwrite newer user changes.

---

# 59. AGENT CONTEXT AFTER FILE CHANGE

After every write:

```text
update project state
invalidate relevant cached context
```

The next model call must know that the file changed.

---

# 60. VERIFICATION

The agent should not say:

```text
Done.
```

just because it wrote files.

Completion should ideally require:

```text
build/test/run verification
```

depending on project type.

Example:

```text
Implementation
 ↓
Build
 ↓
Test
 ↓
Verify
```

If verification is impossible, explicitly state:

```text
Implemented but not verified.
```

---

# 61. FINAL AI RESPONSE

At completion show:

```text
Completed

What changed:
...

Files:
+ ...
M ...

Commands:
...

Verification:
✓ Build passed
✓ Tests passed

Preview:
Running
```

If failure:

```text
Implementation incomplete

Reason:
...

Failed step:
...

Suggested next action:
...
```

Never claim success falsely.

---

# 62. DEMO SCENARIO

The implementation must support this real demonstration.

Create:

```text
ExpenseTracker
```

Then tell AI:

```text
Build a polished expense tracker with:
- dashboard
- transaction list
- categories
- monthly totals
- local persistence
- responsive mobile UI
```

Expected real behavior:

```text
AI
 ↓
inspect_project
 ↓
list_files
 ↓
read relevant files
 ↓
plan
 ↓
create directories
 ↓
create files
 ↓
edit files
 ↓
install dependencies if required
 ↓
run project
 ↓
read output
 ↓
fix errors if any
 ↓
run again
 ↓
preview
 ↓
verify
```

The exact tool sequence must be generated dynamically.

---

# 63. SECOND DEMO — SELF REPAIR

Introduce an intentional bug.

Tell AI:

```text
The application is crashing. Find and fix the problem.
```

AI should:

```text
inspect output
 ↓
identify error
 ↓
search source
 ↓
read relevant code
 ↓
edit
 ↓
run
 ↓
verify
```

Again, no hardcoded error-specific behavior.

---

# 64. THIRD DEMO — NATURAL LANGUAGE EDIT

User:

```text
Make the transaction cards glassmorphic and add swipe-to-delete.
```

AI:

```text
search
 ↓
identify component
 ↓
read
 ↓
edit
 ↓
run
 ↓
preview
```

---

# 65. IMPLEMENTATION ORDER

Implement the real system in this order:

```text
1. Provider abstraction
2. Secure AI settings
3. LLM provider implementations
4. Tool abstraction
5. Tool registry
6. Filesystem tools
7. Search tools
8. Runtime/terminal tools
9. Agent context engine
10. Agent engine
11. Streaming event system
12. Diff engine
13. Checkpoints
14. Chat persistence
15. Project memory
16. Permission system
17. Agent UI
18. Runtime integration
19. Preview integration
20. Diagnostics integration
21. Git tools
22. Testing
```

---

# 66. IMPLEMENTATION QUALITY

Use:

```text
Kotlin
Coroutines
Flow
StateFlow
sealed classes
dependency injection
structured concurrency
suspend functions
```

Avoid:

```text
GlobalScope
blocking main thread
singletons everywhere
hardcoded API keys
hardcoded commands
hardcoded project paths
hardcoded AI responses
```

---

# 67. TESTS

Write tests for:

### Provider

```text
provider selection
authentication
streaming
errors
```

### Tools

```text
create file
read file
edit file
delete file
search
```

### Agent

```text
tool selection
tool execution
loop termination
cancellation
retry
```

### Security

```text
path traversal
project isolation
destructive command approval
credential protection
```

### Persistence

```text
chat history
agent runs
memory
checkpoints
```

---

# 68. BUILD REQUIREMENT

The Android application must continue to build:

```bash
./gradlew assembleDebug
```

and eventually:

```bash
./gradlew assembleRelease
```

Do not break the existing APK build while implementing the agent.

---

# 69. DO NOT IMPLEMENT FAKE FALLBACKS

If Gemini API is unavailable:

DO NOT return:

```text
"Gemini response simulated"
```

Instead return:

```text
Gemini unavailable:
<actual reason>
```

If Node runtime is unavailable:

```text
Node runtime unavailable.
```

If Python runtime is unavailable:

```text
Python runtime unavailable.
```

The UI must accurately represent capability state.

---

# 70. FINAL ACCEPTANCE TEST

The implementation is accepted only if this works:

```text
Velocity-IDE
     ↓
Settings
     ↓
AI
     ↓
Select Gemini
     ↓
Enter API key
     ↓
Test connection
     ↓
Success
     ↓
Create project
     ↓
Open AI
     ↓
Agent mode
     ↓
User gives development task
     ↓
Gemini streams response
     ↓
Agent selects tools
     ↓
Tools execute against REAL project
     ↓
Folders/files are actually created
     ↓
Files actually edited
     ↓
Terminal commands actually execute
     ↓
Output returns to agent
     ↓
Agent observes result
     ↓
Agent fixes errors if necessary
     ↓
Project runs
     ↓
Live preview updates
     ↓
AI reports verified result
```

---

# 71. MOST IMPORTANT PRINCIPLE

Velocity-IDE is not:

```text
ChatGPT inside an editor.
```

It is:

```text
AI
+
Agent
+
Tools
+
Filesystem
+
Runtime
+
Terminal
+
Editor
+
Preview
+
Memory
+
Verification
```

The AI is the **operator of the development environment**.

The Android application provides the controlled environment in which the AI operates.

Therefore the correct architecture is:

```text
             ┌────────────────────┐
             │       GEMINI       │
             │   / Claude / etc.  │
             └─────────┬──────────┘
                       │
                 tool calls
                       │
                       ▼
             ┌────────────────────┐
             │    AGENT ENGINE    │
             └─────────┬──────────┘
                       │
          ┌────────────┼─────────────┐
          ▼            ▼             ▼
     FILESYSTEM      SEARCH       RUNTIME
          │            │             │
          ▼            ▼             ▼
       PROJECT       SOURCE       PROCESSES
                                    │
                                    ▼
                                TERMINAL
                                    │
                                    ▼
                                 PREVIEW
                                    │
                                    ▼
                                OBSERVE
                                    │
                                    └──────→ GEMINI
```

This feedback loop is the heart of Velocity-IDE.

---

# 72. BEFORE CODING

First inspect the current Kotlin project.

Identify:

```text
existing package structure
existing screens
existing navigation
existing ViewModels
existing repositories
existing dependencies
existing filesystem code
existing terminal code
existing WebView/editor code
existing settings
```

Then map the above architecture onto the existing codebase.

Do not rewrite working code unnecessarily.

Do not create duplicate architecture.

Reuse existing components where appropriate.

---

# 73. AFTER IMPLEMENTATION

Return:

```text
AI AGENT IMPLEMENTATION REPORT

Provider system:
...

Gemini:
...

Claude:
...

OpenAI:
...

OpenRouter:
...

Local LLM:
...

Tool system:
...

Filesystem:
...

Search:
...

Terminal:
...

Runtime:
...

Agent loop:
...

Context engine:
...

Memory:
...

Chat history:
...

Diff:
...

Checkpoint:
...

Permissions:
...

Preview:
...

Tests:
...

APK build:
...

Known limitations:
...
```

Every claimed feature must correspond to working implementation.

# START NOW

First inspect the current Kotlin Android repository.

Then implement this architecture against the actual codebase.

Do not create a fake AI.

Build the real agent.
