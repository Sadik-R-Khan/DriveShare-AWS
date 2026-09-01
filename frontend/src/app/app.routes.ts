import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { CarsComponent } from './pages/cars/cars';
import { CarDetailComponent } from './pages/car-detail/car-detail';
import { UserDashboardComponent } from './pages/user-dashboard/user-dashboard';
import { RenterDashboardComponent } from './pages/renter-dashboard/renter-dashboard';
import { roleGuard } from './auth-guard';

export const routes: Routes = [
  { path: '', component: CarsComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'book/:id', component: CarDetailComponent },
  { path: 'cars/:id', component: CarDetailComponent },
  {
    path: 'bookings',
    component: UserDashboardComponent,
    canActivate: [roleGuard],
    data: { role: 'USER' },
  },
  { path: 'dashboard', redirectTo: 'bookings' },
  {
    path: 'renter',
    component: RenterDashboardComponent,
    canActivate: [roleGuard],
    data: { role: 'RENTER' },
  },
  { path: '**', redirectTo: '' },
];
