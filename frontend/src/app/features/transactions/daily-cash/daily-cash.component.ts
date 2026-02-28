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

  constructor(
    private accountService: AccountService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
    this.loadCategories();
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
