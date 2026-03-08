#!/bin/bash

###############################################################################
# Deploy Backend ke Hugging Face Spaces
# 
# Script ini akan:
# 1. Copy isi folder backend/ ke temporary directory
# 2. Buat README.md dengan metadata HF
# 3. Init git repo dan push ke Hugging Face
# 4. Struktur lokal (monorepo) tetap tidak berubah
###############################################################################

set -e  # Exit on error

# Configuration
PROJECT_ROOT="/home/fadlimz/Documents/dompetku-java"
TEMP_DIR="/tmp/hf-deploy-$$"
HF_REMOTE_NAME="huggingface"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Deploy Backend ke Hugging Face${NC}"
echo -e "${GREEN}========================================${NC}"

# Cleanup function
cleanup() {
    echo -e "${YELLOW}Cleaning up temporary files...${NC}"
    rm -rf "$TEMP_DIR"
}

# Set trap to cleanup on exit
trap cleanup EXIT

# Step 1: Create temporary directory
echo -e "${YELLOW}[1/6] Creating temporary directory...${NC}"
mkdir -p "$TEMP_DIR"

# Step 2: Copy backend files to temporary directory
echo -e "${YELLOW}[2/6] Copying backend files to temporary directory...${NC}"

# Copy hanya source files, exclude build artifacts dan IDE files
rsync -av --quiet \
    --exclude 'build/' \
    --exclude 'bin/' \
    --exclude '.gradle/' \
    --exclude '.settings/' \
    --exclude '.project' \
    --exclude '.classpath' \
    --exclude '.factorypath' \
    --exclude '*.log' \
    "$PROJECT_ROOT/backend/" "$TEMP_DIR/"

# Step 3: Create README.md with HF metadata
echo -e "${YELLOW}[3/6] Creating README.md with Hugging Face metadata...${NC}"
cat > "$TEMP_DIR/README.md" << 'EOF'
---
title: Dompetku API
emoji: 💰
colorFrom: blue
colorTo: purple
sdk: docker
pinned: false
---

# Dompetku API - Personal Finance Management Backend

Backend API untuk aplikasi manajemen keuangan pribadi Dompetku.

## 🚀 Features

- ✅ User Authentication & Authorization (JWT)
- ✅ Account Management (Akun)
- ✅ Category Management (Kategori)
- ✅ Transaction Tracking (Transaksi Harian)
- ✅ Transfer Between Accounts
- ✅ Balance Tracking

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **Security**: JWT + BCrypt
- **Build Tool**: Gradle

## 📦 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | `7860` |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | - |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing secret (base64 encoded) | - |

## 🔌 API Endpoints

### Public Endpoints
- `POST /api/users` - Register new user
- `POST /api/auth/login` - Login

### Protected Endpoints (Requires JWT)
- `GET /api/accounts` - List accounts
- `POST /api/accounts` - Create account
- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `GET /api/transactions/daily-cash` - List daily transactions
- `POST /api/transactions/daily-cash` - Create transaction
- `POST /api/transfer` - Transfer between accounts
- `GET /api/balances` - List account balances

## 🏃 Running Locally

```bash
# Build
./gradlew bootJar

# Run with environment variables
java -jar build/libs/dompetku-0.0.1-SNAPSHOT.jar
```

## 📝 License

MIT License
EOF

# Step 4: Initialize git repo in temporary directory
echo -e "${YELLOW}[4/6] Initializing git repository...${NC}"
cd "$TEMP_DIR"
git init
git config user.email "zafad@outlook.com"
git config user.name "fadlimz"
git branch -m main  # Rename to 'main' branch

# Step 5: Add all files and commit
echo -e "${YELLOW}[5/6] Committing files...${NC}"
git add .
git commit -m "Deploy backend to Hugging Face - $(date '+%Y-%m-%d %H:%M:%S')"

# Step 6: Push to Hugging Face
echo -e "${YELLOW}[6/6] Pushing to Hugging Face...${NC}"

# Get the remote URL from the main repo
cd "$PROJECT_ROOT"
HF_URL=$(git remote get-url "$HF_REMOTE_NAME" 2>/dev/null || echo "")

if [ -z "$HF_URL" ]; then
    echo -e "${RED}Error: Hugging Face remote not found!${NC}"
    echo -e "${YELLOW}Please add the remote first:${NC}"
    echo "  git remote add huggingface https://huggingface.co/spaces/fadlimz/dompetku-api"
    exit 1
fi

echo -e "${GREEN}Remote URL: $HF_URL${NC}"

cd "$TEMP_DIR"
git remote add origin "$HF_URL"

# Force push to Hugging Face
git push origin main --force

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  ✅ Deploy Berhasil!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Check your Space at: ${GREEN}https://huggingface.co/spaces/fadlimz/dompetku-api${NC}"
echo ""
echo -e "${YELLOW}Note: Hugging Face akan otomatis build dan deploy.${NC}"
echo -e "${YELLOW}      Monitor logs di tab 'Logs' di Space kamu.${NC}"
