import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { errorMessage } from '../api/client';
import Monster from '../components/monster/Monster';
import { useAuth } from '../context/AuthContext';
import { homePathFor } from '../routes/RequireRole';
import '../styles/auth.css';

export default function LoginPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ loginId: '', password: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  if (user) {
    return <Navigate to={homePathFor(user)} replace />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const me = await login(form.loginId.trim(), form.password);
      navigate(homePathFor(me), { replace: true });
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const update = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  return (
    <main className="auth-page">
      <section className="card auth-card">
        <div className="auth-hero">
          <Monster stage="GROWN" size={120} className="monster-float" />
          <h1>
            우리 반 <em>과제 몬스터</em>
          </h1>
          <p className="muted">오늘의 도전으로 우리 반 몬스터를 키워요!</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="loginId">아이디</label>
            <input
              id="loginId"
              className="input"
              autoComplete="username"
              value={form.loginId}
              onChange={update('loginId')}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              type="password"
              className="input"
              autoComplete="current-password"
              value={form.password}
              onChange={update('password')}
              required
            />
          </div>
          {error && <p className="alert alert-error">{error}</p>}
          <button type="submit" className="btn btn-primary btn-lg" disabled={submitting}>
            {submitting ? '로그인 중…' : '로그인'}
          </button>
        </form>

        {import.meta.env.DEV && (
          <p className="demo-accounts">
            개발용 샘플 계정 · 교사 <strong>teacher</strong> / 학생 <strong>jiwoo</strong> (비밀번호는 README 참고)
          </p>
        )}

        <p className="auth-footer">
          선생님이신가요? <Link to="/signup">교사 계정 만들기</Link>
        </p>
      </section>
    </main>
  );
}
