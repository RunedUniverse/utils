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

		CHANGES_MVN_PARENT = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag mvn-parent .'
			)}"""
		CHANGES_JAVA_UTILS_BOM = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-bom ../java-utils-bom'
			)}"""
		CHANGES_JAVA_UTILS_ASYNC = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-async ../java-utils-async'
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
		CHANGES_JAVA_UTILS_MAVEN = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-maven ../java-utils-maven'
			)}"""
		CHANGES_JAVA_UTILS_PLEXUS = """${sh(
				returnStdout: true,
				script: '.build/git-check-version-tag java-utils-plexus ../java-utils-plexus'
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
				script {
					def parent = new net.runeduniverse.lib.tools.jenkins.MavenProject(this);
					parent.setPath(".maven-parent");
					def v = parent.getVersion();
					sh 'echo ${v}'
					v = parent.getVersion("../java-utils-async";
					sh 'echo ${v}'
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
