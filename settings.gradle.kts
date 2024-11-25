pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/repositories/google/")
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/groups/public/")
        }
        google()
        gradlePluginPortal()  // 保留默认的Gradle插件门户
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/repositories/google/")
        }
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/groups/public/")
        }
        google()  // 使用官方Google仓库
        mavenCentral()  // 使用官方Maven Central仓库
    }
}
rootProject.name = "JoYClean"
include(":app")
 