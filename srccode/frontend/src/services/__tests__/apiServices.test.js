import API from '../api';
import * as categories from '../categoryService';
import * as foods from '../foodService';
import * as feedback from '../feedbackService';
import * as users from '../userService';

jest.mock('../api', () => ({
  get: jest.fn(), post: jest.fn(), put: jest.fn(), patch: jest.fn(), delete: jest.fn(),
}));

beforeEach(() => jest.clearAllMocks());

describe('category API contract', () => {
  test('reads category collection and item', async () => {
    API.get.mockResolvedValueOnce({ data: ['a'] }).mockResolvedValueOnce({ data: { id: 7 } });
    await expect(categories.getAllCategories()).resolves.toEqual(['a']);
    await expect(categories.getCategoryById(7)).resolves.toEqual({ id: 7 });
    expect(API.get).toHaveBeenNthCalledWith(1, '/categories');
    expect(API.get).toHaveBeenNthCalledWith(2, '/categories/7');
  });

  test('uses correct verbs and preserves payload for writes', async () => {
    const payload = { categoryName: 'Main' };
    API.post.mockResolvedValue({ data: payload }); API.put.mockResolvedValue({ data: payload }); API.delete.mockResolvedValue({ data: 'ok' });
    await categories.createCategory(payload); await categories.updateCategory(3, payload); await categories.deleteCategory(3);
    expect(API.post).toHaveBeenCalledWith('/categories', payload);
    expect(API.put).toHaveBeenCalledWith('/categories/3', payload);
    expect(API.delete).toHaveBeenCalledWith('/categories/3');
  });
});

describe('food API contract', () => {
  test('maps all read endpoints exactly', async () => {
    API.get.mockResolvedValue({ data: [] });
    await foods.getAllFoods(); await foods.getAvailableFoods(); await foods.getFoodById(2); await foods.getFoodsByCategory(4);
    expect(API.get.mock.calls).toEqual([['/foods'], ['/foods/available'], ['/foods/2'], ['/foods/category/4']]);
  });

  test('maps create, update, toggle and delete endpoints', async () => {
    const payload = { foodName: 'Soup', price: 10 };
    API.post.mockResolvedValue({ data: payload }); API.put.mockResolvedValue({ data: payload });
    API.patch.mockResolvedValue({ data: payload }); API.delete.mockResolvedValue({ data: 'ok' });
    await foods.createFood(payload); await foods.updateFood(2, payload); await foods.toggleFoodAvailable(2); await foods.deleteFood(2);
    expect(API.post).toHaveBeenCalledWith('/foods', payload);
    expect(API.put).toHaveBeenCalledWith('/foods/2', payload);
    expect(API.patch).toHaveBeenCalledWith('/foods/2/toggle-available');
    expect(API.delete).toHaveBeenCalledWith('/foods/2');
  });
});

describe('feedback and account API contract', () => {
  test('wraps feedback status in the backend DTO shape', async () => {
    API.put.mockResolvedValue({ data: { status: 'RESOLVED' } });
    await feedback.updateFeedbackStatus(9, 'RESOLVED');
    expect(API.put).toHaveBeenCalledWith('/feedbacks/9/status', { status: 'RESOLVED' });
  });

  test('includes acting user for role and status changes', async () => {
    API.put.mockResolvedValue({ data: {} });
    await users.updateUserRole(2, 'STAFF', 1); await users.updateUserStatus(2, false, 1);
    expect(API.put).toHaveBeenNthCalledWith(1, '/users/2/role', { roleName: 'STAFF', currentUserId: 1 });
    expect(API.put).toHaveBeenNthCalledWith(2, '/users/2/status', { isActive: false, currentUserId: 1 });
  });

  test('propagates API rejection to the caller', async () => {
    const error = new Error('network'); API.get.mockRejectedValue(error);
    await expect(feedback.getAllFeedbacks()).rejects.toBe(error);
  });
});
