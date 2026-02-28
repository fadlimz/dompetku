import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { CategoryService } from '../../../core/services/category.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { Account } from '../../../core/models/account.model';
import { Category } from '../../../core/models/category.model';
import { DailyCash } from '../../../core/models/transaction.model';

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
  selectedDate: Date | null = null;
  dailyCashForSelectedDate: DailyCash[] = [];
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

  constructor(
    private accountService: AccountService,
    private categoryService: CategoryService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    this.generateCalendar();
    this.loadDailyCash();
    this.loadAccounts();
    this.loadCategories();
  }

  get currentMonthName(): string {
    return this.currentMonth.toLocaleString('id-ID', { month: 'long', year: 'numeric' });
  }

  previousMonth(): void {
    this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth() - 1, 1);
    this.generateCalendar();
  }

  nextMonth(): void {
    this.currentMonth = new Date(this.currentMonth.getFullYear(), this.currentMonth.getMonth() + 1, 1);
    this.generateCalendar();
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
        this.categories = data
          .filter((c: Category) => c.activeFlag === 'Active')
          .sort((a: Category, b: Category) => a.categoryName.localeCompare(b.categoryName));
      }
    });
  }

  loadDailyCash(): void {
    this.transactionService.getAll().subscribe({
      next: (data) => {
        this.dailyCashList = data;
        this.updateCalendarHasTransaction();
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

  onAccountChange(): void {
    const account = this.accounts.find(a => a.id === this.formAccountId);
    if (account) {
      this.formCashflowFlag = account.cashflowFlag || '';
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
    this.loadDailyCash();
    this.showToastMessage(message);
  }

  showToastMessage(message: string): void {
    this.toastMessage = message;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
