package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Représente une offre d'emploi
 */
public class JobAnnouncement {
    
    // Attributs existants
    private int id;
    private String title;
    private String company;
    private String description;
    private String location;
    private String contractType;
    private String domain;
    private String experienceLevel;
    private Date publishDate;
    private String sourceUrl;
    private List<String> skills;
    private String salary;
    private String publishDateString;
    
    // Nouveaux attributs
    private String secteurActivite;
    private String fonction;
    private String experienceRequise;
    private String niveauEtude;
    private String typeTeletravail;
    private int nombrePostes;

    
    
    
    private String sourceSite;
    
    // Constructeur vide
    public JobAnnouncement() {
        this.skills = new ArrayList<>();
        this.nombrePostes = 1;
    }
    
    // Constructeur avec paramètres essentiels
    public JobAnnouncement(String title, String company, String location) {
        this();
        this.title = title;
        this.company = company;
        this.location = location;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getContractType() {
        return contractType;
    }
    
    public void setContractType(String contractType) {
        this.contractType = contractType;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public String getExperienceLevel() {
        return experienceLevel;
    }
    
    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
    
    public Date getPublishDate() {
        return publishDate;
    }
    
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public List<String> getSkills() {
        return skills;
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
    
    public void addSkill(String skill) {
        if (!this.skills.contains(skill)) {
            this.skills.add(skill);
        }
    }
    
    public String getSalary() {
        return salary;
    }
    
    public void setSalary(String salary) {
        this.salary = salary;
    }
    
    public String getPublishDateString() {
        return publishDateString;
    }

    public void setPublishDateString(String publishDateString) {
        this.publishDateString = publishDateString;
    }

    public String getSecteurActivite() {
        return secteurActivite;
    }

    public void setSecteurActivite(String secteurActivite) {
        this.secteurActivite = secteurActivite;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getExperienceRequise() {
        return experienceRequise;
    }

    public void setExperienceRequise(String experienceRequise) {
        this.experienceRequise = experienceRequise;
    }

    public String getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(String niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public String getTypeTeletravail() {
        return typeTeletravail;
    }

    public void setTypeTeletravail(String typeTeletravail) {
        this.typeTeletravail = typeTeletravail;
    }

    public int getNombrePostes() {
        return nombrePostes;
    }

    public void setNombrePostes(int nombrePostes) {
        this.nombrePostes = nombrePostes;
    }
    
    
    
    
 // Getter
    public String getSourceSite() {
        return sourceSite;
    }

    // Setter
    public void setSourceSite(String sourceSite) {
        this.sourceSite = sourceSite;
    }

    
    
    @Override
    public String toString() {
        return "JobAnnouncement{" +
                "title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", sourceSite='" + sourceSite + '\'' +
                ", contractType='" + contractType + '\'' +
                ", publishDate=" + publishDate +
                ", sourceUrl='" + sourceUrl + '\'' +
                '}';
    }
}