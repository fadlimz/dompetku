export interface BaseEntity {
  id: string;
  version: number;
  createdBy: string;
  createdTime: Date;
  modifiedBy: string;
  modifiedTime: Date;
}

export interface BaseDto extends BaseEntity {}
