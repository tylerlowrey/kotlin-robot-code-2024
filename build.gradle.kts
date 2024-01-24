import edu.wpi.first.deployutils.deploy.artifact.FileTreeArtifact
import edu.wpi.first.gradlerio.deploy.roborio.FRCJavaArtifact
import edu.wpi.first.gradlerio.deploy.roborio.RoboRIO
import edu.wpi.first.gradlerio.wpi.WPIExtension
import edu.wpi.first.toolchain.NativePlatforms


plugins {
    java
    kotlin("jvm") version "1.9.22"
    id("edu.wpi.first.GradleRIO") version "2024.2.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val ROBOT_MAIN_CLASS = "frc.robot.Main"

// Define my targets (RoboRIO) and artifacts (deployable files)
// This is added by GradleRIO's backing project DeployUtils.
deploy {
    targets {
        create("roborio", RoboRIO::class.java) {
            team = project.frc.teamNumber
            debug = project.frc.getDebugOrDefault(false)
            artifacts.create("frcJava", FRCJavaArtifact::class.java) {

            }
            artifacts.create("frcStaticFileDeploy", FileTreeArtifact::class.java) {
                files = project.fileTree("src/main/deploy")
                directory = "/home/lvuser/deploy"
            }
        }
    }
}
val deployArtifact = deploy.targets.getByName("roborio").artifacts.getByName("frcJava") as FRCJavaArtifact

// Set to true to use debug for JNI.
wpi.java.debugJni = false

// Set this to true to enable desktop support.
val includeDesktopSupport = true

repositories {
    maven {
        name = "WPILocal"
        url = project.extensions.getByType(WPIExtension::class.java).frcHome
            .map { x -> x.dir("maven") }
            .get()
            .asFile
            .toURI()
    }
    project.repositories.mavenCentral()
}

fun DependencyHandler.addListOfDependencies(configurationName: String, dependencies: List<Provider<String>>) {
    dependencies.forEach {
        add(configurationName, it)
    }
}

// Defining my dependencies. In this case, WPILib (+ friends), and vendor libraries.
// Also defines JUnit 5.
dependencies {
    wpi.java.deps.wpilib().forEach {
        implementation(it)
    }
    wpi.java.vendor.java().forEach {
        implementation(it)
    }

    addListOfDependencies("roborioDebug", wpi.java.deps.wpilibJniDebug(NativePlatforms.roborio) )
    addListOfDependencies("roborioDebug", wpi.java.vendor.jniDebug(NativePlatforms.roborio) )

    addListOfDependencies("roborioRelease", wpi.java.deps.wpilibJniRelease(NativePlatforms.roborio) )
    addListOfDependencies("roborioRelease", wpi.java.vendor.jniRelease(NativePlatforms.roborio) )

    addListOfDependencies("nativeDebug", wpi.java.deps.wpilibJniDebug(NativePlatforms.desktop))
    addListOfDependencies("nativeDebug", wpi.java.vendor.jniDebug(NativePlatforms.desktop))
    addListOfDependencies("simulationDebug", wpi.sim.enableDebug())

    addListOfDependencies("nativeRelease", wpi.java.deps.wpilibJniRelease(NativePlatforms.desktop))
    addListOfDependencies("nativeRelease", wpi.java.vendor.jniRelease(NativePlatforms.desktop))
    addListOfDependencies("simulationRelease", wpi.sim.enableRelease())

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
}

// Simulation configuration (e.g. environment variables).
wpi.sim.addGui().defaultEnabled = true
wpi.sim.addDriverstation()

// Setting up my Jar File. In this case, adding all libraries into the main jar ('fat jar')
// in order to make them all available at runtime. Also adding the manifest so WPILib
// knows where to look for our Robot Class.
tasks.jar {
    from(project.configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from(sourceSets.main.get().allSource)
    manifest(edu.wpi.first.gradlerio.GradleRIOPlugin.javaManifest(ROBOT_MAIN_CLASS))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Configure jar and deploy tasks
deployArtifact.setJarTask(tasks.jar.get())
wpi.java.configureExecutableTasks(tasks.jar.get())
wpi.java.configureTestTasks(tasks.test.get())

// Configure string concat to always inline compile
tasks.withType(JavaCompile::class.java) {
    options.compilerArgs.add("-XDstringConcat=inline")
}

