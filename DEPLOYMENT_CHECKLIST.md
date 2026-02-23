# ✅ Checklist Deployment - Render + Supabase

## Sebelum Deploy

- [ ] Code sudah di-push ke GitHub
- [ ] Sudah punya akun Supabase (supabase.com)
- [ ] Sudah punya akun Render (render.com)

---

## 1️⃣ Setup Supabase

- [ ] Login ke Supabase
- [ ] Buat project baru (`dompetku`)
- [ ] Simpan database password
- [ ] Pilih region Singapore/Tokyo
- [ ] Tunggu project selesai dibuat (~2 menit)
- [ ] Masuk ke **Settings** → **Database**
- [ ] Copy **Connection String** (Pooler mode, port 6543)
- [ ] Masuk ke **SQL Editor**
- [ ] Copy-paste isi `schema.sql` dari project
- [ ] Run SQL script
- [ ] ✅ Database siap!

---

## 2️⃣ Push ke GitHub (jika belum)

```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

- [ ] Code berhasil di-push ke GitHub

---

## 3️⃣ Deploy ke Render

- [ ] Login ke Render
- [ ] Klik **New +** → **Web Service**
- [ ] Connect GitHub
- [ ] Pilih repository `dompetku`
- [ ] Isi konfigurasi:
  - Name: `dompetku`
  - Region: Singapore
  - Branch: main
  - Runtime: Docker
  - Instance Type: Free
- [ ] Tambahkan Environment Variables:
  - [ ] `SPRING_DATASOURCE_URL` = (connection string dari Supabase)
  - [ ] `SPRING_DATASOURCE_USERNAME` = `postgres`
  - [ ] `SPRING_DATASOURCE_PASSWORD` = (password dari Supabase)
  - [ ] `JWT_SECRET` = (random string, contoh: `dompetku-secret-2024-xyz`)
- [ ] Klik **Create Web Service**
- [ ] Tunggu build selesai (~3-5 menit)

---

## 4️⃣ Testing

- [ ] Copy URL service (contoh: `https://dompetku.onrender.com`)
- [ ] Buka di browser: `https://dompetku.onrender.com/hello`
- [ ] Harus muncul: "Hello World from Dompetku API!"
- [ ] Test register user via API
- [ ] Test login via API

---

## 5️⃣ Setup Uptime Monitor (Optional - Anti Sleep)

- [ ] Daftar di [UptimeRobot](https://uptimerobot.com)
- [ ] Buat monitor baru
- [ ] Pilih type: HTTP(s)
- [ ] URL: `https://dompetku.onrender.com/hello`
- [ ] Interval: 5 minutes
- [ ] ✅ Aplikasi tidak akan sleep!

---

## 🎉 Selesai!

Aplikasi Dompetku sudah live! 🚀

---

## 🔧 Troubleshooting Cepat

| Masalah | Solusi |
|---------|--------|
| Build failed | Cek logs di Render, pastikan Dockerfile benar |
| Database connection error | Verify connection string (gunakan Pooler mode, port 6543) |
| 404 Not Found | Cek endpoint URL, pastikan ada `/api/` prefix |
| Sleep mode | Setup UptimeRobot atau upgrade ke Render Starter |

---

## 📝 Notes

- **Render Free Tier**: Sleep setelah 15 menit idle (wake-up ~30 detik)
- **Supabase Free**: 500MB database, unlimited API requests
- **Total Cost**: $0/bulan (selama dalam limit free tier)

---

**Good luck!** 🎊
