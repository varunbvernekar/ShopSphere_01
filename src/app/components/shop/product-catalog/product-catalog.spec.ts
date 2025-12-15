import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductCatalog } from './product-catalog';
import { Product } from '../../../models/product';

describe('ProductCatalog', () => {
  let component: ProductCatalog;
  let fixture: ComponentFixture<ProductCatalog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCatalog]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ProductCatalog);
    component = fixture.componentInstance;

    // Mock products
    const mockProducts: Product[] = Array.from({ length: 25 }, (_, i) => ({
      productId: `p${i}`,
      name: `Product ${i}`,
      description: 'Desc',
      basePrice: 100,
      previewImage: 'img.jpg',
      images: [],
      category: 'Test',
      stockLevel: 10,
      isActive: true,
      customOptions: [],
      createdAt: new Date(),
      updatedAt: new Date()
    }));
    component.products = mockProducts;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should paginate products correctly', () => {
    // Default itemsPerPage is 12
    expect(component.totalPages).toBe(3); // 25 items / 12 = 2.08 -> 3 pages
    expect(component.paginatedProducts.length).toBe(12);
    expect(component.paginatedProducts[0].name).toBe('Product 0');
  });

  it('should navigate pages correctly', () => {
    component.nextPage();
    expect(component.currentPage).toBe(2);
    expect(component.paginatedProducts[0].name).toBe('Product 12');

    component.nextPage(); // Page 3
    expect(component.paginatedProducts.length).toBe(1); // One item left (index 24)

    component.nextPage(); // Should stick at last page
    expect(component.currentPage).toBe(3);

    component.prevPage(); // Back to 2
    expect(component.currentPage).toBe(2);
  });

  it('should reset page on filter change', () => {
    component.nextPage();
    expect(component.currentPage).toBe(2);

    component.onFilterChange();
    expect(component.currentPage).toBe(1);
  });
});
