pipeline {
    agent any
     tools{
      gradle 'gradle'
    }
    stages {
        stage('Build') {              
            steps {                
               sh 'gradle build --stacktrace' 
                echo "successfully build"                
            }
        }
        stage('testing APK'){
                 post{
                 success{
                     echo "Archiving the Artifacts"
                     archiveArtifacts artifacts: '**/build/*.apk'
                    
                 }
            }            
        }
        stage('Testing status'){
            steps{
            script {
                 def deploymentDelay = input id: 'Test', message: 'Deploy to production?', submitter: 'admin', parameters: [choice(choices: ["Testing Pass","Testing Fail"], description: 'Move to deployment?', name: 'deploy')], [text(name: 'Reason', defaultValue: '', description: 'Enter some information about the Testing status')]
 // sleep time: deploymentDelay.toInteger(), unit: 'HOURS'
                 println deploymentDelay
 }
            }
        }
        Stage(''){
        }
    }
}
