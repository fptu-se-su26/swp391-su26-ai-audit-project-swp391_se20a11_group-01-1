const SearchBar = ({ value, onChange, placeholder = 'Tìm kiếm...' }) => (
  <div className="relative">
    <span className="absolute inset-y-0 left-4 flex items-center text-gray-400">🔍</span>
    <input
      type="text"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
      className="w-full pl-11 pr-4 py-3 rounded-xl border border-gray-200 focus:outline-none focus:ring-2 focus:ring-orange-400 focus:border-transparent text-sm"
    />
  </div>
);

export default SearchBar;
