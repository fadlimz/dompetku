import { BaseEntity } from './base.model';
import { Account } from './account.model';
import { Category } from './category.model';

export interface TransactionCode extends BaseEntity {
  transactionCode: string;
  transactionName: string;
  cashFlowFlag: string;
  activeFlag: string;
}

export interface DailyCash extends BaseEntity {
  transactionCode: TransactionCode;
  transactionNumber: number;
  transactionDate: Date;
  cashflowFlag: string;
  category: Category;
  account: Account;
  value: number;
  description: string;
}
