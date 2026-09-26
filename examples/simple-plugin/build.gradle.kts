plugins {
    java
}

group = "io.github.ryotackey.example"
version = "1.0.0"

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    compileOnly(files("../../build/libs/PaperGraphicGUI-0.1.0-SNAPSHOT.jar"))
    compileOnly("io.papermc.paper:paper-api:26.2.build.126-stable")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.processResources {
    filteringCharset = "UTF-8"
}
