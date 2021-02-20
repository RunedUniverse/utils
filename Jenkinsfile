pipeline {
  agent any
  stages {
    stage('Initialize') {
      steps {
        sh '''
          echo "PATH = ${PATH}"
          echo "M2_HOME = ${M2_HOME}"
          '''
      }
    }

    stage('Build') {
      parallel {
        stage('java-utils') {
          steps {
            dir(path: 'java-utils') {
              sh 'mvn clean compile install deploy'
            }

          }
        }

        stage('java-utils-async') {
          steps {
            dir(path: 'java-utils-async') {
              sh 'mvn clean compile install deploy'
            }

          }
        }

      }
    }

  }
  tools {
    maven 'Maven 3.6.3'
    jdk 'OpenJDK 8'
  }
}
