pipeline {
  agent any
  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        deleteDir()
        checkout scm
      }
    }

    // Make containers write files as the Jenkins user (prevents permission issues on cleanup)
    stage('Init UID/GID') {
      steps {
        script {
          env.HOST_UID = sh(returnStdout: true, script: 'id -u').trim()
          env.HOST_GID = sh(returnStdout: true, script: 'id -g').trim()
          sh 'echo "Using UID=${HOST_UID} GID=${HOST_GID}"'
        }
      }
    }

    stage('Build & Test (Dockerized Maven)') {
      steps {
        sh '''
          docker run --rm \
            --user ${HOST_UID}:${HOST_GID} \
            -v "$PWD":/ws \
            -v "$HOME/.m2":/m2 -e MAVEN_CONFIG=/m2 \
            -w /ws maven:3.9-eclipse-temurin-17 \
            mvn -B -e clean verify
        '''
      }
      post {
        always {
          junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
          archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
      }
    }
  }
}
