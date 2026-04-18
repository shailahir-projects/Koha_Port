import { createContext } from 'react';

export interface AuthState {
  token: string | null;
  isAuthenticated: boolean;
  signIn: (token: string) => void;
  signOut: () => void;
}

export const AuthContext = createContext<AuthState | null>(null);
