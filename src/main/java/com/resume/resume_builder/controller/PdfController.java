package com.resume.resume_builder.controller;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.resume.resume_builder.entity.Resume;
import com.resume.resume_builder.service.ResumeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;

import java.io.ByteArrayInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Controller
public class PdfController {

    private final ResumeService resumeService;

    public PdfController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // =========================================================
    // DOWNLOAD PDF
    // =========================================================

    @GetMapping("/resumes/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long id,
            Authentication authentication) throws Exception {

        Resume resume = resumeService.getResumeById(
                id,
                authentication.getName()
        );

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        String html = createPdfHtml(resume);

        PdfRendererBuilder builder =
                new PdfRendererBuilder();

        // Register Arial Regular
        builder.useFont(
                () -> getClass().getResourceAsStream("/fonts/arial.ttf"),
                "Arial",
                400,
                com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle.NORMAL,
                true
        );

        // Register Arial Bold
        builder.useFont(
                () -> getClass().getResourceAsStream("/fonts/arialbd.ttf"),
                "Arial",
                700,
                com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle.NORMAL,
                true
        );

        // Register Arial Italic
        builder.useFont(
                () -> getClass().getResourceAsStream("/fonts/ariali.ttf"),
                "Arial",
                400,
                com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle.ITALIC,
                true
        );

        // Register Arial Bold Italic
        builder.useFont(
                () -> getClass().getResourceAsStream("/fonts/arialbi.ttf"),
                "Arial",
                700,
                com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle.ITALIC,
                true
        );

        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        builder.run();

        // OpenHTMLToPDF can generate an incorrect /ToUnicode mapping
        // for the Arial hyphen glyph. Repair the generated PDF before
        // returning it so copy/paste produces a normal ASCII hyphen.
        byte[] generatedPdf = outputStream.toByteArray();
        byte[] pdf = repairPdfToUnicode(generatedPdf);

        String fileName =
                (resume.getFullName() != null
                        ? resume.getFullName()
                        : "resume")
                        .replaceAll(
                                "[^a-zA-Z0-9-_]",
                                "_"
                        )
                        + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    // =========================================================
    // CREATE PDF HTML
    // =========================================================

    private String createPdfHtml(Resume resume) {

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml">

                <head>

                    <meta charset="UTF-8" />

                    <style>

                        @page {
                            size: A4;
                            margin: 30px 34px 30px 34px;
                        }

                        * {
                            box-sizing: border-box;
                            font-family: Arial, sans-serif;
                        }

                        body {
                            margin: 0;
                            padding: 0;
                            background: #ffffff;
                            color: #111111;
                            font-family: Arial, sans-serif;
                            font-size: 10.5pt;
                        }


                        /* =================================================
                           HEADER
                           ================================================= */

                        .header-table {
                            width: 100%;
                            border-collapse: collapse;
                            table-layout: fixed;
                            margin-bottom: 5px;
                        }

                        .header-left {
                            width: 50%;
                            padding: 0;
                            vertical-align: top;
                            text-align: left;
                        }

                        .header-right {
                            width: 50%;
                            padding: 0;
                            vertical-align: top;
                            text-align: left;
                        }

                        .header-row {
                            height: 20px;
                            line-height: 20px;
                        }

                        .name {
                            font-size: 17pt;
                            font-weight: bold;
                        }

                        .label {
                            font-weight: bold;
                        }

                        .header-link {
                            font-size: 10pt;
                        }

                        .header-block {
                            margin-bottom: 5px;
                            text-align: center;
                        }

                        .header-block .name {
                            display: block;
                            font-size: 17pt;
                            font-weight: bold;
                            line-height: 1.3;
                            margin-bottom: 3px;
                        }

                        .contact-line {
                            font-size: 10pt;
                            line-height: 1.5;
                            text-align: center;
                        }

                        .contact-label {
                            font-weight: bold;
                        }

                        .project-link {
                            font-weight: normal;
                            color: #1a56db;
                            text-decoration: underline;
                        }

                        .project-tech {
                            font-size: 10pt;
                            font-style: italic;
                            margin-bottom: 3px;
                        }


                        /* =================================================
                           SECTIONS
                           ================================================= */

                        .section {
                            margin-top: 7px;
                            margin-bottom: 5px;
                        }

                        .section-title {
                            border-top: 2px solid #999999;
                            padding-top: 3px;
                            margin-bottom: 6px;
                            font-size: 12pt;
                            font-weight: bold;
                            line-height: 1.2;
                        }


                        /* =================================================
                           PROFESSIONAL SUMMARY
                           ================================================= */

                        .summary {
                            margin: 0;
                            font-size: 10.5pt;
                            line-height: 1.35;
                            text-align: left;
                        }

                        .summary p {
                            margin-top: 0;
                            margin-bottom: 3px;
                        }


                        /* =================================================
                           EDUCATION
                           ================================================= */

                        .education-table {
                            width: 100%;
                            border-collapse: collapse;
                            table-layout: fixed;
                            margin-bottom: 6px;
                        }

                        .education-left {
                            width: 80%;
                            padding: 0;
                            vertical-align: top;
                        }

                        .education-date {
                            width: 20%;
                            padding: 0;
                            vertical-align: top;
                            text-align: right;
                            white-space: nowrap;
                        }

                        .degree {
                            font-weight: bold;
                            font-size: 10.5pt;
                            line-height: 1.25;
                        }

                        .education-details {
                            font-size: 10pt;
                            line-height: 1.25;
                        }


                        /* =================================================
                           COMMON BULLET COLUMN

                           REDUCED SPACING:
                           width: 35px
                           padding-left: 20px
                           ================================================= */

                        .skills-bullet,
                        .project-bullet,
                        .certification-bullet {

                            width: 12px;

                            padding-left: 20px;

                            padding-right: 0;

                            vertical-align: top;

                            text-align: left;
                        }


                        /* =================================================
                           FILLED CIRCLE

                           Used for:
                           Technical Skills
                           Certifications
                           ================================================= */

                        .filled-bullet {

                            display: inline-block;

                            font-size: 10.5pt;
                            line-height: 1;

                            vertical-align: middle;
                        }


                        /* =================================================
                           TECHNICAL SKILLS
                           ================================================= */

                        .skills-table {

                            width: 100%;

                            border-collapse: collapse;

                            table-layout: fixed;

                            font-size: 10.5pt;
                        }

                        .skills-bullet {

                            width: 12px;

                            padding-top: 4px;

                            padding-bottom: 4px;

                            padding-left: 20px;
                        }

                        .skills-category {

                            width: 250px;

                            padding: 3px 0;

                            vertical-align: top;

                            font-weight: bold;

                            white-space: nowrap;
                        }

                        .skills-values {

                            width: auto;

                            padding: 3px 0;

                            vertical-align: top;

                            line-height: 1.3;
                        }


                        /* =================================================
                           PROJECTS
                           ================================================= */

                        .project {

                            margin-bottom: 8px;

                            font-size: 10.5pt;

                            line-height: 1.3;
                        }

                        .project-name {

                            font-weight: bold;

                            margin-bottom: 4px;
                        }

                        .project-list {

                            width: 100%;

                            border-collapse: collapse;

                            table-layout: fixed;

                            margin: 0;

                            padding: 0;
                        }


                        /* =================================================
                           PROJECT BULLET

                           REDUCED SPACING:
                           width: 35px
                           padding-left: 20px
                           ================================================= */

                        .project-bullet {

                            width: 12px;

                            padding-top: 1px;

                            padding-bottom: 0;

                            padding-left: 20px;

                            padding-right: 0;

                            vertical-align: top;

                            text-align: left;

                            line-height: 1.3;
                        }

                        .project-bullet .filled-bullet {

                            line-height: 1.3;

                            vertical-align: baseline;
                        }


                        /* =================================================
                           HOLLOW CIRCLE

                           CSS-DRAWN CIRCLE

                           This prevents the PDF from showing "#".
                           ================================================= */

                        .project-bullet-circle {

                            display: inline-block;

                            width: 6px;
                            height: 6px;

                            background: transparent;

                            border: 1px solid #111111;

                            border-radius: 50%;

                            vertical-align: middle;
                        }

                        .project-text {

                            width: auto;

                            padding: 1px 0;

                            vertical-align: top;

                            text-align: left;

                            line-height: 1.3;
                        }


                        /* =================================================
                           CERTIFICATIONS
                           ================================================= */

                        .certifications-table {

                            width: 100%;

                            border-collapse: collapse;

                            table-layout: fixed;

                            font-size: 10.5pt;
                        }

                        .certification-bullet {

                            width: 12px;

                            padding-top: 5px;

                            padding-bottom: 3px;

                            padding-left: 20px;
                        }

                        .certification-name {

                            width: auto;

                            padding: 3px 0;

                            vertical-align: top;

                            text-align: left;

                            line-height: 1.3;
                        }

                        .certification-name-with-bullet {

                            padding-left: 20px;
                        }


                        .certification-date {

                            width: 130px;

                            padding: 3px 0;

                            vertical-align: top;

                            text-align: right;

                            white-space: nowrap;

                            line-height: 1.3;
                        }


                        /* =================================================
                           TEXT
                           ================================================= */

                        b,
                        strong {
                            font-weight: bold;
                        }

                        i,
                        em {
                            font-style: italic;
                        }

                        u {
                            text-decoration: underline;
                        }

                    </style>

                </head>

                <body>
                """);


        // =========================================================
        // HEADER
        //
        // LEFT SIDE:
        // Name
        // Email
        // GitHub
        //
        // RIGHT SIDE:
        // Location
        // Mobile
        // LinkedIn
        // =========================================================

        // Name on its own line. Contact details sit on separate lines
        // below it, separated by "|" and without "Label:" prefixes so
        // ATS parsers read the name, email, phone and location cleanly.

        java.util.List<String> contactParts = new java.util.ArrayList<>();

        if (!isEmpty(resume.getLocation())) {
            contactParts.add("<span class=\"contact-label\">Location:</span> " + safe(resume.getLocation().trim()));
        }

        if (!isEmpty(resume.getMobile())) {
            contactParts.add("<span class=\"contact-label\">Mobile:</span> " + safe(resume.getMobile().trim()));
        }

        if (!isEmpty(resume.getEmail())) {
            contactParts.add("<span class=\"contact-label\">Email:</span> " + safe(resume.getEmail().trim()));
        }

        java.util.List<String> linkParts = new java.util.ArrayList<>();

        if (!isEmpty(resume.getGithub())) {
            linkParts.add("<span class=\"contact-label\">GitHub:</span> " + buildLink(resume.getGithub()));
        }

        if (!isEmpty(resume.getLinkedin())) {
            linkParts.add("<span class=\"contact-label\">LinkedIn:</span> " + buildLink(resume.getLinkedin()));
        }

        html.append("<div class=\"header-block\">");

        html.append("<span class=\"name\">")
                .append(safe(resume.getFullName()))
                .append("</span>");

        if (!contactParts.isEmpty()) {
            html.append("<div class=\"contact-line\">")
                    .append(String.join(" | ", contactParts))
                    .append("</div>");
        }

        if (!linkParts.isEmpty()) {
            html.append("<div class=\"contact-line\">")
                    .append(String.join(" | ", linkParts))
                    .append("</div>");
        }

        html.append("</div>");


        // =========================================================
        // DYNAMIC RESUME SECTIONS
        //
        // The order and titles are controlled by Resume.sectionOrder
        // and the custom section-title fields.
        // =========================================================

        for (String sectionKey : resume.getSectionOrderList()) {

            switch (sectionKey) {

                // =====================================================
                // PROFESSIONAL SUMMARY
                // =====================================================

                case "summary":

                    if (!isEmpty(
                            resume.getProfessionalSummary()
                    )) {

                        html.append("""
                                <div class="section">

                                    <div class="section-title">
                                """);

                        html.append(
                                safe(
                                        resume.getSummaryTitle()
                                )
                        );

                        html.append("""
                                    </div>

                                    <div class="summary">
                                """);

                        html.append(
                                cleanRichText(
                                        resume.getProfessionalSummary()
                                )
                        );

                        html.append("""
                                    </div>

                                </div>
                                """);
                    }

                    break;


                // =====================================================
                // EDUCATION
                // =====================================================

                case "education":

                    if (resume.getEducations() != null
                            && !resume.getEducations().isEmpty()) {

                        html.append("""
                                <div class="section">

                                    <div class="section-title">
                                """);

                        html.append(
                                safe(
                                        resume.getEducationTitle()
                                )
                        );

                        html.append("""
                                    </div>
                                """);

                        for (var education :
                                resume.getEducations()) {

                            html.append("""
                                    <table class="education-table">

                                        <tr>

                                            <td class="education-left">

                                                <div class="degree">
                                    """);

                            html.append(
                                    safe(
                                            education.getDegree()
                                    )
                            );

                            html.append("""
                                                </div>

                                                <div class="education-details">
                                    """);

                            if (!isEmpty(
                                    education.getInstitution()
                            )) {

                                html.append(
                                        safe(
                                                education.getInstitution()
                                        )
                                );
                            }

                            if (!isEmpty(
                                    education.getLocation()
                            )) {

                                html.append(", ");

                                html.append(
                                        safe(
                                                education.getLocation()
                                        )
                                );
                            }

                            if (!isEmpty(
                                    education.getScore()
                            )) {

                                html.append(" | ");

                                html.append(
                                        safe(
                                                education.getScore()
                                        )
                                );
                            }

                            html.append("""
                                                </div>

                                            </td>

                                            <td class="education-date">
                                    """);

                            if (!isEmpty(
                                    education.getStartYear()
                            )) {

                                html.append(
                                        safe(
                                                education.getStartYear()
                                        )
                                );
                            }

                            if (!isEmpty(
                                    education.getEndYear()
                            )) {

                                html.append(" – ");

                                html.append(
                                        safe(
                                                education.getEndYear()
                                        )
                                );
                            }

                            html.append("""
                                            </td>

                                        </tr>

                                    </table>
                                    """);
                        }

                        html.append("""
                                </div>
                                """);
                    }

                    break;


                // =====================================================
                // TECHNICAL SKILLS
                // =====================================================

                case "skills":

                    if (resume.getSkills() != null
                            && !resume.getSkills().isEmpty()) {

                        html.append("""
                                <div class="section">

                                    <div class="section-title">
                                """);

                        html.append(
                                safe(
                                        resume.getSkillsTitle()
                                )
                        );

                        html.append("""
                                    </div>

                                    <table class="skills-table">
                                """);

                        for (var skill :
                                resume.getSkills()) {

                            if (isEmpty(
                                    skill.getCategory()
                            )
                                    && isEmpty(
                                    skill.getSkillValues()
                            )) {

                                continue;
                            }

                            html.append("""
                                        <tr>

                                            <td class="skills-bullet">

                                                <span class="filled-bullet">•</span>

                                            </td>

                                            <td class="skills-category">
                                    """);

                            if (!isEmpty(
                                    skill.getCategory()
                            )) {

                                html.append(
                                        safe(
                                                skill.getCategory()
                                        )
                                );

                                html.append(":");
                            }

                            html.append("""
                                            </td>

                                            <td class="skills-values">
                                    """);

                            html.append(
                                    safe(
                                            skill.getSkillValues()
                                    )
                            );

                            html.append("""
                                            </td>

                                        </tr>
                                    """);
                        }

                        html.append("""
                                    </table>

                                </div>
                                """);
                    }

                    break;


                // =====================================================
                // PROJECTS
                // =====================================================

                case "projects":

                    if (resume.getProjects() != null
                            && !resume.getProjects().isEmpty()) {

                        html.append("""
                                <div class="section">

                                    <div class="section-title">
                                """);

                        html.append(
                                safe(
                                        resume.getProjectsTitle()
                                )
                        );

                        html.append("""
                                    </div>
                                """);

                        for (var project :
                                resume.getProjects()) {

                            if (isEmpty(
                                    project.getProjectName()
                            )
                                    && isEmpty(
                                    project.getDescription()
                            )) {

                                continue;
                            }

                            html.append("""
                                    <div class="project">

                                        <div class="project-name">
                                """);

                            html.append(
                                    safe(
                                            project.getProjectName()
                                    )
                            );

                            if (!isEmpty(project.getGithubLink())) {

                                html.append(" | ");

                                html.append(
                                        buildProjectLink(
                                                project.getGithubLink(),
                                                "GitHub"
                                        )
                                );
                            }

                            if (!isEmpty(project.getLiveDemoLink())) {

                                html.append(" | ");

                                html.append(
                                        buildProjectLink(
                                                project.getLiveDemoLink(),
                                                "Live Demo"
                                        )
                                );
                            }

                            html.append("""
                                        </div>
                                """);

                            if (!isEmpty(project.getTechStack())) {

                                html.append("<div class=\"project-tech\">");

                                html.append("<b>Tech Stack:</b> ");

                                html.append(
                                        safe(
                                                project.getTechStack().trim()
                                        )
                                );

                                html.append("</div>");
                            }

                            if (!isEmpty(
                                    project.getDescription()
                            )) {

                                html.append(
                                        createProjectBullets(
                                                project.getDescription()
                                        )
                                );
                            }

                            html.append("""
                                    </div>
                                    """);
                        }

                        html.append("""
                                </div>
                                """);
                    }

                    break;


                // =====================================================
                // CERTIFICATIONS
                // =====================================================

                case "certifications":

                    if (resume.getCertifications() != null
                            && !resume.getCertifications().isEmpty()) {

                        html.append("""
                                <div class="section">

                                    <div class="section-title">
                                """);

                        html.append(
                                safe(
                                        resume.getCertificationsTitle()
                                )
                        );

                        html.append("""
                                    </div>

                                    <table class="certifications-table">
                                """);

                        for (var certification :
                                resume.getCertifications()) {

                            if (isEmpty(
                                    certification.getName()
                            )
                                    && isEmpty(
                                    certification.getOrganization()
                            )
                                    && isEmpty(
                                    certification.getDate()
                            )) {

                                continue;
                            }

                            html.append("""
                                        <tr>

                                            <td class="certification-name certification-name-with-bullet">

                                                <span class="filled-bullet">•</span>&#160;
                                    """);

                            if (!isEmpty(
                                    certification.getName()
                            )) {

                                html.append(
                                        safe(
                                                certification.getName()
                                        )
                                );
                            }

                            if (!isEmpty(
                                    certification.getOrganization()
                            )) {

                                html.append(" - ");

                                html.append(
                                        safe(
                                                certification.getOrganization()
                                        )
                                );
                            }

                            html.append("""
                                            </td>

                                            <td class="certification-date">
                                    """);

                            if (!isEmpty(
                                    certification.getDate()
                            )) {

                                html.append(
                                        safe(
                                                certification.getDate()
                                        )
                                );
                            }

                            html.append("""
                                            </td>

                                        </tr>
                                    """);
                        }

                        html.append("""
                                    </table>

                                </div>
                                """);
                    }

                    break;


                default:

                    // Ignore unknown section keys safely.
                    break;
            }
        }


        // =========================================================
        // CLOSE HTML
        // =========================================================

        html.append("""
                </body>

                </html>
                """);


        return html.toString();
    }


    // =========================================================
    // CREATE PROJECT BULLETS
    // =========================================================

    private String createProjectBullets(
            String description) {

        if (description == null
                || description.trim().isEmpty()) {

            return "";
        }


        String text = description;


        // Remove script
        text = text.replaceAll(
                "(?is)<script.*?>.*?</script>",
                ""
        );


        // Remove style
        text = text.replaceAll(
                "(?is)<style.*?>.*?</style>",
                ""
        );


        // List item ending -> newline
        text = text.replaceAll(
                "(?i)</li\\s*>",
                "\n"
        );


        // Paragraph ending -> newline
        text = text.replaceAll(
                "(?i)</p\\s*>",
                "\n"
        );


        // Div ending -> newline
        text = text.replaceAll(
                "(?i)</div\\s*>",
                "\n"
        );


        // BR -> newline
        text = text.replaceAll(
                "(?i)<br\\s*/?>",
                "\n"
        );


        // Remove all remaining HTML tags
        text = text.replaceAll(
                "(?is)<[^>]*>",
                ""
        );


        // Decode entities
        text = decodeProjectEntities(text);


        // Non-breaking space
        text = text.replace(
                '\u00A0',
                ' '
        );


        // Zero-width characters
        text = text.replace(
                "\u200B",
                ""
        );

        text = text.replace(
                "\u200C",
                ""
        );

        text = text.replace(
                "\u200D",
                ""
        );

        text = text.replace(
                "\uFEFF",
                ""
        );


        String[] lines =
                text.split("\\r?\\n");


        StringBuilder result =
                new StringBuilder();


        for (String line :
                lines) {

            String item =
                    cleanProjectItem(line);


            if (item.isBlank()) {
                continue;
            }


            // Escape project text
            item = safe(item);


            // Add CSS hollow circle
            appendProjectBullet(
                    result,
                    item
            );
        }


        return result.toString();
    }


    // =========================================================
    // APPEND PROJECT BULLET
    // =========================================================

    private void appendProjectBullet(
            StringBuilder result,
            String item) {

        result.append("""
                <table class="project-list">

                    <tr>

                        <td class="project-bullet">

                            <span class="filled-bullet">•</span>

                        </td>

                        <td class="project-text">
                """);


        result.append(item);


        result.append("""
                        </td>

                    </tr>

                </table>
                """);
    }


    // =========================================================
    // CLEAN PROJECT ITEM
    // =========================================================

    private String cleanProjectItem(
            String value) {

        if (value == null) {
            return "";
        }


        String result =
                value.trim();


        // Decode #
        result = result.replace(
                "&#35;",
                "#"
        );

        result = result.replace(
                "&#x23;",
                "#"
        );

        result = result.replace(
                "&#X23;",
                "#"
        );

        result = result.replace(
                "&num;",
                "#"
        );


        // Decode filled bullets
        result = result.replace(
                "&bull;",
                "•"
        );

        result = result.replace(
                "&#8226;",
                "•"
        );

        result = result.replace(
                "&#x2022;",
                "•"
        );

        result = result.replace(
                "&#X2022;",
                "•"
        );


        // Decode hollow circles
        result = result.replace(
                "&#9675;",
                "○"
        );

        result = result.replace(
                "&#x25CB;",
                "○"
        );

        result = result.replace(
                "&#X25CB;",
                "○"
        );


        // Non-breaking spaces
        result = result.replace(
                "&nbsp;",
                " "
        );

        result = result.replace(
                "&#160;",
                " "
        );

        result = result.replace(
                "&#xA0;",
                " "
        );

        result = result.replace(
                "&#XA0;",
                " "
        );


        // Zero-width characters
        result = result.replace(
                "\u200B",
                ""
        );

        result = result.replace(
                "\u200C",
                ""
        );

        result = result.replace(
                "\u200D",
                ""
        );

        result = result.replace(
                "\uFEFF",
                ""
        );

        // Replace all soft-hyphen forms with a normal hyphen
        result = result.replace(
                "\u00AD",
                "-"
        );

        result = result.replace(
                "&shy;",
                "-"
        );

        result = result.replace(
                "&#173;",
                "-"
        );

        result = result.replace(
                "&#xAD;",
                "-"
        );

        result = result.replace(
                "&#XAD;",
                "-"
        );

        result = result.replace(
                "&#x00AD;",
                "-"
        );

        result = result.replace(
                "&#X00AD;",
                "-"
        );

        result = result.trim();


        // Remove # bullet
        result = result.replaceFirst(
                "^\\s*#+\\s*",
                ""
        );


        // Remove existing bullets
        result = result.replaceFirst(
                "^\\s*[•○◦▪▫●◉]+\\s*",
                ""
        );


        // Remove dash bullet
        result = result.replaceFirst(
                "^\\s*[-–—]+\\s+",
                ""
        );


        // Remove star bullet
        result = result.replaceFirst(
                "^\\s*\\*+\\s+",
                ""
        );


        // Remove numbered bullet
        result = result.replaceFirst(
                "^\\s*\\d+[.)]\\s*",
                ""
        );


        // Final # safety
        result = result.trim();

        while (result.startsWith("#")) {

            result =
                    result.substring(1).trim();
        }


        return result.trim();
    }


    // =========================================================
    // DECODE PROJECT ENTITIES
    // =========================================================

    private String decodeProjectEntities(
            String value) {

        if (value == null) {
            return "";
        }


        String result = value;


        // Hash
        result = result.replace(
                "&#35;",
                "#"
        );

        result = result.replace(
                "&#x23;",
                "#"
        );

        result = result.replace(
                "&#X23;",
                "#"
        );

        result = result.replace(
                "&num;",
                "#"
        );


        // Filled bullet
        result = result.replace(
                "&bull;",
                "•"
        );

        result = result.replace(
                "&#8226;",
                "•"
        );

        result = result.replace(
                "&#x2022;",
                "•"
        );

        result = result.replace(
                "&#X2022;",
                "•"
        );


        // Hollow circle
        result = result.replace(
                "&#9675;",
                "○"
        );

        result = result.replace(
                "&#x25CB;",
                "○"
        );

        result = result.replace(
                "&#X25CB;",
                "○"
        );


        // Spaces
        result = result.replace(
                "&nbsp;",
                " "
        );

        result = result.replace(
                "&#160;",
                " "
        );

        result = result.replace(
                "&#xA0;",
                " "
        );

        result = result.replace(
                "&#XA0;",
                " "
        );


        // Ampersand
        result = result.replace(
                "&amp;",
                "&"
        );


        // Dashes
        result = result.replace(
                "&ndash;",
                "–"
        );

        result = result.replace(
                "&mdash;",
                "—"
        );

        // Decode soft hyphen entities as normal hyphen
        result = result.replace(
                "&shy;",
                "-"
        );

        result = result.replace(
                "&#173;",
                "-"
        );

        result = result.replace(
                "&#xAD;",
                "-"
        );

        result = result.replace(
                "&#XAD;",
                "-"
        );

        result = result.replace(
                "&#x00AD;",
                "-"
        );

        result = result.replace(
                "&#X00AD;",
                "-"
        );

        // Replace actual Unicode soft hyphen
        result = result.replace(
                "\u00AD",
                "-"
        );


        return result;
    }


    // =========================================================
    // CLEAN RICH TEXT
    // =========================================================

    private String cleanRichText(
            String value) {

        if (value == null) {
            return "";
        }


        String result = value;


        // Remove script
        result = result.replaceAll(
                "(?is)<script.*?>.*?</script>",
                ""
        );


        // Remove style
        result = result.replaceAll(
                "(?is)<style.*?>.*?</style>",
                ""
        );


        // Remove event handlers
        result = result.replaceAll(
                "(?i)\\s+on[a-zA-Z]+\\s*=\\s*(['\"]).*?\\1",
                ""
        );


        // BR
        result = result.replaceAll(
                "(?i)<br\\s*>",
                "<br />"
        );

        result = result.replaceAll(
                "(?i)<br\\s*/>",
                "<br />"
        );


        // HR
        result = result.replaceAll(
                "(?i)<hr\\s*>",
                "<hr />"
        );

        result = result.replaceAll(
                "(?i)<hr\\s*/>",
                "<hr />"
        );


        // Entities
        result = result.replace(
                "&nbsp;",
                "&#160;"
        );

        result = result.replace(
                "&NBSP;",
                "&#160;"
        );

        result = result.replace(
                "&bull;",
                "&#8226;"
        );

        result = result.replace(
                "&ndash;",
                "&#8211;"
        );

        result = result.replace(
                "&mdash;",
                "&#8212;"
        );


        // Remove list containers
        result = result.replaceAll(
                "(?i)</?(ul|ol)[^>]*>",
                ""
        );


        // Remove list items
        result = result.replaceAll(
                "(?i)<li[^>]*>",
                ""
        );

        result = result.replaceAll(
                "(?i)</li>",
                "<br />"
        );


        // Keep only safe formatting tags
        result = result.replaceAll(
                "(?i)<(?!/?(b|strong|i|em|u|br|p|div|hr)(\\s|>|/))[^>]*>",
                ""
        );

        // Replace all soft-hyphen forms with a normal hyphen
        result = result.replace(
                "\u00AD",
                "-"
        );

        result = result.replace(
                "&shy;",
                "-"
        );

        result = result.replace(
                "&#173;",
                "-"
        );

        result = result.replace(
                "&#xAD;",
                "-"
        );

        result = result.replace(
                "&#XAD;",
                "-"
        );

        result = result.replace(
                "&#x00AD;",
                "-"
        );

        result = result.replace(
                "&#X00AD;",
                "-"
        );


        return result;
    }


    // =========================================================
    // REPAIR PDF /ToUnicode
    // =========================================================
    //
    // OpenHTMLToPDF 1.0.10 can generate an Arial /ToUnicode mapping
    // where the visible hyphen glyph is mapped to U+00AD (soft hyphen).
    //
    // This post-process keeps the Arial glyph/appearance unchanged and
    // changes only the Unicode mapping used by copy/paste and extraction:
    //
    //     <0010> <0010> <00AD>
    //                  ↓
    //     <0010> <0010> <002D>
    //
    // U+00AD = soft hyphen
    // U+002D = normal ASCII hyphen "-"
    // =========================================================

    private byte[] repairPdfToUnicode(byte[] pdfBytes) throws IOException {

        try (
                PDDocument document =
                        PDDocument.load(
                                new ByteArrayInputStream(pdfBytes)
                        );

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            for (PDPage page : document.getPages()) {

                PDResources resources = page.getResources();

                if (resources == null) {
                    continue;
                }

                for (COSName fontName : resources.getFontNames()) {

                    PDFont font = resources.getFont(fontName);

                    if (font == null) {
                        continue;
                    }

                    String pdfFontName = font.getName();

                    // Only repair the Arial fonts registered by this
                    // controller. Other fonts remain untouched.
                    if (pdfFontName == null
                            || !pdfFontName
                            .toLowerCase(java.util.Locale.ROOT)
                            .contains("arial")) {
                        continue;
                    }

                    if (!(font.getCOSObject()
                            .getDictionaryObject(COSName.TO_UNICODE)
                            instanceof COSStream toUnicode)) {
                        continue;
                    }

                    repairToUnicodeStream(toUnicode);
                }
            }

            document.save(outputStream);

            return outputStream.toByteArray();
        }
    }


    private void repairToUnicodeStream(
            COSStream stream) throws IOException {

        byte[] decodedBytes;

        try (InputStream inputStream =
                     stream.createInputStream()) {

            decodedBytes = inputStream.readAllBytes();
        }

        String cmap =
                new String(
                        decodedBytes,
                        StandardCharsets.ISO_8859_1
                );

        // This is the exact incorrect mapping found in the generated
        // Arial PDF. Change only this mapping.
        String repairedCmap =
                cmap.replace(
                        "<0010> <0010> <00AD>",
                        "<0010> <0010> <002D>"
                );

        // Nothing to repair in this font's CMap.
        if (cmap.equals(repairedCmap)) {
            return;
        }

        // We read the decoded stream above, so remove the old filter
        // information before writing the repaired plain-text CMap.
        stream.removeItem(COSName.FILTER);
        stream.removeItem(COSName.DECODE_PARMS);

        try (OutputStream outputStream =
                     stream.createOutputStream()) {

            outputStream.write(
                    repairedCmap.getBytes(
                            StandardCharsets.ISO_8859_1
                    )
            );
        }
    }


    // =========================================================
    // BUILD LINK (GitHub / LinkedIn)
    // =========================================================

    private String buildProjectLink(String value, String label) {

        String url = value.trim();

        if (!url.startsWith("http://")
                && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        return "<a class=\"project-link\" href=\""
                + safe(url)
                + "\" target=\"_blank\">"
                + safe(label)
                + "</a>";
    }


    private String buildLink(String value) {

        String text = value.trim();

        String url = text;

        if (!url.startsWith("http://")
                && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        return "<a class=\"header-link\" href=\""
                + safe(url)
                + "\" target=\"_blank\">"
                + safe(text)
                + "</a>";
    }


    // =========================================================
    // CHECK EMPTY
    // =========================================================

    private boolean isEmpty(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }


    // =========================================================
    // ESCAPE TEXT
    // =========================================================

    private String safe(
            String value) {

        if (value == null) {
            return "";
        }


        return value
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                )
                .replace(
                        "\"",
                        "&quot;"
                )
                .replace(
                        "'",
                        "&#39;"
                );
    }
}
