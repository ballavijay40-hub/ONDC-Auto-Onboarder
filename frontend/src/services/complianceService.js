import api from './api';

export const complianceService = {
  getMyCompliance: async () => {
    const response = await api.get('/compliance/my');
    return response.data;
  },

  runComplianceCheck: async (provider) => {
    const response = await api.post('/compliance/check', { provider });
    return response.data;
  }
};

