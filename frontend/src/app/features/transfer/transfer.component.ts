import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from '../../core/services/account.service';
import { TransferService } from '../../core/services/transfer.service';
import { TransactionCodeService } from '../../core/services/transaction-code.service';
import { Account } from '../../core/models/account.model';
import { Transfer } from '../../core/models/transfer.model';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.scss']
})
export class TransferComponent implements OnInit {
  accounts: Account[] = [];
  tr02TransactionCodeId: string | null = null;
  isEditMode = false;
  selectedTransferId: string | null = null;
  
  // Form fields
  formDate = '';
  formFromAccountId = '';
  formToAccountId = '';
  formValue = '';
  formDescription = '';
  
  isSubmitting = false;
  submitError = '';
  showToast = false;
  toastMessage = '';

  constructor(
    private accountService: AccountService,
    private transferService: TransferService,
    private transactionCodeService: TransactionCodeService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
    this.loadTransactionCode();
    this.formDate = this.formatDateForInput(new Date());

    this.route.queryParamMap.subscribe(params => {
      const editId = params.get('editId');
      if (editId) {
        this.enterEditMode(editId);
        return;
      }

      this.enterCreateMode();
    });
  }

  loadAccounts(): void {
    this.accountService.getAll().subscribe({
      next: (data: Account[]) => {
        this.accounts = data
          .filter((a: Account) => a.activeFlag === 'Active')
          .sort((a: Account, b: Account) => a.accountName.localeCompare(b.accountName));
      },
      error: (err) => {
        console.error('Error loading accounts:', err);
      }
    });
  }

  loadTransactionCode(): void {
    this.transactionCodeService.getAll('TR02').subscribe({
      next: (data) => {
        const tr02 = data.find(tc => tc.transactionCode === 'TR02');
        if (tr02) {
          this.tr02TransactionCodeId = tr02.id;
        } else {
          console.error('Transaction code TR02 not found');
        }
      },
      error: (err) => {
        console.error('Error loading transaction code:', err);
      }
    });
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

  enterEditMode(id: string): void {
    this.submitError = '';
    this.isEditMode = true;
    this.selectedTransferId = id;
    this.loadTransferById(id);
  }

  enterCreateMode(): void {
    this.isEditMode = false;
    this.selectedTransferId = null;
    this.formDate = this.formatDateForInput(new Date());
    this.formFromAccountId = '';
    this.formToAccountId = '';
    this.formValue = '';
    this.formDescription = '';
    this.submitError = '';
  }

  loadTransferById(id: string): void {
    this.transferService.getById(id).subscribe({
      next: (transfer) => {
        this.formDate = transfer.transactionDate
          ? this.formatDateForInput(new Date(transfer.transactionDate))
          : this.formatDateForInput(new Date());
        this.formFromAccountId = transfer.fromAccountId || transfer.accountFrom?.id || '';
        this.formToAccountId = transfer.toAccountId || transfer.accountTo?.id || '';
        this.formValue = this.formatNumber(transfer.value || 0);
        this.formDescription = transfer.description || '';
      },
      error: (err) => {
        this.submitError = err.error?.message || 'Gagal memuat data transfer';
      }
    });
  }

  onSubmit(): void {
    // Reset error
    this.submitError = '';

    // Validation
    if (!this.formDate) {
      this.submitError = 'Tanggal harus diisi';
      return;
    }

    if (!this.formFromAccountId) {
      this.submitError = 'Akun asal harus dipilih';
      return;
    }

    if (!this.formToAccountId) {
      this.submitError = 'Akun tujuan harus dipilih';
      return;
    }

    if (this.formFromAccountId === this.formToAccountId) {
      this.submitError = 'Akun asal dan tujuan tidak boleh sama';
      return;
    }

    if (!this.formValue || this.parseFormattedNumber(this.formValue) <= 0) {
      this.submitError = 'Nilai transfer harus lebih besar dari 0';
      return;
    }

    if (!this.tr02TransactionCodeId) {
      this.submitError = 'Kode transaksi tidak ditemukan';
      return;
    }

    const payload: Partial<Transfer> = {
      transactionDate: new Date(this.formDate),
      transactionCodeId: this.tr02TransactionCodeId,
      fromAccountId: this.formFromAccountId,
      toAccountId: this.formToAccountId,
      value: this.parseFormattedNumber(this.formValue),
      description: this.formDescription
    };

    this.isSubmitting = true;

    if (this.isEditMode && this.selectedTransferId) {
      this.transferService.update(this.selectedTransferId, payload).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.handleSuccess('Transfer berhasil diperbarui');
        },
        error: (err) => {
          this.isSubmitting = false;
          this.submitError = err.error?.message || 'Gagal memperbarui transfer';
        }
      });
      return;
    }

    this.transferService.create(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.handleSuccess('Transfer berhasil ditambahkan');
      },
      error: (err) => {
        this.isSubmitting = false;
        this.submitError = err.error?.message || 'Gagal melakukan transfer';
      }
    });
  }

  handleSuccess(message: string): void {
    if (this.isEditMode && this.selectedTransferId) {
      this.loadTransferById(this.selectedTransferId);
    } else {
      this.enterCreateMode();
    }

    this.showToastMessage(message);
  }

  deleteTransfer(): void {
    if (!this.isEditMode || !this.selectedTransferId || !confirm('Yakin ingin menghapus transfer ini?')) {
      return;
    }

    this.isSubmitting = true;
    this.submitError = '';

    this.transferService.delete(this.selectedTransferId).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.showToastMessage('Transfer berhasil dihapus');
        this.router.navigate(['/transfer']);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.submitError = err.error?.message || 'Gagal menghapus transfer';
      }
    });
  }

  showToastMessage(message: string): void {
    this.toastMessage = message;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }
}
