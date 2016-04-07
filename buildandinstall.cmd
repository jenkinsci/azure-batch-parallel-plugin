cmd /C mvn package -Denforcer.skip=true -Dmaven.test.skip=true
REM cmd /C mvn package -Denforcer.skip=true 
cmd /C java -jar "c:\Program Files (x86)\Jenkins\war\WEB-INF\jenkins-cli.jar" -s http://localhost:8080 install-plugin F:\src\batch-jenkins\Jenkins_BatchApplication\jenkins\target\jenkinstip.hpi -restart