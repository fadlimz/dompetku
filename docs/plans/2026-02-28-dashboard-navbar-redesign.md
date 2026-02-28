# Dashboard & Navbar Redesign Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Mengubah layout dashboard dan navbar: memindahkan logout ke dropdown di username, menghapus card transaksi, dan mengubah urutan menu navigasi.

**Architecture:** Perubahan fokus pada file template HTML dashboard component dengan memodifikasi struktur grid untuk cards dan navigation menu. Logout dipindahkan menjadi dropdown menu.

**Tech Stack:** Angular 19, TailwindCSS, TypeScript

---

### Task 1: Ubah Top Bar - Tambahkan Dropdown Logout

**Files:**
- Modify: `frontend/src/app/features/dashboard/dashboard.component.ts`
- Modify: `frontend/src/app/features/dashboard/dashboard.component.html`

**Step 1: Modifikasi TypeScript - Tambahkan state untuk dropdown**

Buka `dashboard.component.ts` dan tambahkan property `isDropdownOpen` serta method toggle:

```typescript
export class DashboardComponent implements OnInit {
  userName: string = 'User';
  isDropdownOpen: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user && user.userName) {
        this.userName = user.userName;
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  closeDropdown(): void {
    this.isDropdownOpen = false;
  }
}
```

**Step 2: Modifikasi HTML - Ubah top bar dengan dropdown**

Ganti section header di `dashboard.component.html` (baris 48-53):

```html
<!-- Top Bar -->
<header class="bg-white shadow-sm px-4 md:px-8 py-4 flex justify-between items-center">
  <h2 class="text-lg md:text-xl font-semibold text-gray-800">Dashboard</h2>
  
  <!-- User Dropdown -->
  <div class="relative">
    <button 
      (click)="toggleDropdown()" 
      class="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
    >
      <span>{{ userName }}</span>
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
      </svg>
    </button>
    
    <!-- Dropdown Menu -->
    <div 
      *ngIf="isDropdownOpen" 
      class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-100 py-2 z-50"
    >
      <button 
        (click)="onLogout(); closeDropdown()" 
        class="flex items-center gap-3 px-4 py-2 w-full text-gray-600 hover:bg-red-50 hover:text-red-600 transition-colors"
      >
        <span class="text-lg">🚪</span>
        <span>Logout</span>
      </button>
    </div>
  </div>
</header>
```

**Step 3: Verifikasi dengan build**

Run: `cd frontend && npm run build`
Expected: Build successful tanpa error

**Step 4: Commit**

```bash
git add frontend/src/app/features/dashboard/dashboard.component.ts frontend/src/app/features/dashboard/dashboard.component.html
git commit -m "feat: add user dropdown with logout in top bar"
```

---

### Task 2: Hapus Logout dari Sidebar

**Files:**
- Modify: `frontend/src/app/features/dashboard/dashboard.component.html`

**Step 1: Hapus tombol logout dari sidebar**

Hapus section div dengan class "p-4 border-t border-gray-100" yang berisi tombol logout (baris 37-42):

```html
<!-- HAPUS INI -->
<div class="p-4 border-t border-gray-100">
  <button (click)="onLogout()" class="flex items-center gap-3 px-4 py-3 w-full rounded-lg text-gray-600 hover:bg-red-50 hover:text-red-600 transition-colors">
    <span class="text-xl">🚪</span>
    <span>Logout</span>
  </button>
</div>
```

**Step 2: Commit**

```bash
git add frontend/src/app/features/dashboard/dashboard.component.html
git commit -m "feat: remove logout button from sidebar"
```

---

### Task 3: Hapus Logout dari Bottom Navigation Mobile

**Files:**
- Modify: `frontend/src/app/features/dashboard/dashboard.component.html`

**Step 1: Hapus tombol logout dari bottom nav**

Hapus button logout dari nav mobile (baris 145-148):

```html
<!-- HAPUS INI -->
<button (click)="onLogout()" class="flex flex-col items-center gap-1 text-gray-400 hover:text-red-500 transition-colors">
  <span class="text-xl">🚪</span>
  <span class="text-xs">Logout</span>
</button>
```

**Step 2: Commit**

```bash
git add frontend/src/app/features/dashboard/dashboard.component.html
git commit -m "feat: remove logout from mobile bottom navigation"
```

---

### Task 4: Ubah Urutan Navigation Menu

**Files:**
- Modify: `frontend/src/app/features/dashboard/dashboard.component.html`

**Step 1: Ubah urutan menu di sidebar**

Ubah urutan `<ul class="space-y-2">` di sidebar (baris 9-34):

```html
<ul class="space-y-2">
  <li>
    <a routerLink="/dashboard" routerLinkActive="bg-blue-50 text-blue-600" class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 hover:bg-blue-50 hover:text-blue-600 transition-colors">
      <span class="text-xl">📊</span>
      <span>Dashboard</span>
    </a>
  </li>
  <li>
    <a routerLink="/transactions" routerLinkActive="bg-blue-50 text-blue-600" class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 hover:bg-blue-50 hover:text-blue-600 transition-colors">
      <span class="text-xl">💰</span>
      <span>Transaksi</span>
    </a>
  </li>
  <li>
    <!-- Menu Transfer Baru - untuk AccountBalance -->
    <a routerLink="/accounts" routerLinkActive="bg-blue-50 text-blue-600" class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 hover:bg-blue-50 hover:text-blue-600 transition-colors">
      <span class="text-xl">🔄</span>
      <span>Transfer</span>
    </a>
  </li>
  <li>
    <a routerLink="/accounts" routerLinkActive="bg-blue-50 text-blue-600" class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 hover:bg-blue-50 hover:text-blue-600 transition-colors">
      <span class="text-xl">💳</span>
      <span>Akun</span>
    </a>
  </li>
  <li>
    <a routerLink="/categories" routerLinkActive="bg-blue-50 text-blue-600" class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 hover:bg-blue-50 hover:text-blue-600 transition-colors">
      <span class="text-xl">🏷️</span>
      <span>Kategori</span>
    </a>
  </li>
</ul>
```

**Step 2: Ubah urutan menu di bottom navigation**

Ubah urutan menu di bottom nav (baris 128-144):

```html
<nav class="md:hidden fixed bottom-0 left-0 right-0 bg-white shadow-[0_-2px_10px_rgba(0,0,0,0.1)] flex justify-around py-3 z-50">
  <a routerLink="/dashboard" routerLinkActive="text-blue-500" class="flex flex-col items-center gap-1 text-gray-400 hover:text-blue-500 transition-colors">
    <span class="text-xl">📊</span>
    <span class="text-xs">Dashboard</span>
  </a>
  <a routerLink="/transactions" routerLinkActive="text-blue-500" class="flex flex-col items-center gap-1 text-gray-400 hover:text-blue-500 transition-colors">
    <span class="text-xl">💰</span>
    <span class="text-xs">Transaksi</span>
  </a>
  <!-- Menu Transfer Baru -->
  <a routerLink="/accounts" routerLinkActive="text-blue-500" class="flex flex-col items-center gap-1 text-gray-400 hover:text-blue-500 transition-colors">
    <span class="text-xl">🔄</span>
    <span class="text-xs">Transfer</span>
  </a>
  <a routerLink="/accounts" routerLinkActive="text-blue-500" class="flex flex-col items-center gap-1 text-gray-400 hover:text-blue-500 transition-colors">
    <span class="text-xl">💳</span>
    <span class="text-xs">Akun</span>
  </a>
  <a routerLink="/categories" routerLinkActive="text-blue-500" class="flex flex-col items-center gap-1 text-gray-400 hover:text-blue-500 transition-colors">
    <span class="text-xl">🏷️</span>
    <span class="text-xs">Kategori</span>
  </a>
</nav>
```

**Step 3: Commit**

```bash
git add frontend/src/app/features/dashboard/dashboard.component.html
git commit -m "feat: reorder navigation menu - Transaksi, Transfer, Akun, Kategori"
```

---

### Task 5: Ubah Stats Cards - Hapus Transaksi, Ubah Urutan, Saldo Colspan

**Files:**
- Modify: `frontend/src/app/features/dashboard/dashboard.component.html`

**Step 1: Modifikasi grid cards**

Ubah section Stats Grid (baris 64-104) menjadi 3 cards dengan urutan Saldo, Pemasukan, Pengeluaran dan Saldo colspan=2 di mobile:

```html
<!-- Stats Grid -->
<div class="grid grid-cols-2 md:grid-cols-3 gap-3 md:gap-6 mb-4 md:mb-6">
  <!-- Saldo - colspan 2 di mobile -->
  <div class="col-span-2 md:col-span-1 bg-white rounded-2xl shadow-sm p-3 md:p-6">
    <div class="flex items-center gap-2 md:gap-4">
      <div class="w-10 h-10 md:w-12 md:h-12 bg-blue-100 rounded-lg md:rounded-xl flex items-center justify-center text-lg md:text-2xl">💵</div>
      <div>
        <h4 class="text-xs md:text-sm text-gray-500">Saldo</h4>
        <p class="text-base md:text-xl font-bold text-blue-600">Rp 0</p>
      </div>
    </div>
  </div>

  <!-- Pemasukan -->
  <div class="bg-white rounded-2xl shadow-sm p-3 md:p-6">
    <div class="flex items-center gap-2 md:gap-4">
      <div class="w-10 h-10 md:w-12 md:h-12 bg-green-100 rounded-lg md:rounded-xl flex items-center justify-center text-lg md:text-2xl">📈</div>
      <div>
        <h4 class="text-xs md:text-sm text-gray-500">Pemasukan</h4>
        <p class="text-base md:text-xl font-bold text-green-600">Rp 0</p>
      </div>
    </div>
  </div>

  <!-- Pengeluaran -->
  <div class="bg-white rounded-2xl shadow-sm p-3 md:p-6">
    <div class="flex items-center gap-2 md:gap-4">
      <div class="w-10 h-10 md:w-12 md:h-12 bg-red-100 rounded-lg md:rounded-xl flex items-center justify-center text-lg md:text-2xl">📉</div>
      <div>
        <h4 class="text-xs md:text-sm text-gray-500">Pengeluaran</h4>
        <p class="text-base md:text-xl font-bold text-red-600">Rp 0</p>
      </div>
    </div>
  </div>
</div>
```

Perubahan:
- Grid dari `grid-cols-2 md:grid-cols-2 lg:grid-cols-4` menjadi `grid grid-cols-2 md:grid-cols-3`
- Card Saldo menjadi `col-span-2 md:col-span-1` (2 kolom di mobile, 1 kolom di desktop)
- Urutan diubah: Saldo, Pemasukan, Pengeluaran
- Card Transaksi dihapus

**Step 2: Commit**

```bash
git add frontend/src/app/features/dashboard/dashboard.component.html
git commit -m "feat: update dashboard cards - remove transaksi, reorder to Saldo, Pemasukan, Pengeluaran"
```

---

### Task 6: Verifikasi Akhir - Build dan Test

**Step 1: Run build**

Run: `cd frontend && npm run build`
Expected: Build successful

**Step 2: Verifikasi perubahan visual**

Buka browser dan periksa:
- Top bar username punya dropdown logout
- Sidebar tidak ada tombol logout
- Bottom nav tidak ada logout
- Urutan menu: Dashboard, Transaksi, Transfer, Akun, Kategori
- Cards: 3 cards (Saldo, Pemasukan, Pengeluaran)
- Mobile: Saldo occupies full row (2 columns)

**Step 3: Final commit jika ada perubahan tambahan**

```bash
git add .
git commit -m "feat: complete dashboard and navbar redesign"
```

---

## Ringkasan Perubahan

| Komponen | Perubahan |
|----------|-----------|
| Top Bar | Username sekarang punya dropdown dengan opsi Logout |
| Sidebar | Logout dihapus, urutan menu: Dashboard, Transaksi, Transfer, Akun, Kategori |
| Bottom Nav | Logout dihapus, urutan menu sama |
| Dashboard Cards | 3 cards: Saldo (colspan=2 mobile), Pemasukan, Pengeluaran |
