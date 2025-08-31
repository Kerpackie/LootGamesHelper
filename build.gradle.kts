plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

dependencies {
    // Add this line to include the LootGames JAR.
    // The path must exactly match the location and name of your file.
    compileOnly(files("libs/LootGames-2.2.0.jar"))
}
