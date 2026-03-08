import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../core/models/category.model';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.scss']
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  isLoading = false;
  errorMessage = '';
  
  showModal = false;
  isSubmitting = false;
  submitError = '';
  
  selectedCategory: Category | null = null;
  isEditMode = false;

  categoryName = '';
  categoryCode = '';
  cashFlowFlag = 'Out';
  categoryType = 'Needs';
  
  showToast = false;
  toastMessage = '';

  constructor(
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categories = data.sort((a, b) => 
          a.categoryName.localeCompare(b.categoryName)
        );
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Gagal memuat data kategori';
        this.isLoading = false;
        console.error('Error loading categories:', err);
      }
    });
  }

  openModal(category?: Category): void {
    this.showModal = true;
    this.submitError = '';

    if (category) {
      this.isEditMode = true;
      this.selectedCategory = category;
      this.categoryName = category.categoryName;
      this.categoryCode = category.categoryCode;
      this.cashFlowFlag = category.cashFlowFlag || 'Out';
      // Set categoryType based on cashFlowFlag: empty for 'In', value for 'Out'
      this.categoryType = category.cashFlowFlag === 'In' ? '' : (category.categoryType || 'Needs');
    } else {
      this.isEditMode = false;
      this.selectedCategory = null;
      this.categoryName = '';
      this.categoryCode = '';
      this.cashFlowFlag = 'Out';
      this.categoryType = 'Needs';
    }
  }

  closeModal(): void {
    this.showModal = false;
    this.submitError = '';
    this.selectedCategory = null;
    this.isEditMode = false;
  }

  onSubmit(): void {
    if (!this.categoryName.trim()) {
      this.submitError = 'Nama kategori harus diisi';
      return;
    }

    this.isSubmitting = true;
    this.submitError = '';

    const categoryData = {
      categoryCode: this.categoryCode.trim(),
      categoryName: this.categoryName.trim(),
      cashFlowFlag: this.cashFlowFlag,
      // Send empty string for categoryType if cashFlowFlag is 'In'
      categoryType: this.cashFlowFlag === 'In' ? '' : this.categoryType
    };

    if (this.isEditMode && this.selectedCategory) {
      this.categoryService.update(this.selectedCategory.id, categoryData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.closeModal();
          this.loadCategories();
          this.showToastMessage('Kategori berhasil diperbarui');
        },
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal memperbarui kategori';
          console.error('Error updating category:', err);
        }
      });
    } else {
      this.categoryService.create(categoryData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.closeModal();
          this.loadCategories();
          this.showToastMessage('Kategori berhasil ditambahkan');
        },
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal menambahkan kategori';
          console.error('Error creating category:', err);
        }
      });
    }
  }

  showToastMessage(message: string): void {
    this.toastMessage = message;
    this.showToast = true;
    setTimeout(() => {
      this.showToast = false;
    }, 3000);
  }
}
