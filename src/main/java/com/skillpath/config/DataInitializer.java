package com.skillpath.config;

import com.skillpath.model.*;
import com.skillpath.repository.*;
import com.skillpath.service.RoadmapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SkillRepository skillRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final StudentRepository studentRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final RoadmapService roadmapService;

    public DataInitializer(SkillRepository skillRepository,
                           CareerRoleRepository careerRoleRepository,
                           StudentRepository studentRepository,
                           StudentSkillRepository studentSkillRepository,
                           RoadmapService roadmapService) {
        this.skillRepository = skillRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.studentRepository = studentRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.roadmapService = roadmapService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and initializing SkillPath seed data...");

        Map<String, Skill> skillMap = seedSkills();
        seedCareerRoles(skillMap);
        seedDefaultStudent(skillMap);

        log.info("SkillPath database seed verification completed successfully.");
    }

    private Map<String, Skill> seedSkills() {
        Map<String, Skill> skillMap = new HashMap<>();

        String[][] initialSkills = {
                {"Java", "Programming", "Core Java syntax, OOP concepts, Collections, Multithreading, and Modern Java features."},
                {"Python", "Programming", "Python language fundamentals, standard library, scripting, and data manipulation."},
                {"C", "Programming", "Low-level procedural programming, memory pointers, and systems programming."},
                {"C++", "Programming", "C++ language, STL containers, object orientation, and high-performance computation."},
                {"SQL", "Database", "Structured query language for relational databases, schema design, and querying."},
                {"Data Structures and Algorithms", "Core CS", "Algorithm complexity analysis, trees, graphs, sorting, searching, dynamic programming."},
                {"Object-Oriented Programming", "Core CS", "Inheritance, encapsulation, polymorphism, abstraction, and SOLID design principles."},
                {"HTML", "Web", "Semantic markup, modern HTML5 structures, forms, and web document accessibility."},
                {"CSS", "Web", "Cascading Style Sheets, responsive layouts (Flexbox/Grid), and modern UI design."},
                {"JavaScript", "Web", "Modern ECMAScript, asynchronous programming, DOM manipulation, and browser APIs."},
                {"Git", "DevOps", "Distributed version control, branching strategies, rebase, and collaborative workflows."},
                {"Spring Boot", "Framework", "Enterprise Java application development, dependency injection, and JPA integration."},
                {"REST APIs", "Architecture", "Designing, building, and consuming standard RESTful web services with JSON."},
                {"Database Management", "Database", "Relational modeling, normalization, indexing, ACID transactions, and performance tuning."},
                {"Docker", "DevOps", "Containerization technology, image building with Dockerfiles, and multi-service orchestration."},
                {"Linux", "Operating System", "Command line proficiency, shell scripting, permission models, and process management."}
        };

        for (String[] entry : initialSkills) {
            String name = entry[0];
            String category = entry[1];
            String desc = entry[2];

            Skill skill = skillRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> skillRepository.save(new Skill(name, category, desc, true)));
            skillMap.put(name, skill);
        }

        // Also index any other existing skills
        for (Skill s : skillRepository.findAll()) {
            skillMap.putIfAbsent(s.getName(), s);
        }

        return skillMap;
    }

    private void seedCareerRoles(Map<String, Skill> skills) {
        // 1. Java Backend Developer
        if (!careerRoleRepository.existsByNameIgnoreCase("Java Backend Developer")) {
            CareerRole role = new CareerRole("Java Backend Developer",
                    "Specializes in server-side Java architecture, building robust scalable RESTful microservices, database persistence, and cloud deployment.");
            role.addRequirement(new RoleRequirement(role, skills.get("Java"), ProficiencyLevel.ADVANCED, false, "Core server programming language."));
            role.addRequirement(new RoleRequirement(role, skills.get("Spring Boot"), ProficiencyLevel.INTERMEDIATE, false, "Primary backend enterprise framework."));
            role.addRequirement(new RoleRequirement(role, skills.get("REST APIs"), ProficiencyLevel.INTERMEDIATE, false, "Service-to-service and client communication."));
            role.addRequirement(new RoleRequirement(role, skills.get("SQL"), ProficiencyLevel.INTERMEDIATE, false, "Data querying and relational persistence."));
            role.addRequirement(new RoleRequirement(role, skills.get("Database Management"), ProficiencyLevel.INTERMEDIATE, false, "Schema design and transactions."));
            role.addRequirement(new RoleRequirement(role, skills.get("Object-Oriented Programming"), ProficiencyLevel.ADVANCED, false, "Maintainable domain architecture."));
            role.addRequirement(new RoleRequirement(role, skills.get("Git"), ProficiencyLevel.BASIC, false, "Code collaboration and versioning."));
            role.addRequirement(new RoleRequirement(role, skills.get("Docker"), ProficiencyLevel.BASIC, true, "Containerized microservice packaging."));
            role.addRequirement(new RoleRequirement(role, skills.get("Linux"), ProficiencyLevel.BASIC, true, "Production server hosting environment."));
            careerRoleRepository.save(role);
        }

        // 2. Python Developer
        if (!careerRoleRepository.existsByNameIgnoreCase("Python Developer")) {
            CareerRole role = new CareerRole("Python Developer",
                    "Builds backend applications, automation scripts, data pipelines, and web services using the Python ecosystem.");
            role.addRequirement(new RoleRequirement(role, skills.get("Python"), ProficiencyLevel.ADVANCED, false, "Primary programming language."));
            role.addRequirement(new RoleRequirement(role, skills.get("SQL"), ProficiencyLevel.INTERMEDIATE, false, "Database persistence and reporting."));
            role.addRequirement(new RoleRequirement(role, skills.get("REST APIs"), ProficiencyLevel.INTERMEDIATE, false, "API development and integration."));
            role.addRequirement(new RoleRequirement(role, skills.get("Git"), ProficiencyLevel.BASIC, false, "Source control."));
            role.addRequirement(new RoleRequirement(role, skills.get("Linux"), ProficiencyLevel.BASIC, false, "Server operations."));
            role.addRequirement(new RoleRequirement(role, skills.get("Data Structures and Algorithms"), ProficiencyLevel.INTERMEDIATE, false, "Efficient logic implementation."));
            role.addRequirement(new RoleRequirement(role, skills.get("Docker"), ProficiencyLevel.BASIC, true, "Containerized workflows."));
            careerRoleRepository.save(role);
        }

        // 3. Full Stack Developer
        if (!careerRoleRepository.existsByNameIgnoreCase("Full Stack Developer")) {
            CareerRole role = new CareerRole("Full Stack Developer",
                    "Bridges end-to-end web engineering, from responsive frontend user experiences to resilient backend microservices and databases.");
            role.addRequirement(new RoleRequirement(role, skills.get("HTML"), ProficiencyLevel.ADVANCED, false, "Modern web document markup."));
            role.addRequirement(new RoleRequirement(role, skills.get("CSS"), ProficiencyLevel.INTERMEDIATE, false, "Responsive presentation and layouts."));
            role.addRequirement(new RoleRequirement(role, skills.get("JavaScript"), ProficiencyLevel.ADVANCED, false, "Client-side interactivity and logic."));
            role.addRequirement(new RoleRequirement(role, skills.get("Java"), ProficiencyLevel.INTERMEDIATE, false, "Backend business logic execution."));
            role.addRequirement(new RoleRequirement(role, skills.get("Spring Boot"), ProficiencyLevel.INTERMEDIATE, false, "Backend API service layer."));
            role.addRequirement(new RoleRequirement(role, skills.get("REST APIs"), ProficiencyLevel.INTERMEDIATE, false, "Client-server API integration."));
            role.addRequirement(new RoleRequirement(role, skills.get("SQL"), ProficiencyLevel.INTERMEDIATE, false, "Data persistence."));
            role.addRequirement(new RoleRequirement(role, skills.get("Git"), ProficiencyLevel.BASIC, false, "Version control."));
            role.addRequirement(new RoleRequirement(role, skills.get("Docker"), ProficiencyLevel.BASIC, true, "Local full-stack development environment."));
            careerRoleRepository.save(role);
        }

        // 4. Frontend Developer
        if (!careerRoleRepository.existsByNameIgnoreCase("Frontend Developer")) {
            CareerRole role = new CareerRole("Frontend Developer",
                    "Crafts intuitive, accessible, high-performance web user interfaces, component design systems, and rich client-side applications.");
            role.addRequirement(new RoleRequirement(role, skills.get("HTML"), ProficiencyLevel.ADVANCED, false, "Accessible DOM semantics."));
            role.addRequirement(new RoleRequirement(role, skills.get("CSS"), ProficiencyLevel.ADVANCED, false, "Modern CSS, animations, and responsive grids."));
            role.addRequirement(new RoleRequirement(role, skills.get("JavaScript"), ProficiencyLevel.ADVANCED, false, "Modern asynchronous JavaScript."));
            role.addRequirement(new RoleRequirement(role, skills.get("Git"), ProficiencyLevel.BASIC, false, "Source control."));
            role.addRequirement(new RoleRequirement(role, skills.get("REST APIs"), ProficiencyLevel.BASIC, false, "Consuming backend JSON endpoints."));
            role.addRequirement(new RoleRequirement(role, skills.get("Docker"), ProficiencyLevel.BEGINNER, true, "Running local services."));
            careerRoleRepository.save(role);
        }

        // 5. Data Analyst
        if (!careerRoleRepository.existsByNameIgnoreCase("Data Analyst")) {
            CareerRole role = new CareerRole("Data Analyst",
                    "Transforms raw datasets into actionable business insights through SQL queries, statistical analysis, and Python data manipulation.");
            role.addRequirement(new RoleRequirement(role, skills.get("SQL"), ProficiencyLevel.ADVANCED, false, "Advanced data extraction, aggregation, and window functions."));
            role.addRequirement(new RoleRequirement(role, skills.get("Python"), ProficiencyLevel.INTERMEDIATE, false, "Data wrangling, cleaning, and analysis scripts."));
            role.addRequirement(new RoleRequirement(role, skills.get("Database Management"), ProficiencyLevel.INTERMEDIATE, false, "Relational modeling and data integrity."));
            role.addRequirement(new RoleRequirement(role, skills.get("Git"), ProficiencyLevel.BASIC, true, "Version controlling analysis scripts."));
            careerRoleRepository.save(role);
        }

        // 6. Software Engineer
        if (!careerRoleRepository.existsByNameIgnoreCase("Software Engineer")) {
            CareerRole role = new CareerRole("Software Engineer",
                    "Applies computer science fundamentals, algorithm design, software architecture, and systems engineering to solve complex computing problems.");
            role.addRequirement(new RoleRequirement(role, skills.get("Data Structures and Algorithms"), ProficiencyLevel.ADVANCED, false, "Foundational problem solving and complexity optimization."));
            role.addRequirement(new RoleRequirement(role, skills.get("Object-Oriented Programming"), ProficiencyLevel.ADVANCED, false, "System design and modular software engineering."));
            role.addRequirement(new RoleRequirement(role, skills.get("Java"), ProficiencyLevel.INTERMEDIATE, false, "High-level object-oriented programming."));
            role.addRequirement(new RoleRequirement(role, skills.get("C++"), ProficiencyLevel.INTERMEDIATE, true, "Systems programming and memory management."));
            role.addRequirement(new RoleRequirement(role, skills.get("SQL"), ProficiencyLevel.INTERMEDIATE, false, "Data modeling and persistence."));
            role.addRequirement(new RoleRequirement(role, skills.get("Git"), ProficiencyLevel.BASIC, false, "Team collaboration and version control."));
            role.addRequirement(new RoleRequirement(role, skills.get("Linux"), ProficiencyLevel.BASIC, false, "Unix environment familiarity."));
            careerRoleRepository.save(role);
        }
    }

    private void seedDefaultStudent(Map<String, Skill> skills) {
        if (studentRepository.count() == 0) {
            Student student = new Student();
            student.setName("Alex Morgan");
            student.setEmail("alex.morgan@university.edu");
            student.setCollegeName("Apex Institute of Technology");
            student.setDepartment("Computer Science & Engineering");
            student.setSemester(6);

            CareerRole defaultRole = careerRoleRepository.findByNameIgnoreCase("Java Backend Developer")
                    .orElse(careerRoleRepository.findAll().get(0));
            student.setTargetCareer(defaultRole);

            Student savedStudent = studentRepository.save(student);

            // Seed initial student skills for Alex
            addStudentSkill(savedStudent, skills.get("Java"), ProficiencyLevel.INTERMEDIATE);
            addStudentSkill(savedStudent, skills.get("Object-Oriented Programming"), ProficiencyLevel.INTERMEDIATE);
            addStudentSkill(savedStudent, skills.get("SQL"), ProficiencyLevel.BASIC);
            addStudentSkill(savedStudent, skills.get("Git"), ProficiencyLevel.BASIC);
            addStudentSkill(savedStudent, skills.get("HTML"), ProficiencyLevel.INTERMEDIATE);
            addStudentSkill(savedStudent, skills.get("CSS"), ProficiencyLevel.BASIC);

            // Generate initial roadmap
            try {
                roadmapService.generateOrRefreshRoadmap(savedStudent.getId(), defaultRole.getId());
                log.info("Initial roadmap generated for default student.");
            } catch (Exception e) {
                log.warn("Roadmap initial generation notice: {}", e.getMessage());
            }
        }
    }

    private void addStudentSkill(Student student, Skill skill, ProficiencyLevel level) {
        if (skill != null) {
            StudentSkill ss = new StudentSkill(student, skill, level);
            ss.setLastUpdated(LocalDate.now().minusDays(5));
            studentSkillRepository.save(ss);
        }
    }
}
