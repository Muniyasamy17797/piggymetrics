@echo off
setlocal enabledelayedexpansion

rem Define the list of modules
set modules=config monitoring registry gateway auth-service account-service statistics-service notification-service turbine-stream-service

rem Define common JVM arguments
set JVM_ARGS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED

rem Loop through each module and run Spring Boot
for %%M in (%modules%) do (
    echo Starting %%M...
    mvn -pl %%M spring-boot:run -Dspring-boot.run.jvmArguments="!JVM_ARGS!"
    echo %%M started successfully!
)

endlocal