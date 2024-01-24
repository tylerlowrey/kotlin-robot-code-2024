pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        val frcYear = "2024"
        val frcHome: File
        val osName = System.getProperty("os.name").lowercase()
        if (osName.contains("windows")) {
            var publicFolder: String? = System.getenv("PUBLIC")
            if (publicFolder == null) {
                publicFolder = "C:\\Users\\Public"
            }
            val homeRoot = File(publicFolder, "wpilib")
            frcHome = File(homeRoot, frcYear)
        } else {
            val userFolder = System.getProperty("user.home")
            val homeRoot = File(userFolder, "wpilib")
            frcHome = File(homeRoot, frcYear)
        }
        val frcHomeMaven = File(frcHome, "maven")
        maven {
            name = "frcHome"
            setUrl(frcHomeMaven)
        }
    }
}

var props = System.getProperties()
props.setProperty("org.gradle.internal.native.headers.unresolved.dependencies.ignore", "true")
