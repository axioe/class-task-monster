import client from './client';

export const authApi = {
  login: (loginId, password) => client.post('/api/auth/login', { loginId, password }).then((r) => r.data),
  signup: (payload) => client.post('/api/auth/signup', payload).then((r) => r.data),
  me: () => client.get('/api/auth/me').then((r) => r.data),
};
