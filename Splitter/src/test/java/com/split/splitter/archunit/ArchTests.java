package com.split.splitter.archunit;

import com.split.splitter.SplitterApplication;
import com.split.splitter.annotations.AggregateRoot;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = SplitterApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchTests {


    @ArchTest
    ArchRule keineZyklen = slices().matching("com.split.splitter.(*)..").should().beFreeOfCycles();


    @ArchTest
     ArchRule isOnionArchitecture = onionArchitecture().withOptionalLayers(true) // we don't have domain services. So we need withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..service..")
            .adapter("web", "..web..")
            .adapter("db","..db..");

    @ArchTest
    ArchRule onlyAggregateRootsArePublic = classes()
            .that()
            .areNotAnnotatedWith(AggregateRoot.class)
            .and()
            .resideInAPackage("..domain.model")
            .should()
            .notBePublic()
            .because("the implementation of an aggregate should be hidden");


}