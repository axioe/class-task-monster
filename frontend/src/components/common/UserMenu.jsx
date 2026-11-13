import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function UserMenu({ avatar = '🙂' }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const onClick = (e) => ref.current && !ref.current.contains(e.target) && setOpen(false);
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <div className="user-menu" ref={ref}>
      <button type="button" className="user-chip" onClick={() => setOpen((v) => !v)} aria-expanded={open}>
        <span className="user-avatar" aria-hidden="true">
          {avatar}
        </span>
        {user?.nickname}
        <span aria-hidden="true">▾</span>
      </button>
      {open && (
        <div className="user-dropdown">
          <button type="button" onClick={handleLogout}>
            로그아웃
          </button>
        </div>
      )}
    </div>
  );
}
