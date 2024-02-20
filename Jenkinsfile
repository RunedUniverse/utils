@Library('runeduniverse-pipeline-library') _

def builder = new net.runeduniverse.lib.tools.jenkins.PipelineBuilder(this);

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
				sh 'printenv | sort'
				script {
					builder.setVersionSystem(new net.runeduniverse.lib.tools.jenkins.Git());
					def maven = new net.runeduniverse.lib.tools.jenkins.Maven(this);
					builder.addBuildTool(maven);
				
					def parent = maven.createProject(id: "mvn-parent", name: "Maven Parent", path: ".maven-parent");
					parent.addModule(id: "java-utils-bom", name: "Bill of Materials", path: "java-utils-bom", modulePath: "../java-utils-bom", bom: true);
					parent.addModule(id: "java-utils-async", name: "Java Utils Async", path: "java-utils-async", modulePath: "../java-utils-async");
					parent.addModule(id: "java-utils-chain", name: "Java Chain Library", path: "java-utils-chain", modulePath: "../java-utils-chain");
					parent.addModule(id: "java-utils-common", name: "Java Utils Common", path: "java-utils-common", modulePath: "../java-utils-common");
					parent.addModule(id: "java-utils-errors", name: "Java Error Handling Library", path: "java-utils-errors", modulePath: "../java-utils-errors");
					parent.addModule(id: "java-utils-logging", name: "Java Logging Tools", path: "java-utils-logging", modulePath: "../java-utils-logging");
					parent.addModule(id: "java-utils-maven", name: "Java Maven Utils", path: "java-utils-maven", modulePath: "../java-utils-maven");
					parent.addModule(id: "java-utils-plexus", name: "Java Plexus Tools", path: "java-utils-plexus", modulePath: "../java-utils-plexus");
					parent.addModule(id: "java-utils-scanner", name: "Java Scanner", path: "java-utils-scanner", modulePath: "../java-utils-scanner");
					
					parent.attachTo(builder);
					
					builder.checkChanges();
					builder.logProjects();
				}
			}
		}
		stage('Update Maven Repo') {
			steps {
				script {
					when(builder.hasChangedProjects()) {
						builder.purgeBuildCaches();
						builder.resolveResources();
					}
				}
				sh 'mkdir -p target/result/'
			}
		}
		stage('License Check') {
			steps {
				script {
					parallel builder.forEachProject(when: { p -> false && p.isActive() && p.hasChanged() }) { project ->
						if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
							project.execDev(profiles: [
								"license-check",
								"license-apache2-approve"
							], modules: ["."]);
						}
					}
				}
			}
		}

		stage('Install Parents') {
			steps {
				script {
					parallel builder.forEachProject(filter: { p -> p.isParent() }, when: { p -> p.isActive() && p.hasChanged() }) { project ->
						try {
							if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
								project.execDev(profiles: [
									"toolchain-openjdk-1-8-0",
									"install"
								], args: [
									"--non-recursive"
								], modules: ["."]);
							}
						} finally {
							dir(path: "${project.getPath()}/target") {
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.asc ../../target/result/'
							}
						}
					}
				}
			}
		}

		stage('Install - BOMs') {
			steps {
				script {
					parallel builder.forEachProject(filter: { p -> p.isBOM() }, when: { p -> p.isActive() && p.hasChanged() }) { project ->
						try {
							if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
								project.execDev(profiles: [
									"toolchain-openjdk-1-8-0",
									"install"
								], modules: ["."]);
							}
						} finally {
							dir(path: "${project.getPath()}/target") {
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

		stage('Build [1st Level]') {
			steps {
				script {
					parallel builder.forEachProject([
							"java-utils-logging",
							"java-utils-errors",
							"java-utils-common",
							"java-utils-async"
						], [
							when: { p -> p.isActive() && p.hasChanged() }
						]) { project ->
						try {
							if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
								project.execDev(profiles: [
									"toolchain-openjdk-1-8-0",
									"install"
								], modules: ["."]);
							}
						} finally {
							dir(path: "${project.getPath()}/target") {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.jar', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.jar *.asc ../../target/result/'
							}
						}
					}
				}
			}
		}

		stage('Build [2nd Level]') {
			steps {
				script {
					parallel builder.forEachProject([
							"java-utils-chain",
							"java-utils-scanner",
							"java-utils-plexus",
							"java-utils-maven"
						], [
							when: { p -> p.isActive() && p.hasChanged() }
						]) { project ->
						try {
							if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
								project.execDev(profiles: [
									"toolchain-openjdk-1-8-0",
									"install"
								], modules: ["."]);
							}
						} finally {
							dir(path: "${project.getPath()}/target") {
								sh 'ls -l'
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.jar', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.jar *.asc ../../target/result/'
							}
						}
					}
				}
			}
		}

		stage('Test') {
			steps {
				script {
					when(builder.hasActiveProjects() && builder.hasChangedProjects()) {
						parallel builder.forEachProject([
							filter: { p -> p.isParent() },
							when: { p -> p.collectProjects( includeSelf: true ).any { it.isActive() && it.hasChanged() }}
						]) { project ->
							// project: maven
							if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
								// select modules here
								List selected = project.getModules([
									filter: { p -> p.isActive() && p.hasChanged() && "jar".equals(p.getPackagingProcedure()) },
									includeSelf: true
								]);
								echo "modules: ${selected.toString()}";
								echo "paths:A: ${project.getModulePaths(filter: { p -> selected.any { it == p } }, includeSelf: true).toString()}";
								echo "paths:B: ${project.getModulePaths(filter: { p -> p.isActive() && p.hasChanged() }, includeSelf: true).toString()}";
								echo "paths:X: ${project.getModulePaths(filter: { p -> true }, includeSelf: true).toString()}";
								echo "paths:Y: ${project.getModulePaths(filter: { p -> true }, includeSelf: false).toString()}";
								echo "paths:Z: ${project.getModulePaths().toString()}";
								// process selected modules
								try {
									project.execDev(profiles: [
										"toolchain-openjdk-1-8-0",
										"test-junit-jupiter"
									], args: [
										"-X"
									], modules: project.getModulePaths([
											filter: { p -> selected.any { it == p } },
											includeSelf: true
										]));
								} catch (Exception e) {
									selected.each {
										archiveArtifacts artifacts: "${it.getPath()}/target/surefire-reports/*.xml"
									}
									throw e;
								} finally {
									selected.each {
										junit "${it.getPath()}/target/surefire-reports/*.xml"
									}
								}
							}
                    
							// project: other
                    
						}
					}
				}
			}
		}

		stage('Deploy') {
			parallel {
				stage('Develop') {
					steps {
						script {
							builder.selectProjects().each {
								stage(it.getName()) {
									when(it.isActive() && it.hasChanged()) {
										if(it instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
											it.execDev(profiles: [
												"dist-repo-development",
												"deploy"
											], modules: ["."]);
										}
									}
								}
							}
						}
					}
				}
				stage('Release') {
					when {
						branch 'master'
					}
					steps {
						script {
							parallel builder.forEachProject([
									when: { p -> p.isActive() && p.hasChanged() }
								]) { project ->
								if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
									project.execDev(profiles: [
										"dist-repo-releases",
										"deploy-pom-signed"
									], modules: ["."]);
								}
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
			steps {
				script {
					parallel builder.forEachProject([
							when: { p -> p.isActive() && p.hasChanged() }
						]) { project ->
						if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
							// never add : -P ${REPOS} => this is ment to fail here
							project.execDev(profiles: [
								"repo-releases",
								"dist-repo-maven-central",
								"deploy-pom-signed"
							], modules: ["."], skipRepos: true);
							sshagent (credentials: ['RunedUniverse-Jenkins']) {
								sh 'git push origin $(git-create-version-tag maven-parent .)'
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
