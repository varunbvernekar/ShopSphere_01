// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Home } from './features/home/home';
import { CartPage } from './features/shop/cart-page/cart-page';
import { ProductPage } from './features/shop/product-page';
import { OrdersPage } from './features/orders/orders';
import { Profile } from './features/profile/profile';
import { Payment } from './features/payment/payment';
import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';
import { AdminDashboard } from './features/admin/admin-dashboard/admin-dashboard';
import { AdminInventory } from './features/admin/admin-inventory/admin-inventory';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'products', component: ProductPage, canActivate: [authGuard] },
  { path: 'cart', component: CartPage, canActivate: [authGuard] },
  { path: 'orders', component: OrdersPage, canActivate: [authGuard] },
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'payment', component: Payment, canActivate: [authGuard] },
  { path: 'admin', component: AdminDashboard, canActivate: [adminGuard] },
  { path: 'admin/inventory', component: AdminInventory, canActivate: [adminGuard] },
  { path: '**', redirectTo: '' }
];
