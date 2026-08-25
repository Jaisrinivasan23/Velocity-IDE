import { useProject } from '../context/ProjectContext';

export function useToast() {
  const { toasts, addToast, removeToast } = useProject();
  return { toasts, addToast, removeToast };
}
