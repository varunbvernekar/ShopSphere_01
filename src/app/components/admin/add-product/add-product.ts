// src/app/components/admin/add-product/add-product.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../../services/product';
import { Product } from '../../../models/product';

@Component({
    selector: 'app-admin-add-product',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './add-product.html',
    styleUrls: ['./add-product.css']
})
export class AdminAddProduct {
    newProduct = {
        name: '',
        description: '',
        category: '',
        basePrice: 0,
        stockLevel: 0,
        reorderThreshold: 0,
        previewImage: ''
    };

    selectedFileName = '';
    imagePreview = '';
    selectedFile: File | null = null;
    showSuccessMessage = false;

    constructor(
        private productService: ProductService,
        private router: Router
    ) { }

    onFileSelected(event: any): void {
        const file = event.target.files[0];
        if (file) {
            this.selectedFile = file;
            this.selectedFileName = file.name;
            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.imagePreview = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    }

    onAddProduct(): void {
        if (!this.newProduct.name || !this.newProduct.basePrice || !this.newProduct.category) {
            return;
        }

        const productPayload = {
            name: this.newProduct.name,
            description: this.newProduct.description,
            category: this.newProduct.category,
            basePrice: Number(this.newProduct.basePrice),
            stockLevel: Number(this.newProduct.stockLevel),
            reorderThreshold: Number(this.newProduct.reorderThreshold),
            customOptions: [],
            isActive: true,
            previewImage: '' // Handled by backend for new uploads
        };

        this.productService
            .addProduct(productPayload, this.selectedFile || undefined)
            .subscribe({
                next: () => {
                    this.showSuccessMessage = true;
                    this.resetForm();

                    // Redirect back to inventory after a short delay
                    setTimeout(() => {
                        this.showSuccessMessage = false;
                        this.router.navigate(['/admin/inventory']);
                    }, 2000);
                },
                error: (err: any) => {
                    console.error('Failed to add product', err);
                    alert('Failed to add product');
                }
            });
    }

    private resetForm(): void {
        this.newProduct = {
            name: '',
            description: '',
            category: '',
            basePrice: 0,
            previewImage: '',
            stockLevel: 0,
            reorderThreshold: 0
        };
        this.selectedFileName = '';
        this.imagePreview = '';
        this.selectedFile = null;

        const fileInput = document.getElementById('productImage') as HTMLInputElement;
        if (fileInput) {
            fileInput.value = '';
        }
    }
}
