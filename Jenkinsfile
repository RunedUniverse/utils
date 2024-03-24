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
	}
	stages {
		stage('Initialize') {
			steps {
				sh 'echo "PATH = ${PATH}"'
				sh 'echo "M2_HOME = ${M2_HOME}"'
				sh 'mvn-dev -P ${REPOS},install --non-recursive'
				script {
					env.CHANGES_MVN_PARENT = sh(
						returnStdout: true,
						script: 'git-check-version-tag mvn-parent .'
					)
					env.CHANGES_JAVA_UTILS_ASYNC = sh(
						returnStdout: true,
						script: 'git-check-version-tag java-utils-async java-utils-async'
					)
					env.CHANGES_JAVA_UTILS_BOM = sh(
						returnStdout: true,
						script: 'git-check-version-tag java-utils-bom java-utils-bom'
					)
					env.CHANGES_JAVA_UTILS_CHAIN = sh(
						returnStdout: true,
						script: 'git-check-version-tag java-utils-chain java-utils-chain'
					)
					env.CHANGES_JAVA_UTILS_COMMON = sh(
						returnStdout: true,
						script: 'git-check-version-tag java-utils-common java-utils-common'
					)
					env.CHANGES_JAVA_UTILS_ERRORS = sh(
						returnStdout: true,
						script: 'git-check-version-tag java-utils-errors java-utils-errors'
					)
					env.CHANGES_JAVA_UTILS_LOGGING = sh(
						returnStdout: true,
						script: 'git-check-version-tag java-utils-logging java-utils-logging'
					)
					env.CHANGES_JAVA_UTILS_SCANNER = sh(
						returnStdout: true,
						script: 'git-check-version-tag java-utils-scanner java-utils-scanner'
					)
					env.RESULT_PATH = "${WORKSPACE}/result/"
					env.ARCHIVE_PATH = "${WORKSPACE}/archive/"
				}
				sh 'printenv | sort'
				sh 'mkdir -p ${RESULT_PATH}'
				sh 'mkdir -p ${ARCHIVE_PATH}'
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
				sh 'mvn-dev -P ${REPOS} dependency:purge-local-repository -DactTransitively=false -DreResolve=false --non-recursive'
				sh 'mvn-dev -P ${REPOS} dependency:resolve --non-recursive'
			}
		}

		stage('Code Validation') {
			parallel {
			    stage('Maven Parent'){
					when {
						environment name: 'CHANGES_MVN_PARENT', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=.'
					}
			    }
			    stage('Bill of Materials'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=java-utils-bom'
					}
			    }
			    stage('Java Utils Async'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=java-utils-async'
					}
			    }
			    stage('Java Chain Library'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=java-utils-chain'
					}
			    }
			    stage('Java Utils Common'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=java-utils-common'
					}
			    }
			    stage('Java Error Handling Library'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=java-utils-errors'
					}
			    }
			    stage('Java Logging Tools'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=java-utils-logging'
					}
			    }
			    stage('Java Scanner'){
					when {
						environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					}
					steps {
						sh 'mvn-dev -P ${REPOS},validate,license-apache2-approve -pl=java-utils-scanner'
					}
			    }
			}
		}
		stage('Install Maven Parent') {
			when {
				environment name: 'CHANGES_MVN_PARENT', value: '1'
			}
			steps {
				sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
				sh 'ls -l target/'
			}
			post {
				always {
					dir(path: '.maven-parent/target') {
						sh 'ls -l'
						sh 'cp *.pom *.asc ${RESULT_PATH}'
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
				sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=java-utils-bom'
			}
			post {
				always {
					dir(path: 'java-utils-bom/target') {
						sh 'ls -l'
						sh 'cp *.pom *.asc ${RESULT_PATH}'
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
						sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=java-utils-logging'
					}
					post {
						always {
							dir(path: 'java-utils-logging/target') {
								sh 'ls -l'
								sh 'cp *.pom *.jar *.asc ${RESULT_PATH}'
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
						sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=java-utils-errors'
					}
					post {
						always {
							dir(path: 'java-utils-errors/target') {
								sh 'ls -l'
								sh 'cp *.pom *.jar *.asc ${RESULT_PATH}'
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
						sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=java-utils-common'
					}
					post {
						always {
							dir(path: 'java-utils-common/target') {
								sh 'ls -l'
								sh 'cp *.pom *.jar *.asc ${RESULT_PATH}'
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
						sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=java-utils-async'
					}
					post {
						always {
							dir(path: 'java-utils-async/target') {
								sh 'ls -l'
								sh 'cp *.pom *.jar *.asc ${RESULT_PATH}'
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
						sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=java-utils-scanner'
					}
					post {
						always {
							dir(path: 'java-utils-scanner/target') {
								sh 'ls -l'
								sh 'cp *.pom *.jar *.asc ${RESULT_PATH}'
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
						sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install -pl=java-utils-chain'
					}
					post {
						always {
							dir(path: 'java-utils-chain/target') {
								sh 'ls -l'
								sh 'cp *.pom *.jar *.asc ${RESULT_PATH}'
							}
						}
					}
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
				sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,build-tests'
				sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,test-junit-jupiter,test-system'
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
				dir(path: "${env.RESULT_PATH}") {
					sh 'ls -l'
					sh 'tar -I "pxz -9" -cvf ${ARCHIVE_PATH}utils.tar.xz *'
					sh 'zip -9 ${ARCHIVE_PATH}utils.zip *'
				}
			}
			post {
				always {
					dir(path: "${env.RESULT_PATH}") {
						archiveArtifacts artifacts: '*', fingerprint: true
					}
					dir(path: "${env.ARCHIVE_PATH}") {
						archiveArtifacts artifacts: '*', fingerprint: true
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
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
							}
						}
						stage('java-utils-bom') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=java-utils-bom'
							}
						}
						stage('java-utils-logging') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=java-utils-logging'
							}
						}
						stage('java-utils-errors') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=java-utils-errors'
							}
						}
						stage('java-utils-common') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=java-utils-common'
							}
						}
						stage('java-utils-async') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=java-utils-async'
							}
						}
						stage('java-utils-scanner') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=java-utils-scanner'
							}
						}
						stage('java-utils-chain') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy -pl=java-utils-chain'
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
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-pom-signed -pl=.'
							}
						}
						stage('java-utils-bom') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-pom-signed -pl=java-utils-bom'
							}
						}
						stage('java-utils-logging') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=java-utils-logging'
							}
						}
						stage('java-utils-errors') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=java-utils-errors'
							}
						}
						stage('java-utils-common') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=java-utils-common'
							}
						}
						stage('java-utils-async') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=java-utils-async'
							}
						}
						stage('java-utils-scanner') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=java-utils-scanner'
							}
						}
						stage('java-utils-chain') {
							when {
								environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed -pl=java-utils-chain'
							}
						}
					}
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
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-pom-signed -pl=.'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag maven-parent .)'
						}
					}
				}
				stage('java-utils-bom') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_BOM', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-pom-signed -pl=java-utils-bom'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag java-utils-bom java-utils-bom)'
						}
					}
				}
				stage('java-utils-logging') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_LOGGING', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=java-utils-logging'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag java-utils-logging java-utils-logging)'
						}
					}
				}
				stage('java-utils-errors') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_ERRORS', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=java-utils-errors'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag java-utils-errors java-utils-errors)'
						}
					}
				}
				stage('java-utils-common') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_COMMON', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=java-utils-common'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag java-utils-common java-utils-common)'
						}
					}
				}
				stage('java-utils-async') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_ASYNC', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=java-utils-async'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag java-utils-async java-utils-async)'
						}
					}
				}
				stage('java-utils-scanner') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_SCANNER', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=java-utils-scanner'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag java-utils-scanner java-utils-scanner)'
						}
					}
				}
				stage('java-utils-chain') {
					when {
						environment name: 'CHANGES_JAVA_UTILS_CHAIN', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=java-utils-chain'
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh 'git push origin $(git-create-version-tag java-utils-chain java-utils-chain)'
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
