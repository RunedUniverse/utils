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
			when { return false }
			steps {
				script {
					parallel builder.forEachProject(when: { p -> p.isActive() && p.hasChanged() }) { project ->
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
						if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
							project.execDev(profiles: [
								"toolchain-openjdk-1-8-0",
								"install"
							], args: [
								"--non-recursive"
							], modules: ["."]);
						}
						post {
							always {
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
		}

		stage('Install - BOMs') {
			steps {
				script {
					parallel builder.forEachProject(filter: { p -> p.isBOM() }, when: { p -> p.isActive() && p.hasChanged() }) { project ->
						if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
							project.execDev(profiles: [
								"toolchain-openjdk-1-8-0",
								"install"
							], modules: ["."]);
						}
						post {
							always {
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
		}
	}
	post {
		cleanup {
			cleanWs()
		}
	}
}
