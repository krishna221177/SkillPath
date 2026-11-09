# SkillPath: Student Skill-Gap Analysis and Career Roadmap System

SkillPath is a full-stack, enterprise-grade Java web application built for college engineering students. It evaluates technical skills against industry career requirements, identifies competency gaps, calculates an explainable career readiness score, and generates an organized, step-by-step learning roadmap with milestone progress tracking.

---

## 1. Project Overview & Objectives

In academic settings, students frequently struggle to identify which industry skills they lack for desired roles like **Java Backend Developer**, **Full Stack Developer**, or **Software Engineer**. 

SkillPath solves this problem by providing:
- **Student Profiling:** Personal and academic management with semester tracking.
- **Skill Management:** CRUD operations on skills with 1–5 proficiency levels (`Beginner`, `Basic`, `Intermediate`, `Advanced`, `Expert`).
- **Predefined Career Tracks:** Benchmarked roles with required skill proficiencies stored persistently in H2.
- **Skill-Gap Analysis:** Categorizes every role requirement into **Satisfied**, **Needs Improvement**, or **Missing**.
- **Career Readiness Score:** Deterministic mathematical scoring with optional skills excluded from the denominator.
- **Curated Learning Roadmap:** Prioritizes Missing Skills (High) &rarr; Weak Skills (Medium) &rarr; Optional Competencies (Low), providing practical learning objectives.
- **Progress Tracking:** Interactive milestone statuses (`Not Started`, `In Progress`, `Completed`) stored persistently in the database.
- **Glassmorphism UI:** Modern frosted-glass aesthetic (`backdrop-filter: blur`, translucent borders, ambient navy/blue/violet gradients).

---

## 2. Technology Stack

- **Language:** Java 17 (Microsoft OpenJDK 17 LTS)
- **Framework:** Spring Boot 3.2.5
- **Web UI:** Vaadin Flow 24.3.12 (Java-based component architecture)
- **Database:** H2 Database (Persistent File-based engine: `./data/skillpathdb.mv.db`)
- **Data Access:** Spring Data JPA / Hibernate ORM
- **Testing:** JUnit 5, Mockito, Spring Boot Test
- **Build Tool:** Apache Maven 3.9.9
- **Design System:** Custom Glassmorphism Theme (Deep Navy `#0B1020`, Electric Blue `#648CFF`, Cyan `#55D6E8`)

---

## 3. Core Architecture & Project Structure

The project strictly follows layered Java enterprise architecture:

```
com.skillpath
├── SkillPathApplication.java           # Spring Boot bootstrap & Vaadin AppShellConfigurator
├── config/
│   └── DataInitializer.java           # Database seed runner for roles, skills, and default student
├── dto/
│   ├── DashboardSummaryDto.java       # Aggregated dashboard metrics
│   ├── SkillGapAnalysisResultDto.java # Readiness percentage & gap aggregates
│   └── SkillGapItemDto.java           # Individual skill comparison, points, and status
├── exception/
│   └── SkillPathException.java        # Custom application runtime exception
├── model/
│   ├── CareerRole.java                # Predefined industry role entity
│   ├── ProficiencyLevel.java          # Enum (BEGINNER=1 to EXPERT=5)
│   ├── Roadmap.java                   # Student career roadmap entity
│   ├── RoadmapItem.java               # Sequenced roadmap milestone entity
│   ├── RoadmapItemStatus.java         # Enum (NOT_STARTED, IN_PROGRESS, COMPLETED)
│   ├── RoadmapPriority.java           # Enum (HIGH, MEDIUM, LOW)
│   ├── RoleRequirement.java           # Required skill, level, optional flag
│   ├── Skill.java                     # Technical skill entity (predefined & custom)
│   ├── SkillGapStatus.java            # Enum (SATISFIED, NEEDS_IMPROVEMENT, MISSING)
│   ├── Student.java                   # Student academic profile entity
│   └── StudentSkill.java              # Student-to-Skill association with unique constraint
├── repository/
│   ├── CareerRoleRepository.java
│   ├── RoadmapItemRepository.java
│   ├── RoadmapRepository.java
│   ├── RoleRequirementRepository.java
│   ├── SkillRepository.java
│   ├── StudentRepository.java
│   └── StudentSkillRepository.java
├── service/
│   ├── CareerRoleService.java
│   ├── DashboardService.java
│   ├── RoadmapService.java
│   ├── SkillGapAnalysisService.java   # Deterministic gap & readiness scoring engine
│   ├── SkillService.java
│   └── StudentService.java
├── ui/
│   ├── MainLayout.java                # Responsive glass navigation sidebar and top header
│   ├── HomeView.java                  # Landing page with workflow cards
│   ├── DashboardView.java             # Live statistics cards & readiness indicators
│   ├── ProfileView.java               # Student profile form with validations
│   ├── SkillsView.java                # Portfolio grid with search, filter, and CRUD dialogs
│   ├── CareerRolesView.java           # Career roles with skill chips and selection
│   ├── SkillGapView.java              # Competency matrix & explainable formula display
│   ├── RoadmapView.java               # Sequenced milestone cards with practical objectives
│   ├── ProgressView.java              # Interactive milestone status switcher & completion bar
│   └── components/
│       └── GlassCard.java             # Reusable frosted-glass container component
└── util/
    └── LearningObjectivesProvider.java# Concrete technical objectives for curriculum roadmaps
```

---

## 4. Career Readiness Calculation Algorithm

The readiness score is deterministic, transparent, and bounded:

1. **Numerical Mapping:**
   $$\text{Beginner} = 1, \quad \text{Basic} = 2, \quad \text{Intermediate} = 3, \quad \text{Advanced} = 4, \quad \text{Expert} = 5$$

2. **Per-Requirement Points:**
   $$\text{Achieved Points} = \min(\text{Student Proficiency}, \text{Required Proficiency})$$
   - Missing skills contribute **0 points**.
   - Student skills exceeding requirement are capped at the required level.
   - Optional skills are **excluded** from the mandatory readiness denominator.

3. **Readiness Percentage:**
   $$\text{Readiness \%} = \left(\frac{\sum \text{Achieved Points}}{\sum \text{Required Points}}\right) \times 100$$

### Verified Example (From Prompt):
- Required: Java (Intermediate = 3), SQL (Basic = 2), DSA (Advanced = 4) &rarr; Total Required = 9
- Student: Java (Intermediate = 3), SQL (Beginner = 1), DSA (Missing = 0) &rarr; Total Achieved = $3 + 1 + 0 = 4$
- Readiness: $(4 / 9) \times 100 \approx \mathbf{44.44\%}$

---

## 5. Database & Persistence

- **Engine:** Persistent H2 file database stored at `./data/skillpathdb.mv.db`.
- **Durability:** Data persists across application restarts.
- **Seed Data:** Initialized on startup via `DataInitializer.java` without duplicating records.
  - 16 Predefined technical skills (Java, Python, C, C++, SQL, DSA, OOP, HTML, CSS, JavaScript, Git, Spring Boot, REST APIs, DBMS, Docker, Linux).
  - 6 Predefined career roles (Java Backend Developer, Python Developer, Full Stack Developer, Frontend Developer, Data Analyst, Software Engineer).
  - Default student Alex Morgan with existing skills and generated roadmap.
- **H2 Console:** Accessible in browser at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/skillpathdb`
  - User: `sa`
  - Password: *(blank)*

---

## 6. How to Run the Application

### Prerequisites
- Java 17+ installed and set to `JAVA_HOME`.
- Apache Maven 3.9+ installed.

### Start the Application
In PowerShell or Terminal from the project root:

```powershell
mvn spring-boot:run
```

Once started, open your web browser to:
**[http://localhost:8080](http://localhost:8080)**

---

## 7. How to Run the Tests

To execute the unit and integration test suite:

```powershell
mvn test
```

### Verified Test Suite:
- `SkillGapAnalysisServiceTest`:
  - `testPromptExampleReadinessCalculation`: Confirms exact $44.44\%$ calculation.
  - `testPerfectReadiness100Percent`: Confirms $100\%$ on complete skill coverage.
  - `testZeroReadiness`: Confirms $0\%$ when no matching skills are present.
  - `testExceedingRequirementsCapped`: Confirms student proficiency capping.
  - `testOptionalSkillsExcludedFromDenominator`: Confirms optional skills do not distort the denominator.
- `RoadmapServiceTest`:
  - `testRoadmapPriorityOrdering`: Confirms sequence: 1. Missing (High), 2. Needs Improvement (Medium), 3. Optional (Low).
  - `testUpdateItemStatus`: Confirms persistent milestone updates.
- `StudentSkillServiceTest`:
  - Duplicate student skill prevention.
  - Protection against deleting predefined skills.
  - Validation of unique custom skill names.
- `SkillPathApplicationTests`:
  - Context loads and verifies Spring Boot, JPA entity mapping, and HikariCP connection.

---

## 8. Application Navigation & Pages

| Route | Page | Purpose |
| :--- | :--- | :--- |
| `/` | Landing Page | Hero banner, core workflow explanation, and fast navigation |
| `/dashboard` | Dashboard | Career readiness gauge, KPI stat cards, and active milestones |
| `/profile` | My Profile | Form for student name, college, email, department, semester |
| `/skills` | My Skills | Grid portfolio with search, category filtering, and modal CRUD dialogs |
| `/roles` | Career Roles | Cards displaying industry role requirements and one-click role selection |
| `/gap-analysis` | Skill-Gap Analysis | Matrix classifying competencies into Satisfied, Needs Improvement, Missing |
| `/roadmap` | Learning Roadmap | Sequenced milestones with concrete learning objectives and priority tiers |
| `/progress` | Progress Tracking | Milestone status toggles (Not Started, In Progress, Completed) |
| `/h2-console` | H2 Database Console | Direct SQL inspection of database tables |

---

## 9. College Demonstration Guide

1. **Open `http://localhost:8080`**: Introduce SkillPath as a career navigator built purely in Java with Spring Boot, Vaadin Flow, and H2.
2. **Dashboard (`/dashboard`)**: Showcase the live career readiness score for Alex Morgan against *Java Backend Developer*. Point out the real-time statistics aggregated from H2.
3. **Skill-Gap Analysis (`/gap-analysis`)**: Walk through the competency matrix. Show how *Java* is Satisfied, *SQL* Needs Improvement, and *Spring Boot* / *Docker* are Missing.
4. **Interactive Update**: Click **"Set Level"** next to *Spring Boot* in the gap analysis grid, change it to *Intermediate*, and click **"Update & Recalculate"**. Notice how the readiness percentage automatically increases!
5. **Learning Roadmap (`/roadmap`)**: Show how Missing skills are ranked as **Step #1, #2 (High Priority)**, followed by Needs Improvement (**Medium Priority**), each with practical learning objectives.
6. **Progress Tracking (`/progress`)**: Click **"Mark Completed"** on a milestone. The completion progress bar updates instantly and persists in H2.
7. **Skills Portfolio (`/skills`)**: Search for skills, filter by category, or click **"Create Custom Skill"** to register a new skill like *GraphQL* or *Kubernetes*.
