import client from './client';

export const studentApi = {
  getDashboard: () => client.get('/api/student/dashboard').then((r) => r.data),
  submitAssignment: (assignmentId, note) =>
    client.post(`/api/student/assignments/${assignmentId}/submit`, { note }).then((r) => r.data),
  feed: (optionId) => client.post('/api/student/feed', { optionId }).then((r) => r.data),
  getGrowth: () => client.get('/api/student/growth').then((r) => r.data),
};
