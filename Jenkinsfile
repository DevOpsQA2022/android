pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'make' 
                sh './gradlew clean'
                archiveArtifacts artifacts: '**/target/*.apk', fingerprint: true 
            }
        }
    }
}
