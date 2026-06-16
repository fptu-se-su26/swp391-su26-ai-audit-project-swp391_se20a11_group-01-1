import axiosInstance from './axiosInstance';

/** GET /api/reviews — public */
export const getAllReviews = () => axiosInstance.get('/reviews');

/** POST /api/reviews — requires auth, body: { foodId, rating, comment } */
export const createReview = (data) => axiosInstance.post('/reviews', data);
