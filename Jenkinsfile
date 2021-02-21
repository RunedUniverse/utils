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
							sh 'mvn -DskipTests clean compile install deploy'
						}

					}
				}
				stage('java-utils-async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -DskipTests clean compile install deploy'
						}
					}
				}
			}
		}
      
		stage('Test') {
			steps {
				dir(path: 'java-utils') {
					sh 'mvn test'
				}
				dir(path: 'java-utils-async') {
					sh 'mvn test'
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
					junit '*/target/surefire-reports/*.xml'
				}
			}
		}
    }
	tools {
		maven 'Maven 3.6.3'
		jdk 'OpenJDK 8'
	}
}
