pipeline {
    agent any
    tools{
      gradle 'GRADLE_HOME'
    }
    

    stages {
        stage('Build') {
            steps {
//                 sh 'make' 
                sh './gradlew clean'
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
