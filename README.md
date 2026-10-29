# MusicSystem

Application de gestion pour le Conservatoire Virtuel, une école de musique.

## Fonctionnalités

Les menus de gestion des personnes et des examens ont été ajoutés :

- **MenuPersonnes** : ajouter un étudiant ou un professeur, rechercher une personne par identifiant et afficher toutes les personnes.
- **MenuExamens** : créer un examen, inscrire un étudiant, saisir son résultat (type et note sur 20), et afficher les examens avec leurs inscrits.
- **Menu** : classe abstraite commune aux sous-menus, avec boucle de navigation, lecture et validation des saisies (nombres, dates et valeurs d’énumération) et affichage des erreurs.
- **GestionPersonnes** et **GestionExamens** : services appelés par les menus pour effectuer les opérations sur les données de l’école. La gestion des examens vérifie notamment la salle et l’instrument, les inscriptions en double, la capacité et la validité des notes.

Les classes métier utilisées par ces menus (personnes, école, salles, instruments, activités, participations et résultats) sont présentes dans le projet. L'application reste en développement : le démarrage Spring Boot n'est pas encore relié à ces menus console, et les salles/instruments doivent être ajoutés à l'école avant de pouvoir créer un examen.

## Compilation

Le projet Maven se trouve dans le dossier `oop`. Les sources de l'application sont dans `oop/src/main/java/com/project/oop` et déclarent le package `com.project.oop`. Les tests sont dans `oop/src/test/java/com/project/oop`. Les ressources non Java, comme `application.properties`, restent dans `oop/src/main/resources`.

Pour compiler le projet depuis `oop` :

```powershell
.\mvnw.cmd clean compile
```

Pour compiler et exécuter les tests :

```powershell
.\mvnw.cmd test
```
