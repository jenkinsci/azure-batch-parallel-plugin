cmd /C mvn package -Denforcer.skip=true -Dmaven.test.skip=true
REM cmd /C mvn package -Denforcer.skip=true 
cmd /C java -jar "%ProgramFiles(x86)%\Jenkins\war\WEB-INF\jenkins-cli.jar" -s http://localhost:8080 install-plugin ".\target\jenkinsbatch.hpi" -restart