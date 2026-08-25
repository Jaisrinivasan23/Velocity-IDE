import React, { useState } from 'react';
import { ProjectFile } from '../../types';
import { useProject } from '../../context/ProjectContext';
import { Folder, FolderOpen, FileCode, FileText, ChevronRight, ChevronDown } from 'lucide-react';
import './FileTree.css';

interface FileTreeProps {
  files: ProjectFile[];
  onSelectFile: (path: string) => void;
}

export const FileTree: React.FC<FileTreeProps> = ({ files, onSelectFile }) => {
  return (
    <div className="v-file-tree font-mono">
      <div className="v-tree-header">
        <span className="project-root-name">PocketLedger</span>
        <span className="project-type-tag font-ui">React · Vite</span>
      </div>
      <div className="v-tree-node-list">
        {files.map(file => (
          <FileTreeNode key={file.path} item={file} onSelectFile={onSelectFile} depth={0} />
        ))}
      </div>
    </div>
  );
};

const FileTreeNode: React.FC<{
  item: ProjectFile;
  onSelectFile: (path: string) => void;
  depth: number;
}> = ({ item, onSelectFile, depth }) => {
  const { selectedFile, modifiedFiles } = useProject();
  const [isOpen, setIsOpen] = useState(true);

  const isSelected = selectedFile === item.path;
  const isModified = modifiedFiles.includes(item.path);

  if (item.type === 'folder') {
    return (
      <div className="v-tree-folder-group">
        <button
          className="v-tree-node v-tree-folder"
          style={{ paddingLeft: `${depth * 14 + 12}px` }}
          onClick={() => setIsOpen(!isOpen)}
        >
          <span className="v-tree-chevron">
            {isOpen ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
          </span>
          <span className="v-tree-icon text-accent">
            {isOpen ? <FolderOpen size={16} /> : <Folder size={16} />}
          </span>
          <span className="v-tree-name">{item.name}</span>
        </button>

        {isOpen && item.children && (
          <div className="v-tree-children">
            {item.children.map(child => (
              <FileTreeNode
                key={child.path}
                item={child}
                onSelectFile={onSelectFile}
                depth={depth + 1}
              />
            ))}
          </div>
        )}
      </div>
    );
  }

  const getFileIcon = (name: string) => {
    if (name.endsWith('.jsx') || name.endsWith('.js') || name.endsWith('.json')) {
      return <FileCode size={16} className="file-icon-code" />;
    }
    return <FileText size={16} className="file-icon-txt" />;
  };

  return (
    <button
      className={`v-tree-node v-tree-file ${isSelected ? 'selected' : ''}`}
      style={{ paddingLeft: `${depth * 14 + 28}px` }}
      onClick={() => onSelectFile(item.path)}
    >
      <span className="v-tree-icon">{getFileIcon(item.name)}</span>
      <span className="v-tree-name">{item.name}</span>
      {isModified && <span className="v-tree-modified-dot" title="Modified" />}
    </button>
  );
};
