// src/app/app.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth';
import { CartService } from './services/cart';
import { ProductService } from './services/product';
import { Subscription } from 'rxjs';
import { LowStockAlerts } from './shared/components/low-stock-alerts/low-stock-alerts';
import { CustomerNotifications } from './shared/components/customer-notifications/customer-notifications';
import { Navbar } from './shared/components/navbar/navbar';
import { Footer } from './shared/components/footer/footer';
import { OrderService } from './services/order';
import { InventoryService } from './services/inventory';
import { NotificationService } from './services/notification';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, LowStockAlerts, CustomerNotifications, Navbar, Footer],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit, OnDestroy {
  lowStockCount = 0;
  showLowStockModal = false;
  notificationCount = 0;
  showNotificationsModal = false;
  private productsSubscription?: Subscription;
  private notifSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private router: Router,
    public cartService: CartService,
    private productService: ProductService,
    private orderService: OrderService,
    private notificationService: NotificationService,
    private inventoryService: InventoryService
  ) { }

  ngOnInit(): void {
    // Subscribe to low stock count from Inventory Service
    this.productsSubscription = this.inventoryService.lowStockCount$.subscribe(
      count => this.lowStockCount = count
    );

    // Subscribe to notification service
    this.notifSubscription = this.notificationService.unreadCount$.subscribe(
      count => this.notificationCount = count
    );

    // Initialize service with current user if logged in
    const user = this.authService.getCurrentUser();
    if (user && user.id) {
      this.notificationService.loadForUser(user.id);
    }
  }

  ngOnDestroy(): void {
    if (this.productsSubscription) {
      this.productsSubscription.unsubscribe();
    }
    if (this.notifSubscription) {
      this.notifSubscription.unsubscribe();
    }
  }

  get isLoggedIn(): boolean {
    return !!this.authService.getCurrentUser();
  }

  get isAdmin(): boolean {
    const user = this.authService.getCurrentUser();
    return !!user && user.role === 'ADMIN';
  }

  // 👇 show current cart item count (used in navbar)
  get cartCount(): number {
    return this.cartService.getItemCount();
  }


  openLowStockAlerts(): void {
    this.showLowStockModal = true;
  }

  closeLowStockAlerts(): void {
    this.showLowStockModal = false;
  }

  openNotifications(): void {
    this.showNotificationsModal = true;
  }

  closeNotifications(): void {
    this.showNotificationsModal = false;
  }

  logout(): void {
    this.authService.logout();
    this.cartService.clear();
    this.notificationService.clear();
    this.router.navigate(['/login']);
  }
}
