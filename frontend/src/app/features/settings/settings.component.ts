import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CutOffDateService } from '../../core/services/cut-off-date.service';
import { CutOffDate } from '../../core/models/cut-off-date.model';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  isLoading = false;
  errorMessage = '';
  submitError = '';
  successMessage = '';

  isEditMode = false;
  isSubmitting = false;

  cutOffDateValue: number | null = null;
  existingId: string | null = null;

  constructor(
    private cutOffDateService: CutOffDateService
  ) {}

  ngOnInit(): void {
    this.loadCutOffDate();
  }

  loadCutOffDate(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.cutOffDateService.getAll().subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.cutOffDateValue = data[0].cutOffDate;
          this.existingId = data[0].id;
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Gagal memuat pengaturan';
        this.isLoading = false;
        console.error('Error loading cut-off date:', err);
      }
    });
  }

  openEdit(): void {
    this.isEditMode = true;
    this.submitError = '';
    this.successMessage = '';
  }

  cancelEdit(): void {
    this.isEditMode = false;
    this.submitError = '';
    // Reload to restore original value
    this.loadCutOffDate();
  }

  validateInput(): boolean {
    if (this.cutOffDateValue === null || this.cutOffDateValue === undefined) {
      this.submitError = 'Tanggal Cut Off harus diisi';
      return false;
    }

    if (this.cutOffDateValue < 1 || this.cutOffDateValue > 31) {
      this.submitError = 'Tanggal Cut Off harus antara 1 sampai 31';
      return false;
    }

    return true;
  }

  onSubmit(): void {
    if (!this.validateInput()) {
      return;
    }

    this.isSubmitting = true;
    this.submitError = '';

    const cutOffData = {
      cutOffDate: this.cutOffDateValue!
    };

    if (this.existingId) {
      // Update existing
      this.cutOffDateService.update(this.existingId, cutOffData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.isEditMode = false;
          this.successMessage = 'Pengaturan berhasil disimpan';
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
          // Reload to get updated data
          this.loadCutOffDate();
        },
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal menyimpan pengaturan';
          console.error('Error updating cut-off date:', err);
        }
      });
    } else {
      // Create new
      this.cutOffDateService.create(cutOffData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.isEditMode = false;
          this.successMessage = 'Pengaturan berhasil disimpan';
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
          // Reload to get updated data
          this.loadCutOffDate();
        },
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal menyimpan pengaturan';
          console.error('Error creating cut-off date:', err);
        }
      });
    }
  }

  onNumberInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    // Remove non-numeric characters except empty string
    const value = input.value.replace(/[^0-9]/g, '');
    if (value === '') {
      this.cutOffDateValue = null;
    } else {
      const numValue = parseInt(value, 10);
      this.cutOffDateValue = numValue;
    }
  }
}
