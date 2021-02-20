pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        git(url: 'https://github.com/RunedUniverse/utils', branch: '*/master', changelog: true)
      }
    }

  }
}