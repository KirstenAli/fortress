# Fortress

Fortress stands as an extension of Spring Security, streamlining the development of secure web applications by offering essential security elements, allowing you to concentrate on your core business concept. Right out of the box, Fortress furnishes you with:

- JWT Authentication:
   - Token management intricacies are handled by Fortress, relieving you of the burden.

- Role-based Access Control

- User Account Management  

   Via the Fortress Account Management web interface, administrators can:
   - Create new user accounts
   - Modify user details and permissions
   - Delete accounts
   - Lock accounts
   - Perform account searches
   - Initiate password resets   
   
   Fortress enforces a default username and password policy, while also allowing for your tailored implementation.

- Token-based password reset for users

- Smooth error propagation to the frontend

- Abstract CRUD controller and service classes for simplifying CRUD operations, adhering to the DTO design pattern

- Extensive customization capabilities

# Getting Started with Fortress

## Step 1: Dependencies 

We need three dependencies in our build.gradle:
- Spring Security 
- Thymeleaf 
- Fortress 

`implementation 'org.springframework.boot:spring-boot-starter-security'`

`implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'`

Since Fortress is a module in our project we can simply do

`implementation project(':security')`


## Step 2: Define Component Scanning

We need to notify the Spring Framework about our utilization of Fortress's Controllers, Services, Repositories, and other essential components. To achieve this, introduce the subsequent annotations at the class level within a configuration or main class:

`@ComponentScan(basePackages = {"com.fortress.security", "com.yourGroup.yourProject"})`

`@EnableMongoRepositories(basePackages ={"com.fortress.security", "com.yourGroup.yourProject"})`


## Step 3: Configure Your application.properties:

`spring.data.mongodb.uri=`

`spring.data.mongodb.database=`

`jwt.secret=`

`jwt.expiration=`

`password.length = 8`

`spring.mail.host=`

`spring.mail.port=`

`spring.mail.username=`

`spring.mail.password=`

`spring.mail.properties.mail.smtp.auth=true`

`spring.mail.properties.mail.smtp.starttls.enable=true`

## Step 4: Define a Controller for your Web Page:

You must define two public endpoints as follows: 

```java
@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/login")
    ModelAndView login(){
        return new ModelAndView("login",
                "redirect",
                "myWebPage"); //replace with the name of your web page
    }

    @GetMapping("/myWebPage") //replace with the name of your web page
    String testPage(){
        return "myWebPage"; //replace with the name of your web page
    }
}

```

## Step 5: Configure Permissions

Finally, you must extend the FortressConfiguration class and implement the configure method as follows:

```java
@EnableWebSecurity // do not forget this annotation
@Configuration // do not forget this annotation
public class SecurityConfiguration extends FortressConfiguration {

@Override
public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authz,
HttpSecurity http) {
        authz.requestMatchers("/test/**")
                .permitAll()
                .requestMatchers("/secure/**")
                .hasAuthority(Role.ADMIN.toString())
                .anyRequest()
                .authenticated();
    }
}

```

## Accessing the JWT 

After configuring Fortress, it becomes necessary to include a JSON Web Token (JWT) in the request header when attempting to access secure endpoints. The issuing, storing, and validating of the JWT is seamlessly handled by Fortress in the background. You can access and utilize the JWT as follows:

```javascript
const token = localStorage.getItem('access_token');
const expirationTime = localStorage.getItem('expiration_time');

try {
       const response = await fetch(endpoint, {
           method: 'GET',
           headers: {
               'Authorization': 'Bearer ' + token
           }
       });
} catch (e){
       console.log(e);
}
```

## Error Propagation

Effortlessly return a message to the frontend by throwing a FrotressBeacon as follows:

```java
throw new FortressBeacon("My Message");
```

Fortress will intercept these exceptions and return the following JSON response:

```javascript
{
 "message": "My Message",
 "Status": 400
}
```

## Defining a Custom Username or Password Policy

You must implement the Validator interface:

```java
public class PasswordValidator implements Validator {

    public static final String passwordPolicy ="Must contain at least one lowercase letter, " +
            "uppercase letter, one number, one special character, " +
            "no white spaces, and be at least 8 characters long";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public boolean validate(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}

```

Next, you must register your custom validator with the IoC container. Create a configuration class as follows: 

```java
@Configuration
public class Config{
    @Bean
    public Validator passwordValidator() {
        return new PasswordValidator();
    }
}
```

## Abstract CRUD Controller and Service classes

These classes provide you with common CRUD operations while adhering to the DTO design pattern:

```java
   public abstract class CRUDController<T extends EntityInterface, U extends DTOInterface>{
    private final CRUDService<T, U> CRUDService;

    public CRUDController(CRUDService<T,U> CRUDService) {
        this.CRUDService = CRUDService;
    }

    @PostMapping("add")
    void add(@RequestBody U dto){
        CRUDService.add(dto);
    }

    @GetMapping("get/{id}")
    U get(@PathVariable String id){
        return CRUDService.get(id);
    }

    @PostMapping("update")
    void update(@RequestBody U dto){
        CRUDService.update(dto);
    }

    @PostMapping("delete")
    void delete(@RequestBody U dto){
        CRUDService.delete(dto);
    }

    @GetMapping("getAll")
    List<U> getAll(){
        return CRUDService.getAll();
    }

    @PostMapping("deleteAllById")
    void deleteAllById(@RequestBody Iterable<String> ids){
        CRUDService.deleteAllById(ids);
    }

```

```java
public abstract class CRUDService<T extends EntityInterface, U extends DTOInterface> {

    private final MongoRepository<T, String> repository;

    @Autowired
    protected ModelMapper modelMapper;

    public CRUDService(MongoRepository<T, String> repository) {
        this.repository = repository;
    }

    public U get(String id){
        var entity = findById(id);

        if(entity==null)
            return null;

        return mapToDTO(entity);
    }

    public void update(U dto){
        String id = dto.getId();
        T currentEntity = findById(id);
        assert currentEntity != null;

        updateEntity(dto, currentEntity);

        repository.save(currentEntity);
    }

    public void add(U dto){
        if(beforeAdd(dto)) {
            var entity = (T) dto.getEntity();
            repository.save(entity);
            dto.setId(entity.getId());
            update(dto);
            afterAdd(dto);
        }
    }

    public List<U> getAll(){
        List<T> users = repository.findAll();

        return mapToDTO(users);
    }

    public void deleteAllById(Iterable<String> ids){
        repository.deleteAllById(ids);
    }

    public abstract void updateEntity(U dto, T currentEntity);

    public abstract boolean beforeAdd(U dto);
    public abstract void afterAdd(U dto);

    public void delete(U dto){
        repository.deleteById(dto.getId());

    }

    public T findById(String id){
        return repository.findById(id).orElse(null);
    }

    public List<U> mapToDTO(List<T> entities){
        return entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public U mapToDTO(T entity){
        var dto = entity.getDTO();
        modelMapper.map(entity, dto);

        return (U) dto;
    }

    public T mapToEntity(U dto){
        var entity = dto.getEntity();
        modelMapper.map(dto, entity);

        return (T) entity;
    }
}
```

To use these classes, your Entity and DTO must implement the EntityInterface and DTOInterface respectively.  

## Accessing the User Management Interface

You can access Fortress's default User Management interface from the following endpoints:

/admin/login OR
/admin/dashboard

![user management](https://github.com/KirstenAli/fortress/assets/86775811/a973b700-be4d-412f-b20b-a350bda8f70f)

![edit user](https://github.com/KirstenAli/fortress/assets/86775811/2675d30d-4ed1-4c64-951b-69b933c9de5c)

![reset password warning](https://github.com/KirstenAli/fortress/assets/86775811/ca8bbbb2-04c2-4905-a2be-980235da9f9e)

# UI Components Provided by Fortress

## Login Modal 
![login modal](https://github.com/KirstenAli/fortress/assets/86775811/b6b63e77-0c45-444c-8daf-d40b756c0efb)

## Reset Password Modal 
![reset password modal](https://github.com/KirstenAli/fortress/assets/86775811/f4d3a9ef-cc39-4ad9-9878-d4f314306978)

## Change Password Modal 
![change password modal](https://github.com/KirstenAli/fortress/assets/86775811/deb5493d-b7fd-4dcd-a11c-09eb01b0fd4b)

## Navbar
![navbar](https://github.com/KirstenAli/fortress/assets/86775811/b97d9211-f6f9-44a5-a168-50e423010136)

## Message Modal
![message modal](https://github.com/KirstenAli/fortress/assets/86775811/d7cb6cf2-ee43-4816-925c-72df57b1d37b)
