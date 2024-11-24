pluginManagement {
    repositories {
        google()
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
        gradlePluginPortal()  // 保留默认的Gradle插件门户
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // 使用官方Google仓库
        mavenCentral()  // 使用官方Maven Central仓库
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/repositories/google/")
        }
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/groups/public/")
        }
    }
}
rootProject.name = "JoYClean"
include(":app")
 