// src/app/components/customer/product-page/product-page.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Product } from '../../../models/product';
import { UserRole, User } from '../../../models/user';
import { ProductService } from '../../../services/product';
import { AuthService } from '../../../services/auth';
import { CartItem } from '../../../models/cart-item';
import { CartService } from '../../../services/cart';
import { ProductCatalog } from './product-catalog/product-catalog';
import { ProductCustomizer } from './product-customizer/product-customizer';
import { Cart } from './cart/cart';

type CustomerView = 'catalog' | 'customizer' | 'cart';

@Component({
  selector: 'app-product-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductCatalog, ProductCustomizer, Cart],
  templateUrl: './product-page.html',
  styleUrls: ['./product-page.css']
})
export class ProductPage implements OnInit {
  products: Product[] = [];
  role: UserRole | null = null;

  view: CustomerView = 'catalog';
  selectedProduct: Product | null = null;

  // Admin add-product form model
  newProduct = {
    name: '',
    description: '',
    category: '',
    basePrice: 0,
    previewImage: '',
    stockLevel: 0,
    reorderThreshold: 0
  };

  // Image upload for new products
  selectedImageFile: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private cartService: CartService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();

    const user = this.authService.getCurrentUser();
    this.role = user?.role ?? null;

    this.route.queryParamMap.subscribe(params => {
      const view = params.get('view') as CustomerView | null;
      if (view === 'cart' || view === 'catalog' || view === 'customizer') {
        this.view = view;
      } else {
        this.view = 'catalog';
      }
    });
  }

  private loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: products => {
        this.products = products;
      },
      error: err => {
        console.error('Failed to load products', err);
      }
    });
  }

  get isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  // ---- ADMIN flow: add product ----

  onAddProduct(): void {
    if (!this.isAdmin) {
      return;
    }

    if (!this.newProduct.name || !this.newProduct.basePrice) {
      return;
    }

    this.productService
      .addProduct({
        name: this.newProduct.name,
        description: this.newProduct.description,
        category: this.newProduct.category,
        basePrice: this.newProduct.basePrice,
        previewImage: this.newProduct.previewImage,
        stockLevel: this.newProduct.stockLevel,
        reorderThreshold: this.newProduct.reorderThreshold,
        // ✅ required by Product model
        customOptions: []
      })
      .subscribe({
        next: () => {
          this.resetNewProductForm();
          this.loadProducts();
        },
        error: err => {
          console.error('Failed to add product', err);
        }
      });
  }

  private resetNewProductForm(): void {
    this.newProduct = {
      name: '',
      description: '',
      category: '',
      basePrice: 0,
      previewImage: '',
      stockLevel: 0,
      reorderThreshold: 0
    };
    this.selectedImageFile = null;
    this.imagePreview = null;
  }

  // Handle image file selection for new product
  onImageFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Validate file type
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }

      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('Image size should be less than 5MB');
        return;
      }

      this.selectedImageFile = file;

      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
        // Set preview image URL to data URL
        this.newProduct.previewImage = this.imagePreview;
      };
      reader.readAsDataURL(file);
    }
  }

  // Clear selected image
  clearSelectedImage(): void {
    this.selectedImageFile = null;
    this.imagePreview = null;
    this.newProduct.previewImage = '';
  }

  // ---- CUSTOMER flow ----

  get cartItems(): CartItem[] {
    return this.cartService.getItems();
  }

  handleSelectProduct(product: Product): void {
    if (this.role !== 'CUSTOMER') {
      return;
    }
    this.selectedProduct = product;
    this.view = 'customizer';
  }

  handleBackToCatalog(): void {
    this.selectedProduct = null;
    this.view = 'catalog';
  }

  handleAddToCart(event: {
    product: Product;
    customization: { color: string; size: string; material: string };
    price: number;
  }): void {
    if (!this.isProductInStock(event.product)) {
      alert(`${event.product.name} is out of stock. Please select another product.`);
      return;
    }
    const id = `item-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;

    const item: CartItem = {
      id,
      product: event.product,
      quantity: 1,
      customization: {
        color: event.customization.color,
        size: event.customization.size,
        material: event.customization.material
      },
      price: event.price
    };

    // this.cartService.addItem(item);
    const added = this.cartService.addItem(item);
    if (!added) {
      const availableStock = this.getAvailableStock(event.product);
      alert(`Cannot add ${event.product.name} to cart. Only ${availableStock} unit(s) available.`);
      return;
    }
    this.view = 'cart';
  }

  handleUpdateQuantity(event: { itemId: string; quantity: number }): void {
    const items = this.cartService.getItems();
    const item = items.find(i => i.id === event.itemId);
    
    if (!item) {
      return;
    }

    // Validate stock before updating
    const availableStock = this.getAvailableStock(item.product);
    const otherItemsQuantity = items
      .filter(i => i.id !== event.itemId && i.product.productId === item.product.productId)
      .reduce((sum, i) => sum + i.quantity, 0);

    if (otherItemsQuantity + event.quantity > availableStock) {
      const maxAllowed = availableStock - otherItemsQuantity;
      alert(`Only ${availableStock} unit(s) available for ${item.product.name}. Maximum ${maxAllowed} unit(s) can be added.`);
      // Set quantity to maximum allowed
      event.quantity = Math.max(1, maxAllowed);
    }

    this.cartService.updateQuantity(event.itemId, event.quantity, item.product);
  }

  isProductInStock(product: Product): boolean {
    return product.stockLevel === undefined || product.stockLevel === null || product.stockLevel > 0;
  }

  getAvailableStock(product: Product): number {
    if (product.stockLevel === undefined || product.stockLevel === null) {
      return Infinity;
    }
    return product.stockLevel;
  }

  handleRemoveItem(id: string): void {
    this.cartService.removeItem(id);
  }

  handleContinueShopping(): void {
    this.view = 'catalog';
  }

  handleCheckout(): void {
    const user = this.authService.getCurrentUser();

    if (!user || user.role !== 'CUSTOMER' || !user.id) {
      alert('Please log in as a customer before placing an order.');
      return;
    }

    const items = this.cartService.getItems();
    if (!items.length) {
      alert('Your cart is empty.');
      return;
    }
    
    // Navigate to payment page instead of creating order directly
    this.router.navigate(['/payment']);
  }


  viewProductInsights(product: Product): void {
    alert(`Admin: Viewing insights for "${product.name}".`);
  }

  deactivateProduct(product: Product): void {
    alert(`Admin: Deactivating "${product.name}".`);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/300x200?text=Preview';
  }
}
