pipeline {
    agent {
        label 'griffins'
    }

    environment {
        MVN_BASE = "/usr/local/maven/bin/mvn "
        MVN_COMMAND = "${MVN_BASE} --settings ${pwd()}/.ci/settings.xml --show-version --batch-mode --errors --fail-at-end -DinstallAtEnd=true -DdeployAtEnd=true "
        DEPLOY_GOAL = " " // Deploy goal used by maven ; typically "deploy" for master* branches & "" (nothing) for everything else (we don't deploy) ; keep a space so can work in other branches than develop
        CI = credentials("app-jenkins")
        SERVICE_SONAR_URL = credentials("service-sonar-url")
        SERVICE_NEXUS_URL = credentials("service-nexus-url")
        SERVICE_CHECKMARX_URL = credentials("service-checkmarx-url")
        SERVICE_REPO_SSHURL = credentials("repository-connection-string")
        SERVICE_GIT_URL = credentials("service-gitlab-url")
        GITHUB_ACCOUNT_TOKEN = credentials("vitam-prg-token")
        PIC_PROD_URL = credentials("service-repository-url")
        JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
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
                    tag pattern: "^[1-9]+\\.[0-9]+\\.[0-9]+-?[0-9]*\$", comparator: "REGEXP"
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
                dir('${HOME}/.m2/repository') {
                    deleteDir()
                }
                    sh '$MVN_BASE --settings .ci/settings.xml -f pom.xml clean test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage ("Execute unit tests when Pull Request") {
            when {
                branch "PR*"
            }
            steps {
                githubNotify status: "PENDING", description: "Building & testing", credentialsId: "vitam-prg-token"
                dir('${HOME}/.m2/repository') {
                    deleteDir()
                }
                    sh '$MVN_BASE --settings .ci/settings_internet.xml -f pom.xml clean test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                success {
                    githubNotify status: "SUCCESS", description: "Build successul", credentialsId: "vitam-prg-token"
                }
                failure {
                    githubNotify status: "FAILURE", description: "Build failed", credentialsId: "vitam-prg-token"
                }
                unstable {
                    githubNotify status: "ERROR", description: "Build unstable", credentialsId: "vitam-prg-token"
                }
                aborted {
                    githubNotify status: "FAILURE", description: "Build canceled", credentialsId: "vitam-prg-token"
                }
                unsuccessful {
                    githubNotify status: "ERROR", description: "Build unsuccessful", credentialsId: "vitam-prg-token"
                }
            }
        }

        stage("Build packages") {
            environment {
                DEPLOY_GOAL = readFile("deploy_goal.txt")
            }
            steps {
                parallel(
                    "Package griffins" : {
                        sh '$MVN_COMMAND -f pom.xml -Dmaven.test.skip=true -DskipTests=true package rpm:attached-rpm jdeb:jdeb $DEPLOY_GOAL -Drevision=${BUILD_NUMBER}'
                    },
                    "Checkout publishing scripts" : {
                        dir('vitam-build.git') {
                            deleteDir()
                        }
                        checkout([$class: 'GitSCM',
                            branches: [[name: 'scaleway_j11']],
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
             steps {
                parallel(
                    "Download deb packages": {
                          sh './build_repo.sh deb http://pic-prod-repository.vitam-factory/griffins_binaries'
                    },
                    "Download rpm packages": {
                          sh './build_repo.sh rpm http://pic-prod-repository.vitam-factory/griffins_binaries'
                    }
                )
            }

        }

        stage("Prepare selinux packages building") {
            // when {
            //     anyOf {
            //         branch "develop*"
            //         branch "master_*"
            //         branch "master"
            //         tag pattern: "^[1-9]+\\.[0-9]+\\.[0-9]+-?[0-9]*\$", comparator: "REGEXP"
            //     }
            // }
            // when {
            //     environment(name: 'CHANGED_VITAM_PRODUCT', value: 'true')
            // }
            steps {
                sh 'rm -rf selinux/target'
            }
        }

        stage("Build selinux packages") {
            // when {
            //     anyOf {
            //         branch "develop*"
            //         branch "master_*"
            //         branch "master"
            //         tag pattern: "^[1-9]+\\.[0-9]+\\.[0-9]+-?[0-9]*\$", comparator: "REGEXP"
            //     }
            // }
            // when {
            //     environment(name: 'CHANGED_VITAM_PRODUCT', value: 'true')
            // }
            steps {
                parallel(
                    "Build selinux rpm": {
                        dir('selinux') {
                            sh 'chmod 755 build-all-docker.sh'
                            sh './build-all-docker.sh'
                        }
                    }
                )
            }
        }

        stage("Publish packages") {
            steps {
                parallel(
                    "Upload vitam-griffons packages": {
                        dir('vitam-build.git') {
                            sshagent (credentials: ['jenkins_sftp_to_repository']) {
                                // sh 'pwd; ls -lah'
                                sh './push_griffons_repo.sh griffins $SERVICE_REPO_SSHURL'
                        }
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
    }

    post {
        always {
            // Cleanup workspace
            cleanWs()
        }
    }
}
