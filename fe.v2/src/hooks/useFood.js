import { useState, useEffect } from 'react';
import { getAllFoods, getFoodById } from '../services/foodService';

export const useFoods = (params) => {
  const [foods, setFoods] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    getAllFoods(params)
      .then(({ data }) => setFoods(data))
      .catch((err) => setError(err.response?.data?.message || 'Lỗi tải dữ liệu'))
      .finally(() => setLoading(false));
  }, []);

  return { foods, loading, error };
};

export const useFood = (id) => {
  const [food, setFood] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getFoodById(id)
      .then(({ data }) => setFood(data))
      .catch((err) => setError(err.response?.data?.message || 'Lỗi tải dữ liệu'))
      .finally(() => setLoading(false));
  }, [id]);

  return { food, loading, error };
};
