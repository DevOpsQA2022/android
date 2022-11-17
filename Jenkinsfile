pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'make' 
//                 sh './gradlew clean'
                     sh './gradlew assembleRelease'
                archiveArtifacts artifacts: '**/target/*.apk'
            }
        }
    }
}
