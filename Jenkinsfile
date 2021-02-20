pipeline {
  agent any
  tools {
    maven 'Maven 3.6.3' 
    jdk 'OpenJDK 8'
   }
  stages {
    stage ('Initialize') {
      steps {
        sh '''
          echo "PATH = ${PATH}"
          echo "M2_HOME = ${M2_HOME}"
          '''
      }
    }
    stage('Build') {
      steps {
        dir(path: 'java-utils') {
          sh 'mvn clean compile install deploy'
        }

        dir(path: 'java-utils-async') {
          sh 'mvn clean compile install deploy'
        }

      }
    }

  }
}
