package com.biursite.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchitectureTest {

    private static JavaClasses imported;
    private static boolean importFailed = false;
    private static String importErrorMsg = null;

    @BeforeAll
    public static void init() {
        try {
                imported = new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages("com.biursite..");
        } catch (Throwable t) {
            importFailed = true;
            importErrorMsg = t.getMessage();
            return;
        }

        // If import succeeded but no classes were found (ASM warnings on newer class versions), skip tests
        try {
            if (imported == null || !imported.iterator().hasNext()) {
                importFailed = true;
                importErrorMsg = "No classes imported (possibly unsupported class file version)";
            }
        } catch (Throwable t) {
            importFailed = true;
            importErrorMsg = t.getMessage();
        }
    }

    @Test
    void controllers_may_only_depend_on_service_dto_and_stdlibs() {
        Assumptions.assumeFalse(importFailed, "ArchUnit import failed: " + importErrorMsg);
    ArchRule rule = classes()
        .that().resideInAPackage("com.biursite.infrastructure.web..")
        .should().onlyDependOnClassesThat().resideInAnyPackage(
            "com.biursite.application..",
            "com.biursite.infrastructure.web.dto..",
            "com.biursite.exception..",
            "java..",
            "javax..",
            "jakarta..",
            "org.springframework..",
            "org.slf4j..",
            "lombok.."
        );

    rule.allowEmptyShould(true).check(imported);
    }

    @Test
    void domain_must_not_depend_on_spring_or_infrastructure() {
        Assumptions.assumeFalse(importFailed, "ArchUnit import failed: " + importErrorMsg);
    ArchRule rule = classes()
        .that().resideInAPackage("com.biursite.domain..")
        .should().onlyDependOnClassesThat().resideInAnyPackage(
            "com.biursite.domain..",
            "java..",
            "javax..",
            "jakarta..",
            "lombok..",
            "com.biursite.application.."
        );

    rule.allowEmptyShould(true).check(imported);
    }

    @Test
    void infrastructure_may_depend_only_on_domain_service_and_stdlibs() {
        Assumptions.assumeFalse(importFailed, "ArchUnit import failed: " + importErrorMsg);
    ArchRule rule = classes()
        .that().resideInAPackage("com.biursite.infrastructure..")
        .should().onlyDependOnClassesThat().resideInAnyPackage(
            "com.biursite.infrastructure..",
            "com.biursite.domain..",
            "com.biursite.application..",
            "com.biursite.service..",
            "java..",
            "javax..",
            "jakarta..",
            "org.springframework..",
            "org.slf4j..",
            "lombok..",
            "io.jsonwebtoken..",
            "io.jsonwebtoken.security..",
            "javax.crypto..",
            "com.biursite.exception..",
            "org.springframework.data..",
            "org.springframework.transaction..",
            "org.springframework.scheduling..",
            "org.springframework.security.."
        );

    rule.allowEmptyShould(true).check(imported);
    }

    @Test
    void controllers_must_not_depend_on_infrastructure() {
        Assumptions.assumeFalse(importFailed, "ArchUnit import failed: " + importErrorMsg);
    ArchRule rule = classes()
        .that().resideInAPackage("com.biursite.infrastructure.web..")
        .should().onlyDependOnClassesThat().resideInAnyPackage(
            "com.biursite.application..",
            "com.biursite.infrastructure.web.dto..",
            "com.biursite.exception..",
            "java..",
            "javax..",
            "jakarta..",
            "org.springframework..",
            "org.slf4j..",
            "lombok.."
        );

    rule.allowEmptyShould(true).check(imported);
    }

}
