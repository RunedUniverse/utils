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
			env.RESULT_PATH  = "${ WORKSPACE }/result/"
			env.ARCHIVE_PATH = "${ WORKSPACE }/archive/"
			sh "mkdir -p ${ RESULT_PATH }"
			sh "mkdir -p ${ ARCHIVE_PATH }"
			
			addModule id: 'maven-parent',          path: '.',                     name: 'Maven Parent',                tags: [ 'parent' ]
			addModule id: 'java-utils-async',      path: 'java-utils-async',      name: 'Java Utils Async',            tags: [ 'test' ]
			addModule id: 'java-utils-bom',        path: 'java-utils-bom',        name: 'Bill of Materials',           tags: [ 'bom' ]
			addModule id: 'java-utils-chain',      path: 'java-utils-chain',      name: 'Java Chain Library',          tags: [ 'test' ]
			addModule id: 'java-utils-common',     path: 'java-utils-common',     name: 'Java Utils Common',           tags: [ 'test' ]
			addModule id: 'java-utils-conditions', path: 'java-utils-conditions', name: 'Java Utils Conditions',       tags: [ 'test' ]
			addModule id: 'java-utils-errors',     path: 'java-utils-errors',     name: 'Java Error Handling Library', tags: [ 'test' ]
			addModule id: 'java-utils-logging',    path: 'java-utils-logging',	  name: 'Java Logging Tools',          tags: [ 'test' ]
			addModule id: 'java-utils-maven',      path: 'java-utils-maven',      name: 'Java Maven Utils',            tags: [ 'test' ]
			addModule id: 'java-utils-plexus',     path: 'java-utils-plexus',     name: 'Java Plexus Utils',           tags: [ 'test' ]
			addModule id: 'java-utils-scanner',    path: 'java-utils-scanner',    name: 'Java Scanner',                tags: [ 'test' ]
		}

		stage('Init Modules') {
			sshagent (credentials: ['RunedUniverse-Jenkins']) {
				perModule(failFast: true) {
					module.activate(
						!module.hasTag('skip') && sh(
								returnStdout: true,
								script: "git-check-version-tag ${ module.id() } ${ module.relPathFrom('maven-parent') }"
							) == '1'
					);
				}
			}
		}
		stage ('Info') {
			sh 'printenv | sort'
		}

		stage('Update Maven Repo') {
			if(checkAllModules(match: 'all', active: false)) {
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
				sh "mvn-dev -P ${ REPOS },validate,license-apache2-approve,license-epl-v10-approve -pl=${ module.relPathFrom('maven-parent') }"
			}
		}
	
		stage('Install Maven Parent') {
			if(!getModule(id: 'maven-parent').active()) {
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
		stage('Install - BOMs') {
			perModule(withTagIn: [ 'bom' ]) {
				if(!module.active()) {
					skipStage()
					return
				}
				try {
					sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,install -pl=${ module.relPathFrom('maven-parent') }"
				} finally {
					dir(path: "${ module.path() }/target") {
						sh 'ls -l'
						sh "cp *.pom *.asc ${ RESULT_PATH }"
					}
				}
			}
		}

		stage('Build [1st Level]') {
			perModule(withIds: [ 'java-utils-logging', 'java-utils-errors', 'java-utils-common', 'java-utils-async' ]) {
				if(!module.active()) {
					skipStage()
					return
				}
				try {
					sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,install -pl=${ module.relPathFrom('maven-parent') }"
				} finally {
					dir(path: "${ module.path() }/target") {
						sh 'ls -l'
						sh "cp *.pom *.jar *.asc ${ RESULT_PATH }"
					}
				}
			}
		}
		stage('Build [2nd Level]') {
			perModule(withIds: [ 'java-utils-scanner', 'java-utils-chain', 'java-utils-conditions', 'java-utils-maven', 'java-utils-plexus' ]) {
				if(!module.active()) {
					skipStage()
					return
				}
				try {
					sh "mvn-dev -P ${ REPOS },toolchain-openjdk-1-8-0,install -pl=${ module.relPathFrom('maven-parent') }"
				} finally {
					dir(path: "${ module.path() }/target") {
						sh 'ls -l'
						sh "cp *.pom *.jar *.asc ${ RESULT_PATH }"
					}
				}
			}
		}

		stage('Test') {
			if(!checkAllModules(withTagIn: [ 'test' ], active: true)) {
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
			if(checkAllModules(match: 'all', active: false)) {
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
					sh "mvn-dev -P ${ REPOS },dist-repo-development,deploy -pl=${ module.relPathFrom('maven-parent') }"
				}
				stage('Release') {
					if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE') || env.GIT_BRANCH != 'master') {
						skipStage()
						return
					}
					sh "mvn-dev -P ${ REPOS },dist-repo-releases,deploy-signed -pl=${ module.relPathFrom('maven-parent') }"
				}
				stage('Stage at Maven-Central') {
					if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE') || env.GIT_BRANCH != 'master') {
						skipStage()
						return
					}
					// never add : -P ${REPOS} => this is ment to fail here
					sh "mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed -pl=${ module.relPathFrom('maven-parent') }"
					sshagent (credentials: ['RunedUniverse-Jenkins']) {
						sh "git push origin \$(git-create-version-tag ${ module.id() } ${ module.relPathFrom('maven-parent') })"
					}
				}
			}
		}

		cleanWs()
	}
}
