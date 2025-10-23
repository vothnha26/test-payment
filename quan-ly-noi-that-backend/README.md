# 🏠 Hệ Thống Thanh Toán Nội Thất - SePay Integration

Hệ thống thanh toán tự động sử dụng SePay với QR Code VNPay có số tiền cố định.

## ✨ Tính năng

- ✅ Tạo thanh toán với QR Code VNPay (số tiền tự động điền sẵn)
- ✅ Webhook tự động từ SePay cập nhật trạng thái
- ✅ Tự động kiểm tra trạng thái thanh toán mỗi 3 giây
- ✅ Lưu lịch sử giao dịch vào database
- ✅ Giao diện web đẹp, responsive
- ✅ Test webhook không cần deploy (chế độ dev)

## 🚀 Chạy Local

### 1. Cài đặt

```bash
# Clone project
git clone <repo-url>
cd quan-ly-noi-that-backend

# Cấu hình database (PostgreSQL)
# Sửa file src/main/resources/application.properties
```

### 2. Chạy

```bash
# Với Maven wrapper
./mvnw spring-boot:run

# Hoặc với Maven đã cài
mvn spring-boot:run
```

### 3. Truy cập

- Web: http://localhost:8080
- API Docs: http://localhost:8080/api/payment

## 🌐 Deploy lên Render.com

### Quick Start

1. **Push code lên GitHub**

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin <your-github-repo>
git push -u origin main
```

2. **Tạo tài khoản Render.com**
   - Truy cập: https://render.com
   - Sign up with GitHub

3. **Tạo PostgreSQL Database**
   - New + → PostgreSQL
   - Name: `noithat-db`
   - Region: Singapore
   - Plan: Free
   - Create Database
   - Copy **Internal Database URL**

4. **Deploy Web Service**
   - New + → Web Service
   - Connect GitHub repository
   - Cấu hình:
     - Name: `noithat-payment`
     - Region: Singapore
     - Build Command: `./mvnw clean install -DskipTests`
     - Start Command: `java -Dserver.port=$PORT -jar target/quan-ly-noi-that-backend-0.0.1-SNAPSHOT.jar`

5. **Environment Variables**

Thêm các biến môi trường:

```
SPRING_DATASOURCE_URL=<Internal Database URL>
SPRING_DATASOURCE_USERNAME=<db username>
SPRING_DATASOURCE_PASSWORD=<db password>
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SEPAY_API_KEY=<your_sepay_api_key>
SEPAY_ACCOUNT_NUMBER=<your_account_number>
SEPAY_ACCOUNT_NAME=<your_account_name>
SEPAY_BIN=<your_bank_bin>
SEPAY_WEBHOOK_SECRET=<your_webhook_secret>
```

6. **Deploy**
   - Click "Create Web Service"
   - Đợi 5-10 phút
   - URL: `https://noithat-payment.onrender.com`

7. **Cấu hình SePay Webhook**
   - Login: https://my.sepay.vn
   - Webhook URL: `https://noithat-payment.onrender.com/api/payment/webhook`

## 📝 API Endpoints

### Tạo thanh toán
```
POST /api/payment/create
Content-Type: application/json

{
  "amount": 50000,
  "description": "Thanh toán đơn hàng",
  "customerName": "Nguyễn Văn A",
  "customerEmail": "email@example.com",
  "customerPhone": "0123456789"
}
```

### Kiểm tra trạng thái
```
GET /api/payment/status/{orderId}
```

### Webhook (SePay gọi tự động)
```
POST /api/payment/webhook
```

### Test webhook (chỉ dùng dev)
```
POST /api/payment/test-webhook/{orderId}
```

## 🧪 Test

### Test Local (không cần chuyển khoản thật)

1. Tạo thanh toán
2. Click "🧪 Test Webhook"
3. Trạng thái tự động cập nhật thành COMPLETED

### Test với Ngrok (webhook thật)

```bash
# Cài Ngrok
# Download: https://ngrok.com/download

# Chạy Ngrok
ngrok http 8080

# Copy URL ngrok và cấu hình trên SePay
# Ví dụ: https://abc123.ngrok.io/api/payment/webhook
```

## 🔧 Cấu hình SePay

1. Đăng ký tài khoản tại: https://my.sepay.vn
2. Liên kết tài khoản ngân hàng
3. Lấy API Key từ dashboard
4. Cấu hình webhook URL

### Thông tin cần thiết:
- `SEPAY_API_KEY`: API key từ SePay dashboard
- `SEPAY_ACCOUNT_NUMBER`: Số tài khoản ngân hàng
- `SEPAY_ACCOUNT_NAME`: Tên chủ tài khoản
- `SEPAY_BIN`: Mã BIN ngân hàng (VD: 970415 cho VietinBank)

## 📱 Ngân hàng hỗ trợ VNPay QR

- VietinBank (970415)
- Vietcombank (970436)
- BIDV (970418)
- Agribank (970405)
- Techcombank (970407)
- MBBank (970422)
- ACB (970416)
- VPBank (970432)
- Và hầu hết các ngân hàng khác...

## 🛠 Tech Stack

- Spring Boot 3.5.6
- Spring Data JPA
- PostgreSQL
- Lombok
- Jackson
- Maven

## 📄 License

MIT License
