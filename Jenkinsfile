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
				
					def parent = maven.createProject(id: "mvn-parent", name: "mvn-parent", path: ".maven-parent");
					parent.addModule(id: "java-utils-bom", name: "java-utils-bom", path: "java-utils-bom", modulePath: "../java-utils-bom");
					parent.addModule(id: "java-utils-async", name: "java-utils-async", path: "java-utils-async", modulePath: "../java-utils-async");
					parent.addModule(id: "java-utils-chain", name: "java-utils-chain", path: "java-utils-chain", modulePath: "../java-utils-chain");
					parent.addModule(id: "java-utils-common", name: "java-utils-common", path: "java-utils-common", modulePath: "../java-utils-common");
					parent.addModule(id: "java-utils-errors", name: "java-utils-errors", path: "java-utils-errors", modulePath: "../java-utils-errors");
					parent.addModule(id: "java-utils-logging", name: "java-utils-logging", path: "java-utils-logging", modulePath: "../java-utils-logging");
					parent.addModule(id: "java-utils-maven", name: "java-utils-maven", path: "java-utils-maven", modulePath: "../java-utils-maven");
					parent.addModule(id: "java-utils-plexus", name: "java-utils-plexus", path: "java-utils-plexus", modulePath: "../java-utils-plexus");
					parent.addModule(id: "java-utils-scanner", name: "java-utils-scanner", path: "java-utils-scanner", modulePath: "../java-utils-scanner");
					
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
					parallel builder.collectProjects().collectEntries { project ->
						[
							(project.getName()): {
								stage(project.getName()) {
									when(project.hasChanged()) {
										if(project instanceof net.runeduniverse.lib.tools.jenkins.MavenProject) {
											project.execDev(profiles: "license-check,license-apache2-approve");
										}
									}
								}
							}
						]
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
