const API_BASE = import.meta.env.VITE_API_BASE || '/metadata';

export function apiUrl(path: string) {
  if (path.startsWith('/')) {
    return `${API_BASE}${path}`;
  }
  return `${API_BASE}/${path}`;
}
