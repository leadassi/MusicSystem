# MusicSystem

Application de gestion pour le Conservatoire Virtuel, une école de musique.

## Fonctionnalités en cours

Les menus de gestion des personnes et des examens ont été ajoutés :

- **MenuPersonnes** : ajouter un étudiant ou un professeur, rechercher une personne par identifiant et afficher toutes les personnes.
- **MenuExamens** : créer un examen, inscrire un étudiant, saisir son résultat (type et note sur 20), et afficher les examens avec leurs inscrits.
- **Menu** : classe abstraite commune aux sous-menus, avec boucle de navigation, lecture et validation des saisies (nombres, dates et valeurs d’énumération) et affichage des erreurs.
- **GestionPersonnes** et **GestionExamens** : services appelés par les menus pour effectuer les opérations sur les données de l’école. La gestion des examens vérifie notamment la salle et l’instrument, les inscriptions en double, la capacité et la validité des notes.

Le projet est encore en cours de développement. Les menus s’appuient sur les classes métier de l’école (étudiants, examens, activités, inscriptions et résultats) et doivent être reliés au point d’entrée de l’application.

## Compilation

Le projet Maven se trouve dans le dossier `oop`. Maven compile les sources Java placées dans `oop/src/main/java` (et ses sous-dossiers), pas celles placées dans `oop/src/main/resources`. Les fichiers Java des menus et de leur gestion sont actuellement rangés dans `oop/src/main/resources`, dans des sous-dossiers portant leur nom : ils ne sont donc pas inclus dans la compilation Maven à cet emplacement.

Pour les compiler avec Maven, placez les fichiers Java dans un package sous `oop/src/main/java` et faites correspondre la déclaration `package` au chemin du dossier. Vous pourrez ensuite compiler depuis `oop` avec :

```powershell
.\mvnw.cmd clean compile
```
