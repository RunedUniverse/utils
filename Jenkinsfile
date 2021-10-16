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
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-logging') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-error-handling') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('Java Utils') {
					steps {
						dir(path: 'java-utils') {
							sh 'mvn -DskipTests clean compile install'
						}
					}
				}
				stage('Java Utils Async') {
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
				stage('Java Scanner') {
					steps {
						dir(path: 'java-scanner') {
							sh 'mvn -DskipTests clean compile install'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-chain-library') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
			}
		}

		stage('Test') {
			parallel {
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-logging') {
							sh 'mvn -P jenkins-test'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-error-handling') {
							sh 'mvn -P jenkins-test'
						}
					}
				}
				stage('Java Utils') {
					steps {
						dir(path: 'java-utils') {
							sh 'mvn test'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn test'
						}
					}
				}
				stage('Java Scanner') {
					steps {
						dir(path: 'java-scanner') {
							sh 'mvn test'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-chain-library') {
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
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-logging') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-error-handling') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Utils') {
					steps {
						dir(path: 'java-utils') {
							sh 'mvn deploy'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn deploy'
						}
					}
				}
				stage('Java Scanner') {
					steps {
						dir(path: 'java-scanner') {
							sh 'mvn deploy'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-chain-library') {
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
