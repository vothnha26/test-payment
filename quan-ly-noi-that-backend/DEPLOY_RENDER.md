# Hướng dẫn Deploy lên Render.com

## Bước 1: Chuẩn bị

1. Push code lên GitHub
2. Đăng ký tài khoản tại https://render.com

## Bước 2: Tạo PostgreSQL Database

1. Vào Render Dashboard
2. Click "New +" → "PostgreSQL"
3. Điền thông tin:
   - Name: `noithat-db`
   - Region: Singapore (gần Việt Nam nhất)
   - Plan: Free
4. Click "Create Database"
5. Copy các thông tin:
   - Internal Database URL (dùng này)
   - External Database URL

## Bước 3: Deploy Web Service

1. Click "New +" → "Web Service"
2. Connect GitHub repository
3. Cấu hình:
   - **Name**: `noithat-payment`
   - **Region**: Singapore
   - **Branch**: main
   - **Build Command**: `./mvnw clean install -DskipTests`
   - **Start Command**: `java -jar target/quan-ly-noi-that-backend-0.0.1-SNAPSHOT.jar`
   - **Instance Type**: Free

## Bước 4: Environment Variables

Thêm các biến môi trường:

```
SPRING_DATASOURCE_URL=<Internal Database URL từ bước 2>
SPRING_DATASOURCE_USERNAME=<username từ database>
SPRING_DATASOURCE_PASSWORD=<password từ database>
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SEPAY_API_KEY=<YOUR_SEPAY_API_KEY>
SEPAY_ACCOUNT_NUMBER=<YOUR_ACCOUNT_NUMBER>
SEPAY_ACCOUNT_NAME=<YOUR_ACCOUNT_NAME>
SEPAY_BIN=<YOUR_BANK_BIN>
SEPAY_WEBHOOK_SECRET=<YOUR_WEBHOOK_SECRET>
```

## Bước 5: Deploy

1. Click "Create Web Service"
2. Đợi build & deploy (5-10 phút)
3. URL của bạn: `https://noithat-payment.onrender.com`

## Bước 6: Cấu hình SePay Webhook

1. Đăng nhập https://my.sepay.vn
2. Vào "Cài đặt Webhook"
3. Nhập URL: `https://noithat-payment.onrender.com/api/payment/webhook`
4. Lưu

## Bước 7: Test

1. Truy cập: `https://noithat-payment.onrender.com`
2. Tạo thanh toán
3. Quét QR và chuyển khoản
4. Kiểm tra webhook tự động cập nhật

## Lưu ý:

- Free tier của Render sẽ sleep sau 15 phút không hoạt động
- Lần đầu truy cập sau khi sleep sẽ mất 30-50s để wake up
- Database free có giới hạn 1GB
- Để không bị sleep, nâng cấp lên plan trả phí ($7/tháng)
