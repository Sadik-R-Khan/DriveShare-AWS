import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth';

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const expectedRole = route.data['role'];

  if (!auth.isLoggedIn) {
    return router.createUrlTree(['/login']);
  }

  if (expectedRole === 'RENTER' && !auth.isRenter) {
    return router.createUrlTree(['/bookings']);
  }

  if (expectedRole === 'USER' && auth.isRenter) {
    return router.createUrlTree(['/renter']);
  }

  return true;
};
