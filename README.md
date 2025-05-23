# Application de Gestion des Patients 

## Introduction

Cette application Web JEE permet de gérer des patients dans un hôpital à l’aide de Spring Boot 3.2, Spring MVC, Thymeleaf et Spring Data JPA. Elle est développée dans le cadre d’un TP pratique et comprend trois parties : la gestion des patients, la validation de formulaire avec templates, et la sécurité avec Spring Security.

---

## Partie 1 : Gestion des Patients

### Fonctionnalités
- Affichage de la liste des patients avec pagination
- Recherche par nom
- Suppression de patients

### Extrait de code clé (contrôleur) :
```java
@GetMapping("/index")
public String patients(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "5") int size,
                       @RequestParam(name = "keyword", defaultValue = "") String keyword) {
    Page<Patient> pagePatients = patientRepository.findByNomContains(keyword, PageRequest.of(page, size));
    model.addAttribute("patients", pagePatients.getContent());
    model.addAttribute("pages", new int[pagePatients.getTotalPages()]);
    model.addAttribute("currentPage", page);
    model.addAttribute("keyword", keyword);
    return "patients";
}
```
![Screenshot 2025-05-23 181900](https://github.com/user-attachments/assets/e3a0fe32-0cf0-4afb-a2ba-4f8a795a1e7a)


## Partie 2 : Template et Validation de Formulaire - Application de Gestion des Patients

Cette partie du projet montre comment créer un formulaire avec **Thymeleaf** pour ajouter un patient, tout en intégrant une **validation de formulaire** avec **Bean Validation**.

---

### Objectifs

- Créer un formulaire d'ajout de patient
- Utiliser les annotations de validation (`@NotEmpty`, `@PastOrPresent`, `@Min`, etc.)
- Gérer les erreurs de validation côté serveur et les afficher dans l’interface utilisateur

---

### 1. Entité `Patient` avec Validation

```java
@Entity
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Le nom est obligatoire")
    private String nom;

    @PastOrPresent(message = "La date doit être dans le passé ou aujourd’hui")
    private Date dateNaissance;

    private boolean malade;

    @Min(value = 10, message = "Le score doit être ≥ 10")
    private int score;
}
```
### 2. Formulaire Thymeleaf
```html

<form th:action="@{/save}" th:object="${patient}" method="post">
    <label>Nom:</label>
    <input type="text" th:field="*{nom}">
    <div th:if="${#fields.hasErrors('nom')}" th:errors="*{nom}"></div>

    <label>Date de Naissance:</label>
    <input type="date" th:field="*{dateNaissance}">
    <div th:if="${#fields.hasErrors('dateNaissance')}" th:errors="*{dateNaissance}"></div>

    <label>Score:</label>
    <input type="number" th:field="*{score}">
    <div th:if="${#fields.hasErrors('score')}" th:errors="*{score}"></div>

    <label>Malade:</label>
    <input type="checkbox" th:field="*{malade}">

    <button type="submit">Enregistrer</button>
</form>
```

### Contrôleur - Gérer la validation
```java

@GetMapping("/formPatient")
public String formPatient(Model model) {
    model.addAttribute("patient", new Patient());
    return "formPatient";
}

@PostMapping("/save")
public String savePatient(Model model,
                          @Valid Patient patient,
                          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return "formPatient";
    patientRepository.save(patient);
    return "redirect:/index";
}
```

### Formulaire d'ajout d'un patient :
![Screenshot 2025-05-23 181933](https://github.com/user-attachments/assets/678a0b57-2fe6-43f9-b7c3-293c0a8364a5)
### Rechercher un patient par son nom :
![Screenshot 2025-05-23 182021](https://github.com/user-attachments/assets/027e65a3-b030-4358-829f-e0346f484423)
### Détails d'un patient :
![Screenshot 2025-05-23 182034](https://github.com/user-attachments/assets/4047ec35-117f-4da1-93e2-d9719bad9ced)
### Modifier les informations d'un patient :
![Screenshot 2025-05-23 181956](https://github.com/user-attachments/assets/8b7b2270-b36a-4038-aeee-f490179430b5)
### Supprimer un patient :
![Screenshot 2025-05-23 182051](https://github.com/user-attachments/assets/9b7fb189-d4d3-4876-9d44-0bfb9a7f91d4)



## Partie 3 : Sécurité avec Spring Security - Application de Gestion des Patients

Cette partie intègre la sécurité dans l'application en utilisant **Spring Security**. Elle permet de restreindre l'accès aux pages en fonction des rôles (`USER`, `ADMIN`), d’ajouter une page de login personnalisée et de sécuriser les actions sensibles.

---

### Objectifs

- Protéger les pages avec authentification
- Gérer les rôles des utilisateurs
- Ajouter une page de connexion personnalisée
- Autoriser l’accès aux actions selon le rôle

---

### 1. Dépendance Maven

Assurez-vous d’avoir dans le `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
### 2. Configuration de sécurité (Java)
```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user1 = User.withUsername("user").password("{noop}1234").roles("USER").build();
        UserDetails admin = User.withUsername("admin").password("{noop}1234").roles("USER", "ADMIN").build();
        return new InMemoryUserDetailsManager(user1, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").permitAll();
        http.authorizeHttpRequests()
            .requestMatchers("/webjars/**").permitAll()
            .requestMatchers("/index/**", "/delete/**", "/edit/**", "/formPatient/**", "/save/**").hasRole("ADMIN")
            .anyRequest().authenticated();
        http.exceptionHandling().accessDeniedPage("/403");
        return http.build();
    }
}
```
### 3. Page de connexion personnalisée
login.html dans /templates :

```html
Copy
Edit
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Connexion</title>
</head>
<body>
<h2>Connexion</h2>
<form th:action="@{/login}" method="post">
    <label>Nom d'utilisateur :</label>
    <input type="text" name="username" />
    <br/>
    <label>Mot de passe :</label>
    <input type="password" name="password" />
    <br/>
    <button type="submit">Se connecter</button>
</form>
</body>
</html>
```
### 4. Page d’accès refusé
error.html dans /templates :

```html

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Accès refusé</title>
</head>
<body>
<h2>Erreur 403 - Accès refusé</h2>
<p>Vous n'avez pas les droits nécessaires pour accéder à cette ressource.</p>
<a th:href="@{/logout}">Se déconnecter</a>
</body>
</html>
```
### Page Connexion 

![Screenshot 2025-05-23 181654](https://github.com/user-attachments/assets/9690bb1d-0d70-4e29-9280-7d35ddcb369c)


### Résultat attendu
Seuls les utilisateurs connectés peuvent accéder à l'application.

Les utilisateurs ayant le rôle ADMIN peuvent supprimer ou modifier des patients.

Une page de connexion personnalisée est utilisée.

En cas d'accès non autorisé, l'utilisateur est redirigé vers /error.

