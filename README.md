## 🧩 Description du projet

Ce projet consiste à développer une application Java de bureau permettant l’analyse et l’exploitation d’offres d’emploi collectées depuis différentes sources. L’objectif est d’aider les utilisateurs à explorer le marché de l’emploi, analyser les tendances et obtenir des recommandations adaptées à leur profil.

## 🚀 Étapes de réalisation du projet

### 1. Collecte et stockage des données
- Récupération des offres d’emploi depuis différentes sources (scraping).
- Stockage des données dans une base de données relationnelle.
- Gestion des entités principales : offres d’emploi, compétences, entreprises, villes, types de contrat, etc.

### 2. Nettoyage et normalisation des données
- Nettoyage des textes (suppression de la ponctuation, mise en minuscule, suppression des accents).
- Normalisation des informations :
  - Villes (ex : Casa → Casablanca)
  - Types de contrat (CDI, CDD, Stage, Freelance)
  - Niveaux d’expérience (Débutant, Junior, Confirmé, Senior)
- Extraction des compétences à partir des descriptions d’offres.

### 3. Analyse et traitement des données
- Analyse des tendances du marché (compétences les plus demandées, villes les plus actives, domaines dominants).
- Calcul de statistiques globales à partir des données stockées.
- Mise en place d’un système de matching entre les compétences d’un CV et les offres disponibles.

### 4. Développement de l’interface graphique
- Création d’une interface desktop avec **Java Swing**.
- Utilisation de `JFrame` pour les fenêtres principales et `JPanel` pour l’organisation modulaire de l’interface.
- Tableau de bord principal pour consulter les offres d’emploi.
- Filtres pour affiner les résultats (ville, contrat, source, domaine).
- Visualisation des statistiques avec des graphiques.

### 5. Visualisation des données
- Intégration de **JFreeChart** pour afficher :
  - Graphiques des compétences les plus demandées.
  - Répartition des offres par ville.
  - Analyse par domaine d’activité.

### 6. Tests et validation
- Mise en place de tests unitaires avec **JUnit 5**.
- Tests des services principaux :
  - Nettoyage et normalisation des données.
  - Extraction des compétences.
  - Calcul des scores de matching.
  - Accès à la base de données.
- Vérification de la robustesse et de la fiabilité de l’application.

## 🛠️ Technologies et bibliothèques utilisées
- **Java**
- **Java Swing** (interface graphique)
- **JFreeChart** (visualisation des données)
- **JUnit 5** (tests unitaires)
- **JDBC** (accès base de données)
- **Base de données relationnelle**

## ✅ Résultat final
L’application permet à l’utilisateur de :
- Explorer des offres d’emploi de manière interactive.
- Analyser les tendances du marché du travail.
- Visualiser des statistiques claires sous forme de graphiques.
- Obtenir des recommandations d’offres adaptées à ses compétences.
