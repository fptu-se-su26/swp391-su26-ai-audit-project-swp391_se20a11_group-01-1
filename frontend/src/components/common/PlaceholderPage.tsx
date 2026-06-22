import React from 'react';

interface PlaceholderPageProps {
  title: string;
}

export const PlaceholderPage: React.FC<PlaceholderPageProps> = ({ title }) => {
  return (
    <div style={{ padding: '2rem' }}>
      <h3>{title}</h3>
      <p>This is a placeholder page for {title}. Future business logic will go here.</p>
    </div>
  );
};
