def evalValue(expression, path = null) {
	return sh( returnStdout: true,
		script: "mvn org.apache.maven.plugins:maven-help-plugin:evaluate -Dexpression=${ expression } -q -DforceStdout ${ path==null ? '' : ('-pl='+path) } | tail -1")
}

def getToolchainId(mod) {
	if(mod.hasTag('jdk-11'))
		return 'toolchain-openjdk-11';
	return 'toolchain-openjdk-1-8-0';
}

def installArtifact(mod, parent = null) {
	if(!mod.active()) {
		skipStage()
		return
	}
	def relPath = (parent == null ? '.' : mod.relPathFrom(parent))
	// get module metadata
	def groupId = evalValue('project.groupId', relPath)
	def artifactId = evalValue('project.artifactId', relPath)
	def version = evalValue('project.version', relPath)
	echo "Building: ${ groupId }:${ artifactId }:${ version }"
	try {
		sh "mvn-dev -P ${ REPOS },${ getToolchainId(mod) },ci-install -pl=${ relPath }"
	} finally {
		def baseName = "${ artifactId }-${ version }"
		// create spec .pom in target/ path
		sh "cp -T '${ mod.path() }/pom.xml' '${ mod.path() }/target/${ baseName }.pom'"
		// archive artifacts
		dir(path: "${ mod.path() }/target") {
			sh 'ls -l'
			archiveArtifacts artifacts: "${ baseName }.pom", fingerprint: true
			if(mod.hasTag('pack-jar')) {
				archiveArtifacts artifacts: "${ baseName }*.jar", fingerprint: true
			}
		}
		// create signatures
		signArtifacts(artifacts: "${ baseName }*")
		// bundle artifacts + signatures
		bundleArtifacts( bundle: mod.id(), artifacts: "${ baseName }.pom*", metadata: [
			'groupId': groupId, 'artifactId': artifactId, 'version': version
		])
		for (test in [ false, true ]) {
			for (classifier in [ '', 'javadoc', 'sources' ]) {
				if(test)
					classifier = classifier=='' ? 'tests' : ('test-'+classifier)
				bundleArtifacts( bundle: mod.id(), artifacts: "${ baseName }${ classifier=='' ? '' : ('-'+classifier) }.jar*", metadata: [
					'groupId': groupId, 'artifactId': artifactId, 'version': version, 'classifier': classifier
				])
			}
		}
	}
}

def testArtifacts(toolchainId, tag, parent, flags = []) {
	stage(tag) {
		def mods = getModules(withTags: [ 'test', tag ]);

		if(!mods) {
			skipStage()
			return
		}

		def modPaths = mods.collect({ it.relPathFrom(parent) }).join(',');
		def testFlags = flags.collect({ "-D${ it }" }).join(' ');

		sh "mvn-dev -P ${ REPOS },${ toolchainId },ci-test-build -pl=${ modPaths } ${ testFlags }"
		sh "mvn-dev --fail-never -P ${ REPOS },${ toolchainId },ci-test-exec,test-system -pl=${ modPaths } ${ testFlags }"
		// check tests, archive reports in case junit flags errors
		junit '*/target/surefire-reports/*.xml'
		if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE')) {
			archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
		}
	}
}

node( label: 'linux' ) {
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
		if(env.BRANCH_NAME == 'master') {
			env.REPOS = 'repo-releases'
		} else {
			env.REPOS = 'repo-releases,repo-development'
		}

		stage('Initialize') {
			env.RESULT_PATH  = "${ WORKSPACE }/result/"
			env.ARCHIVE_PATH = "${ WORKSPACE }/archive/"
			sh "mkdir -p ${ RESULT_PATH }"
			sh "mkdir -p ${ ARCHIVE_PATH }"
			
			addModule id: 'maven-parent',               path: '.',                          name: 'Maven Parent',                      tags: [ 'parent' ]
			addModule id: 'java-utils-bom',             path: 'java-utils-bom',             name: 'Bill of Materials',                 tags: [ 'bom' ]
			addModule id: 'java-utils-async-api',       path: 'java-utils-async-api',       name: 'Java Async Utils [API]',            tags: [         'build1a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-async',           path: 'java-utils-async',           name: 'Java Async Utils',                  tags: [ 'test', 'build1',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-chain-api',       path: 'java-utils-chain-api',       name: 'Java Chain Library [API]',          tags: [         'build2a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-chain',           path: 'java-utils-chain',           name: 'Java Chain Library',                tags: [ 'test', 'build2',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-common-api',      path: 'java-utils-common-api',      name: 'Java Common Utils [API]',           tags: [         'build1a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-common',          path: 'java-utils-common',          name: 'Java Common Utils',                 tags: [ 'test', 'build1',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-conditional-api', path: 'java-utils-conditional-api', name: 'Java Conditional Utils [API]',      tags: [         'build2a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-conditional',     path: 'java-utils-conditional',     name: 'Java Conditional Utils',            tags: [ 'test', 'build2',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-config-api',      path: 'java-utils-config-api',      name: 'Java Config Utils [API]',           tags: [         'build1a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-config',          path: 'java-utils-config',          name: 'Java Config Utils',                 tags: [ 'test', 'build1',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-errors',          path: 'java-utils-errors',          name: 'Java Error Handling Library',       tags: [ 'test', 'build1',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-logging-api',     path: 'java-utils-logging-api',	    name: 'Java Logging Tools [API]',          tags: [         'build1a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-logging',         path: 'java-utils-logging',	        name: 'Java Logging Tools',                tags: [ 'test', 'build1',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-maven3-api',      path: 'java-utils-maven3-api',      name: 'Java Maven3 Utils [API]',           tags: [         'build1a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-maven3',          path: 'java-utils-maven3',          name: 'Java Maven3 Utils',                 tags: [ 'test', 'build1',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-maven3-ext-api',  path: 'java-utils-maven3-ext-api',  name: 'Java Maven3 Extension Utils [API]', tags: [         'build3a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-maven3-ext',      path: 'java-utils-maven3-ext',      name: 'Java Maven3 Extension Utils',       tags: [ 'test', 'build3',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-plexus',          path: 'java-utils-plexus',          name: 'Java Plexus Utils',                 tags: [ 'test', 'build2',  'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-scanner-api',     path: 'java-utils-scanner-api',     name: 'Java Scanner [API]',                tags: [         'build2a', 'pack-jar', 'jdk-1.8.0' ]
			addModule id: 'java-utils-scanner',         path: 'java-utils-scanner',         name: 'Java Scanner',                      tags: [ 'test', 'build2',  'pack-jar', 'jdk-1.8.0' ]
		}
		def parentMod = getModule(id: 'maven-parent');
		def bomMod = getModule(id: 'java-utils-bom');

		stage('Init Modules') {
			sshagent (credentials: ['RunedUniverse-Jenkins']) {
				perModule(failFast: true) {
					def mod = getModule();
					mod.activate(
						!mod.hasTag('skip') && sh(
								returnStdout: true,
								script: "git-check-version-tag ${ mod.id() } ${ mod.relPathFrom(parentMod) }"
							) == '1'
					);
				}
			}
		}
		stage ('Info') {
			sh 'printenv | sort'
		}

		stage('Update Maven Repo') {
			echo 'purging local maven repository'
			sh "mvn-dev -P ${ REPOS } dependency:purge-local-repository -DactTransitively=false -DreResolve=false"

			echo 'caching validation dependencies'
			sh "mvn-dev -P ${ REPOS },ci-validate dependency:resolve-plugins dependency:resolve -U --fail-never"

			if(checkAllModules(match: 'all', active: false)) {
				echo 'skipping build dependency download » unused'
			} else {
				echo 'caching build dependencies'
				sh "mvn-dev -P ${ REPOS },ci-install dependency:resolve -U --fail-never"
			}
		}

		stage('Code Validation') {
			sh "mvn-dev -P ${ REPOS },ci-validate,license-apache2-approve,license-epl-v10-approve --fail-at-end -T1C"
		}

		bundleContext {
			stage('Install Maven Parent') {
				installArtifact( parentMod );
			}
			stage('Install - BOMs') {
				perModule(withTagIn: [ 'bom' ]) {
					installArtifact( getModule(), parentMod );
				}
			}

			stage('Build [1st Level]') {
				perModule(withTagIn: [ 'build1a' ]) {
					installArtifact( getModule(), parentMod );
				}
				perModule(withTagIn: [ 'build1' ]) {
					installArtifact( getModule(), parentMod );
				}
			}
			stage('Build [2nd Level]') {
				perModule(withTagIn: [ 'build2a' ]) {
					installArtifact( getModule(), parentMod );
				}
				perModule(withTagIn: [ 'build2' ]) {
					installArtifact( getModule(), parentMod );
				}
			}
			stage('Build [3rd Level]') {
				perModule(withTagIn: [ 'build3a' ]) {
					installArtifact( getModule(), parentMod );
				}
				perModule(withTagIn: [ 'build3' ]) {
					installArtifact( getModule(), parentMod );
				}
			}

			stage('Test') {
				if(!checkAllModules(withTagIn: [ 'test' ], active: true)) {
					skipStage()
					return
				}

				echo 'force update bom version for tests -> test for possible collisions caused by this update'
				def bomVersion = evalValue('project.version', bomMod.relPathFrom(parentMod));
				def flags = [ "runeduniverse-utils-bom-version=${ bomVersion }" ];

				testArtifacts('toolchain-openjdk-1-8-0', 'jdk-1.8.0', parentMod, flags);
				testArtifacts('toolchain-openjdk-11',    'jdk-11',    parentMod, flags);
			}

			stage('Package Build Result') {
				if(checkAllModules(match: 'all', active: false)) {
					skipStage()
					return
				}
				dir(path: "${ env.RESULT_PATH }") {
					unarchive mapping: ['*':'.']
					sh 'ls -l'
					sh "tar -I \"pxz -9\" -cvf ${ ARCHIVE_PATH }utils.tar.xz *"
					sh "zip -9 ${ ARCHIVE_PATH }utils.zip *"
				}
				dir(path: "${ env.ARCHIVE_PATH }") {
					archiveArtifacts artifacts: '*', fingerprint: true
				}
			}

			stage('Deploy') {
				perModule {
					def mod = getModule();
					if(!mod.active()) {
						skipStage()
						return
					}
					// bundle info
					bundleInfo( bundle: mod.id(), metadata: true )
					// deploy to development repo
					stage('Develop'){
						deployArtifacts( bundle: mod.id(), repo: 'nexus-runeduniverse>maven-development' )
					}
					// deploy to release repo
					stage('Release') {
						if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE') || env.BRANCH_NAME != 'master') {
							skipStage()
							return
						}
						deployArtifacts( bundle: mod.id(), repo: 'nexus-runeduniverse>maven-releases' )
						def groupId = evalValue('project.groupId', mod.relPathFrom(parentMod))
						def artifactId = evalValue('project.artifactId', mod.relPathFrom(parentMod))
						def version = evalValue('project.version', mod.relPathFrom(parentMod))
						sshagent (credentials: ['RunedUniverse-Jenkins']) {
							sh "git tag -a ${ mod.id() }/v${ version } -f -m '[artifact] ${ groupId }:${ artifactId }:${ version }'"
							sh "git push origin ${ mod.id() }/v${ version }"
						}
					}
					// merge bundles into default
					bundleMerge( source: mod.id() )
				}
				stage('Stage at Maven-Central') {
					if(currentBuild.resultIsWorseOrEqualTo('UNSTABLE') || env.BRANCH_NAME != 'master') {
						skipStage()
						return
					}
					deployArtifacts( repo: 'maven-central>net.runeduniverse' )
				}
			}
		}

		cleanWs()
	}
}
