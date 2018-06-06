# SSQR

用于解析包含 Shadowsocks 服务器信息的 QR Code

![](./assets/server.png)

```
D:\Download>SSQR.exe server.png
address:  sga.ss8.site
port:     18621
method:   rc4-md5
password: 81681525

D:\Download>SSQR.exe server.png -json
{
  "address": "sga.ss8.site",
  "port": 18621,
  "method": "rc4-md5",
  "password": "81681525"
}

// 从屏幕获取二维码
D:\Download>SSQR.exe screen
```
