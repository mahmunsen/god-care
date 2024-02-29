pipeline {
    agent any

    triggers {
        pollSCM('*/3 * * * *')
    }

    environment {
        imagename = "mahmunsen/god-care"      // docker build로 만들 이미지 이름
        registryCredential = 'docker-hub-god-care'
        dockerImage = ''
    }

    stages {
      // git에서 repository clone
        stage('Prepare') {
          steps {
            echo 'Clonning Repository'
            git url: 'git@github.com:mahmunsen/god-care.git',
              branch: 'develop',
              credentialsId: 'github-god-care'
          }
          post {
            success {
               echo 'Successfully Cloned Repository'
            }
           	failure {
               error 'This pipeline stops here...'
            }
          }
        }

        stage('Bulid Gradle') {
          steps {
            echo 'Bulid Gradle'
            dir('.'){
                sh './gradlew build -x test'
                sh './gradlew clean build'
			}
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        stage('Bulid Docker') {
          steps {
            echo 'Bulid Docker'
            script {
                dockerImage = docker.build imagename
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        stage('Push Docker') {
          steps {
            echo 'Push Docker'
            script {
              docker.withRegistry( '', registryCredential) {
                 dockerImage.push("latest")
              }
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        stage('Remove Docker Image') {
           steps {
               sh 'docker rmi mahmunsen/god-care'
           }
        }
    }
	post {
        failure {
          error 'This pipeline stops here...'
        }
    }
}
     // 추후 배포, 알림 부분 추가 예정