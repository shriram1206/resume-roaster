import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const uploadResume = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await axios.post(`${API_BASE_URL}/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  
  return response.data;
};

export const analyzeResume = async (uploadId, jobDescription) => {
  const response = await axios.post(`${API_BASE_URL}/analyze`, {
    uploadId,
    jobDescription,
  });
  
  return response.data;
};

export const checkHealth = async () => {
  const response = await axios.get(`${API_BASE_URL}/health`);
  return response.data;
};
