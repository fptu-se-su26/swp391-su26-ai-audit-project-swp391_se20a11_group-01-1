export const getDefaultRouteByRoles = (roles?: string[]): string => {
  if (!roles || roles.length === 0) return '/';

  if (roles.includes('ROLE_ADMIN')) return '/admin';
  if (roles.includes('ROLE_STAFF')) return '/staff';
  if (roles.includes('ROLE_KITCHEN')) return '/kitchen';
  if (roles.includes('ROLE_CUSTOMER')) return '/customer';

  return '/';
};
