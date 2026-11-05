import api from './api';

export const authService = {
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    if (response.data.token) {
      localStorage.setItem('ondc_token', response.data.token);
      localStorage.setItem('ondc_seller', JSON.stringify(response.data.seller));
    }
    return response.data;
  },

  register: async (sellerData) => {
    const response = await api.post('/auth/register', sellerData);
    if (response.data.token) {
      localStorage.setItem('ondc_token', response.data.token);
      localStorage.setItem('ondc_seller', JSON.stringify(response.data.seller));
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('ondc_token');
    localStorage.removeItem('ondc_seller');
  },

  getCurrentSeller: () => {
    const sellerStr = localStorage.getItem('ondc_seller');
    return sellerStr ? JSON.parse(sellerStr) : null;
  },

  getToken: () => {
    return localStorage.getItem('ondc_token');
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('ondc_token');
  }
};

