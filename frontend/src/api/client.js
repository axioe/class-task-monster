import axios from 'axios';

const TOKEN_KEY = 'ctm.accessToken';

/** 브라우저 저장소 접근은 실패할 수 있어(사생활 보호 모드 등) 항상 try/catch 로 감싼다. */
export const tokenStorage = {
  get() {
    try {
      return localStorage.getItem(TOKEN_KEY);
    } catch {
      return null;
    }
  },
  set(token) {
    try {
      localStorage.setItem(TOKEN_KEY, token);
    } catch {
      /* 저장 실패 시 이번 탭에서만 로그인 유지 */
    }
  },
  clear() {
    try {
      localStorage.removeItem(TOKEN_KEY);
    } catch {
      /* noop */
    }
  },
};

export const AUTH_EXPIRED_EVENT = 'auth:expired';

const client = axios.create({
  // 개발: vite proxy(/api → 8080). 배포 시 VITE_API_BASE_URL 로 서버 주소 지정
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  headers: { 'Content-Type': 'application/json' },
});

client.interceptors.request.use((config) => {
  const token = tokenStorage.get();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

client.interceptors.response.use(
  (response) => response,
  (error) => {
    const isLoginRequest = error.config?.url?.includes('/api/auth/login');
    if (error.response?.status === 401 && !isLoginRequest) {
      tokenStorage.clear();
      window.dispatchEvent(new Event(AUTH_EXPIRED_EVENT));
    }
    return Promise.reject(error);
  },
);

/** 서버 ErrorResponse 에서 화면에 보여줄 메시지를 꺼낸다. */
export function errorMessage(error, fallback = '잠시 후 다시 시도해 주세요.') {
  const data = error?.response?.data;
  if (data?.fieldErrors?.length) {
    return data.fieldErrors[0].message || data.message;
  }
  return data?.message || fallback;
}

export default client;
