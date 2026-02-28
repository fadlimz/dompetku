import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  userName: string = '';
  fullName: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    // Validation
    if (!this.userName || !this.fullName || !this.email || !this.password) {
      this.errorMessage = 'Semua field harus diisi';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Password tidak cocok';
      return;
    }

    if (this.password.length < 6) {
      this.errorMessage = 'Password minimal 6 karakter';
      return;
    }

    this.isLoading = true;

    this.authService.register({
      userName: this.userName,
      fullName: this.fullName,
      email: this.email,
      password: this.password
    })
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Registrasi berhasil! Silakan login.';
          setTimeout(() => {
            this.router.navigate(['/auth/login']);
          }, 2000);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Registrasi gagal. Silakan coba lagi.';
        }
      });
  }
}
