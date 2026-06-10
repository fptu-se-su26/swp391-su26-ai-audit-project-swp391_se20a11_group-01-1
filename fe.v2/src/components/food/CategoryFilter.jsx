const CategoryFilter = ({ categories = [], selectedId, onSelect }) => (
  <aside className="w-full md:w-52 shrink-0">
    <h2 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">Danh mục</h2>
    <ul className="space-y-1">
      <li>
        <button
          onClick={() => onSelect(null)}
          className={`w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium transition-colors ${
            selectedId === null ? 'bg-orange-500 text-white' : 'text-gray-600 hover:bg-orange-50 hover:text-orange-500'
          }`}
        >
          🍽️ Tất cả
        </button>
      </li>
      {categories.map((cat) => (
        <li key={cat.id}>
          <button
            onClick={() => onSelect(cat.id)}
            className={`w-full text-left px-4 py-2.5 rounded-xl text-sm font-medium transition-colors ${
              selectedId === cat.id ? 'bg-orange-500 text-white' : 'text-gray-600 hover:bg-orange-50 hover:text-orange-500'
            }`}
          >
            {cat.icon || '🍴'} {cat.name}
          </button>
        </li>
      ))}
    </ul>
  </aside>
);

export default CategoryFilter;
