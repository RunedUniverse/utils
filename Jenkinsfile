node {
	withModules {
		tool(name: 'maven-latest', type: 'maven')

		stage('Checkout SCM') {
			checkout(scm)
		}

		sh 'chmod +x $WORKSPACE/.build/*'
		env.setProperty('PATH+SCRIPTS', "${ env.WORKSPACE }/.build")
		env.GLOBAL_MAVEN_SETTINGS     = '/srv/jenkins/.m2/global-settings.xml'
		env.MAVEN_SETTINGS            = "${ env.WORKSPACE }/.mvn/settings.xml"
		env.MAVEN_TOOLCHAINS          = "${ env.WORKSPACE }/.mvn/toolchains.xml"
		if(env.GIT_BRANCH == 'master') {
			env.REPOS = 'repo-releases'
		} else {
			env.REPOS = 'repo-releases,repo-development'
		}

		stage('Initialize') {
			echo 'install parent to tmp repo'
			sh "mvn-dev -P ${ REPOS },install --non-recursive"
			env.RESULT_PATH  = "${ WORKSPACE }/result/"
			env.ARCHIVE_PATH = "${ WORKSPACE }/archive/"
			sh "mkdir -p ${ RESULT_PATH }"
			sh "mkdir -p ${ ARCHIVE_PATH }"
			
			addModule id: 'mvn-parent',			path: '.',					name: 'Maven Parent'
			addModule id: 'java-utils-async',	path: 'java-utils-async',	name: 'Java Utils Async'
			addModule id: 'java-utils-bom',		path: 'java-utils-bom',		name: 'Bill of Materials'
			addModule id: 'java-utils-chain',	path: 'java-utils-chain',	name: 'Java Chain Library'
			addModule id: 'java-utils-common',	path: 'java-utils-common',	name: 'Java Utils Common'
			addModule id: 'java-utils-errors',	path: 'java-utils-errors',	name: 'Java Error Handling Library'
			addModule id: 'java-utils-logging',	path: 'java-utils-logging',	name: 'Java Logging Tools'
			addModule id: 'java-utils-maven',	path: 'java-utils-maven',	name: 'Java Maven Utils'
			addModule id: 'java-utils-plexus',	path: 'java-utils-plexus',	name: 'Java Plexus Utils'
			addModule id: 'java-utils-scanner',	path: 'java-utils-scanner',	name: 'Java Scanner'
		}
		// init after "Initialize" since the selection requires maven, which in turn requires the parent to be already installed!
		stage('Init Module Router') {
			perModule(failFast: true) {
				module.activate(
					sh(
						returnStdout: true,
						script: "git-check-version-tag ${ module.id() } ${ module.relPathFrom('mvn-parent') }"
					) == '1'
				);
			}
		}
		stage ('Info') {
			sh 'printenv | sort'
		}

		stage('Update Maven Repo') {
			if(!isAnyModuleActive()) {
				skipStage()
				return
			}
			sh "mvn-dev -P ${ REPOS } dependency:purge-local-repository -DactTransitively=false -DreResolve=false --non-recursive"
			sh "mvn-dev -P ${ REPOS } dependency:resolve-plugins -U"
		}

		stage('Code Validation') {
			perModule {
				if(!module.active()) {
					skipStage()
					return
				}
				sh "mvn-dev -P ${ REPOS },validate,license-apache2-approve,license-epl-v10-approve -pl=${ module.relPathFrom('mvn-parent') }"
			}
		}
	
		stage('Install Maven Parent') {
			if(!getModule(id: 'mvn-parent').active()) {
				skipStage()
				return
			}
			try {
				sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,install --non-recursive"
			} finally {
				dir(path: 'target') {
					sh 'ls -l'
					sh "cp *.pom *.asc ${ RESULT_PATH }"
				}
			}
		}
		stage('Install - Bill of Materials') {
			if(!isModuleActive(id: 'java-utils-bom')) {
				skipStage()
				return
			}
			try {
				sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,install -pl=java-utils-bom"
			} finally {
				dir(path: 'java-utils-bom/target') {
					sh 'ls -l'
					sh "cp *.pom *.asc ${ RESULT_PATH }"
				}
			}
		}

		stage('Build [1st Level]') {
			perModule(selectIds: [ 'java-utils-logging', 'java-utils-errors', 'java-utils-common', 'java-utils-async' ]) {
				if(!module.active()) {
					skipStage()
					return
				}
				try {
					sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,install -pl=${ module.relPathFrom('mvn-parent') }"
				} finally {
					dir(path: "${ module.path() }/target") {
						sh 'ls -l'
						sh "cp *.pom *.jar *.asc ${ RESULT_PATH }"
					}
				}
			}
		}
		stage('Build [2nd Level]') {
			perModule(selectIds: [ 'java-utils-scanner', 'java-utils-chain', 'java-utils-maven', 'java-utils-plexus' ]) {
				if(!module.active()) {
					skipStage()
					return
				}
				try {
					sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,install -pl=${ module.relPathFrom('mvn-parent') }"
				} finally {
					dir(path: "${ module.path() }/target") {
						sh 'ls -l'
						sh "cp *.pom *.jar *.asc ${ RESULT_PATH }"
					}
				}
			}
		}

		stage('Test') {
			if(!isModuleSelectionActive([
					'java-utils-logging',
					'java-utils-errors',
					'java-utils-common',
					'java-utils-async',
					'java-utils-scanner',
					'java-utils-chain',
					'java-utils-maven',
					'java-utils-plexus'
				])) {
				skipStage()
				return
			}
			sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,build-tests"
			sh "mvn-dev --fail-never -P ${ REPOS },toolchain-openjdk-1-8-0,test-junit-jupiter,test-system"
			// check tests, archive reports in case junit flags errors
			junit '*/target/surefire-reports/*.xml'
			if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE')) {
				archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
			}
		}

		stage('Package Build Result') {
			if(!isAnyModuleActive()) {
				skipStage()
				return
			}
			dir(path: "${ env.RESULT_PATH }") {
				sh 'ls -l'
				archiveArtifacts artifacts: '*', fingerprint: true
				sh "tar -I \"pxz -9\" -cvf ${ ARCHIVE_PATH }utils.tar.xz *"
				sh "zip -9 ${ ARCHIVE_PATH }utils.zip *"
			}
			dir(path: "${ env.ARCHIVE_PATH }") {
				archiveArtifacts artifacts: '*', fingerprint: true
			}
		}

		stage('Deploy') {
			perModule {
				if(!module.active()) {
					skipStage()
					return
				}
				stage('Develop'){
					sh "mvn-dev -P ${ REPOS },dist-repo-development,deploy -pl=${ module.relPathFrom('mvn-parent') }"
				}
				stage('Release') {
					if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE') || env.GIT_BRANCH != 'master') {
						skipStage()
						return
					}
					sh "mvn-dev -P ${ REPOS },dist-repo-releases,deploy-signed -pl=${ module.relPathFrom('mvn-parent') }"
				}
				stage('Stage at Maven-Central') {
					if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE') || env.GIT_BRANCH != 'master') {
						skipStage()
						return
					}
					// never add : -P ${REPOS} => this is ment to fail here
					sh "mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=${ module.relPathFrom('mvn-parent') }"
					sshagent (credentials: ['RunedUniverse-Jenkins']) {
						sh "git push origin \$(git-create-version-tag ${ module.id() } ${ module.relPathFrom('mvn-parent') })"
					}
				}
			}
		}

		cleanWs()
	}
}
