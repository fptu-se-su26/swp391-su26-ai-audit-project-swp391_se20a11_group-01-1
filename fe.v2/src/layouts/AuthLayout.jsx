import { Outlet } from 'react-router-dom';

const AuthLayout = () => (
  <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 to-amber-50 px-4 py-10">
    <Outlet />
  </div>
);

export default AuthLayout;
