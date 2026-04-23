# 匠心美业O2O平台 - 前端API接口文档

## 基础信息

- **Base URL**: `http://localhost:8124/api`（开发环境）
- **响应格式**: `{ code: number, msg: string, data: any }`
- **认证方式**: Bearer Token（请求头：`Authorization: Bearer {token}`）

## 通用错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 401 | 未登录或token过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## C端用户接口

### 1. 用户认证

#### 1.1 用户登录

**接口**: `POST /customer/login`

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "nickname": "张三",
      "avatar": "https://example.com/avatar.jpg",
      "phone": "13800138000"
    }
  }
}
```

#### 1.2 获取用户信息

**接口**: `GET /customer/profile`

**响应示例**:
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "nickname": "张三",
    "avatar": "https://example.com/avatar.jpg",
    "phone": "13800138000",
    "gender": 1,
    "birthday": "1990-01-01"
  }
}
```

#### 1.3 更新用户信息

**接口**: `PUT /customer/profile`

**请求参数**:
```json
{
  "nickname": "张三",
  "avatar": "https://example.com/new-avatar.jpg",
  "gender": 1,
  "birthday": "1990-01-01"
}
```

---

### 2. 技师相关

#### 2.1 获取附近技师列表

**接口**: `GET /customer/technician/nearby`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| longitude | number | 是 | 经度 |
| latitude | number | 是 | 纬度 |
| radius | number | 否 | 搜索半径（公里），默认3 |
| pageNum | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "name": "李师傅",
        "avatar": "https://example.com/tech1.jpg",
        "skills": ["美发", "染发"],
        "rating": 4.8,
        "distance": 1.2,
        "workStart": "09:00",
        "workEnd": "21:00"
      }
    ]
  }
}
```

#### 2.2 获取技师详情

**接口**: `GET /customer/technician/{id}`

**响应示例**:
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "name": "李师傅",
    "avatar": "https://example.com/tech1.jpg",
    "skills": ["美发", "染发"],
    "rating": 4.8,
    "reviewCount": 120,
    "description": "10年美发经验，擅长日韩风格",
    "services": [
      {
        "id": 1,
        "name": "精剪",
        "price": 88,
        "duration": 60
      }
    ],
    "timeSlots": [
      {
        "id": 1,
        "date": "2026-04-24",
        "startTime": "10:00",
        "endTime": "11:00",
        "status": 0
      }
    ]
  }
}
```

#### 2.3 搜索技师

**接口**: `GET /customer/technician/search`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

---

### 3. 服务相关

#### 3.1 获取服务分类列表

**接口**: `GET /customer/service/category/list`

**响应示例**:
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "name": "美发",
      "icon": "https://example.com/icon1.png",
      "sort": 1
    },
    {
      "id": 2,
      "name": "美甲",
      "icon": "https://example.com/icon2.png",
      "sort": 2
    }
  ]
}
```

#### 3.2 获取服务列表

**接口**: `GET /customer/service/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | number | 否 | 分类ID |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

---

### 4. 预约相关

#### 4.1 创建预约

**接口**: `POST /customer/order/create`

**请求参数**:
```json
{
  "technicianId": 1,
  "serviceId": 1,
  "timeSlotId": 1,
  "remark": "希望剪短一点"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "预约成功",
  "data": {
    "orderId": 123,
    "orderNo": "ORD202604230001"
  }
}
```

**冲突响应**:
```json
{
  "code": 400,
  "msg": "该时间已被占用，请重新选择",
  "data": null
}
```

#### 4.2 获取订单列表

**接口**: `GET /customer/order/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | number | 否 | 订单状态：0-待接单，1-已接单，2-已完成，3-已取消 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

**响应示例**:
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "total": 10,
    "list": [
      {
        "id": 123,
        "orderNo": "ORD202604230001",
        "technicianName": "李师傅",
        "serviceName": "精剪",
        "appointmentTime": "2026-04-24 10:00",
        "status": 1,
        "statusText": "已接单",
        "createTime": "2026-04-23 15:30:00"
      }
    ]
  }
}
```

#### 4.3 获取订单详情

**接口**: `GET /customer/order/{id}`

#### 4.4 取消订单

**接口**: `POST /customer/order/{id}/cancel`

---

### 5. 评价相关

#### 5.1 提交评价

**接口**: `POST /customer/review/create`

**请求参数**:
```json
{
  "orderId": 123,
  "rating": 5,
  "content": "技术很好，服务态度也很棒！",
  "images": ["https://example.com/img1.jpg"]
}
```

#### 5.2 获取技师评价列表

**接口**: `GET /customer/review/technician/{technicianId}`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

---

### 6. 案例相关

#### 6.1 获取案例列表

**接口**: `GET /customer/case/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | number | 否 | 分类ID |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

#### 6.2 获取案例详情

**接口**: `GET /customer/case/{id}`

---

### 7. 消息相关

#### 7.1 获取消息列表

**接口**: `GET /customer/message/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | number | 否 | 消息类型：1-系统通知，2-订单通知 |
| isRead | boolean | 否 | 是否已读 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

#### 7.2 标记消息已读

**接口**: `POST /customer/message/{id}/read`

---

## B端管理接口

### 1. 管理员认证

#### 1.1 管理员登录

**接口**: `POST /admin/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "adminInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "role": "merchant_admin"
    }
  }
}
```

---

### 2. 数据统计

#### 2.1 获取门店统计数据

**接口**: `GET /admin/statistics/merchant`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| timeRange | string | 否 | 时间范围：today/week/month，默认today |

**响应示例**:
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "orderCount": 50,
    "revenue": 12800.00,
    "appointmentCount": 30,
    "newCustomerCount": 10,
    "trendData": [
      { "date": "2026-04-23", "count": 15, "revenue": 3200.00 }
    ]
  }
}
```

---

### 3. 技师管理

#### 3.1 获取技师列表

**接口**: `GET /admin/staff/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 否 | 姓名搜索 |
| status | number | 否 | 状态：0-离职，1-在职 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

#### 3.2 新增技师

**接口**: `POST /admin/staff/save`

**请求参数**:
```json
{
  "name": "李师傅",
  "phone": "13800138000",
  "avatar": "https://example.com/avatar.jpg",
  "skills": ["美发", "染发"],
  "workStart": "09:00",
  "workEnd": "21:00",
  "description": "10年美发经验"
}
```

#### 3.3 编辑技师

**接口**: `PUT /admin/staff/update`

**请求参数**: 同新增，需包含 `id` 字段

#### 3.4 删除技师

**接口**: `DELETE /admin/staff/{id}`

---

### 4. 排班管理

#### 4.1 获取技师排班

**接口**: `GET /admin/schedule/technician/{technicianId}`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | string | 是 | 开始日期 YYYY-MM-DD |
| endDate | string | 是 | 结束日期 YYYY-MM-DD |

#### 4.2 批量设置排班

**接口**: `POST /admin/schedule/batch`

**请求参数**:
```json
{
  "technicianIds": [1, 2, 3],
  "startDate": "2026-04-24",
  "endDate": "2026-04-30",
  "workStart": "09:00",
  "workEnd": "21:00",
  "slotDuration": 60
}
```

---

### 5. 服务管理

#### 5.1 获取服务列表

**接口**: `GET /admin/service/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | number | 否 | 分类ID |
| status | number | 否 | 状态：0-下架，1-上架 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

#### 5.2 新增服务

**接口**: `POST /admin/service/save`

**请求参数**:
```json
{
  "categoryId": 1,
  "name": "精剪",
  "price": 88.00,
  "duration": 60,
  "description": "专业精剪服务",
  "images": ["https://example.com/img1.jpg"]
}
```

#### 5.3 编辑服务

**接口**: `PUT /admin/service/update`

#### 5.4 上下架服务

**接口**: `POST /admin/service/{id}/toggle-status`

---

### 6. 订单管理

#### 6.1 获取订单列表

**接口**: `GET /admin/order/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | number | 否 | 订单状态 |
| technicianId | number | 否 | 技师ID |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

#### 6.2 接单

**接口**: `POST /admin/order/{id}/accept`

#### 6.3 拒绝订单

**接口**: `POST /admin/order/{id}/reject`

**请求参数**:
```json
{
  "reason": "时间冲突"
}
```

#### 6.4 完成订单

**接口**: `POST /admin/order/{id}/complete`

---

### 7. 客户管理

#### 7.1 获取客户列表

**接口**: `GET /admin/customer/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | string | 否 | 手机号搜索 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

---

### 8. 消息管理

#### 8.1 获取消息列表

**接口**: `GET /admin/message/list`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | number | 否 | 消息类型 |
| isRead | boolean | 否 | 是否已读 |
| pageNum | number | 否 | 页码 |
| pageSize | number | 否 | 每页数量 |

#### 8.2 标记消息已读

**接口**: `POST /admin/message/{id}/read`

#### 8.3 批量标记已读

**接口**: `POST /admin/message/read-batch`

**请求参数**:
```json
{
  "ids": [1, 2, 3]
}
```

---

## WebSocket消息格式

### 连接地址

- **B端**: `ws://{domain}/ws/merchant/{shopId}?token={token}`
- **C端**: `ws://{domain}/ws/customer/{userId}?token={token}`

### 消息类型

#### 1. 新订单通知（B端接收）

```json
{
  "type": "NEW_ORDER",
  "data": {
    "orderId": 123,
    "orderNo": "ORD202604230001",
    "customerName": "张三",
    "serviceName": "精剪",
    "appointmentTime": "2026-04-24 10:00"
  }
}
```

#### 2. 订单状态变更（C端接收）

```json
{
  "type": "ORDER_STATUS_CHANGE",
  "data": {
    "orderId": 123,
    "status": 1,
    "statusText": "已接单",
    "message": "您的订单已被李师傅接单"
  }
}
```

#### 3. 心跳消息

```json
{
  "type": "HEARTBEAT",
  "data": {
    "timestamp": 1713859200000
  }
}
```

---

## 前端调用示例

### Axios请求封装

```javascript
import request from '@/api/http'

// GET请求
export function getTechnicianList(params) {
  return request({
    url: '/customer/technician/nearby',
    method: 'get',
    params
  })
}

// POST请求
export function createOrder(data) {
  return request({
    url: '/customer/order/create',
    method: 'post',
    data
  })
}
```

### WebSocket使用

```javascript
import wsClient from '@/utils/websocket'

// B端连接
wsClient.connect(shopId, 'merchant')

// 监听消息
wsClient.on('NEW_ORDER', (data) => {
  console.log('收到新订单:', data)
})

// C端连接
wsClient.connect(userId, 'customer')

// 监听订单状态变更
wsClient.on('ORDER_STATUS_CHANGE', (data) => {
  console.log('订单状态变更:', data)
})
```

---

## 注意事项

1. **Token过期处理**: 当接口返回401时，自动跳转登录页
2. **并发控制**: 预约时间片选择时需做防重复提交处理
3. **缓存策略**: 门店/技师基础信息缓存30分钟
4. **超时设置**: 接口请求超时时间为5秒
5. **错误提示**: 所有异常状态需给出友好提示
