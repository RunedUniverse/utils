pipeline {
	agent any
	tools {
		maven 'maven-latest'
		jdk 'java-1.8.0'
	}
	environment {
		REPOS = """${sh(
				returnStdout: true,
				script: 'REPOS=repo-releases; if [ $GIT_BRANCH != master ]; then REPOS=$REPOS,repo-development; fi; printf $REPOS'
			)}"""
	}
	stages {
		stage('Initialize') {
			steps {
				sh 'echo "PATH = ${PATH}"'
				sh 'echo "M2_HOME = ${M2_HOME}"'
				sh 'printenv | sort'
			}
		}
		stage('Update Maven Repo') {
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn -P ${REPOS} dependency:resolve --non-recursive'
					sh 'mvn -P ${REPOS},install --non-recursive'
					sh 'ls -l target'
				}
			}
		}
		
		stage('License Check') {
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn -P ${REPOS},license-check,license-prj-utils-approve,license-apache2-approve'
				}
			}
		}
		
		stage('Install - Bill of Materials') {
			steps {
				dir(path: 'java-utils-bom') {
					sh 'mvn -P ${REPOS},install --non-recursive'
					sh 'ls -l target'
				}
			}
		}

		stage('Build [1st Level]') {
			parallel {
				stage('Java Logging Tools') {
					steps {
						dir(path: 'java-utils-logging') {
							sh 'mvn -P ${REPOS},install --non-recursive'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Error Handling Library') {
					steps {
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P ${REPOS},install --non-recursive'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Utils Common') {
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P ${REPOS},install --non-recursive'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Utils Async') {
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P ${REPOS},install --non-recursive'
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
							sh 'mvn -P ${REPOS},install --non-recursive'
							sh 'ls -l target'
						}
					}
				}
				stage('Java Chain Library') {
					steps {
						dir(path: 'java-utils-chain') {
							sh 'mvn -P ${REPOS},install --non-recursive'
							sh 'ls -l target'
						}
					}
				}
			}
		}
		
		stage ('Tracing-Data'){
			steps {
				script {
					switch(GIT_BRANCH) {
						case 'master':
							dir(path: 'java-utils-bom') {
								sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
								sh 'mvn -P repo-releases,deploy-signed,gen-eff-pom'
							}
							dir(path: 'java-utils-logging') {
								sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
								sh 'mvn -P repo-releases,deploy-signed,gen-eff-pom'
							}
							dir(path: 'java-utils-error-handling') {
								sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
								sh 'mvn -P repo-releases,deploy-signed,gen-eff-pom'
							}
							dir(path: 'java-utils-common') {
								sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
								sh 'mvn -P repo-releases,deploy-signed,gen-eff-pom'
							}
							dir(path: 'java-utils-async') {
								sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
								sh 'mvn -P repo-releases,deploy-signed,gen-eff-pom'
							}
							dir(path: 'java-utils-scanner') {
								sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
								sh 'mvn -P repo-releases,deploy-signed,gen-eff-pom'
							}
							dir(path: 'java-utils-chain') {
							}
							break
					}
				}
			}
			
			
			
			parallel {
				stage('Development') {
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-bom') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-logging') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-common') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-async') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-chain') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P dist-repo-development,deploy,gen-eff-pom'
						}
					}
				}
				stage('Release') {
					when {
						branch 'master'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-bom') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-logging') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-common') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-async') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-chain') {
							sh 'mvn -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
					}
				}
			}
			post {
				always {
					archiveArtifacts artifacts: 'maven-build-trace/*.xml', fingerprint: true
				}
			}
		}

		stage('Test') {
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn -P ${REPOS},test-junit-jupiter'
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
				stage('Development') {
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
						dir(path: 'java-utils-bom') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
						dir(path: 'java-utils-logging') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
						dir(path: 'java-utils-common') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
						dir(path: 'java-utils-async') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
						dir(path: 'java-utils-chain') {
							sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
						}
					}
				}
				stage('Release') {
					when {
						branch 'master'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
						dir(path: 'java-utils-bom') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
						dir(path: 'java-utils-logging') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
						dir(path: 'java-utils-error-handling') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
						dir(path: 'java-utils-common') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
						dir(path: 'java-utils-async') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
						dir(path: 'java-utils-chain') {
							sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
						}
					}
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '*/target/*.pom', fingerprint: true
					archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
					archiveArtifacts artifacts: '*/target/*.asc', fingerprint: true
				}
			}
		}
		
		stage('Stage at Maven-Central') {
			when {
				branch 'master'
			}
			steps {
				// never add : -P ${REPOS} => this is ment to fail here
				dir(path: '.maven-parent') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
				}
				dir(path: 'java-utils-bom') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
				}
				dir(path: 'java-utils-logging') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
				}
				dir(path: 'java-utils-error-handling') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
				}
				dir(path: 'java-utils-common') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
				}
				dir(path: 'java-utils-async') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
				}
				dir(path: 'java-utils-scanner') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
				}
				dir(path: 'java-utils-chain') {
					sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
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
