# Car Rental Agency Management — Gestion d'Agence de Location de Voitures

Projet semestriel JEE 4ème année &bull; UIR &bull; Advanced Web Development 2026/2027

---

## 1. Stack Technologique & Versions Épinglées

| Composant | Technologie | Version | Rôle & Justification |
|---|---|---|---|
| **Langage & Runtime** | Java OpenJDK | **17 (Temurin)** | Contrainte du sujet (`--release 17`) |
| **Gestionnaire de build** | Apache Maven | **3.9.9** | Packaging WAR automatisé |
| **Serveur d'application** | Apache Tomcat | **9.0 (JDK 17)** | Servlet 4.0, namespace standard `javax.*` |
| **Framework MVC** | Apache Struts 2 | **6.7.0** | Namespace `javax.*`, intercepteurs d'authentification et CSRF |
| **Persistance / ORM** | Hibernate ORM | **5.6.15.Final** | Standard `javax.persistence` compatible Tomcat 9 (évite Jakarta) |
| **Pool de connexions** | HikariCP via `hibernate-hikaricp` | **5.6.15.Final** | Gestion de pool haute performance |
| **Base de données** | MySQL Server | **8.0** | Encodage `utf8mb4_unicode_ci`, contraintes relationnelles |
| **Driver JDBC** | MySQL Connector/J | **8.4.0** | Driver JDBC officiel MySQL 8 |
| **Sécurité & Hashage** | jBCrypt | **0.4** | Hachage sécurisé des mots de passe (facteur 12) |
| **Interface & Vues** | JSP 2.3 + JSTL 1.2 + Bootstrap 5 | **5.3.3** | Vues sans aucun scriptlet Java (`<%`), composants UI réactifs |
| **Machine Learning** | Smile (Statistical Machine Intelligence) | **3.1.0** | Forêt aléatoire pour prédiction du risque de retard (compatible JDK 17) |
| **Tests unitaires** | JUnit 5 (Jupiter) | **5.11.4** | Validation des règles métier et du hachage de mot de passe |
| **Logging** | Log4j2 + SLF4J | **2.26.1** | Journalisation applicative et Hibernate |

---

## 2. Structure du Projet

```
c:\adv_web_dev\projects\rental\
├── .agent/skills/           # Définition et règles de conformité du projet
├── docs/
│   └── decisions.md         # Registre d'architecture et décisions techniques (AD-01 à AD-10)
├── sql/
│   ├── schema.sql           # DDL complet (tables, contraintes, index)
│   ├── dev-seed.sql         # Données de démonstration (3 rôles BCrypt, catégories, 10 véhicules)
│   └── seed.sql             # Alias pour initialisation automatique
├── src/
│   ├── main/
│   │   ├── java/ma/uir/awd/
│   │   │   ├── model/       # Entités JPA pures (Utilisateur, Vehicule, Reservation, etc.) et énumérations
│   │   │   ├── dao/         # Interfaces DAO génériques et spécifiques (IUtilisateurDao, IVehiculeDao, etc.)
│   │   │   │   └── impl/    # Implémentations Hibernate (requêtes HQL paramétrées, Criteria API)
│   │   │   ├── metier/      # Interfaces des services métier (IAuthMetier, IVehiculeMetier, IRiskMetier)
│   │   │   │   └── impl/    # Logique métier et gestion des transactions (AuthMetierImpl, VehiculeMetierImpl, RiskMetierImpl)
│   │   │   └── web/         # Contrôleurs et infrastructure Struts 2
│   │   │       ├── action/  # Actions Struts (LoginAction, LogoutAction, RegisterAction, VehiculeAction, HomeAction)
│   │   │       ├── interceptor/ # Intercepteurs (AuthInterceptor, RoleInterceptor)
│   │   │       ├── listener/# AppInitListener (Composition Root - seul point d'assemblage des *Impl)
│   │   │       └── util/    # AppContext (Registre de services)
│   │   ├── resources/
│   │   │   ├── app.properties       # Configuration base de données et répertoires externes
│   │   │   ├── hibernate.cfg.xml    # Configuration Hibernate 5 & mapping des entités
│   │   │   ├── log4j2.xml           # Configuration des logs
│   │   │   └── struts.xml           # Configuration des packages, piles d'intercepteurs et routes Struts 2
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── jsp/             # Vues JSP protégées (login, register, vehicules, admin, agent, client)
│   │       │   ├── tags/            # layout.tag (Gabarit partagé Bootstrap 5 avec menu selon le rôle)
│   │       │   └── web.xml          # Déclaration du filtre Struts 2 et du listener de démarrage
│   │       └── assets/              # Fichiers statiques (CSS, JS, images)
│   └── test/
│       └── java/ma/uir/awd/metier/  # Tests unitaires JUnit 5 (PasswordHashTest, VehiculeMetierTest)
├── docker-compose.yml       # Déploiement multi-conteneurs MySQL 8 + Tomcat 9 JDK 17
├── .env.example             # Exemple de variables d'environnement (ignoré par Git)
├── pom.xml                  # Configuration Maven (packaging WAR, plugins compiler 17 & war)
└── README.md
```

---

## 3. Comptes de Démonstration (Dev Seed)

Les mots de passe sont tous chiffrés avec **BCrypt (facteur de coût 12)** :

| Rôle | Email | Mot de passe | Description de l'accès |
|---|---|---|---|
| **ADMIN** | `admin@rental.ma` | `admin123` | Supervision générale, gestion utilisateurs, catégories, ML et paramètres |
| **AGENT** | `agent@rental.ma` | `agent123` | Gestion opérationnelle de la flotte, prises en charge et retours |
| **CLIENT** | `client@rental.ma` | `client123` | Consultation du catalogue, réservations en ligne et historique personnel |

---

## 4. Démarrage Rapide

### Prérequis
- **JDK 17** installé et configuré (`java -version`)
- **Maven 3.9+** installé (`mvn -version`)
- **Docker Desktop** démarré avec le support des conteneurs Linux

### Étape 1 : Cloner et configurer l'environnement
Copiez le fichier d'exemple des variables d'environnement :
```bash
cp .env.example .env
```

### Étape 2 : Compiler et packager l'application
Exécutez la compilation et la création du fichier WAR :
```powershell
mvn clean package
```
> Le fichier généré est situé dans : `target/car-rental.war`.

### Étape 3 : Lancer les conteneurs Docker
Démarrez les services MySQL 8 et Tomcat 9 :
```powershell
docker compose up -d
```
*MySQL 8 initialise automatiquement le schéma (`sql/schema.sql`) et les données de test (`sql/dev-seed.sql`) lors du premier démarrage via le répertoire `/docker-entrypoint-initdb.d/`.*

Pour importer manuellement les fichiers SQL si la base existait déjà :
```powershell
docker compose exec -T mysql mysql -u carapp -pcarapp123 car_rental < sql/schema.sql
docker compose exec -T mysql mysql -u carapp -pcarapp123 car_rental < sql/dev-seed.sql
```

### Étape 4 : Accéder à l'application
Ouvrez votre navigateur sur :
👉 **[http://localhost:8080/car-rental](http://localhost:8080/car-rental)**

1. Connectez-vous avec l'un des comptes de test (`admin@rental.ma`, `agent@rental.ma` ou `client@rental.ma`).
2. Observez que chaque rôle accède à son tableau de bord spécifique et à son menu personnalisé.
3. Cliquez sur **"Véhicules"** pour visualiser la tranche verticale du catalogue avec les statuts colorés :
   - 🟢 **AVAILABLE** (Vert) : Disponible
   - 🟡 **RENTED** (Jaune/Orange) : En location
   - 🔴 **MAINTENANCE** (Rouge) : En révision

---

## 5. Exécution des Tests Unitaires & Audit de Conformité

### Tests unitaires JUnit 5
```powershell
mvn test
```
*Valide le hachage sécurisé BCrypt et la logique métier du catalogue sans dépendance à une base externe.*

### Script d'audit automatique du projet
Vérifiez l'absence de violation des règles architecturales et de sécurité :
```powershell
& "C:\Program Files\Git\bin\bash.exe" .agent/skills/car-rental-jee-project/scripts/audit.sh .
```
> Résultat attendu : `RESULT: no hard violation found.`
