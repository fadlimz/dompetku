import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AccountService } from '../../../core/services/account.service';
import { CategoryService } from '../../../core/services/category.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { TransferService } from '../../../core/services/transfer.service';
import { Account } from '../../../core/models/account.model';
import { Category } from '../../../core/models/category.model';
import { DailyCash } from '../../../core/models/transaction.model';
import { Transfer } from '../../../core/models/transfer.model';

@Component({
  selector: 'app-daily-cash',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './daily-cash.component.html',
  styleUrls: ['./daily-cash.component.scss']
})
export class DailyCashComponent implements OnInit {
  accounts: Account[] = [];
  categories: Category[] = [];
  currentMonth: Date = new Date();
  calendarDays: { date: Date; isCurrentMonth: boolean; isToday: boolean; hasTransaction: boolean }[] = [];
  dailyCashList: DailyCash[] = [];
  transferList: Transfer[] = [];
  selectedDate: Date | null = null;
  dailyCashForSelectedDate: DailyCash[] = [];
  transferForSelectedDate: Transfer[] = [];
  showDateDialog = false;
  selectedDailyCash: DailyCash | null = null;

  // Form modal properties
  showFormModal = false;
  isEditMode = false;

  // Form fields
  formDate = '';
  formAccountId = '';
  formCategoryId = '';
  formValue = '';
  formDescription = '';
  formCashflowFlag = '';

  isSubmitting = false;
  submitError = '';
  showToast = false;
  toastMessage = '';

  isLoading = false;
  errorMessage = '';

  constructor(
    private accountService: AccountService,
    private categoryService: CategoryService,
    private transactionService: TransactionService,
    private transferService: TransferService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.generateCalendar();
    this.loadMonthlyData();
    this.loadAccounts();
    this.loadCategories();
  }

  get currentMonthName(): string {
    return this.currentMonth.toLocaleString('id-ID', { month: 'long', year: 'numeric' });
  }

  previousMonth(): void {
    this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth() - 1, 1);
    this.generateCalendar();
    this.closeDateDialog();
    this.loadMonthlyData();
  }

  nextMonth(): void {
    this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth() + 1, 1);
    this.generateCalendar();
    this.closeDateDialog();
    this.loadMonthlyData();
  }

  generateCalendar(): void {
    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const today = new Date();
    
    this.calendarDays = [];
    
    // Days from previous month
    const startDay = firstDay.getDay();
    for (let i = startDay - 1; i >= 0; i--) {
      const date = new Date(year, month, -i);
      this.calendarDays.push({
        date,
        isCurrentMonth: false,
        isToday: false,
        hasTransaction: false
      });
    }
    
    // Days of current month
    for (let i = 1; i <= lastDay.getDate(); i++) {
      const date = new Date(year, month, i);
      const isToday = date.toDateString() === today.toDateString();
      this.calendarDays.push({
        date,
        isCurrentMonth: true,
        isToday,
        hasTransaction: false
      });
    }
    
    // Days from next month
    const remaining = 42 - this.calendarDays.length;
    for (let i = 1; i <= remaining; i++) {
      const date = new Date(year, month + 1, i);
      this.calendarDays.push({
        date,
        isCurrentMonth: false,
        isToday: false,
        hasTransaction: false
      });
    }
  }

  selectDate(date: Date): void {
    this.selectedDate = date;
    this.loadDailyCashForDate(date);
    this.showDateDialog = true;
  }

  loadAccounts(): void {
    this.accountService.getAll().subscribe({
      next: (data: Account[]) => {
        this.accounts = data.sort((a: Account, b: Account) =>
          a.accountName.localeCompare(b.accountName)
        );
      }
    });
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data: Category[]) => {
        // Filter categories with categoryType and sort alphabetically
        this.categories = data
          .filter((c: Category) => c.categoryType)
          .sort((a: Category, b: Category) => a.categoryName.localeCompare(b.categoryName));
      }
    });
  }

  loadMonthlyData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth() + 1;

    forkJoin({
      dailyCash: this.transactionService.getAll(year, month),
      transfers: this.transferService.getAll(year, month)
    }).subscribe({
      next: ({ dailyCash, transfers }) => {
        this.dailyCashList = dailyCash;
        this.transferList = transfers;
        this.updateCalendarHasTransaction();

        if (this.selectedDate) {
          this.loadDailyCashForDate(this.selectedDate);
        }

        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Gagal memuat data transaksi';
        this.isLoading = false;
        console.error('Error loading monthly data:', err);
      }
    });
  }

  updateCalendarHasTransaction(): void {
    const dateMap = new Map<string, boolean>();
    this.dailyCashList.forEach(dc => {
      if (dc.transactionDate) {
        const dateStr = new Date(dc.transactionDate).toDateString();
        dateMap.set(dateStr, true);
      }
    });
    
    this.calendarDays.forEach(day => {
      day.hasTransaction = dateMap.has(day.date.toDateString());
    });
  }

  loadDailyCashForDate(date: Date): void {
    this.dailyCashForSelectedDate = this.dailyCashList.filter(dc => {
      if (!dc.transactionDate) return false;
      return new Date(dc.transactionDate).toDateString() === date.toDateString();
    });

    this.transferForSelectedDate = this.transferList.filter(transfer => {
      if (!transfer.transactionDate) return false;
      return new Date(transfer.transactionDate).toDateString() === date.toDateString();
    });
  }

  getAccountNameById(accountId?: string): string {
    if (!accountId) {
      return '-';
    }

    const account = this.accounts.find(a => a.id === accountId);
    return account?.accountName || '-';
  }

  goToTransferEdit(transfer: Transfer): void {
    if (!transfer.id) {
      return;
    }

    this.closeDateDialog();
    this.router.navigate(['/transfer'], {
      queryParams: { editId: transfer.id }
    });
  }

  closeDateDialog(): void {
    this.showDateDialog = false;
  }

  formatDateForInput(date: Date): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  parseFormattedNumber(value: string): number {
    return parseFloat(value.replace(/\./g, ''));
  }

  formatNumber(value: number): string {
    return new Intl.NumberFormat('id-ID').format(value || 0);
  }

  openFormModal(dc?: DailyCash): void {
    if (dc) {
      this.isEditMode = true;
      this.selectedDailyCash = dc;
      this.formDate = this.formatDateForInput(new Date(dc.transactionDate!));
      this.formAccountId = dc.account?.id || '';
      this.formCategoryId = dc.category?.id || '';
      this.formValue = this.formatNumber(dc.value || 0);
      this.formDescription = dc.description || '';
      this.formCashflowFlag = dc.cashflowFlag || '';
    } else {
      this.isEditMode = false;
      this.selectedDailyCash = null;
      this.formDate = this.selectedDate ? this.formatDateForInput(this.selectedDate) : this.formatDateForInput(new Date());
      this.formAccountId = '';
      this.formCategoryId = '';
      this.formValue = '';
      this.formDescription = '';
      this.formCashflowFlag = '';
    }
    this.submitError = '';
    this.showFormModal = true;
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.submitError = '';
  }

  onCategoryChange(): void {
    const category = this.categories.find(c => c.id === this.formCategoryId);
    if (category) {
      this.formCashflowFlag = category.cashFlowFlag || '';
    }
  }

  onSubmit(): void {
    if (!this.formAccountId || !this.formCategoryId || !this.formValue) {
      this.submitError = 'Mohon lengkapi semua field wajib';
      return;
    }

    const payload: any = {
      transactionDate: new Date(this.formDate),
      account: { id: this.formAccountId },
      category: { id: this.formCategoryId },
      transactionCode: { transactionCode: 'TR01' },
      value: this.parseFormattedNumber(this.formValue),
      cashflowFlag: this.formCashflowFlag,
      description: this.formDescription
    };

    this.isSubmitting = true;

    if (this.isEditMode && this.selectedDailyCash) {
      this.transactionService.update(this.selectedDailyCash.id, payload).subscribe({
        next: () => this.handleSuccess('Transaksi berhasil diperbarui'),
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal memperbarui transaksi';
        }
      });
    } else {
      this.transactionService.create(payload).subscribe({
        next: () => this.handleSuccess('Transaksi berhasil ditambahkan'),
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal menambahkan transaksi';
        }
      });
    }
  }

  handleSuccess(message: string): void {
    this.isSubmitting = false;
    this.closeFormModal();
    this.closeDateDialog();
    this.loadMonthlyData();
    this.showToastMessage(message);
  }

  deleteDailyCash(): void {
    if (!this.selectedDailyCash || !confirm('Yakin ingin menghapus transaksi ini?')) {
      return;
    }

    this.isSubmitting = true;
    this.transactionService.delete(this.selectedDailyCash.id).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.closeFormModal();
        this.closeDateDialog();
        this.loadMonthlyData();
        this.showToastMessage('Transaksi berhasil dihapus');
      },
      error: (err) => {
        this.isSubmitting = false;
        this.submitError = err.error?.message || 'Gagal menghapus transaksi';
      }
    });
  }

  showToastMessage(message: string): void {
    this.toastMessage = message;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
