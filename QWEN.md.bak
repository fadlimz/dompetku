# Aturan Komunikasi
1. Gunakan bahasa Indonesia untuk semua komunikasi

# Dompetku - Aplikasi Manajemen Keuangan Pribadi

## Gambaran Umum Proyek

Dompetku adalah aplikasi manajemen keuangan pribadi yang dibangun dengan Spring Boot (Java 17) yang memungkinkan pengguna melacak transaksi keuangan mereka, mengelola akun, dan mengategorikan pengeluaran/pemasukan. Aplikasi ini menggunakan otentikasi dan otorisasi berbasis JWT, dengan SQLite sebagai database utama.

### Teknologi & Fitur Utama:
- **Framework Backend**: Spring Boot 3.5.0
- **Bahasa Pemrograman**: Java 17
- **Database**: SQLite (dengan Hibernate/JPA)
- **Otentikasi**: JWT (JSON Web Tokens)
- **Keamanan**: Spring Security
- **Sistem Build**: Gradle
- **ORM**: JPA/Hibernate
- **Arsitektur**: REST API dengan arsitektur berlapis (Controllers, Services, Entities)

### Model Domain:
Berdasarkan skema Prisma, aplikasi ini mengelola:
- **Pengguna**: Akun pengguna dengan otentikasi
- **Akun**: Akun keuangan dengan saldo
- **Kategori**: Kategori transaksi (Pemasukan/Pengeluaran)
- **Transaksi**: Arus kas harian dan transfer antar akun
- **Saldo**: Pelacakan saldo akun

## Membangun dan Menjalankan

### Prasyarat
- Java 17
- Gradle (wrapper disertakan)

### Perintah Build
```bash
# Membangun proyek
./gradlew build

# Menjalankan aplikasi
./gradlew bootRun

# Menjalankan tes
./gradlew test

# Membersihkan build
./gradlew clean
```

### Metode Jalankan Alternatif
```bash
# Mengompilasi dan menjalankan JAR
./gradlew bootJar
java -jar build/libs/dompetku-0.0.1-SNAPSHOT.jar
```

### Endpoint API
- `/hello` - Endpoint cek kesehatan dasar yang mengembalikan "Hello World from Dompetku API!"
- `/api/users` - Pembuatan pengguna (publik)
- `/api/auth/**` - Endpoint otentikasi (publik)
- Endpoint terotentikasi lainnya untuk mengelola keuangan

## Konvensi Pengembangan

### Aturan Coding & Arsitektur
1. **Layanan (Services)**:
   - **Satu Entity = Satu Service Class**: Jangan membuat Interface terpisah untuk Service. Buat langsung class concrete yang meng-extend `BaseService<T>`.
   - **Gunakan BaseService**: Manfaatkan method `findById`, `save`, `update`, `delete` dari `BaseService` sebisa mungkin untuk mengurangi duplikasi kode. Override hanya jika diperlukan (misalnya untuk validasi keamanan tambahan).
2. **Entity Creation/Update**:
   - Gunakan pendekatan **Setter** (`entity.setX(val)`) atau method `toEntity()` daripada Builder pattern untuk logika pembuatan atau pembaruan entity di dalam Service. Ini lebih disukai untuk konsistensi dengan gaya Java Beans/JPA.

3. **DTO Standardization**:
- Semua variabel state dalam DTO harus menggunakan access modifier **public**.
- Setiap kelas DTO harus memiliki metode berikut:
- `toEntity()`: Mengonversi DTO ke Entity baru.
- `static fromEntity(Entity entity)`: Mengonversi Entity ke DTO.
- `static toEntityList(List<Dto> dtos)`: Mengonversi list DTO ke list Entity.
- `static fromEntityList(List<Entity> entities)`: Mengonversi list Entity ke list DTO.
- **Konvensi Pemetaan (Mapping)**:
    - **Audit Fields**: Tetap gunakan `super.toEntity(Class)` dan `BaseDto.fromEntity(Class, entity)` untuk menangani field audit (`id`, `version`, `createdBy`, dsb) secara otomatis.
    - **Direct Assignment**: Pada metode `fromEntity`, gunakan akses langsung ke field DTO (`dto.field = entity.getField()`) karena field DTO bersifat `public`. Jangan gunakan setter untuk DTO jika tidak diperlukan.
    - **Entity Setters**: Pada metode `toEntity`, tetap gunakan setter (`entity.setField()`) karena field pada Entity bersifat `private`.
    - **Null Safety**: Selalu tambahkan *null check* pada parameter `entity` di awal metode `fromEntity`.
- Jangan gunakan manual mapping di Controller atau Service jika memungkinkan, gunakan metode konversi yang ada di DTO.

4. **String Handling**:
- Gunakan `StringUtil.isBlank(str)` dari package `com.fadlimz.dompetku.config` untuk pengecekan string `null`, kosong (`""`), atau hanya berisi spasi (`" "`). Jangan gunakan manual check seperti `str == null || str.isEmpty()`.
### Struktur Proyek
```
src/
├── main/
│   ├── java/com/fadlimz/dompetku/
│   │   ├── base/           # Kelas dasar, DTO, entitas
│   │   │   ├── dtos/       # Object Transfer Data
│   │   │   ├── entities/   # Kelas Entitas JPA
│   │   │   └── services/   # Antarmuka layanan dasar
│   │   ├── config/         # Kelas keamanan dan konfigurasi
│   │   ├── user/           # Fungsionalitas terkait pengguna
│   │   ├── DompetkuApplication.java  # Kelas aplikasi utama
│   │   └── HelloController.java      # Contoh controller
│   └── resources/
│       ├── application.properties    # Properti konfigurasi
│       ├── static/         # Sumber daya statis
│       └── templates/      # File template
```

### Keamanan
- Otentikasi berbasis JWT telah diterapkan
- Kata sandi dienkripsi menggunakan BCrypt
- Manajemen sesi tanpa status
- Endpoint publik: Registrasi dan otentikasi pengguna
- Endpoint terproteksi: Semua fitur manajemen keuangan lainnya

### Database
- Database SQLite disimpan sebagai file `dompetku.db`
- Pembaruan skema otomatis diaktifkan (`ddl-auto=update`)
- JPA/Hibernate dengan dialek SQLite khusus

### Pengujian
- JUnit 5 untuk pengujian unit/integrasi
- Starter pengujian Spring Boot standar disertakan
- File tes terletak di `src/test/java/`

## Konfigurasi Lingkungan

Aplikasi ini menggunakan konfigurasi berikut di `application.properties`:
- Koneksi database SQLite
- Pembaruan otomatis Hibernate DDL
- Logging SQL diaktifkan untuk debugging

## Catatan Arsitektur

Aplikasi ini mengikuti arsitektur Spring Boot khas dengan:
- Controller REST untuk endpoint API
- Layer layanan untuk logika bisnis
- Layer repositori untuk akses data
- Konfigurasi keamanan dengan filter JWT
- Kelas entitas yang dipetakan ke tabel database
- DTO untuk transfer data antar layer