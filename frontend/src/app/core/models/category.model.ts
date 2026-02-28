import { BaseEntity } from './base.model';

export interface Category extends BaseEntity {
  categoryCode: string;
  categoryName: string;
  cashFlowFlag: string;
  activeFlag: string;
}
