import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../models/product';

@Component({
  selector: 'app-product-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styleUrl: './product-catalog.css',
  templateUrl: './product-catalog.html'
})
export class ProductCatalog {
  @Input() products: Product[] = [];
  @Output() productSelect = new EventEmitter<Product>();

  searchTerm = '';
  selectedCategory = 'All';

  // Segmentation state
  currentPage = 1;
  itemsPerPage = 12;

  get categories(): string[] {
    const set = new Set<string>();
    this.products.forEach(p => {
      if (p.category) {
        set.add(p.category);
      }
    });
    return ['All', ...Array.from(set)];
  }

  get filteredProducts(): Product[] {
    const term = this.searchTerm.trim().toLowerCase();

    const filtered = this.products.filter(p => {

      const matchCategory =
        this.selectedCategory === 'All' ||
        (!!p.category && p.category === this.selectedCategory);

      const matchSearch =
        !term ||
        p.name.toLowerCase().includes(term) ||
        (p.description
          ? p.description.toLowerCase().includes(term)
          : false);

      return matchCategory && matchSearch;
    });

    return filtered.sort((a, b) => {
      const aInStock = this.isInStock(a);
      const bInStock = this.isInStock(b);

      if (aInStock && !bInStock) return -1;
      if (!aInStock && bInStock) return 1;
      return 0; // Keep original order for same stock status
    });
  }

  get paginatedProducts(): Product[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredProducts.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredProducts.length / this.itemsPerPage);
  }

  onFilterChange(): void {
    this.currentPage = 1;
  }

  setPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  isInStock(product: Product): boolean {
    // Product is in stock if stockLevel is undefined, null, or greater than 0
    // Product is out of stock if stockLevel is 0
    return product.stockLevel === undefined || product.stockLevel === null || product.stockLevel > 0;
  }

  isOutOfStock(product: Product): boolean {
    return !this.isInStock(product);
  }

  select(product: Product): void {
    this.productSelect.emit(product);
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/400x250?text=Preview';
  }
}
