import { BaseEntity } from './base.model';

export interface Account extends BaseEntity {
  accountCode: string;
  accountName: string;
  activeFlag: string;
}
