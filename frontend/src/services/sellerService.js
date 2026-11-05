import api from './api';

export const sellerService = {
  getProfile: async () => {
    const response = await api.get('/seller/profile');
    localStorage.setItem('ondc_seller', JSON.stringify(response.data));
    return response.data;
  },

  updateProfile: async (data) => {
    const response = await api.post('/seller', data);
    localStorage.setItem('ondc_seller', JSON.stringify(response.data));
    return response.data;
  }
};

