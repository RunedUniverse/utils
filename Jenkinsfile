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

		stage('Build [1st Level]') {
			parallel {
				stage('java-logging') {
					steps {
						dir(path: 'java-logging') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('java-error-handling') {
					steps {
						dir(path: 'java-error-handling') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('java-utils') {
					steps {
						dir(path: 'java-utils') {
							sh 'mvn -DskipTests clean compile install'
						}
					}
				}
				stage('java-utils-async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -DskipTests clean compile install'
						}
					}
				}
			}
		}
		
		stage('Build [2nd Level]') {
			parallel {
				stage('java-scanner') {
					steps {
						dir(path: 'java-scanner') {
							sh 'mvn -DskipTests clean compile install'
						}
					}
				}
				stage('java-chain') {
					steps {
						dir(path: 'java-chain') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
			}
		}

		stage('Test') {
			parallel {
				stage('java-logging') {
					steps {
						dir(path: 'java-logging') {
							sh 'mvn -P jenkins-test'
						}
					}
				}
				stage('java-error-handling') {
					steps {
						dir(path: 'java-error-handling') {
							sh 'mvn -P jenkins-test'
						}
					}
				}
				stage('java-utils') {
					steps {
						dir(path: 'java-utils') {
							sh 'mvn test'
						}
					}
				}
				stage('java-utils-async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn test'
						}
					}
				}
				stage('java-scanner') {
					steps {
						dir(path: 'java-scanner') {
							sh 'mvn test'
						}
					}
				}
				stage('java-chain') {
					steps {
						dir(path: 'java-chain') {
							sh 'mvn -P jenkins-test'
						}
					}
				}
			}
			post {
				always {
					junit '*/target/surefire-reports/*.xml'
				}
				failure {
					archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
				}
			}
		}

		stage('Deploy') {
			parallel {
				stage('java-logging') {
					steps {
						dir(path: 'java-logging') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('java-error-handling') {
					steps {
						dir(path: 'java-error-handling') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('java-utils') {
					steps {
						dir(path: 'java-utils') {
							sh 'mvn deploy'
						}
					}
				}
				stage('java-utils-async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn deploy'
						}
					}
				}
				stage('java-scanner') {
					steps {
						dir(path: 'java-scanner') {
							sh 'mvn deploy'
						}
					}
				}
				stage('java-chain') {
					steps {
						dir(path: 'java-chain') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
				}
			}
		}
    }
	tools {
		maven 'Maven 3.6.3'
		jdk 'OpenJDK 8'
	}
}
