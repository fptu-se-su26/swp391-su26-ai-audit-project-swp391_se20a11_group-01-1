/** Format VND currency */
export const formatPrice = (price) =>
  price?.toLocaleString('vi-VN') + '₫';

/** Format date to vi-VN locale */
export const formatDate = (dateStr) =>
  dateStr ? new Date(dateStr).toLocaleDateString('vi-VN') : '';

/** Get initials from full name */
export const getInitials = (name = '') =>
  name.split(' ').map((w) => w[0]).join('').slice(0, 2).toUpperCase();

/** Truncate text */
export const truncate = (text = '', maxLength = 100) =>
  text.length > maxLength ? text.slice(0, maxLength) + '...' : text;
