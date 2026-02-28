import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BalanceService } from '../../core/services/balance.service';
import { AccountBalance } from '../../core/models/balance.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  balances: AccountBalance[] = [];
  totalBalance = 0;
  showBalanceDialog = false;
  isLoadingBalances = false;

  constructor(private balanceService: BalanceService) {}

  ngOnInit(): void {
    this.loadBalances();
  }

  loadBalances(): void {
    this.isLoadingBalances = true;
    this.balanceService.getAll().subscribe({
      next: (data) => {
        this.balances = data;
        this.totalBalance = data.reduce((sum, b) => sum + (b.value || 0), 0);
        this.isLoadingBalances = false;
      },
      error: () => {
        this.isLoadingBalances = false;
      }
    });
  }

  openBalanceDialog(): void {
    this.loadBalances();
    this.showBalanceDialog = true;
  }

  closeBalanceDialog(): void {
    this.showBalanceDialog = false;
  }

  formatNumber(value: number): string {
    return new Intl.NumberFormat('id-ID').format(value || 0);
  }
}
