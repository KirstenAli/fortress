# Warning: Current Version Deprecated

# Fortress

Fortress stands as an extension of Spring Security, streamlining the development of secure RESTful web applications by offering essential security elements, allowing you to concentrate on your core business concept. Right out of the box, Fortress furnishes you with:

- JWT Authentication:
   - Token management intricacies are handled by Fortress, relieving you of the burden.

- Role-Based Access Control

- User Account Management  

   Via the Fortress Account Management interface, administrators can:
   - Create new user accounts
   - Modify user details and permissions
   - Delete accounts
   - Lock accounts
   - Perform account searches
   - Initiate password resets   
   
   Fortress enforces a default username and password policy, while also allowing for your tailored implementation.

- Token-based password reset

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

`@ComponentScan(basePackages = {"com.fortress", "com.yourGroup.yourProject"})`

`@EnableMongoRepositories(basePackages ={"com.fortress", "com.yourGroup.yourProject"})`

## Step 3: Configure Your application.properties:
```java
spring.data.mongodb.uri=

spring.data.mongodb.database=

jwt.secret=

jwt.expiration=

password.length = 8

spring.mail.host=

spring.mail.port=

spring.mail.username=

spring.mail.password=

spring.mail.properties.mail.smtp.auth=true

spring.mail.properties.mail.smtp.starttls.enable=true
```

## Step 4: Define a Controller for your Web Page:

You must extend the FortressController class and define a public endpoint as follows: 

```java
@Controller
@RequestMapping("/myWebPage")
public class TestController extends FortressController {

    @GetMapping("/view")
    String myWebPage(){
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
        authz.requestMatchers("/myWebPage/*")
                .permitAll()
                .requestMatchers("/secure/**") //endpoint you wish to secure
                .hasAuthority(Role.ADMIN.toString())
                .anyRequest()
                .authenticated();
    }
}

```

## Accessing the JWT 

After configuring Fortress, it becomes necessary to include a JSON Web Token (JWT) in the request header when attempting to access secure endpoints. The issuing, storing, and validation of the JWT is seamlessly handled by Fortress in the background. You can access and utilize the JWT as follows:

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
## User Management interface

You can access Fortress's default User Management interface from the following endpoints:

/admin/login

/admin/dashboard

![user management](https://github.com/KirstenAli/fortress/assets/86775811/c38672e7-7895-40af-ad01-dbf554bfa0bd)

![edit user](https://github.com/KirstenAli/fortress/assets/86775811/3dbc9384-e89c-483d-8396-b22d490f907e)

![reset password warning](https://github.com/KirstenAli/fortress/assets/86775811/8dc97d4d-3de7-4c57-bf14-3eaa007c4ee5)

# UI Components Provided by Fortress

## Login Modal 
![login modal](https://github.com/KirstenAli/fortress/assets/86775811/8ac25862-7378-4d87-948f-3e19714d5ca4)

## Reset Password Modal 
![reset password modal](https://github.com/KirstenAli/fortress/assets/86775811/ee75a635-03cb-45de-b777-a098f51632cb)

## Change Password Modal 
![change password modal](https://github.com/KirstenAli/fortress/assets/86775811/6715b9e5-65ef-48b2-ac74-cf4efedbf6a8)

## Navbar
![navbar](https://github.com/KirstenAli/fortress/assets/86775811/c4003bb3-df16-4a30-a0dd-24120f30fdde)

## Message Modal
![message modal](https://github.com/KirstenAli/fortress/assets/86775811/31a72d0a-5d14-4c8e-8bd3-2c0da7242dc1)
