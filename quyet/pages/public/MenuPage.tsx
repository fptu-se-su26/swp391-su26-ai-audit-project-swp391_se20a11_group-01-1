import React, { useState, useEffect } from 'react';
import { foodApi } from '../../api/foodApi';
import { categoryApi } from '../../api/categoryApi';
import type { FoodResponse, FoodSearchParams } from '../../types/food';
import type { CategoryResponse } from '../../types/category';
import type { Page } from '../../types/common';
import { FoodCard } from '../../components/food/FoodCard';
import './MenuPage.css';

interface MenuPageProps {
  baseUrl: string;
}

export const MenuPage: React.FC<MenuPageProps> = ({ baseUrl }) => {
  const [foodsData, setFoodsData] = useState<Page<FoodResponse> | null>(null);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [keyword, setKeyword] = useState<string>('');
  const [searchInput, setSearchInput] = useState<string>('');
  const [categoryId, setCategoryId] = useState<number | undefined>(undefined);
  const [page, setPage] = useState<number>(0);

  // Fetch Categories once
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await categoryApi.getActiveCategories();
        setCategories(data);
      } catch (err) {
        console.error("Failed to load categories", err);
        // non-fatal, just log
      }
    };
    fetchCategories();
  }, []);

  // Fetch Foods
  useEffect(() => {
    const fetchFoods = async () => {
      setLoading(true);
      setError(null);
      try {
        const params: FoodSearchParams = {
          page,
          size: 12,
        };
        if (keyword) params.keyword = keyword;
        if (categoryId) params.categoryId = categoryId;

        const data = await foodApi.getPublicFoods(params);
        setFoodsData(data);
      } catch (err: unknown) {
        const errorResponse = err as { response?: { data?: { message?: string } } };
        setError(errorResponse?.response?.data?.message || 'Failed to load menu. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchFoods();
  }, [page, keyword, categoryId]);

  // Debounced search
  useEffect(() => {
    const timer = setTimeout(() => {
      setKeyword(searchInput.trim());
      setPage(0); // reset page on search
    }, 500);
    return () => clearTimeout(timer);
  }, [searchInput]);

  const handleCategoryChange = (id?: number) => {
    setCategoryId(id);
    setPage(0); // reset page on filter
  };

  return (
    <div className="menu-page">
      <div className="menu-header">
        <h2>Thực đơn</h2>
        <div className="menu-filters">
          <input
            type="text"
            placeholder="Tìm kiếm món ăn..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            className="search-input"
          />
          <select 
            value={categoryId || ''} 
            onChange={(e) => handleCategoryChange(e.target.value ? Number(e.target.value) : undefined)}
            className="category-select"
          >
            <option value="">Tất cả danh mục</option>
            {categories.map(c => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>
      </div>

      {error ? (
        <div className="error-state">{error}</div>
      ) : loading && !foodsData ? (
        <div className="loading-state">Đang tải thực đơn...</div>
      ) : foodsData && foodsData.content.length === 0 ? (
        <div className="empty-state">Không tìm thấy món ăn nào phù hợp.</div>
      ) : (
        <>
          <div className="food-grid">
            {foodsData?.content.map(food => (
              <FoodCard key={food.id} food={food} baseUrl={baseUrl} />
            ))}
          </div>

          {foodsData && foodsData.totalPages > 1 && (
            <div className="pagination">
              <button 
                disabled={foodsData.first} 
                onClick={() => setPage(p => Math.max(0, p - 1))}
              >
                Trang trước
              </button>
              <span>Trang {foodsData.number + 1} / {foodsData.totalPages}</span>
              <button 
                disabled={foodsData.last} 
                onClick={() => setPage(p => p + 1)}
              >
                Trang sau
              </button>
            </div>
          )}
        </>
      )}
      
      {loading && foodsData && (
        <div className="loading-overlay">Đang tải...</div>
      )}
    </div>
  );
};
