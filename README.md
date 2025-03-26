# 항해플러스 TDD 1주차 과제

## 과제 내용

### 기본 과제

+ 포인트 충전, 사용에 대한 정책 추가 (잔고 부족, 최대 잔고 등)
+ 주어진 4가지 기능에 대한 단위테스트 작성

### 심화 과제

+ 동일한 사용자에 대한 동시 요청이 정상적으로 처리될 수 있도록 개선
+ 주어진 4가지 기능에 대한 통합 테스트 작성
+ 동시성 제어 방식 및 각 적용의 장/단점을 기술한 보고서 작성

## 요구사항

### 포인트 충전

+ 충전 요청금액은 0보다 커야 한다.
+ 포인트 충전 최대 금액은 1000만원 까지다.
+ 같은 사용자가 동시에 충전할 경우, 해당 요청 모두 정상이여야 함.
+ 요청한 충전금액을 충전한다.

### 포인트 사용

+ 사용 요청금액은 0보다 커야한다.
+ 포인트 사용 후, 잔고는 0이 될 수 없다.
+ 같은 사용자가 동시에 사용할 경우, 해당 요청 모두 정상이여야 함.
+ 요청한 사용금액을 사용한다.

### 포인트 조회

+ 요청한 사용자의 포인트를 조회한다.

### 포인트 내역 조회

+ 요청한 사용자의 포인트 내역을 내림차순으로 정렬한다.

## 동시성 제어 방식 및 적용 장단점 기술 보고서

### 동시성 문제란?

여러 쓰레드가 동시에 공유 자원에 접근하거나 수정하려 할 때 발생하는 문제를 이야기한다.

### 동시성 문제 유형

**🏁 Race Condition (경쟁 조건)**

여러 쓰레드가 순서를 제어하지 못하고 동시에 자원에 접근할 때 발생하며, 결과가 실행 순서에 따라 달라진다.

```java
int count = 0;

/*
 * ❌ 여러 쓰레드가 동시에 increment()를 호출하면 실행 순서에 의해 최종 값이 예상한 값보다 작을 수 있는 문제가 발생한다.
 *
 * */
public void increment() {
    couunt++; // 내부적으로는 Read-Modify-Write 단계로 실행된다. 
}
```

**🧩 데이터 불일치**

여러 쓰레드가 동시에 공유 자원을 수정해서 정합성 있는 결과가 보장되지 않음

```java
long remainAmount = 10000;

/*
 * ❌ 여러 쓰레드가 동시에 출금을 시도할 때,
 * 지연으로 인해 검증에는 통과하고 출금을 하는 상황이 발생하여 잔고가 음수가 될 수 있다.
 *
 * */
public void withdraw(long withdrawAmount) {
    if (withdrawAmount > remainAmount) {
        throw new IllegalArgumentException("잔고가 부족합니다.");
    }

    Thread.sleep(1000); // 의도적으로 지연 발생
    remainAmount = remainAmount - withdrawAmount;
}
```

**⛔ 데드락 - 교착상태 (Deadlock)**

서로가 가진 락을 상대방이 요청 하면서 무한 대기 상태에 빠짐

> 위 유형 이외에도 동시성을 방치하면, 동시성 문제로 생긴 해결하기 힘들고 추적이 어려운 버그를 만날 수 있다.

### 동시성 해결 방법

**1️⃣ synchronized**

자바 기본 동기화 키워드로, 메서드나 코드 블럭에 락을 걸어 블로킹 방식으로 단일 쓰레드만 허용해서 동시성 문제를 제어한다.

[예제]

```java
private int count = 0;

public synchronized void increment() { // ✅ synchronized 키워드를 사용해 메서드 블록 동기화 제어
    count++;
}
```

[장점]

+ 문법이 간단하고 직관적이다.

[단점]

+ 쓰레드 락을 풀릴 때까지 무한 대기하여 성능 저하에 영향을 끼친다.
+ 공정성 보장이 되지 않아 특정 락이 오랜기간 동안 락을 획득하지 못할 수 있다.

**2️⃣ ReentrantLock**

재진입이 가능하고 조건 제어나 타임아웃, 인터럽트를 통해 세밀하게 동기화 제어를 할 수 있는 락

[예제]
```java
private int count = 0;
private final ReentrantLock lock = new ReentrantLock();

public void increment() {
    lock.lock(); // ✅ ReentrantLock을 통한 락
    try {
        count++;
    } finally {
        lock.unlock(); // ✅ ReentrantLock을 통한 락 해제
    }
}
```

[장점]

+ 순서를 보장하는 공정성 설정이 가능하다. 
+ 세밀하게 동기화를 제어

[단점]

+ 코드 복잡도가 증가한다.

**3️⃣ volatile**

변수의 가시성을 보장, CPU 캐시를 사용하지 않고 메인 메모리에서 직접 읽는다. 

[예제]
```java
private volatile boolean running = true;

public void stop() {
    running = false;
}

public void run() {
    while (running) { // ✅ 캐시에서 읽는게 아니라, 직접 메인 메모리를 읽어 가시성을 보장한다.
        // 작업 수행
    }
}
```

[장점]

+ 매우 가볍고 빠르다. 
+ 플래그에 적합하다. 

[단점]

+ 원자성 보장이 되지 않는다. 

**4️⃣ Atomic 클래스**

CAS(Compare-And-Set) 알고리즘 기반으로 원자성을 보장하며 락 없이 연산이 가능하다.

[예제]
```java
private AtomicInteger count = new AtomicInteger(0);

public void increment() {
    count.incrementAndGet(); // ✅ 락 없이 CAS 기반으로 원자성 연산을 한다.
}
```

[장점] 

+ 락이 없는 연산으로 높은 성능
+ 데드락이 없다. 
+ 멀티쓰레드 환경에서 안정적이다.

[단점]

+ 단일 변수 수준에서만 효과적이다. 
+ 복잡한 연산에서는 사용하기 어렵다.
+ 반복 실패로 인한 지연 발생 가능

**5️⃣ 동시성 컬렉션**

Thread Safe한 자바 컬렉션

```java
private Map<String, Integer> map = new ConcurrentHashMap<>(); // ✅ 동시성 컬렉션으로 인스턴스화 해서 일반 Map 문법과 동일하게 사용한다.

public void put(String key, int value) {
    map.put(key, value);
}

public int get(String key) {
    return map.getOrDefault(key, 0);
}
```

[장점]

+ 프록시 컬렉션(Collections.synchronizedList() 등)과 달리 무분별하게 락을 하지 않아 성능적으로 우수하다. 
+ 내부에서 동기화 처리로 코드가 간결하다.

[단점]

+ 성능이 일반 컬렉션보다 좋지 않다. 

> 이외에도 동시성 도구가 많이 있고 동시성 흐름을 제어하는 클래스도 있다.   
> TMI) Java 24 LTS 버전에서 커널 스레드 방식이 개선되어 컨텍스트 스위칭을 없앨 수 있다. 

### 동시성 도구 적용 

**"동일한 사용자에 대한 동시 요청이 정상적으로 처리될 수 있도록 개선"하는 것이 요구사항이다.**

[선택받지 못한 동시성 도구]

+ synchronized - 👎 : 사용자 구분 없이 단일 쓰레드로 동작하기 때문에 성능 저하 이슈가 있다. 
+ volatile - 👎 : 요구사항을 만족시키기에는 적합하지 않은 것 같다고 판단하였다. 
+ Atomic 클래스 - 🤔 : `UserPointTable`클래스의 포인트 변수 타입을 Atomic 클래스로 대체할 수 있겠지만 수정이 불가능하다는 요구사항이 있기 때문에 제외 시켰다. 

[채택]

유저 별로 `ReentrantLock`을 관리하는 동시성 컬렉션 `ConcurrentHashMap`을 사용하여 구현을 진행하였다.

```java
private Map<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

public void lock(long userId, Runnable runnable) {
    // ✅ 사용자 ID로 동시성 컬렉션에서 락을 가져오는 로직이다. 공정모드로 순서를 보장한다.  
    ReentrantLock reentrantLock = map.computeIfAbsent(id, k -> new ReentrantLock(true)); 

    reentrantLock.lock(); // 🔒 락 설정
    try {
        runnable.run();
    } finally {
        reentrantLock.unlock(); // 🔓 락 해제
    }
}
```

🔗 동시성 로직 구현 커밋 링크 : [b662b64](https://github.com/discphy/hhplus-tdd/pull/2/commits/b662b64cb9aef3d29fb7db58dfdece56e4908400)

### 키워드 정리

**동시성 컬렉션이 항상 답은 아니다.**

단일 스레드가 컬렉션을 사용하는 경우에는 동시성 컬렉션이 아닌 일반 컬렉션을 사용해야 한다. 불필요한 성능 저하를 발생 시킬 수 있다. 

**동시성 흐름제어 자바 키워드**

+ CompletableFuture
+ ExecutorService
+ Flow

**원자성? 가시성?**