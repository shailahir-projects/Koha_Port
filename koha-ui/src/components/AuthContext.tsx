import React, { useState, useCallback } from 'react';
import { clearToken, getToken, setToken } from '../api/client';
import { AuthContext } from './authContext';

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setTokenState] = useState<string | null>(getToken);

  const signIn = useCallback((tok: string) => {
    setToken(tok);
    setTokenState(tok);
  }, []);

  const signOut = useCallback(() => {
    clearToken();
    setTokenState(null);
  }, []);

  return (
    <AuthContext.Provider
      value={{ token, isAuthenticated: !!token, signIn, signOut }}
    >
      {children}
    </AuthContext.Provider>
  );
}


