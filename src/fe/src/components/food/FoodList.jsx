import FoodCard from './FoodCard';
import Loading from '../common/Loading';

const FoodList = ({ foods, loading }) => {
  if (loading) return <Loading />;

  if (!foods?.length) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-gray-400">
        <span className="text-6xl mb-4">🍽️</span>
        <p className="text-lg font-medium">Không tìm thấy món ăn</p>
        <p className="text-sm">Hãy thử từ khóa khác hoặc chọn danh mục khác.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
      {foods.map((food) => <FoodCard key={food.id} food={food} />)}
    </div>
  );
};

export default FoodList;
