import client from './client';

const data = (r) => r.data;

export const teacherApi = {
  // 반
  getClassrooms: () => client.get('/api/teacher/classrooms').then(data),
  getClassroom: (id) => client.get(`/api/teacher/classrooms/${id}`).then(data),
  createClassroom: (payload) => client.post('/api/teacher/classrooms', payload).then(data),
  updateClassroom: (id, payload) => client.put(`/api/teacher/classrooms/${id}`, payload).then(data),
  deleteClassroom: (id) => client.delete(`/api/teacher/classrooms/${id}`),

  // 학생
  getStudents: (classroomId) => client.get(`/api/teacher/classrooms/${classroomId}/students`).then(data),
  createStudent: (classroomId, payload) =>
    client.post(`/api/teacher/classrooms/${classroomId}/students`, payload).then(data),
  updateStudent: (studentId, payload) => client.put(`/api/teacher/students/${studentId}`, payload).then(data),
  deleteStudent: (studentId) => client.delete(`/api/teacher/students/${studentId}`),

  // 과제
  getAssignments: (classroomId, status) =>
    client
      .get(`/api/teacher/classrooms/${classroomId}/assignments`, { params: status ? { status } : {} })
      .then(data),
  createAssignment: (classroomId, payload) =>
    client.post(`/api/teacher/classrooms/${classroomId}/assignments`, payload).then(data),
  updateAssignment: (assignmentId, payload) =>
    client.put(`/api/teacher/assignments/${assignmentId}`, payload).then(data),
  deleteAssignment: (assignmentId) => client.delete(`/api/teacher/assignments/${assignmentId}`),
  reviewAssignment: (assignmentId, approved, comment) =>
    client.post(`/api/teacher/assignments/${assignmentId}/review`, { approved, comment }).then(data),

  // 표현 미션
  getMissions: (classroomId) => client.get(`/api/teacher/classrooms/${classroomId}/missions`).then(data),
  createMission: (classroomId, payload) =>
    client.post(`/api/teacher/classrooms/${classroomId}/missions`, payload).then(data),
  updateMission: (missionId, payload) => client.put(`/api/teacher/missions/${missionId}`, payload).then(data),
  deleteMission: (missionId) => client.delete(`/api/teacher/missions/${missionId}`),
};
