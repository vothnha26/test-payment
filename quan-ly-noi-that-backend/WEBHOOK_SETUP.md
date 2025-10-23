# Hướng dẫn cấu hình Webhook SePay

## Khi develop (localhost):

### Bước 1: Cài đặt Ngrok
1. Tải ngrok tại: https://ngrok.com/download
2. Giải nén và chạy lệnh:
```bash
ngrok http 8080
```

3. Ngrok sẽ cung cấp URL public, ví dụ: `https://abc123.ngrok.io`

### Bước 2: Cấu hình webhook trên SePay
1. Đăng nhập vào https://my.sepay.vn
2. Vào mục "Cài đặt Webhook"
3. Nhập URL: `https://abc123.ngrok.io/api/payment/webhook`
4. Lưu cấu hình

### Bước 3: Test
- Tạo thanh toán trên web
- Quét QR và chuyển khoản
- SePay sẽ gọi webhook tự động
- Check log backend để xem có nhận webhook không

## Khi deploy production:

Thay URL webhook thành domain thật:
- Ví dụ: `https://yourdomain.com/api/payment/webhook`
- Cấu hình trên SePay dashboard

## Lưu ý:
- Webhook URL phải là HTTPS (ngrok free đã hỗ trợ HTTPS)
- SePay chỉ gọi webhook khi có giao dịch thực tế
- Kiểm tra log để debug
