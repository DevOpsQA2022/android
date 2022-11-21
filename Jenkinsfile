pipeline {
    agent any
    tools{
      gradle 'GRADLE_HOME'
    }
    

    stages {
        stage('Build') {
            steps {
//                 sh 'make' 
               sh 'gradle clean build'
//                      sh './gradlew assembleRelease'               
            }
              post{
                 success{
                     echo "Archiving the Artifacts"
                     archiveArtifacts artifacts: '**/target/*.apk'
                    
                 }
            }            
        }
    }
}
