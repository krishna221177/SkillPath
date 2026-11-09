package com.skillpath.util;

import com.skillpath.model.ProficiencyLevel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LearningObjectivesProvider {

    private final Map<String, Map<ProficiencyLevel, String>> objectiveMap = new HashMap<>();

    public LearningObjectivesProvider() {
        initObjectives();
    }

    private void initObjectives() {
        // Java
        add("Java", ProficiencyLevel.BASIC, "Understand Java syntax, primitive data types, control flow, methods, and compile/run basic console programs.");
        add("Java", ProficiencyLevel.INTERMEDIATE, "Master Object-Oriented Programming (encapsulation, inheritance, polymorphism, interfaces), Collections framework, and Exception handling.");
        add("Java", ProficiencyLevel.ADVANCED, "Build multi-threaded applications, use Streams API, Lambdas, Generics, File I/O (NIO), and implement design patterns.");
        add("Java", ProficiencyLevel.EXPERT, "Deep dive into JVM internals, garbage collection tuning, concurrent memory model, high-throughput microservices, and profiling.");

        // Python
        add("Python", ProficiencyLevel.BASIC, "Learn basic syntax, variables, lists, dictionaries, functions, and standard control structures.");
        add("Python", ProficiencyLevel.INTERMEDIATE, "Implement OOP in Python, list/dict comprehensions, file handling, modules/packages, and virtual environments.");
        add("Python", ProficiencyLevel.ADVANCED, "Work with generators, decorators, asynchronous programming (asyncio), automated testing with pytest, and package packaging.");
        add("Python", ProficiencyLevel.EXPERT, "Master C-extensions/Cython, metaprogramming, memory management, and scaling distributed Python systems.");

        // C
        add("C", ProficiencyLevel.BASIC, "Learn procedural syntax, data types, loops, conditionals, functions, and simple arrays.");
        add("C", ProficiencyLevel.INTERMEDIATE, "Master pointer arithmetic, dynamic memory allocation (malloc/free), structs, and header file modularization.");
        add("C", ProficiencyLevel.ADVANCED, "Build system-level utilities, work with bitwise operators, function pointers, file system calls, and prevent memory leaks.");
        add("C", ProficiencyLevel.EXPERT, "Perform bare-metal embedded programming, kernel module design, and exploit mitigation.");

        // C++
        add("C++", ProficiencyLevel.BASIC, "Learn basic syntax, functions, input/output streams, and fundamentals of class declarations.");
        add("C++", ProficiencyLevel.INTERMEDIATE, "Master classes, constructors/destructors, operator overloading, Standard Template Library (STL containers & algorithms).");
        add("C++", ProficiencyLevel.ADVANCED, "Implement modern C++ (C++17/20), smart pointers, move semantics, template metaprogramming, and concurrency.");
        add("C++", ProficiencyLevel.EXPERT, "Develop low-latency systems, custom memory allocators, lock-free data structures, and game/graphics engines.");

        // SQL
        add("SQL", ProficiencyLevel.BASIC, "Execute SELECT queries, WHERE filters, ORDER BY, aggregate functions (COUNT, SUM, AVG), and basic GROUP BY.");
        add("SQL", ProficiencyLevel.INTERMEDIATE, "Write multi-table JOINs (INNER, LEFT, RIGHT), subqueries, UNIONs, and understand primary/foreign key constraints.");
        add("SQL", ProficiencyLevel.ADVANCED, "Design optimized relational schemas, indexes, composite keys, stored procedures, transactions (ACID), and window functions.");
        add("SQL", ProficiencyLevel.EXPERT, "Tune execution plans (EXPLAIN ANALYZE), design database partitioning, replication topologies, and query optimizations.");

        // Data Structures and Algorithms
        add("Data Structures and Algorithms", ProficiencyLevel.BASIC, "Understand Big-O time and space complexity, arrays, strings, linear search, and basic sorting (bubble, insertion).");
        add("Data Structures and Algorithms", ProficiencyLevel.INTERMEDIATE, "Implement singly/doubly linked lists, stacks, queues, binary search trees, and recursion.");
        add("Data Structures and Algorithms", ProficiencyLevel.ADVANCED, "Master graph algorithms (BFS/DFS, Dijkstra), heaps, dynamic programming, divide-and-conquer, and hash tables.");
        add("Data Structures and Algorithms", ProficiencyLevel.EXPERT, "Solve complex NP-hard approximations, segment trees, Trie structures, and optimize algorithms for high concurrency.");

        // Object-Oriented Programming
        add("Object-Oriented Programming", ProficiencyLevel.BASIC, "Learn classes, objects, state, behavior, and basic method calls.");
        add("Object-Oriented Programming", ProficiencyLevel.INTERMEDIATE, "Apply 4 OOP pillars: Encapsulation, Abstraction, Inheritance, and Polymorphism in real projects.");
        add("Object-Oriented Programming", ProficiencyLevel.ADVANCED, "Apply SOLID principles, Gang of Four design patterns (Factory, Singleton, Strategy, Observer, Decorator).");
        add("Object-Oriented Programming", ProficiencyLevel.EXPERT, "Architect enterprise domain-driven designs (DDD) and maintainable multi-layered modular software architectures.");

        // HTML
        add("HTML", ProficiencyLevel.BASIC, "Understand basic tags, headings, paragraphs, lists, links, images, and form elements.");
        add("HTML", ProficiencyLevel.INTERMEDIATE, "Implement HTML5 semantic tags (header, nav, article, section, footer), web accessibility (ARIA attributes), and form validations.");
        add("HTML", ProficiencyLevel.ADVANCED, "Optimize DOM hierarchy, metadata for SEO/OpenGraph, Canvas APIs, and responsive picture/source tags.");
        add("HTML", ProficiencyLevel.EXPERT, "Design accessible enterprise web components and PWAs with offline manifest and service worker integrations.");

        // CSS
        add("CSS", ProficiencyLevel.BASIC, "Understand color, fonts, margins, paddings, borders, and CSS selector specificity.");
        add("CSS", ProficiencyLevel.INTERMEDIATE, "Master modern Flexbox and CSS Grid layouts, media queries for responsive mobile-first design.");
        add("CSS", ProficiencyLevel.ADVANCED, "Implement CSS custom properties (variables), transitions, keyframe animations, glassmorphism, and BEM architecture.");
        add("CSS", ProficiencyLevel.EXPERT, "Build design systems, optimize render pipelines (compositing/reflows), and handle cross-browser layout engines.");

        // JavaScript
        add("JavaScript", ProficiencyLevel.BASIC, "Learn syntax, primitive types, loops, conditionals, functions, and DOM selection.");
        add("JavaScript", ProficiencyLevel.INTERMEDIATE, "Master ES6+ features (arrow functions, destructuring, modules), event listeners, Promises, and fetch API.");
        add("JavaScript", ProficiencyLevel.ADVANCED, "Master async/await, closures, prototypes, event loop/concurrency model, and error handling patterns.");
        add("JavaScript", ProficiencyLevel.EXPERT, "Build high-performance JavaScript applications, manage memory leaks, and profile V8 execution.");

        // Git
        add("Git", ProficiencyLevel.BASIC, "Understand git init, clone, add, commit, push, pull, and checking git status and log.");
        add("Git", ProficiencyLevel.INTERMEDIATE, "Branch management (checkout -b, merge), resolving merge conflicts, git stash, and pull request workflows.");
        add("Git", ProficiencyLevel.ADVANCED, "Interactive rebase (git rebase -i), cherry-pick, bisect for bug hunting, and clean commit history practices.");
        add("Git", ProficiencyLevel.EXPERT, "Architect Git workflows (GitFlow/trunk-based), configure Git hooks, submodules, and repository maintenance.");

        // Spring Boot
        add("Spring Boot", ProficiencyLevel.BASIC, "Bootstrap a Spring Boot app, understand @SpringBootApplication, controller endpoints, and application.properties.");
        add("Spring Boot", ProficiencyLevel.INTERMEDIATE, "Master Dependency Injection (@Autowired, @Service, @Repository), Spring Data JPA, and H2/MySQL integration.");
        add("Spring Boot", ProficiencyLevel.ADVANCED, "Implement Spring Security (JWT/session auth), custom exception handling, DTO mapping, validations, and actuator metrics.");
        add("Spring Boot", ProficiencyLevel.EXPERT, "Design production-grade microservices with Spring Cloud, asynchronous event listeners, caching (Redis), and distributed tracing.");

        // REST APIs
        add("REST APIs", ProficiencyLevel.BASIC, "Understand HTTP methods (GET, POST, PUT, DELETE), URL paths, status codes (200, 201, 400, 404, 500).");
        add("REST APIs", ProficiencyLevel.INTERMEDIATE, "Design RESTful resource naming, JSON request/response bodies, request params, and proper headers.");
        add("REST APIs", ProficiencyLevel.ADVANCED, "Implement pagination, sorting, filtering, API versioning, OpenAPI/Swagger documentation, and rate limiting.");
        add("REST APIs", ProficiencyLevel.EXPERT, "Architect hypermedia APIs (HATEOAS), idempotent webhook delivery, and zero-downtime contract management.");

        // Database Management
        add("Database Management", ProficiencyLevel.BASIC, "Understand relational concepts, tables, columns, rows, primary keys, and foreign keys.");
        add("Database Management", ProficiencyLevel.INTERMEDIATE, "Apply database normalization (1NF, 2NF, 3NF), manage relational integrity, and perform backup/restore.");
        add("Database Management", ProficiencyLevel.ADVANCED, "Implement database transactions with ACID guarantees, deadlock prevention, indexing strategies, and connection pooling.");
        add("Database Management", ProficiencyLevel.EXPERT, "Design sharded database clusters, multi-region replication, failover automation, and disaster recovery strategies.");

        // Docker
        add("Docker", ProficiencyLevel.BASIC, "Learn container concepts vs VMs, docker pull, run, ps, stop, and logs.");
        add("Docker", ProficiencyLevel.INTERMEDIATE, "Write multi-stage Dockerfiles for Java/Spring Boot applications, map ports, and mount volumes.");
        add("Docker", ProficiencyLevel.ADVANCED, "Create multi-container environments using Docker Compose (App + Database), network bridging, and environment variables.");
        add("Docker", ProficiencyLevel.EXPERT, "Optimize image security (distroless/Alpine), layer caching, non-root user execution, and container orchestration.");

        // Linux
        add("Linux", ProficiencyLevel.BASIC, "Navigate file systems (cd, ls, pwd), file operations (mkdir, cp, mv, rm), and viewing files (cat, less).");
        add("Linux", ProficiencyLevel.INTERMEDIATE, "Manage file permissions (chmod, chown), environment variables, process inspection (ps, top, kill), and package managers.");
        add("Linux", ProficiencyLevel.ADVANCED, "Write Bash automation scripts, configure systemd services, SSH key-based auth, and inspect system log files (journalctl).");
        add("Linux", ProficiencyLevel.EXPERT, "Harden server security (iptables/ufw), kernel tuning, storage volume management (LVM), and network diagnostics.");
    }

    private void add(String skill, ProficiencyLevel level, String objective) {
        objectiveMap.computeIfAbsent(skill.toLowerCase(), k -> new HashMap<>()).put(level, objective);
    }

    public String getObjective(String skillName, ProficiencyLevel targetLevel) {
        if (skillName != null) {
            Map<ProficiencyLevel, String> levels = objectiveMap.get(skillName.trim().toLowerCase());
            if (levels != null) {
                // Try target level
                if (levels.containsKey(targetLevel)) {
                    return levels.get(targetLevel);
                }
                // Fallback to highest available below target
                for (int val = targetLevel.getValue(); val >= 1; val--) {
                    ProficiencyLevel l = ProficiencyLevel.fromValue(val);
                    if (levels.containsKey(l)) {
                        return levels.get(l);
                    }
                }
            }
        }

        // Generic dynamic fallback objective
        return "Advance knowledge in " + skillName + " to achieve " + targetLevel.getLabel()
                + " proficiency through hands-on coding exercises, project implementations, and architectural best practices.";
    }
}
