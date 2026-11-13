import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { errorMessage } from '../api/client';
import Monster from '../components/monster/Monster';
import { useAuth } from '../context/AuthContext';
import '../styles/auth.css';

/** 교사 회원가입. 학생 계정은 교사가 반 관리 화면에서 만든다. */
export default function SignupPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ loginId: '', password: '', nickname: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await authApi.signup({ ...form, loginId: form.loginId.trim(), nickname: form.nickname.trim() });
      await login(form.loginId.trim(), form.password);
      navigate('/teacher', { replace: true });
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
          <Monster stage="BABY" size={96} className="monster-float" />
          <h1>교사 계정 만들기</h1>
          <p className="muted">반을 만들고 학생들에게 맞춤 과제를 내 보세요.</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="nickname">이름 (학생에게 보이는 이름)</label>
            <input id="nickname" className="input" value={form.nickname} onChange={update('nickname')} maxLength={30} required />
          </div>
          <div className="field">
            <label htmlFor="loginId">아이디 (영문·숫자·_ 4자 이상)</label>
            <input
              id="loginId"
              className="input"
              autoComplete="username"
              value={form.loginId}
              onChange={update('loginId')}
              minLength={4}
              maxLength={50}
              pattern="[A-Za-z0-9_]+"
              required
            />
          </div>
          <div className="field">
            <label htmlFor="password">비밀번호 (8자 이상)</label>
            <input
              id="password"
              type="password"
              className="input"
              autoComplete="new-password"
              value={form.password}
              onChange={update('password')}
              minLength={8}
              required
            />
          </div>
          {error && <p className="alert alert-error">{error}</p>}
          <button type="submit" className="btn btn-primary btn-lg" disabled={submitting}>
            {submitting ? '만드는 중…' : '가입하고 시작하기'}
          </button>
        </form>

        <p className="auth-footer">
          이미 계정이 있나요? <Link to="/login">로그인</Link>
        </p>
      </section>
    </main>
  );
}
