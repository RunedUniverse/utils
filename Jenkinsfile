pipeline {
	agent any
	tools {
		maven 'Maven 3.6.3'
		jdk 'OpenJDK 8'
	}
	stages {
		stage('Update Maven Repo') {
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn dependency:resolve'
					sh 'mvn -P install --non-recursive'
					sh 'ls -l target'
				}
			}
		}
		stage('Initialize') {
			steps {
				sh '''
					echo "PATH = ${PATH}"
					echo "M2_HOME = ${M2_HOME}"
				'''
			}
		}
		
		stage('License Check') {
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
				}
			}
		}
		
		stage('Install - Bill of Materials') {
			steps {
				dir(path: 'java-utils-bom') {
					sh 'mvn -P install --non-recursive'
					sh 'ls -l target'
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '*/target/trace/install*', fingerprint: true
				}
			}
		}

		stage('Build [1st Level]') {
			parallel {
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-utils-logging') {
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
					post {
						always {
							archiveArtifacts artifacts: '*/target/trace/install*', fingerprint: true
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
					post {
						always {
							archiveArtifacts artifacts: '*/target/trace/install*', fingerprint: true
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
					post {
						always {
							archiveArtifacts artifacts: '*/target/trace/install*', fingerprint: true
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
					post {
						always {
							archiveArtifacts artifacts: '*/target/trace/install*', fingerprint: true
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
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
					post {
						always {
							archiveArtifacts artifacts: '*/target/trace/install*', fingerprint: true
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
					post {
						always {
							archiveArtifacts artifacts: '*/target/trace/install*', fingerprint: true
						}
					}
				}
			}
		}

		stage('Test') {
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn -P test-junit-jupiter'
				}
			}
			post {
				always {
					junit '*/target/surefire-reports/*.xml'
					archiveArtifacts artifacts: '*/target/trace/test-junit-jupiter*', fingerprint: true
				}
				failure {
				    archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
				}
			}
		}

		stage('Deploy') {
			steps {
				dir(path: '.maven-parent') {				
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
				archiveArtifacts artifacts: '*/target/trace/deploy*', fingerprint: true
				archiveArtifacts artifacts: '*/target/*.pom', fingerprint: true
				archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: '*/target/*.asc', fingerprint: true
			}
		}

		stage('Stage at Maven-Central') {
			steps {
				dir(path: '.maven-parent') {	
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
	post {
		cleanup {
			cleanWs()
		}
	}
}
