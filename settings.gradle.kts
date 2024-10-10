pluginManagement {
    repositories {
        google {
            mavenCentral()
        }
    }

    dependencyResolutionManagement {
        repositories {
            google()
            mavenCentral()
        }
    }

    rootProject.name = "Lab_04"
    include(":app")
}