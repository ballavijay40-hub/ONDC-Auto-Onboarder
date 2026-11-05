import api from './api';

export const documentService = {
  uploadDocument: async (file, documentType) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('documentType', documentType);

    const response = await api.post('/document/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  getMyDocuments: async () => {
    const response = await api.get('/document/my');
    return response.data;
  },

  deleteDocument: async (id) => {
    await api.delete(`/document/${id}`);
  },

  runOcr: async (documentId) => {
    const response = await api.post('/ocr/extract', { documentId });
    return response.data;
  }
};

