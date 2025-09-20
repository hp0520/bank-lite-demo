pipeline {
  agent any

  environment {
    IMAGE_NAME = "bank-lite-demo"
    IMAGE_TAG  = "${BUILD_NUMBER}"
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        deleteDir()
        checkout scm
        sh 'mkdir -p reports'
      }
    }

    stage('Build & Test (Dockerized Maven)') {
      steps {
        sh '''
          docker run --rm \
            -v "$PWD":/ws \
            -v "$HOME/.m2":/root/.m2 \
            -w /ws maven:3.9-eclipse-temurin-17 \
            mvn -B -e clean verify
        '''
      }
      post {
        always {
          junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }
      }
    }

    stage('Package (Dockerized Maven)') {
      steps {
        sh '''
          docker run --rm \
            -v "$PWD":/ws \
            -v "$HOME/.m2":/root/.m2 \
            -w /ws maven:3.9-eclipse-temurin-17 \
            mvn -q -DskipTests package
        '''
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }

    stage('Build Docker Image') {
      steps {
        sh '''
          docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
          docker images | grep ${IMAGE_NAME} || (echo "Image build failed" && exit 1)
        '''
      }
    }

    stage('Run Container & Smoke Test') {
      steps {
        sh '''
          docker rm -f bank-lite || true
          docker run -d --name bank-lite -p 8080:8080 ${IMAGE_NAME}:${IMAGE_TAG}

          # wait for /health
          for i in {1..30}; do
            docker run --rm --network container:bank-lite curlimages/curl:8.8.0 \
              -sSf http://localhost:8080/health >/dev/null && exit 0
            sleep 2
          done
          echo "App did not start in time" && exit 1
        '''
      }
      post {
        always {
          sh 'docker logs bank-lite > app.log || true'
          archiveArtifacts artifacts: 'app.log', fingerprint: true
          sh 'docker rm -f bank-lite || true'
        }
      }
    }
  }
}
