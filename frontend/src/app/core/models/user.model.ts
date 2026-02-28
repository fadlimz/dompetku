import { BaseEntity } from './base.model';

export interface User extends BaseEntity {
  userName: string;
  fullName: string;
  email: string;
}

export interface UserDto extends User {
  password?: string;
}
