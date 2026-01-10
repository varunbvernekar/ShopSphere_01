import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product, CustomOptionGroup } from '../models/product';
import { Observable, map, switchMap } from 'rxjs';
import { DEFAULT_CUSTOM_OPTIONS } from '../config/product.config';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) { }

  /**
   * Get all products from db.json (/products).
   * If customOptions is missing/empty, inject default options.
   */
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`).pipe(
      map(products => products.map(p => this.normalizeProduct(p)))
    );
  }

  /**
   * Add a new product with an optional image file.
   * Uses FormData for multipart/form-data support.
   */
  addProduct(
    product: Omit<Product, 'productId'> & { productId?: string },
    imageFile?: File
  ): Observable<Product> {
    const formData = new FormData();
    formData.append('product', JSON.stringify(product));
    if (imageFile) {
      formData.append('image', imageFile);
    }
    return this.http.post<Product>(`${this.apiUrl}/products`, formData).pipe(
      map(p => this.normalizeProduct(p))
    );
  }

  getProductById(productId: string): Observable<Product | undefined> {
    return this.getProducts().pipe(
      map(products => products.find(p => p.productId === productId))
    );
  }

  /** Update an existing product */
  updateProduct(product: Product): Observable<Product> {
    if (!product.productId) {
      throw new Error('Product ID is required to update a product');
    }
    const payload: Product & { id: string } = {
      ...product,
      id: product.productId
    };
    return this.http.put<Product>(`${this.apiUrl}/products/${product.productId}`, payload).pipe(
      map(p => this.normalizeProduct(p))
    );
  }

  /** Delete a product */
  deleteProduct(productId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${productId}`);
  }

  /** Update product stock level */
  updateStock(productId: string, stockLevel: number): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${productId}/stock`, { stockLevel }).pipe(
      map(p => this.normalizeProduct(p))
    );
  }

  /** Update reorder threshold */
  updateReorderThreshold(productId: string, reorderThreshold: number): Observable<Product> {
    return this.getProductById(productId).pipe(
      switchMap(product => {
        if (!product) {
          throw new Error('Product not found');
        }
        const updated: Product = { ...product, reorderThreshold };
        return this.updateProduct(updated);
      })
    );
  }

  private normalizeProduct(p: Product): Product {
    let previewImage = p.previewImage || '';
    if (previewImage.startsWith('/api')) {
      previewImage = `http://localhost:8080${previewImage}`;
    }

    return {
      ...p,
      productId: p.productId || (p as any).id,
      previewImage,
      customOptions:
        p.customOptions && p.customOptions.length
          ? p.customOptions
          : this.getDefaultCustomOptions()
    };
  }

  // ---- default custom options (used if db.json doesn't define them) ----
  private getDefaultCustomOptions(): CustomOptionGroup[] {
    return DEFAULT_CUSTOM_OPTIONS;
  }
}
