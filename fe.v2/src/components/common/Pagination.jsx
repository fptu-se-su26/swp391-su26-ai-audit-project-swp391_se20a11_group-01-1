const Pagination = ({ page, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-2 mt-8">
      <button
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
        className="px-3 py-2 rounded-lg border text-sm font-medium disabled:opacity-40 hover:bg-orange-50 hover:border-orange-300 transition-colors"
      >
        ←
      </button>

      {Array.from({ length: totalPages }, (_, i) => (
        <button
          key={i}
          onClick={() => onPageChange(i)}
          className={`px-3 py-2 rounded-lg border text-sm font-medium transition-colors ${
            i === page ? 'bg-orange-500 text-white border-orange-500' : 'hover:bg-orange-50 hover:border-orange-300'
          }`}
        >
          {i + 1}
        </button>
      ))}

      <button
        onClick={() => onPageChange(page + 1)}
        disabled={page === totalPages - 1}
        className="px-3 py-2 rounded-lg border text-sm font-medium disabled:opacity-40 hover:bg-orange-50 hover:border-orange-300 transition-colors"
      >
        →
      </button>
    </div>
  );
};

export default Pagination;
