# Spring Framework Guidelines

## Table of Contents

<!-- TOC -->
* [Spring Framework Guidelines](#spring-framework-guidelines)
  * [Table of Contents](#table-of-contents)
  * [Spring Dependency Injection](#spring-dependency-injection)
    * [STRICTLY FORBIDDEN](#strictly-forbidden)
    * [Constructor Parameter Limit Warning](#constructor-parameter-limit-warning)
    * [Test Package Exception](#test-package-exception)
<!-- TOC -->

## Spring Dependency Injection

**Always use constructor injection** for Spring beans in production code.

```java
// Preferred - constructor injection
@Service
public class UserService {
  
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final AuditLogger auditLogger;
  
  public UserService(
      UserRepository userRepository,
      EmailService emailService,
      AuditLogger auditLogger
  ) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.auditLogger = auditLogger;
  }
  
  public User createUser(UserRequest request) {
    var user = userRepository.save(request.toEntity());
    emailService.sendWelcomeEmail(user);
    auditLogger.logUserCreation(user);
    return user;
  }
}

// Avoid - field injection
@Service
public class UserService {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private EmailService emailService;
  
  @Autowired
  private AuditLogger auditLogger;
}
```

### STRICTLY FORBIDDEN

Never use field injections or setter injections in production code.

### Constructor Parameter Limit Warning

**Add a warning comment** if a constructor takes more than 7 parameters (violates Single Responsibility Principle).

```java
// Warning: constructor has too many dependencies
@Service
public class ComplexService {
  
  private final RepoA repoA;
  private final RepoB repoB;
  private final ServiceC serviceC;
  private final ServiceD serviceD;
  private final ServiceE serviceE;
  private final HelperF helperF;
  private final UtilG utilG;
  private final ValidatorH validatorH;
  
  // WARNING: Constructor has 8 parameters - consider refactoring to reduce dependencies
  public ComplexService(
      RepoA repoA,
      RepoB repoB,
      ServiceC serviceC,
      ServiceD serviceD,
      ServiceE serviceE,
      HelperF helperF,
      UtilG utilG,
      ValidatorH validatorH
  ) {
    this.repoA = repoA;
    this.repoB = repoB;
    this.serviceC = serviceC;
    this.serviceD = serviceD;
    this.serviceE = serviceE;
    this.helperF = helperF;
    this.utilG = utilG;
    this.validatorH = validatorH;
  }
}
```

**Exception**: Spring controllers (`@Controller` or `@RestController`) are exempt from this warning.

```java
// OK - Controllers can have many dependencies without warning
@RestController
@RequestMapping("/api/users")
public class UserController {
  
  private final UserService userService;
  private final AuthService authService;
  private final ValidationService validationService;
  private final AuditService auditService;
  private final CacheService cacheService;
  private final MetricsService metricsService;
  private final NotificationService notificationService;
  private final ReportService reportService;
  
  // No warning needed for controllers
  public UserController(
      UserService userService,
      AuthService authService,
      ValidationService validationService,
      AuditService auditService,
      CacheService cacheService,
      MetricsService metricsService,
      NotificationService notificationService,
      ReportService reportService
  ) {
    this.userService = userService;
    this.authService = authService;
    this.validationService = validationService;
    this.auditService = auditService;
    this.cacheService = cacheService;
    this.metricsService = metricsService;
    this.notificationService = notificationService;
    this.reportService = reportService;
  }
}
```

### Test Package Exception

**Use `@Autowired`** in test classes, as constructor injection might not work properly with test frameworks.

```java
// Test class - use @Autowired
@SpringBootTest
class UserServiceTest {
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private TestRestTemplate restTemplate;
  
  @Test
  void shouldCreateUser() {
    var request = new UserRequest("John Doe", "john@example.com");
    var user = userService.createUser(request);
    
    assertThat(user).isNotNull();
    assertThat(user.getName()).isEqualTo("John Doe");
  }
}

// Also acceptable in tests - constructor injection with @Autowired annotation
@SpringBootTest
class UserServiceTest {
  
  private final UserService userService;
  private final UserRepository userRepository;
  
  @Autowired
  UserServiceTest(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }
}
```

Benefits of constructor injection:

- Dependencies are explicit and required

- Easier to test (can use plain constructors in unit tests)

- Immutable dependencies (final fields)

- Prevents circular dependencies at compile time

- No reflection magic needed for testing
