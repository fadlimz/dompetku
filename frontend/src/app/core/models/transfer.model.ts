import { BaseEntity } from './base.model';

export interface Transfer extends BaseEntity {
  transactionCodeId?: string;
  transactionNumber?: number;
  transactionDate?: Date;
  fromAccountId?: string;
  toAccountId?: string;
  value?: number;
  description?: string;
}
