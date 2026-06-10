import axiosInstance from './axiosInstance';

/** GET /api/reviews/food/{foodId} */
export const getReviewsByFood = (foodId) => axiosInstance.get(`/reviews/food/${foodId}`);

/** GET /api/reviews — Admin only */
export const getAllReviews = () => axiosInstance.get('/reviews');

/** POST /api/reviews — body: { foodId, rating, comment } */
export const createReview = (data) => axiosInstance.post('/reviews', data);

/** PUT /api/reviews/{id} */
export const updateReview = (id, data) => axiosInstance.put(`/reviews/${id}`, data);

/** DELETE /api/reviews/{id} */
export const deleteReview = (id) => axiosInstance.delete(`/reviews/${id}`);
