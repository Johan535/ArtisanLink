# 前後端聯調清單（API 對照）

更新時間：2026-04-24

## 聯調說明

- 前端基準：`ArtisanLink-fronted/src/api/index.js`、`ArtisanLink-fronted/src/api/customer.js`
- 後端基準：`artisanlink-server/src/main/java/com/johan/artisanlink/server/controller`
- 已完成驗證：
  - 前端建置：`npm run build` ✅
  - 後端編譯：`mvn -pl artisanlink-server -am compile` ✅
- 狀態標記：
  - `✅` 前後端接口已對齊，具備聯調基礎
  - `⚠️` 前端有接口，但後端尚未提供
  - `🧪` 已對齊但建議進一步做真實資料測試

## 管理端（admin）接口對照

| 前端接口 | 後端接口 | 狀態 | 備註 |
|---|---|---|---|
| POST `/admin/login` | POST `/admin/login` | ✅ | 已對齊 |
| POST `/admin/logout` | POST `/admin/logout` | ✅ | 已對齊 |
| GET `/admin/merchant/list` | GET `/admin/merchant/list` | ✅ | 已對齊 |
| POST `/admin/merchant/save` | POST `/admin/merchant/save` | ✅ | 已對齊 |
| PUT `/admin/merchant/update` | PUT `/admin/merchant/update` | ✅ | 已對齊 |
| GET `/admin/merchant/info` | GET `/admin/merchant/info` | ✅ | 本輪新增後端 |
| PUT `/admin/merchant/info` | PUT `/admin/merchant/info` | ✅ | 本輪新增後端 |
| GET `/admin/staff/list` | GET `/admin/staff/list` | ✅ | 已對齊 |
| POST `/admin/staff/save` | POST `/admin/staff/save` | ✅ | 已對齊 |
| PUT `/admin/staff/update/{id}` | PUT `/admin/staff/update/{id}` | ✅ | 本輪新增後端 |
| DELETE `/admin/staff/delete/{id}` | DELETE `/admin/staff/delete/{id}` | ✅ | 本輪新增後端 |
| GET `/admin/service/list` | GET `/admin/service/list` | ✅ | 已對齊 |
| POST `/admin/service/save` | POST `/admin/service/save` | ✅ | 已對齊 |
| PUT `/admin/service/{id}` | PUT `/admin/service/{id}` | ✅ | 本輪新增後端 |
| DELETE `/admin/service/{id}` | DELETE `/admin/service/{id}` | ✅ | 本輪新增後端 |
| GET `/admin/service/category/list` | GET `/admin/service/category/list` | ✅ | 已對齊 |
| GET `/admin/order/list` | GET `/admin/order/list` | ✅ | 已對齊 |
| GET `/admin/order/{id}` | GET `/admin/order/{id}` | ✅ | 已對齊 |
| POST `/admin/order/{id}/accept` | POST `/admin/order/{id}/accept` | ✅ | 本輪新增後端 |
| POST `/admin/order/{id}/reject` | POST `/admin/order/{id}/reject` | ✅ | 本輪新增後端 |
| POST `/admin/order/{id}/complete` | POST `/admin/order/{id}/complete` | ✅ | 本輪新增後端 |
| GET `/admin/customer/list` | GET `/admin/customer/list` | ✅ | 已對齊 |
| GET `/admin/statistics/merchant` | GET `/admin/statistics/merchant` | ✅ | 本輪完成後端實作 |
| GET `/admin/schedule/technician` | - | ⚠️ | 前端排班頁依賴，後端未實作 |
| PUT `/admin/schedule/update` | - | ⚠️ | 前端排班頁依賴，後端未實作 |
| POST `/admin/schedule/batch-set` | - | ⚠️ | 前端排班頁依賴，後端未實作 |
| GET `/admin/appointment/time-slots` | - | ⚠️ | 前端時間片管理依賴，後端未實作 |
| POST `/admin/appointment/time-slots/batch` | - | ⚠️ | 前端時間片管理依賴，後端未實作 |
| PUT `/admin/appointment/time-slot/disable/{id}` | - | ⚠️ | 前端時間片管理依賴，後端未實作 |
| PUT `/admin/appointment/time-slot/enable/{id}` | - | ⚠️ | 前端時間片管理依賴，後端未實作 |
| GET `/admin/message/list` | - | ⚠️ | 前端消息中心依賴，後端未實作 |
| PUT `/admin/message/{id}/read` | - | ⚠️ | 前端消息中心依賴，後端未實作 |
| PUT `/admin/message/read-all` | - | ⚠️ | 前端消息中心依賴，後端未實作 |
| DELETE `/admin/message/clear` | - | ⚠️ | 前端消息中心依賴，後端未實作 |
| GET `/admin/statistics/revenue-trend` | - | ⚠️ | 前端有調用，後端未實作 |
| GET `/admin/statistics/order-source` | - | ⚠️ | 前端有調用，後端未實作 |

## C 端（customer）接口對照

> 目前後端控制器尚未提供 `/customer/**` 路由，因此 C 端接口以「待後端補齊」為主。

| 前端接口 | 後端接口 | 狀態 | 備註 |
|---|---|---|---|
| POST `/customer/login` | - | ⚠️ | 後端尚未實作 customer 控制器 |
| POST `/customer/sms/send` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/service/categories` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/service/list` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/technician/list` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/technician/{id}` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/technician/{id}/timeslots` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/technician/nearby` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/order/create` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/order/list` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/order/{id}` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/order/{id}/cancel` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/review/create` | - | ⚠️ | 後端尚未實作 |
| PUT `/customer/review/update` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/review/{id}` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/review/list` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/profile` | - | ⚠️ | 後端尚未實作 |
| PUT `/customer/profile` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/message/list` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/message/{id}/read` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/message/unread-count` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/case/list` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/case/{id}` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/case/{id}/view` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/case/{id}/like` | - | ⚠️ | 後端尚未實作 |
| GET `/customer/case/{id}/comments` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/case/{id}/comments` | - | ⚠️ | 後端尚未實作 |
| POST `/customer/comment/{id}/like` | - | ⚠️ | 後端尚未實作 |

## 本輪已完成的前端 API 層修正

- 修復 `http is not defined` 問題，統一改為 `request` 封裝呼叫
- 在 `adminApi` 補齊舊頁面常用別名方法（`getStaff/getOrders/getCustomers/getServices/...`）
- 在 `customerApi` 補齊舊頁面常用別名方法（`getMyOrders/submitReview/updateUserProfile/searchTechnicians/...`）
- `index.js` 補 `customerApi` 轉出，避免 `import { adminApi, customerApi } from '../../api'` 報錯

## 建議下一步聯調順序

1. 先完成管理端缺口：排班、時間片、消息中心、統計擴展接口
2. 再補齊 C 端 `/customer/**` 控制器與 service
3. 補自動化 API 測試（Postman/Newman 或 integration test），將本清單轉為可執行測試集

