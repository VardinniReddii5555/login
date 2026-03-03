# OIDC Integration Guide for this project

This project currently supports:
- Username/password login via `/login`.
- Google login via Firebase JS on `login.jsp` posting an ID token to `/google-login`.

A cleaner OIDC setup is to let Spring Security handle the Authorization Code flow directly.

## 1) Add Spring Security OIDC client dependency

Update `pom.xml` with:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

## 2) Configure OIDC provider and client in `application.properties`

Example (replace values with your IdP values):

```properties
spring.security.oauth2.client.registration.univ.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.univ.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.univ.scope=openid,profile,email
spring.security.oauth2.client.registration.univ.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.univ.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

spring.security.oauth2.client.provider.univ.issuer-uri=https://YOUR-IDP/.well-known/openid-configuration
```

If your IdP provides a discovery URL, `issuer-uri` is preferred.

## 3) Replace current `SecurityConfig` with an HTTP security filter chain

Current `SecurityConfig` only defines a password encoder. Add `SecurityFilterChain` so:
- `/css/**`, `/register`, `/login`, and `/oauth2/**` are public.
- Protected pages (e.g., `/dashboard`) require authentication.
- OIDC login is enabled with `.oauth2Login(...)`.
- Logout redirects to `/login`.

Skeleton:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http
    .authorizeHttpRequests(auth -> auth
      .requestMatchers("/css/**", "/register", "/login", "/oauth2/**").permitAll()
      .anyRequest().authenticated()
    )
    .oauth2Login(oauth -> oauth
      .loginPage("/login")
      .defaultSuccessUrl("/dashboard", true)
    )
    .logout(logout -> logout
      .logoutSuccessUrl("/login")
    );
  return http.build();
}
```

Keep the existing `PasswordEncoder` bean for manual login unless you fully migrate away from local auth.

## 4) Replace Firebase login button in `login.jsp`

Instead of Firebase JS + popup + token POST, use a plain link to Spring’s OIDC entry point:

```html
<a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/oauth2/authorization/univ">
  Log in with University SSO
</a>
```

This removes Firebase SDK dependency from the UI.

## 5) Upsert local user after OIDC login

Today user creation/update for SSO happens in `AuthController#googleLogin`.

With OIDC, move this logic to one of these approaches:
- `AuthenticationSuccessHandler`, or
- custom `OidcUserService`.

On successful login:
1. Read claims (`email`, `name`, `sub`).
2. Enforce allowed domain (`@kanchiuniv.ac.in`) if needed.
3. Create/update `Users` table with `registration_mode = GOOGLE_SSO` (or rename to `OIDC_SSO`).
4. Start audit using `LoginAuditService.startSessionAudit(...)`.

## 6) Keep or retire existing endpoint

After OIDC rollout:
- Deprecate `/google-login` and Firebase config class/files.
- Or keep both paths temporarily behind feature flags.

## 7) Testing checklist

1. `/login` loads without auth.
2. Clicking SSO button redirects to IdP.
3. Callback creates/updates user.
4. Unauthorized email domain is rejected.
5. Dashboard is accessible after login.
6. Logout returns to `/login` and invalidates session.

## Migration notes specific to this codebase

- `AuthController` currently logs audit with `LoginModes.MANUAL` for Google login; this should be corrected to an SSO mode during migration.
- `login.jsp` currently performs domain check in browser JS; keep equivalent validation on server side in OIDC success handling.
- `FirebaseConfig` and `firebase-service-account.json` become unnecessary if all social login is moved to OIDC.