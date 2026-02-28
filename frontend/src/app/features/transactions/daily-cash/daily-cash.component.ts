import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { CategoryService } from '../../../core/services/category.service';
import { Account } from '../../../core/models/account.model';
import { Category } from '../../../core/models/category.model';

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

  constructor(
    private accountService: AccountService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.generateCalendar();
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
    // Will be implemented in Task 4
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
}
