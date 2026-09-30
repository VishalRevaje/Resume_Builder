package com.resume.resume_builder.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // USER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;


    // =========================================================
    // PERSONAL INFORMATION
    // =========================================================

    private String fullName;

    private String location;

    private String email;

    private String mobile;

    private String linkedin;

    private String github;


    // =========================================================
    // RESUME FORMATTING
    // =========================================================

    private String fontFamily;

    private Integer fontSize;


    // =========================================================
    // PROFESSIONAL SUMMARY
    // =========================================================

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String professionalSummary;


    // =========================================================
    // SECTION ORDER
    //
    // This stores the order selected by the user.
    //
    // Example:
    //
    // summary,education,skills,projects,certifications
    //
    // Or:
    //
    // summary,projects,skills,education,certifications
    // =========================================================

    @Column(
            name = "section_order",
            length = 500
    )
    private String sectionOrder =
            "summary,education,skills,projects,certifications";


    // =========================================================
    // CUSTOM SECTION TITLES
    // =========================================================

    @Column(
            name = "summary_title",
            length = 100
    )
    private String summaryTitle =
            "Professional Summary";


    @Column(
            name = "education_title",
            length = 100
    )
    private String educationTitle =
            "Education";


    @Column(
            name = "skills_title",
            length = 100
    )
    private String skillsTitle =
            "Technical Skills";


    @Column(
            name = "projects_title",
            length = 100
    )
    private String projectsTitle =
            "Projects";


    @Column(
            name = "certifications_title",
            length = 100
    )
    private String certificationsTitle =
            "Certifications";


    // =========================================================
    // EDUCATION
    // =========================================================

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Education> educations =
            new ArrayList<>();


    // =========================================================
    // SKILLS
    // =========================================================

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Skill> skills =
            new ArrayList<>();


    // =========================================================
    // PROJECTS
    // =========================================================

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Project> projects =
            new ArrayList<>();


    // =========================================================
    // CERTIFICATIONS
    // =========================================================

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Certification> certifications =
            new ArrayList<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Resume() {
    }


    // =========================================================
    // ID
    // =========================================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // USER
    // =========================================================

    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    // =========================================================
    // PERSONAL INFORMATION
    // =========================================================

    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobile() {
        return mobile;
    }


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getLinkedin() {
        return linkedin;
    }


    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }


    public String getGithub() {
        return github;
    }


    public void setGithub(String github) {
        this.github = github;
    }


    // =========================================================
    // FORMATTING
    // =========================================================

    public String getFontFamily() {
        return fontFamily;
    }


    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }


    public Integer getFontSize() {
        return fontSize;
    }


    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }


    // =========================================================
    // PROFESSIONAL SUMMARY
    // =========================================================

    public String getProfessionalSummary() {
        return professionalSummary;
    }


    public void setProfessionalSummary(
            String professionalSummary
    ) {

        this.professionalSummary =
                professionalSummary;
    }


    // =========================================================
    // SECTION ORDER
    // =========================================================

    public String getSectionOrder() {

        return sectionOrder;
    }


    public void setSectionOrder(
            String sectionOrder
    ) {

        if (
                sectionOrder == null
                        ||
                        sectionOrder.trim().isEmpty()
        ) {

            this.sectionOrder =
                    "summary,education,skills,projects,certifications";

            return;
        }

        this.sectionOrder =
                sectionOrder.trim();
    }


    /**
     * Returns the section order as a List.
     *
     * This is used by Thymeleaf preview
     * and can also be used by other views.
     *
     * Old resumes are automatically given
     * the missing sections at the end.
     */
    @Transient
    public List<String> getSectionOrderList() {

        List<String> result =
                new ArrayList<>();


        String order =
                sectionOrder;


        if (
                order == null
                        ||
                        order.trim().isEmpty()
        ) {

            order =
                    "summary,education,skills,projects,certifications";
        }


        String[] parts =
                order.split(",");


        for (String part : parts) {

            if (part == null) {
                continue;
            }


            String key =
                    part.trim().toLowerCase();


            if (
                    key.equals("summary")
                            ||
                            key.equals("education")
                            ||
                            key.equals("skills")
                            ||
                            key.equals("projects")
                            ||
                            key.equals("certifications")
            ) {

                if (!result.contains(key)) {

                    result.add(key);
                }
            }
        }


        /*
         * Add any missing section to the end.
         *
         * This is important for resumes created
         * before this feature was added.
         */

        String[] defaultSections = {

                "summary",
                "education",
                "skills",
                "projects",
                "certifications"
        };


        for (
                String defaultSection :
                defaultSections
        ) {

            if (
                    !result.contains(
                            defaultSection
                    )
            ) {

                result.add(defaultSection);
            }
        }


        return result;
    }


    // =========================================================
    // SUMMARY TITLE
    // =========================================================

    public String getSummaryTitle() {

        return isBlank(summaryTitle)
                ? "Professional Summary"
                : summaryTitle;
    }


    public void setSummaryTitle(
            String summaryTitle
    ) {

        this.summaryTitle =
                isBlank(summaryTitle)
                        ? "Professional Summary"
                        : summaryTitle.trim();
    }


    // =========================================================
    // EDUCATION TITLE
    // =========================================================

    public String getEducationTitle() {

        return isBlank(educationTitle)
                ? "Education"
                : educationTitle;
    }


    public void setEducationTitle(
            String educationTitle
    ) {

        this.educationTitle =
                isBlank(educationTitle)
                        ? "Education"
                        : educationTitle.trim();
    }


    // =========================================================
    // SKILLS TITLE
    // =========================================================

    public String getSkillsTitle() {

        return isBlank(skillsTitle)
                ? "Technical Skills"
                : skillsTitle;
    }


    public void setSkillsTitle(
            String skillsTitle
    ) {

        this.skillsTitle =
                isBlank(skillsTitle)
                        ? "Technical Skills"
                        : skillsTitle.trim();
    }


    // =========================================================
    // PROJECTS TITLE
    // =========================================================

    public String getProjectsTitle() {

        return isBlank(projectsTitle)
                ? "Projects"
                : projectsTitle;
    }


    public void setProjectsTitle(
            String projectsTitle
    ) {

        this.projectsTitle =
                isBlank(projectsTitle)
                        ? "Projects"
                        : projectsTitle.trim();
    }


    // =========================================================
    // CERTIFICATIONS TITLE
    // =========================================================

    public String getCertificationsTitle() {

        return isBlank(certificationsTitle)
                ? "Certifications"
                : certificationsTitle;
    }


    public void setCertificationsTitle(
            String certificationsTitle
    ) {

        this.certificationsTitle =
                isBlank(certificationsTitle)
                        ? "Certifications"
                        : certificationsTitle.trim();
    }


    // =========================================================
    // EDUCATION
    // =========================================================

    public List<Education> getEducations() {

        return educations;
    }


    public void setEducations(
            List<Education> educations
    ) {

        this.educations = educations;
    }


    // =========================================================
    // SKILLS
    // =========================================================

    public List<Skill> getSkills() {

        return skills;
    }


    public void setSkills(
            List<Skill> skills
    ) {

        this.skills = skills;
    }


    // =========================================================
    // PROJECTS
    // =========================================================

    public List<Project> getProjects() {

        return projects;
    }


    public void setProjects(
            List<Project> projects
    ) {

        this.projects = projects;
    }


    // =========================================================
    // CERTIFICATIONS
    // =========================================================

    public List<Certification> getCertifications() {

        return certifications;
    }


    public void setCertifications(
            List<Certification> certifications
    ) {

        this.certifications = certifications;
    }


    // =========================================================
    // HELPER
    // =========================================================

    private boolean isBlank(String value) {

        return value == null
                ||
                value.trim().isEmpty();
    }
}