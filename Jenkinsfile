pipeline {
    agent any
    tools{
        maven 'Maven_Home'
    }
    stages {
        stage ('Build') {
            steps {
              echo 'successfully'
                sh './gradlew clean'  
//               sh 'mvn -f web12/pom.xml <goals>'
               //  sh "mv target/*.war target/web12.war"
            }
            post{
                 success{
                     echo "Archiving the Artifacts"
                     archiveArtifacts artifacts: '**/target/*apk'
                    
                 }
            }
        }
   
//         stage ('Deploy') {
//             steps {
//                 deploy adapters: [tomcat8(credentialsId: 'a113e3b9-6ba1-471a-8989-c99776136ead', path: '', url: 'http://localhost:9090/')], contextPath: null, war: '**/*.war'
//             }
//         }
    }
}
