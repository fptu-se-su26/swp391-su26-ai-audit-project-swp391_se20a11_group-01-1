// Generic sidebar — used by AdminLayout
// For category filter sidebar see components/food/CategoryFilter.jsx
const Sidebar = ({ children }) => (
  <aside className="w-64 shrink-0 bg-white shadow-sm flex flex-col min-h-screen">
    {children}
  </aside>
);

export default Sidebar;
