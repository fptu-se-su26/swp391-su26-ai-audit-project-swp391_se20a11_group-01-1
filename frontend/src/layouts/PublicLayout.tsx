import React from 'react';
import { Outlet, Link } from 'react-router-dom';

export const PublicLayout: React.FC = () => {
  return (
    <div className="public-layout">
      <header>
        <h2>Public Layout</h2>
        <nav>
          <Link to="/">Home</Link> | <Link to="/login">Login</Link> | <Link to="/register">Register</Link>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
};
