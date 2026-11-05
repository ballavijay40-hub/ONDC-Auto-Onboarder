import api from './api';

export const productService = {
  getMyProducts: async () => {
    const response = await api.get('/product/my');
    return response.data;
  },

  createProduct: async (productData) => {
    const response = await api.post('/product', productData);
    return response.data;
  },

  deleteProduct: async (id) => {
    await api.delete(`/product/${id}`);
  },

  generateAiCatalog: async (generateRequest) => {
    const response = await api.post('/product/generate', generateRequest);
    return response.data;
  }
};

