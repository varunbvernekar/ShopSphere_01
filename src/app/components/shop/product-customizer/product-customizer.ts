import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Product } from '../../../models/product';

@Component({
  selector: 'app-product-customizer',
  standalone: true,
  imports: [CommonModule],
  styleUrl: './product-customizer.css',
  templateUrl: './product-customizer.html'
})
export class ProductCustomizer implements OnInit {
  @Input() product!: Product;

  @Output() addToCart = new EventEmitter<{
    product: Product;
    customization: { color: string; size: string; material: string };
    price: number;
  }>();

  @Output() back = new EventEmitter<void>();

  selectedColor = '';
  selectedSize = '';
  selectedMaterial = '';
  totalPrice = 0;
  showSuccess = false;

  ngOnInit(): void {
    if (!this.product) {
      return;
    }

    const colourOpt = this.product.customOptions.find(
      o => o.type === 'colour'
    );
    const sizeOpt = this.product.customOptions.find(o => o.type === 'size');
    const materialOpt = this.product.customOptions.find(
      o => o.type === 'material'
    );

    this.selectedColor = colourOpt?.options[0]?.label ?? '';
    this.selectedSize = sizeOpt?.options[0]?.label ?? '';
    this.selectedMaterial = materialOpt?.options[0]?.label ?? '';

    this.recalculatePrice();
  }

  get colourOptions() {
    return (
      this.product.customOptions.find(o => o.type === 'colour')?.options ?? []
    );
  }

  get sizeOptions() {
    return this.product.customOptions.find(o => o.type === 'size')?.options ?? [];
  }

  get materialOptions() {
    return (
      this.product.customOptions.find(o => o.type === 'material')?.options ?? []
    );
  }

  selectColour(value: string): void {
    this.selectedColor = value;
    this.recalculatePrice();
  }

  selectSize(value: string): void {
    this.selectedSize = value;
    this.recalculatePrice();
  }

  selectMaterial(value: string): void {
    this.selectedMaterial = value;
    this.recalculatePrice();
  }

  private recalculatePrice(): void {
    if (!this.product) {
      return;
    }

    let current = this.product.basePrice;

    for (const group of this.product.customOptions) {
      let selectedLabel = '';
      if (group.type === 'colour') selectedLabel = this.selectedColor;
      else if (group.type === 'size') selectedLabel = this.selectedSize;
      else if (group.type === 'material') selectedLabel = this.selectedMaterial;

      const selectedOption = group.options.find(o => o.label === selectedLabel);
      if (selectedOption) {
        current += selectedOption.priceModifier;
      }
    }

    this.totalPrice = Number(current.toFixed(2));
  }

  handleAddToCart(): void {
    // Check if product is in stock
    const isInStock = this.product.stockLevel === undefined ||
      this.product.stockLevel === null ||
      this.product.stockLevel > 0;

    if (!isInStock) {
      alert(`${this.product.name} is out of stock. Please select another product.`);
      return;
    }

    this.addToCart.emit({
      product: this.product,
      customization: {
        color: this.selectedColor,
        size: this.selectedSize,
        material: this.selectedMaterial
      },
      price: this.totalPrice
    });

    this.showSuccess = true;
    setTimeout(() => (this.showSuccess = false), 2000);
  }

  get isInStock(): boolean {
    return this.product.stockLevel === undefined ||
      this.product.stockLevel === null ||
      this.product.stockLevel > 0;
  }
}
