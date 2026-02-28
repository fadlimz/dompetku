import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../core/services/account.service';
import { AuthService } from '../../core/services/auth.service';
import { Account } from '../../core/models/account.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent implements OnInit {
  accounts: Account[] = [];
  isLoading = false;
  errorMessage = '';
  
  // Modal state
  showModal = false;
  isSubmitting = false;
  submitError = '';
  
  // Edit mode
  selectedAccount: Account | null = null;
  isEditMode = false;
  
  // Form fields
  accountName = '';
  activeFlag = true;
  
  // Toast state
  showToast = false;
  toastMessage = '';

  constructor(
    private accountService: AccountService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.accountService.getAll().subscribe({
      next: (data) => {
        this.accounts = data.sort((a, b) => 
          a.accountName.localeCompare(b.accountName)
        );
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Gagal memuat data akun';
        this.isLoading = false;
        console.error('Error loading accounts:', err);
      }
    });
  }

  openModal(account?: Account): void {
    this.showModal = true;
    this.submitError = '';
    
    if (account) {
      this.isEditMode = true;
      this.selectedAccount = account;
      this.accountName = account.accountName;
      this.activeFlag = account.activeFlag === 'Active';
    } else {
      this.isEditMode = false;
      this.selectedAccount = null;
      this.accountName = '';
      this.activeFlag = true;
    }
  }

  closeModal(): void {
    this.showModal = false;
    this.submitError = '';
    this.selectedAccount = null;
    this.isEditMode = false;
  }

  onSubmit(): void {
    if (!this.accountName.trim()) {
      this.submitError = 'Nama akun harus diisi';
      return;
    }

    this.isSubmitting = true;
    this.submitError = '';

    const accountData = {
      accountName: this.accountName.trim(),
      activeFlag: this.activeFlag ? 'Active' : 'Inactive'
    };

    if (this.isEditMode && this.selectedAccount) {
      this.accountService.update(this.selectedAccount.id, accountData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.closeModal();
          this.loadAccounts();
          this.showToastMessage('Akun berhasil diperbarui');
        },
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal memperbarui akun';
          console.error('Error updating account:', err);
        }
      });
    } else {
      this.accountService.create(accountData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.closeModal();
          this.loadAccounts();
          this.showToastMessage('Akun berhasil ditambahkan');
        },
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal menambahkan akun';
          console.error('Error creating account:', err);
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

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
