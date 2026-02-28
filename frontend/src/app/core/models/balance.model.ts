import { BaseEntity } from './base.model';

export interface AccountBalance extends BaseEntity {
  accountId: string;
  accountCode: string;
  accountName: string;
  value: number;
}
