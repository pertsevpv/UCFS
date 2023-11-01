plugins {
  kotlin("jvm") version "1.9.20"
//  `java-library`
  application
//  `maven-publish`
}

group = "hollowcoder"
version = "1.0.0"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  //testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
  implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
}

tasks.test { useJUnitPlatform() }

kotlin { jvmToolchain(11) }

application { mainClass.set("org.srcgll.MainKt") }
//
//tasks.withType<Jar> {
//  dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
//  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//  manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
//  val sourcesMain = sourceSets.main.get()
//  val contents =
//      configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } +
//          sourcesMain.output
//  from(contents)
//}
//
//sourceSets {
//  main {
//    java {
//      setSrcDirs(listOf("src"))
//    }
//  }
//}
//
//publishing {
//  publications {
//    create<MavenPublication>("srcgll") {
//      from(components["java"])
//
//      versionMapping {
//        usage("java-api") { fromResolutionOf("runtimeClasspath") }
//        usage("java-runtime") { fromResolutionResult() }
//      }
//
//      pom {
//        name.set("srcgll")
//        url.set("https://github.com/cyb3r-b4stard/srcgll")
//        developers {
//          developer {
//            id.set("hollowcoder")
//            name.set("Ivan Lomikovskiy")
//            email.set("hollowcoder@yandex.ru")
//          }
//        }
//      }
//    }
//  }
//  repositories {
//    maven {
//      name = "GitHubPackages"
//      url = uri("https://maven.pkg.github.com/cyb3r-b4stard/srcgll")
//      credentials {
//        username = System.getenv("GITHUB_ACTOR")
//        password = System.getenv("GITHUB_TOKEN")
//      }
//    }
//  }
//}
