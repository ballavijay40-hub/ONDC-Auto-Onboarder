import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';
import { sellerService } from '../services/sellerService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [seller, setSeller] = useState(authService.getCurrentSeller());
  const [token, setToken] = useState(authService.getToken());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = authService.getToken();
      if (storedToken) {
        try {
          const profile = await sellerService.getProfile();
          setSeller(profile);
        } catch (err) {
          console.error("Failed to fetch profile with stored token", err);
          authService.logout();
          setSeller(null);
          setToken(null);
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (email, password) => {
    const data = await authService.login(email, password);
    setToken(data.token);
    setSeller(data.seller);
    return data;
  };

  const register = async (sellerData) => {
    const data = await authService.register(sellerData);
    setToken(data.token);
    setSeller(data.seller);
    return data;
  };

  const logout = () => {
    authService.logout();
    setSeller(null);
    setToken(null);
  };

  const refreshSeller = async () => {
    try {
      const updated = await sellerService.getProfile();
      setSeller(updated);
      return updated;
    } catch (e) {
      console.error("Failed to refresh seller profile", e);
    }
  };

  return (
    <AuthContext.Provider value={{
      seller,
      token,
      isAuthenticated: !!token,
      loading,
      login,
      register,
      logout,
      refreshSeller
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

