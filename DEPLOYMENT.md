# Deployment Guide - Render + Supabase

Panduan lengkap deploy aplikasi Dompetku ke **Render** dengan database **Supabase**.

> ✅ **Update Terbaru**: CORS sudah dikonfigurasi untuk allow semua origin (penting untuk frontend terpisah).

---

## 📋 Prerequisites

1. **GitHub Account** - Code harus di-push ke GitHub
2. **Supabase Account** - Untuk database PostgreSQL
3. **Render Account** - Untuk hosting aplikasi

---

## 🚀 Langkah 1: Setup Supabase (Database)

### 1.1 Buat Project Baru
1. Login ke [supabase.com](https://supabase.com)
2. Klik **"New Project"**
3. Isi detail:
   - **Name**: `dompetku`
   - **Database Password**: Simpan password ini (akan dipakai di Render)
   - **Region**: Pilih yang terdekat (Singapore/Tokyo untuk latency rendah dari Indonesia)
4. Klik **"Create new project"** (tunggu ~2 menit)

### 1.2 Dapatkan Connection String
1. Di dashboard Supabase, masuk ke **Settings** → **Database**
2. Copy **Connection String** (Pooler mode)
   - Format: `postgresql://postgres.xxx:[PASSWORD]@aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres`
3. Simpan untuk langkah berikutnya

### 1.3 Setup Database Schema
1. Di dashboard Supabase, masuk ke **SQL Editor**
2. Klik **"New Query"**
3. Copy-paste isi file `schema.sql` dari project ini
4. Klik **"Run"** untuk execute

> ✅ Database siap!

---

## 🚀 Langkah 2: Push Code ke GitHub

Jika belum di GitHub:

```bash
# Initialize git (jika belum)
git init
git add .
git commit -m "Initial commit"

# Add remote repository (ganti dengan URL GitHub kamu)
git remote add origin https://github.com/USERNAME/dompetku.git

# Push ke GitHub
git branch -M main
git push -u origin main
```

---

## 🚀 Langkah 3: Deploy ke Render

### 3.1 Buat Web Service Baru

1. Login ke [render.com](https://render.com)
2. Klik **"New +"** → **"Web Service"**
3. Connect GitHub account (jika belum)
4. Pilih repository **`dompetku`**
5. Klik **"Connect"**

### 3.2 Konfigurasi Web Service

Isi form dengan detail berikut:

| Field | Value |
|-------|-------|
| **Name** | `dompetku` |
| **Region** | `Singapore` (atau terdekat) |
| **Branch** | `main` |
| **Root Directory** | *(kosongkan)* |
| **Runtime** | `Docker` |
| **Build Command** | *(kosongkan - sudah ada Dockerfile)* |
| **Start Command** | *(kosongkan - sudah ada Dockerfile)* |
| **Instance Type** | `Free` |

### 3.3 Setup Environment Variables

Klik **"Advanced"** → **"Add Environment Variable"**, tambahkan:

| Key | Value |
|-----|-------|
| `SPRING_DATASOURCE_URL` | Connection string dari Supabase (langkah 1.2) |
| `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Password database dari Supabase |
| `JWT_SECRET` | Generate random string (contoh: `dompetku-secret-key-2024-xyz123`) |

> ⚠️ **Penting**: Pastikan connection string Supabase menggunakan **Pooler mode** (port 6543), bukan direct connection (port 5432).

### 3.4 Deploy

1. Klik **"Create Web Service"**
2. Render akan mulai build (~3-5 menit)
3. Setelah selesai, aplikasi akan otomatis deploy

---

## 🚀 Langkah 4: Verifikasi Deployment

### 4.1 Cek Log
1. Di dashboard Render, klik service `dompetku`
2. Masuk ke tab **"Logs"**
3. Pastikan tidak ada error

### 4.2 Test Endpoint
1. Copy URL service (contoh: `https://dompetku.onrender.com`)
2. Test endpoint:
   - `https://dompetku.onrender.com/hello` → Harus return "Hello World from Dompetku API!"
   - `https://dompetku.onrender.com/api/auth/login` → Test login

---

## 🔧 Troubleshooting

### Aplikasi tidak start
- Cek **Logs** di Render dashboard
- Pastikan environment variables benar
- Verify connection string Supabase (gunakan Pooler mode)

### Database connection error
- Pastikan schema.sql sudah di-run di Supabase
- Cek firewall settings di Supabase (harus allow all IPs untuk Render)
- Di Supabase: **Settings** → **Database** → **Configuration** → Pastikan port 6543 accessible

### Sleep Mode (Free Tier)
Render free tier akan **sleep setelah 15 menit idle**. Solusi:

1. **UptimeRobot** (Recommended):
   - Daftar di [uptimerobot.com](https://uptimerobot.com)
   - Buat monitor baru → HTTP(s)
   - URL: `https://dompetku.onrender.com/hello`
   - Interval: 5 minutes
   
2. Atau upgrade ke Render **Starter** ($7/bulan) untuk disable sleep

---

## 🔐 Security Best Practices

1. **JWT_SECRET**: Gunakan random string yang kuat
   ```bash
   # Generate random secret
   openssl rand -base64 32
   ```

2. **Database Password**: Jangan commit ke Git
   - Sudah aman karena pakai environment variables

3. **CORS**: Update `SecurityConfig` jika perlu restrict domain

---

## 📊 Monitoring

### Render Dashboard
- **Logs**: Real-time logs
- **Metrics**: CPU, Memory, Request count
- **Events**: Deploy history & incidents

### Supabase Dashboard
- **Database**: Query count, storage usage
- **Logs**: Database query logs
- **Settings**: Connection pool stats

---

## 💰 Cost Estimate

| Service | Plan | Cost |
|---------|------|------|
| **Render** | Free | $0/bulan |
| **Supabase** | Free | $0/bulan |
| **Total** | | **$0/bulan** |

### Limits

**Render Free:**
- 512MB RAM
- 0.1 CPU
- Sleep setelah 15 menit idle
- 750 hours/bulan (cukup untuk 1 service 24/7)

**Supabase Free:**
- 500MB database
- 50,000 monthly active users
- Unlimited API requests

---

## 🔄 Update Deployment

Setiap kali push ke GitHub:

```bash
git add .
git commit -m "Fix: your changes"
git push origin main
```

Render akan **auto-deploy** dalam 1-2 menit.

---

## 🎉 Selesai!

Aplikasi Dompetku sudah live di Render dengan database Supabase.

**Next Steps:**
1. Test registrasi user baru
2. Test CRUD transaksi
3. Setup custom domain (optional)
4. Setup monitoring alerts (optional)

---

## 📞 Support

Jika ada masalah:
1. Cek logs di Render dashboard
2. Test database connection lokal dengan credential Supabase
3. Pastikan semua environment variables sudah benar

Good luck! 🚀
