pipeline {
	agent any
	tools {
		maven 'Maven 3.6.3'
		jdk 'OpenJDK 8'
	}
	stages {
		stage('Initialize') {
			steps {
				sh '''
					echo "PATH = ${PATH}"
					echo "M2_HOME = ${M2_HOME}"
				'''
			}
		}
		
		stage('Install - Bill of Materials') {
			steps {
				dir(path: 'java-utils-bom') {
					sh 'mvn dependency:resolve'
					sh 'mvn -P jenkins-install --non-recursive'
				}
			}
		}

		stage('Build [1st Level]') {
			parallel {
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-utils-logging') {
							sh 'mvn -P license-check'
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P license-check'
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P license-check'
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P license-check'
							sh 'mvn -P jenkins-install'
						}
					}
				}
			}
		}
		
		stage('Build [2nd Level]') {
			parallel {
				stage('Java Scanner') {
					steps {
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P license-check'
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							sh 'mvn -P license-check'
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
						dir(path: 'java-utils-logging') {
							sh 'mvn -P jenkins-test-system'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P jenkins-test-system'
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P jenkins-test-system'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P jenkins-test-system'
						}
					}
				}
				stage('Java Scanner') {
					steps {
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P jenkins-test-system'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							sh 'mvn -P jenkins-test-system'
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
				stage('Bill of Materials') {
					steps {
						dir(path: 'java-utils-bom') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-utils-logging') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Scanner') {
					steps {
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P jenkins-deploy'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-java-utils') {
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
}
