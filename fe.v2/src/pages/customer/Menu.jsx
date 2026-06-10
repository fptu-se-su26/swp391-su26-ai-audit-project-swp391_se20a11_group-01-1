import { useState, useEffect } from 'react';
import FoodList from '../../components/food/FoodList';
import CategoryFilter from '../../components/food/CategoryFilter';
import SearchBar from '../../components/common/SearchBar';
import Pagination from '../../components/common/Pagination';
import { getAllFoods } from '../../services/foodService';
import { getAllCategories } from '../../services/categoryService';

const Menu = () => {
  const [foods, setFoods] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selectedCategoryId, setSelectedCategoryId] = useState(null);
  const [page, setPage] = useState(0);
  const PAGE_SIZE = 9;

  useEffect(() => {
    getAllCategories().then(({ data }) => setCategories(data)).catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    getAllFoods()
      .then(({ data }) => setFoods(data))
      .catch(() => setFoods([]))
      .finally(() => setLoading(false));
  }, []);

  const filtered = foods.filter((f) => {
    const matchCat = selectedCategoryId === null || f.categoryId === selectedCategoryId;
    const matchSearch = f.name.toLowerCase().includes(search.toLowerCase());
    return matchCat && matchSearch;
  });

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paginated = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);

  const handleSearch = (val) => { setSearch(val); setPage(0); };
  const handleCategory = (id) => { setSelectedCategoryId(id); setPage(0); };

  return (
    <div className="max-w-6xl mx-auto px-4 py-10">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Thực đơn</h1>
        <p className="text-gray-500">Khám phá các món ăn ngon từ khắp nơi</p>
      </div>

      <div className="mb-8 max-w-md">
        <SearchBar value={search} onChange={handleSearch} placeholder="Tìm kiếm món ăn..." />
      </div>

      <div className="flex flex-col md:flex-row gap-8">
        <CategoryFilter categories={categories} selectedId={selectedCategoryId} onSelect={handleCategory} />
        <div className="flex-1">
          <FoodList foods={paginated} loading={loading} />
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>
    </div>
  );
};

export default Menu;
