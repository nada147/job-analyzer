-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3307
-- Généré le : mer. 11 fév. 2026 à 14:17
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `jobs_maroc_db`
--
CREATE DATABASE IF NOT EXISTS `jobs_maroc_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `jobs_maroc_db`;

-- --------------------------------------------------------

--
-- Structure de la table `job_announcements`
--

DROP TABLE IF EXISTS `job_announcements`;
CREATE TABLE `job_announcements` (
  `id` int(11) NOT NULL,
  `title` varchar(500) NOT NULL,
  `company` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `contract_type` varchar(50) DEFAULT NULL,
  `experience_level` varchar(100) DEFAULT NULL,
  `experience_requise` varchar(100) DEFAULT NULL,
  `niveau_etude` varchar(100) DEFAULT NULL,
  `secteur_activite` varchar(200) DEFAULT NULL,
  `fonction` varchar(200) DEFAULT NULL,
  `type_teletravail` varchar(50) DEFAULT NULL,
  `nombre_postes` int(11) DEFAULT 1,
  `salary` varchar(100) DEFAULT NULL,
  `source_url` varchar(1000) DEFAULT NULL,
  `source_site` varchar(50) DEFAULT NULL,
  `publish_date` date DEFAULT NULL,
  `publish_date_string` varchar(100) DEFAULT NULL,
  `scraped_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `job_classifications`
--

DROP TABLE IF EXISTS `job_classifications`;
CREATE TABLE `job_classifications` (
  `id` int(11) NOT NULL,
  `job_id` int(11) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `confidence` decimal(5,2) DEFAULT NULL,
  `classified_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `job_skills`
--

DROP TABLE IF EXISTS `job_skills`;
CREATE TABLE `job_skills` (
  `job_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `scraping_history`
--

DROP TABLE IF EXISTS `scraping_history`;
CREATE TABLE `scraping_history` (
  `id` int(11) NOT NULL,
  `source_site` varchar(50) DEFAULT NULL,
  `jobs_scraped` int(11) DEFAULT NULL,
  `execution_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` varchar(20) DEFAULT NULL,
  `error_message` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `skills`
--

DROP TABLE IF EXISTS `skills`;
CREATE TABLE `skills` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `job_announcements`
--
ALTER TABLE `job_announcements`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `source_url` (`source_url`) USING HASH,
  ADD KEY `idx_location` (`location`),
  ADD KEY `idx_contract` (`contract_type`),
  ADD KEY `idx_source_site` (`source_site`),
  ADD KEY `idx_publish_date` (`publish_date`),
  ADD KEY `idx_secteur` (`secteur_activite`);

--
-- Index pour la table `job_classifications`
--
ALTER TABLE `job_classifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `job_id` (`job_id`);

--
-- Index pour la table `job_skills`
--
ALTER TABLE `job_skills`
  ADD PRIMARY KEY (`job_id`,`skill_id`),
  ADD KEY `skill_id` (`skill_id`);

--
-- Index pour la table `scraping_history`
--
ALTER TABLE `scraping_history`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `skills`
--
ALTER TABLE `skills`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `job_announcements`
--
ALTER TABLE `job_announcements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `job_classifications`
--
ALTER TABLE `job_classifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `scraping_history`
--
ALTER TABLE `scraping_history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `skills`
--
ALTER TABLE `skills`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `job_classifications`
--
ALTER TABLE `job_classifications`
  ADD CONSTRAINT `job_classifications_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job_announcements` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `job_skills`
--
ALTER TABLE `job_skills`
  ADD CONSTRAINT `job_skills_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job_announcements` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `job_skills_ibfk_2` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
