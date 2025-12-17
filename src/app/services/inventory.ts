
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, switchMap } from 'rxjs';
import { Product } from '../models/product';
import { ProductService } from './product';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private lowStockCountSubject = new BehaviorSubject<number>(0);
  public lowStockCount$ = this.lowStockCountSubject.asObservable();

  constructor(
    private http: HttpClient,
    private productService: ProductService
  ) {
    // Initial load
    this.refreshLowStockCount();
  }

  getProducts(): Observable<Product[]> {
    return this.productService.getProducts();
  }

  refreshLowStockCount(): void {
    this.getProducts().subscribe({
      next: products => {
        const count = products.filter(
          p =>
            typeof p.stockLevel === 'number' &&
            typeof p.reorderThreshold === 'number' &&
            p.stockLevel <= p.reorderThreshold
        ).length;
        this.lowStockCountSubject.next(count);
      },
      error: err => console.error('Failed to refresh low stock count', err)
    });
  }

  updateStock(productId: string, stockLevel: number): Observable<Product> {
    return this.productService.getProductById(productId).pipe(
      switchMap(product => {
        if (!product) throw new Error('Product not found');
        const updated: Product = { ...product, stockLevel };
        return this.productService.updateProduct(updated);
      }),
      tap(() => this.refreshLowStockCount())
    );
  }

  updateReorderThreshold(productId: string, reorderThreshold: number): Observable<Product> {
    return this.productService.getProductById(productId).pipe(
      switchMap(product => {
        if (!product) throw new Error('Product not found');
        const updated: Product = { ...product, reorderThreshold };
        return this.productService.updateProduct(updated);
      }),
      tap(() => this.refreshLowStockCount())
    );
  }

  updateInventory(productId: string, stockLevel: number, reorderThreshold: number): Observable<Product> {
    return this.productService.getProductById(productId).pipe(
      switchMap(product => {
        if (!product) throw new Error('Product not found');
        const updated: Product = { ...product, stockLevel, reorderThreshold };
        return this.productService.updateProduct(updated);
      }),
      tap(() => this.refreshLowStockCount())
    );
  }
}
