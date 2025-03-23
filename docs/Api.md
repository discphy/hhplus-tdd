# API 명세서

## 포인트 API

### 포인트 충전

**[Description]**  
포인트를 충전한다.

**[Request]**

+ URL : `/point/{id}/charge`
+ Method : `PATCH`
+ Path Parameters

| Parameter | Description |
|-----------|-------------|
| id        | 사용자 ID      |

+ Request Body

```json
{
  "amount": 100000
}
```

+ Request Fields

| Path   | Type   | Required | Description |
|--------|--------|----------|-------------|
| amount | Number | true     | 충전 금액       |

**[Response]**

+ Response

```json
{
  "id": 1,
  "point": 10000,
  "updateMillis": 1234567890
}
```

+ Response Fields

| Path         | Type   | Description |
|--------------|--------|-------------|
| id           | Number | 사용자 ID      |
| point        | Number | 잔여 포인트      |
| updateMillis | Number | 수정 일시       |

### 포인트 사용

**[Description]**  
포인트를 사용한다.

**[Request]**

+ URL : `/point/{id}/use`
+ Method : `PATCH`
+ Path Parameters

| Parameter | Description |
|-----------|-------------|
| id        | 사용자 ID      |

+ Request Body

```json
{
  "amount": 100000
}
```

+ Request Fields

| Path   | Type   | Required | Description |
|--------|--------|----------|-------------|
| amount | Number | true     | 사용 금액       |

**[Response]**

+ Response

```json
{
  "id": 1,
  "point": 10000,
  "updateMillis": 1234567890
}
```

+ Response Fields

| Path         | Type   | Description |
|--------------|--------|-------------|
| id           | Number | 사용자 ID      |
| point        | Number | 잔여 포인트      |
| updateMillis | Number | 수정 일시       |

### 포인트 조회

**[Description]**  
포인트를 조회한다.

**[Request]**

+ URL : `/point/{id}`
+ Method : `GET`
+ Path Parameters

| Parameter | Description |
|-----------|-------------|
| id        | 사용자 ID      |

**[Response]**

+ Response

```json
{
  "id": 1,
  "point": 10000,
  "updateMillis": 1234567890
}
```

+ Response Fields

| Path         | Type   | Description |
|--------------|--------|-------------|
| id           | Number | 사용자 ID      |
| point        | Number | 잔여 포인트      |
| updateMillis | Number | 수정 일시       |

### 포인트 내역 조회

**[Description]**  
포인트 내역을 조회한다.

**[Request]**

+ URL : `/point/{id}/histories`
+ Method : `GET`
+ Path Parameters

| Parameter | Description |
|-----------|-------------|
| id        | 사용자 ID      |

**[Response]**

+ Response

```json
[
  {
    "id": 1,
    "userId": 1,
    "amount": 10000,
    "type": "CHARGE",
    "updateMillis": 1234567890
  }
]
```

+ Response Fields

| Path         | Type   | Description                |
|--------------|--------|----------------------------|
| id           | Number | 트랜잭션 ID                    |
| userId       | Number | 사용자 ID                     |
| amount       | Number | 포인트 충전/사용 금액               |
| type         | Number | 포인트 충전/사용 종류 (CHARGE, USE) |
| updateMillis | Number | 수정 일시                      |
