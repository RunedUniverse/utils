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

		CHANGES_MVN_PARENT = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh .maven-parent/ mvn-parent'
			)}"""
		CHANGES_JAVA_UTILS_ASYNC = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh java-utils-async/ java-utils-async'
			)}"""
		CHANGES_JAVA_UTILS_BOM = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh java-utils-bom/ java-utils-bom'
			)}"""
		CHANGES_JAVA_UTILS_CHAIN = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh java-utils-chain/ java-utils-chain'
			)}"""
		CHANGES_JAVA_UTILS_COMMON = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh java-utils-common/ java-utils-common'
			)}"""
		CHANGES_JAVA_UTILS_ERRORS = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh java-utils-errors/ java-utils-errors'
			)}"""
		CHANGES_JAVA_UTILS_LOGGING = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh java-utils-logging/ java-utils-logging'
			)}"""
		CHANGES_JAVA_UTILS_SCANNER = """${sh(
				returnStdout: true,
				script: '.build/check-for-change.sh java-utils-scanner/ java-utils-scanner'
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
			when {
				anyOf {
					environment name: 'CHANGES_MVN_PARENT', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
				}
			}
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn -P ${REPOS} dependency:resolve --non-recursive'
					sh 'mvn -P ${REPOS},install --non-recursive'
					sh 'ls -l target'
				}
			}
		}
		
		stage('License Check') {
			when {
				anyOf {
					environment name: 'CHANGES_MVN_PARENT', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
				}
			}
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn -P ${REPOS},license-check,license-prj-utils-approve,license-apache2-approve'
				}
			}
		}
		
		stage('Install - Bill of Materials') {
			when {
				anyOf {
					environment name: 'CHANGES_MVN_PARENT', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
				}
			}
			steps {
				dir(path: 'java-utils-bom') {
					sh 'mvn -P ${REPOS},install --non-recursive'
					sh 'ls -l target'
				}
			}
		}

		stage('Build [1st Level]') {
			when {
				anyOf {
					environment name: 'CHANGES_MVN_PARENT', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
				}
			}
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
						dir(path: 'java-utils-errors') {
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
			when {
				anyOf {
					environment name: 'CHANGES_MVN_PARENT', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
				}
			}
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
			when {
				anyOf {
					environment name: 'CHANGES_MVN_PARENT', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
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
						dir(path: 'java-utils-errors') {
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
						dir(path: 'java-utils-errors') {
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
			when {
				anyOf {
					environment name: 'CHANGES_MVN_PARENT', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
				}
			}
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
					stages {
						stage('mvn-parent') {
							when {
								environment name: 'CHANGES_MVN_PARENT', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy-pom --non-recursive'
								}
							}
						}
						stage('java-utils-bom') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
							}
							steps {
								dir(path: 'java-utils-bom') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy-pom --non-recursive'
								}
							}
						}
						stage('java-utils-logging') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
							}
							steps {
								dir(path: 'java-utils-logging') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('java-utils-errors') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
							}
							steps {
								dir(path: 'java-utils-errors') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('java-utils-common') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
							}
							steps {
								dir(path: 'java-utils-common') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('java-utils-async') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
							}
							steps {
								dir(path: 'java-utils-async') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('java-utils-scanner') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
							}
							steps {
								dir(path: 'java-utils-scanner') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('java-utils-chain') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
							}
							steps {
								dir(path: 'java-utils-chain') {
									sh 'mvn -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
					}
				}
				stage('Release') {
					when {
						branch 'master'
					}
					stages {
						stage('mvn-parent') {
							when {
								environment name: 'CHANGES_MVN_PARENT', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-pom-signed --non-recursive'
								}
							}
						}
						stage('java-utils-bom') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
							}
							steps {
								dir(path: 'java-utils-bom') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-pom-signed --non-recursive'
								}
							}
						}
						stage('java-utils-logging') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
							}
							steps {
								dir(path: 'java-utils-logging') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('java-utils-errors') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
							}
							steps {
								dir(path: 'java-utils-errors') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('java-utils-common') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
							}
							steps {
								dir(path: 'java-utils-common') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('java-utils-async') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
							}
							steps {
								dir(path: 'java-utils-async') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('java-utils-scanner') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
							}
							steps {
								dir(path: 'java-utils-scanner') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('java-utils-chain') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
							}
							steps {
								dir(path: 'java-utils-chain') {
									sh 'mvn -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
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
			stages {
				// never add : -P ${REPOS} => this is ment to fail here
				stage('mvn-parent') {
					when {
						environment name: 'CHANGES_MVN_PARENT', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn -P dist-repo-maven-central,deploy-pom-signed --non-recursive'
						}
					}
				}
				stage('java-utils-bom') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					}
					steps {
						dir(path: 'java-utils-bom') {
							sh 'mvn -P dist-repo-maven-central,deploy-pom-signed --non-recursive'
						}
					}
				}
				stage('java-utils-logging') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					}
					steps {
						dir(path: 'java-utils-logging') {
							sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('java-utils-errors') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					}
					steps {
						dir(path: 'java-utils-errors') {
							sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('java-utils-common') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					}
					steps {
						dir(path: 'java-utils-common') {
							sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('java-utils-async') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					}
					steps {
						dir(path: 'java-utils-async') {
							sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('java-utils-scanner') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					}
					steps {
						dir(path: 'java-utils-scanner') {
							sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('java-utils-chain') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
					}
					steps {
						dir(path: 'java-utils-chain') {
							sh 'mvn -P dist-repo-maven-central,deploy-signed --non-recursive'
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
