# AGENTS.md - Coding Guidelines for Dompetku

## Build Commands

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.fadlimz.dompetku.base.services.BaseServiceImplTest"

# Run a single test method
./gradlew test --tests "com.fadlimz.dompetku.base.services.BaseServiceImplTest.testMethodName"

# Run the application
./gradlew bootRun

# Create executable JAR
./gradlew bootJar

# Clean build artifacts
./gradlew clean
```

## Technology Stack

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Build Tool**: Gradle
- **Database**: SQLite with Hibernate/JPA
- **Security**: Spring Security + JWT
- **Utilities**: Lombok, JUnit 5

## Code Style Guidelines

### Package Structure
```
com.fadlimz.dompetku/
├── base/               # Base classes (entities, DTOs, services)
├── config/             # Configuration and utility classes
├── master/             # Master data modules
│   ├── account/
│   ├── category/
│   ├── transactionCode/
│   └── user/
└── transactions/       # Transaction modules
    ├── accountBalanceTransfer/
    └── dailyCash/
```

### Entity Classes
- Extend `BaseEntity` for audit fields (id, version, createdBy, createdTime, etc.)
- Use Lombok `@Getter` and `@Setter` on the class
- Use JPA annotations: `@Entity`, `@Table`, `@Id`, etc.
- Fields should be `private`

```java
@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account extends BaseEntity {
    @Column(name = "account_code")
    private String accountCode;
}
```

### DTO Classes
- Extend `BaseDto`
- Fields must be `public` (no access modifier or explicit `public`)
- Required Lombok annotations: `@Data`, `@EqualsAndHashCode(callSuper = true)`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@SuperBuilder`
- Must implement four conversion methods:

```java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountDto extends BaseDto {
    public String accountCode;
    public String accountName;
    
    public Account toEntity() {
        Account account = super.toEntity(Account.class);
        account.setAccountCode(accountCode);
        return account;
    }
    
    public static AccountDto fromEntity(Account entity) {
        if (entity == null) return null;
        AccountDto dto = BaseDto.fromEntity(AccountDto.class, entity);
        dto.accountCode = entity.getAccountCode();
        return dto;
    }
    
    public static List<Account> toEntityList(List<AccountDto> dtos) {
        return dtos.stream().map(AccountDto::toEntity).collect(Collectors.toList());
    }
    
    public static List<AccountDto> fromEntityList(List<Account> entities) {
        return entities.stream().map(AccountDto::fromEntity).collect(Collectors.toList());
    }
}
```

### Service Classes
- Extend `BaseService<T>` (concrete class, no interface)
- Annotate with `@Service` and `@Transactional`
- Use constructor injection for dependencies
- Override `findById` and `findAll` for user isolation

```java
@Service
@Transactional
public class AccountService extends BaseService<Account> {
    private final AccountRepository accountRepository;
    private final UserService userService;
    
    public AccountService(AccountRepository accountRepository, UserService userService) {
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.userService = userService;
    }
}
```

### Controller Classes
- Use `@RestController` and `@RequestMapping`
- Use `@RequiredArgsConstructor` for dependency injection
- Return `ResponseEntity<T>`

### Imports Order
1. Java standard libraries (`java.*`, `javax.*`)
2. Third-party libraries (Spring, Lombok, etc.)
3. Project internal imports

### String Handling
Always use `StringUtil.isBlank(str)` from `com.fadlimz.dompetku.config` instead of manual null/empty checks:

```java
import com.fadlimz.dompetku.config.StringUtil;

// Good
if (StringUtil.isBlank(keyword)) { ... }

// Bad
if (keyword == null || keyword.isEmpty()) { ... }
```

### Error Handling
- Throw `RuntimeException` with descriptive messages for business logic errors
- Use `Optional<T>` for repository queries that may return empty results
- Global exception handler exists in `config/GlobalExceptionHandler`

### Naming Conventions
- **Classes**: PascalCase (e.g., `AccountService`, `DailyCashDto`)
- **Methods**: camelCase (e.g., `findById`, `toEntity`)
- **Variables**: camelCase (e.g., `accountCode`, `createdTime`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Packages**: lowercase with dots (e.g., `com.fadlimz.dompetku.master.account`)

### Lombok Usage
- Entities: Use `@Getter` and `@Setter` on class level
- DTOs: Use `@Data`, `@EqualsAndHashCode(callSuper = true)`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@SuperBuilder`
- Controllers: Use `@RequiredArgsConstructor` for final field injection

### Testing
- Use JUnit 5 (`@Test` from `org.junit.jupiter.api`)
- Test classes in `src/test/java` mirroring main package structure
- Use Spring Boot test starter for integration tests

### Security
- All endpoints require authentication except `/api/auth/**` and user registration
- Services must enforce user isolation by filtering on the current user
- Use `UserService.getLoggedInUser()` to get the authenticated user
