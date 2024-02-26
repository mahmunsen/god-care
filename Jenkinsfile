pipeline {
    agent any
    environment {
        imagename = "god-care"      // docker build로 만들 이미지 이름
        registryCredential = 'docker-hub-god-care'
        dockerImage = ''
    }

    stages {

        stage('Prepare') {
          steps {
            echo 'Clonning Repository'
            git url: 'git@github.com:f-lab-edu/god-care.git',
              branch: 'main',
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
                    dockerImage.push()
                }
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

				stage('Docker Run') {
            steps {
                echo 'Pull Docker Image & Docker Image Run'
                sshagent (credentials: 'ssh-god-care') {
                    sh "ssh -o StrictHostKeyChecking=no root@101.101.218.151 'docker pull mahmunsen/god-care'"
                    sh "ssh -o StrictHostKeyChecking=no root@101.101.218.151 'docker ps -q --filter name=GodCare | grep -q . && docker rm -f \$(docker ps -aq --filter name=GodCare); docker run -d --name GodCare -p 8080:8080 mahmunsen/god-care'"

                }
            }
        }
    }
		post {
          success {
              discordSend description: "알림테스트",
                footer: "테스트 빌드가 성공했습니다.",
                link: env.BUILD_URL, result: currentBuild.currentResult,
                title: "테스트 젠킨스 job",
                webhookURL: "https://discord.com/api/webhooks/1211628671350210590/nrF2R2cOTHm1eDIJtzoObhEPFgPAFy4VGfAs6Pilmb-9Xibg5051fPDnFPs2J-2UPDc4"
          }
          failure {
              discordSend description: "알림테스트",
                footer: "테스트 빌드가 실패했습니다.",
                link: env.BUILD_URL, result: currentBuild.currentResult,
                title: "테스트 젠킨스 job",
                webhookURL: "https://discord.com/api/webhooks/1211628671350210590/nrF2R2cOTHm1eDIJtzoObhEPFgPAFy4VGfAs6Pilmb-9Xibg5051fPDnFPs2J-2UPDc4"
          }
      }
}