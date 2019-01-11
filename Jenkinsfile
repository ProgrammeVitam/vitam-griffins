pipeline {
    agent {
        label 'griffins'
    }

    environment {
        MVN_BASE = "/usr/local/maven/bin/mvn --settings ${pwd()}/.ci/settings.xml"
        MVN_COMMAND = "${MVN_BASE} --show-version --batch-mode --errors --fail-at-end -DinstallAtEnd=true -DdeployAtEnd=true "
        DEPLOY_GOAL = " " // Deploy goal used by maven ; typically "deploy" for master* branches & "" (nothing) for everything else (we don't deploy) ; keep a space so can work in other branches than develop
        CI = credentials("app-jenkins")
        SERVICE_SONAR_URL = credentials("service-sonar-url")
        SERVICE_NEXUS_URL = credentials("service-nexus-url")
        SERVICE_CHECKMARX_URL = credentials("service-checkmarx-url")
        SERVICE_REPO_SSHURL = credentials("repository-connection-string")
        SERVICE_GIT_URL = credentials("service-gitlab-url")
        SERVICE_PROXY_HOST = credentials("http-proxy-host")
        SERVICE_PROXY_PORT = credentials("http-proxy-port")
    }

   stages {

       stage("Tools configuration") {
           steps {
               echo "Workspace location : ${env.WORKSPACE}"
               echo "Branch : ${env.GIT_BRANCH}"
           }
       }

        stage("Detecting changes for build") {
            steps {
                script {
                    writeFile file: 'deploy_goal.txt', text: "${env.DEPLOY_GOAL}"
                }
                sh "$MVN_BASE -q -f pom.xml --non-recursive -Dexec.args='\${project.version}' -Dexec.executable=\"echo\" org.codehaus.mojo:exec-maven-plugin:1.3.1:exec > version_projet.txt"
                echo "Changed VITAM : ${env.CHANGED_VITAM}"
                echo "Changed VITAM : ${env.CHANGED_VITAM_PRODUCT}"
            }
        }

        stage("Computing maven target") {
            when {
                anyOf {
                    branch "master"
                }
            }
            environment {
                DEPLOY_GOAL = "deploy"
                MASTER_BRANCH = "true"
            }
            steps {
                script {
                    writeFile file: 'deploy_goal.txt', text: "${env.DEPLOY_GOAL}"
                    writeFile file: 'master_branch.txt', text: "${env.MASTER_BRANCH}"
                 }
                echo "We are on master branch (${env.GIT_BRANCH}) ; deploy goal is \"${env.DEPLOY_GOAL}\""
            }
        }

        stage ("Execute unit tests") {
            steps {
                sh '$MVN_COMMAND -f pom.xml clean test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        


        stage("Build packages") {
            environment {
                DEPLOY_GOAL = readFile("deploy_goal.txt")
            }
            steps {
                parallel(
                    "Package VITAM solution" : {
                        sh '$MVN_COMMAND -f pom.xml -Dmaven.test.skip=true -DskipTests=true clean package rpm:attached-rpm jdeb:jdeb $DEPLOY_GOAL -Drevision=${BUILD_NUMBER}'
                    },
                    "Checkout publishing scripts" : {
                        checkout([$class: 'GitSCM',
                            branches: [[name: 'oshimae']],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'vitam-build.git']],
                            submoduleCfg: [],
                            userRemoteConfigs: [[credentialsId: 'app-jenkins', url: "$SERVICE_GIT_URL"]]
                        ])
                    }
                )
            }
        }
        stage("Download internet packages") {
            environment {
                http_proxy = credentials("http-proxy-url")
                https_proxy = credentials("http-proxy-url")
            }
            steps {
                parallel(
                    "Download deb packages": {
                        dir('deb') {
                            sh './build_repo.sh'
                         }
                    },
                    "Download rpm packages": {
                        dir('rpm') {
                            sh './build_repo.sh'
                         }
                    }
                )
            }

        }
        stage("Publish packages") {
            steps {
                parallel(
                    "Upload vitam-griffons packages": {
                        sshagent (credentials: ['jenkins_sftp_to_repository']) {
                            sh 'vitam-build.git/push_griffons_repo.sh griffins $SERVICE_REPO_SSHURL'
                        }
                    }
                )
            }
        }
        stage("Update symlink") {
            steps {
                sshagent (credentials: ['jenkins_sftp_to_repository']) {
                    sh 'vitam-build.git/push_symlink_griffin.sh griffins $SERVICE_REPO_SSHURL'
                }
            }
        }

        stage("Clean") {
            steps {
               deleteDir()
            }
        }
    }
}
