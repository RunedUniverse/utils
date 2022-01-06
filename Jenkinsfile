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
					sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
					sh 'mvn -P install --non-recursive'
					sh 'ls -l target'
				}
			}
		}

		stage('Build [1st Level]') {
			parallel {
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-utils-logging') {
							sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
							sh 'mvn -P install'
							sh 'ls -l target'
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
							sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
							sh 'mvn -P install'
							sh 'ls -l target'
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
							sh 'mvn -P test-junit-jupiter,test-system'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P test-junit-jupiter,test-system'
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P test-junit-jupiter,test-system'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P test-junit-jupiter,test-system'
						}
					}
				}
				stage('Java Scanner') {
					steps {
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P test-junit-jupiter,test-system'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							sh 'mvn -P test-junit-jupiter,test-system'
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
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-releases,deploy-signed'
										break
									default:
										sh 'mvn -P repo-development,deploy'
										break
								}
							}
						}
					}
				}
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-utils-logging') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-releases,deploy-signed'
										break
									default:
										sh 'mvn -P repo-development,deploy'
										break
								}
							}
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-releases,deploy-signed'
										break
									default:
										sh 'mvn -P repo-development,deploy'
										break
								}
							}
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-releases,deploy-signed'
										break
									default:
										sh 'mvn -P repo-development,deploy'
										break
								}
							}
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-releases,deploy-signed'
										break
									default:
										sh 'mvn -P repo-development,deploy'
										break
								}
							}
						}
					}
				}
				stage('Java Scanner') {
					steps {
						dir(path: 'java-utils-scanner') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-releases,deploy-signed'
										break
									default:
										sh 'mvn -P repo-development,deploy'
										break
								}
							}
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-releases,deploy-signed'
										break
									default:
										sh 'mvn -P repo-development,deploy'
										break
								}
							}
						}
					}
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '*/**/target/*.pom', fingerprint: true
					archiveArtifacts artifacts: '*/**/target/*.jar', fingerprint: true
					archiveArtifacts artifacts: '*/**/target/*.asc', fingerprint: true
				}
			}
		}
		
		stage('Stage at Maven-Central') {
			parallel {
				stage('Bill of Materials') {
					steps {
						dir(path: 'java-utils-bom') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-maven-central,deploy-signed'
										break
									default:
										break
								}
							}
						}
					}
				}
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-utils-logging') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-maven-central,deploy-signed'
										break
									default:
										break
								}
							}
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-maven-central,deploy-signed'
										break
									default:
										break
								}
							}
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-maven-central,deploy-signed'
										break
									default:
										break
								}
							}
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-maven-central,deploy-signed'
										break
									default:
										break
								}
							}
						}
					}
				}
				stage('Java Scanner') {
					steps {
						dir(path: 'java-utils-scanner') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-maven-central,deploy-signed'
										break
									default:
										break
								}
							}
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							script {
								switch(GIT_BRANCH) {
									case 'master':
										sh 'mvn -P repo-maven-central,deploy-signed'
										break
									default:
										break
								}
							}
						}
					}
				}
			}
		}
	}
}
