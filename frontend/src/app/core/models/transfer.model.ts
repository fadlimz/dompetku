import { BaseEntity } from './base.model';
import { Account } from './account.model';

export interface Transfer extends BaseEntity {
  transactionCodeId?: string;
  transactionCode?: string;
  transactionNumber?: number;
  transactionDate?: Date;
  fromAccountId?: string;
  toAccountId?: string;
  accountFrom?: Account;
  accountTo?: Account;
  value?: number;
  description?: string;
  categoryType?: string;
}
