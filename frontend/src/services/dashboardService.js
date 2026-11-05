import api from './api';

export const dashboardService = {
  getStats: async () => {
    const response = await api.get('/dashboard/stats');
    return response.data;
  },

  exportBecknCatalog: async () => {
    const response = await api.get('/ondc/export/my');
    return response.data;
  }
};

