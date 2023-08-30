pipeline {
	agent any
	tools {
		maven 'maven-latest'
	}
	environment {
		PATH = """${sh(
				returnStdout: true,
				script: 'chmod +x $WORKSPACE/.build/*; printf $WORKSPACE/.build:$PATH'
			)}"""

		GLOBAL_MAVEN_SETTINGS = """${sh(
				returnStdout: true,
				script: 'printf /srv/jenkins/.m2/global-settings.xml'
			)}"""
		MAVEN_SETTINGS = """${sh(
				returnStdout: true,
				script: 'printf $WORKSPACE/.mvn/settings.xml'
			)}"""
		MAVEN_TOOLCHAINS = """${sh(
				returnStdout: true,
				script: 'printf $WORKSPACE/.mvn/toolchains.xml'
			)}"""
		REPOS = """${sh(
				returnStdout: true,
				script: 'REPOS=repo-releases; if [ $GIT_BRANCH != master ]; then REPOS=$REPOS,repo-development; fi; printf $REPOS'
			)}"""

		CHANGES_MVN_PARENT = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag mvn-parent .'
			)}"""
		CHANGES_JAVA_UTILS_ASYNC = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-async ../java-utils-async'
			)}"""
		CHANGES_JAVA_UTILS_BOM = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-bom ../java-utils-bom'
			)}"""
		CHANGES_JAVA_UTILS_CHAIN = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-chain ../java-utils-chain'
			)}"""
		CHANGES_JAVA_UTILS_COMMON = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-common ../java-utils-common'
			)}"""
		CHANGES_JAVA_UTILS_ERRORS = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-errors ../java-utils-errors'
			)}"""
		CHANGES_JAVA_UTILS_LOGGING = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-logging ../java-utils-logging'
			)}"""
		CHANGES_JAVA_UTILS_SCANNER = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-scanner ../java-utils-scanner'
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
					sh 'mvn-dev -P ${REPOS} dependency:purge-local-repository -DactTransitively=false -DreResolve=false --non-recursive'
					sh 'mvn-dev -P ${REPOS} dependency:resolve --non-recursive'
					sh 'mkdir -p ../target/result/'
				}
			}
		}

		stage('License Check') {
			parallel {
			    stage('Maven Parent'){
					when {
						environment name: 'CHANGES_MVN_PARENT', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=.'
						}
					}
			    }
			    stage('Bill of Materials'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=../java-utils-bom'
						}
					}
			    }
			    stage('Java Utils Async'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=../java-utils-async'
						}
					}
			    }
			    stage('Java Chain Library'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=../java-utils-chain'
						}
					}
			    }
			    stage('Java Utils Common'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=../java-utils-common'
						}
					}
			    }
			    stage('Java Error Handling Library'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=../java-utils-errors'
						}
					}
			    }
			    stage('Java Logging Tools'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=../java-utils-logging'
						}
					}
			    }
			    stage('Java Scanner'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},license-check,license-apache2-approve -pl=../java-utils-scanner'
						}
					}
			    }
			}
		}
		stage('Install Maven Parent') {
			when {
				environment name: 'CHANGES_MVN_PARENT', value: '1'
			}
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
					sh 'ls -l target'
				}
			}
			post {
				always {
					dir(path: '.maven-parent/target') {
						archiveArtifacts artifacts: '*.pom', fingerprint: true
						archiveArtifacts artifacts: '*.asc', fingerprint: true
						sh 'cp *.pom *.asc ../../target/result/'
					}
				}
			}
		}

		stage('Install - Bill of Materials') {
			when {
				anyOf {
					environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
				}
			}
			steps {
				dir(path: '.maven-parent') {
					sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=../java-utils-bom'
				}
			}
			post {
				always {
					dir(path: 'java-utils-bom/target') {
						sh 'ls -l'
						archiveArtifacts artifacts: '*.pom', fingerprint: true
						archiveArtifacts artifacts: '*.asc', fingerprint: true
						sh 'cp *.pom *.asc ../../target/result/'
					}
				}
			}
		}

		stage('Build [1st Level]') {
			parallel {
				stage('Java Logging Tools') {
					when {
						anyOf {
							environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
						}
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=../java-utils-logging'
						}
					}
					post {
						always {
							dir(path: 'java-utils-logging/target') {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.asc ../../target/result/'
							}
						}
					}
				}
				stage('Java Error Handling Library') {
					when {
						anyOf {
							environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
						}
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=../java-utils-errors'
						}
					}
					post {
						always {
							dir(path: 'java-utils-errors/target') {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.asc ../../target/result/'
							}
						}
					}
				}
				stage('Java Utils Common') {
					when {
						anyOf {
							environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
						}
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=../java-utils-common'
						}
					}
					post {
						always {
							dir(path: 'java-utils-common/target') {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.asc ../../target/result/'
							}
						}
					}
				}
				stage('Java Utils Async') {
					when {
						anyOf {
							environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
						}
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=../java-utils-async'
						}
					}
					post {
						always {
							dir(path: 'java-utils-async/target') {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.asc ../../target/result/'
							}
						}
					}
				}
			}
		}
		
		stage('Build [2nd Level]') {
			parallel {
				stage('Java Scanner') {
					when {
						anyOf {
							environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
						}
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=../java-utils-scanner'
						}
					}
					post {
						always {
							dir(path: 'java-utils-scanner/target') {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.asc ../../target/result/'
							}
						}
					}
				}
				stage('Java Chain Library') {
					when {
						anyOf {
							environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
						}
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=../java-utils-chain'
						}
					}
					post {
						always {
							dir(path: 'java-utils-chain/target') {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.asc ../../target/result/'
							}
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
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-bom') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-logging') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-errors') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-common') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-async') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-scanner') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
						dir(path: 'java-utils-chain') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P dist-repo-development,deploy,gen-eff-pom'
						}
					}
				}
				stage('Release') {
					when {
						branch 'master'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-bom') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-logging') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-errors') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-common') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-async') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-scanner') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
						}
						dir(path: 'java-utils-chain') {
							sh 'mvn-dev -P ${REPOS},test-junit-jupiter,gen-eff-pom'
							sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed,gen-eff-pom'
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
					sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,test-junit-jupiter -X'
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

		stage('Package Build Result') {
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
				dir(path: 'target/result') {
					sh 'ls -l'
					sh 'tar -I "pxz -9" -cvf ../utils.tar.xz *'
					sh 'zip -9 ../utils.zip *'
				}
			}
			post {
				always {
					dir(path: 'target') {
						archiveArtifacts artifacts: '*.tar.xz', fingerprint: true
						archiveArtifacts artifacts: '*.zip', fingerprint: true
					}
				}
			}
		}

		stage('Deploy') {
			parallel {
				stage('Develop') {
					stages {
						stage('mvn-parent') {
							when {
								environment name: 'CHANGES_MVN_PARENT', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('java-utils-bom') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=../java-utils-bom'
								}
							}
						}
						stage('java-utils-logging') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=../java-utils-logging'
								}
							}
						}
						stage('java-utils-errors') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=../java-utils-errors'
								}
							}
						}
						stage('java-utils-common') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=../java-utils-common'
								}
							}
						}
						stage('java-utils-async') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=../java-utils-async'
								}
							}
						}
						stage('java-utils-scanner') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=../java-utils-scanner'
								}
							}
						}
						stage('java-utils-chain') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=../java-utils-chain'
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
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-pom-signed -pl=.'
								}
							}
						}
						stage('java-utils-bom') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-pom-signed -pl=../java-utils-bom'
								}
							}
						}
						stage('java-utils-logging') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=../java-utils-logging'
								}
							}
						}
						stage('java-utils-errors') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=../java-utils-errors'
								}
							}
						}
						stage('java-utils-common') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=../java-utils-common'
								}
							}
						}
						stage('java-utils-async') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=../java-utils-async'
								}
							}
						}
						stage('java-utils-scanner') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=../java-utils-scanner'
								}
							}
						}
						stage('java-utils-chain') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
							}
							steps {
								dir(path: '.maven-parent') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=../java-utils-chain'
								}
							}
						}
					}
				}
			}
			post {
				always {
					archiveArtifacts artifacts: '*/target/*.pom,*/target/*.jar,*/target/*.asc' fingerprint: true
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
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-pom-signed -pl=.'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag maven-parent .)'
							}
						}
					}
				}
				stage('java-utils-bom') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-pom-signed -pl=../java-utils-bom'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag java-utils-bom ../java-utils-bom)'
							}
						}
					}
				}
				stage('java-utils-logging') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=../java-utils-logging'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag java-utils-logging ../java-utils-logging)'
							}
						}
					}
				}
				stage('java-utils-errors') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=../java-utils-errors'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag java-utils-errors ../java-utils-errors)'
							}
						}
					}
				}
				stage('java-utils-common') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=../java-utils-common'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag java-utils-common ../java-utils-common)'
							}
						}
					}
				}
				stage('java-utils-async') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=../java-utils-async'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag java-utils-async ../java-utils-async)'
							}
						}
					}
				}
				stage('java-utils-scanner') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=../java-utils-scanner'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag java-utils-scanner ../java-utils-scanner)'
							}
						}
					}
				}
				stage('java-utils-chain') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
					}
					steps {
						dir(path: '.maven-parent') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=../java-utils-chain'
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag java-utils-chain ../java-utils-chain)'
							}
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
