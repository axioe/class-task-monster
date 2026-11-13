import { Link } from 'react-router-dom';
import Monster from '../monster/Monster';

export default function Brand({ to = '/' }) {
  return (
    <Link to={to} className="brand" aria-label="우리 반 과제 몬스터 홈">
      <Monster stage="GROWN" size={52} />
      <span className="brand-title">
        우리 반 <em>과제 몬스터</em>
      </span>
    </Link>
  );
}
