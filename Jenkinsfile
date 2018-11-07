// https://jenkins.io/doc/book/pipeline/syntax/
// https://jenkins.io/doc/pipeline/steps/
// https://www.cloudbees.com/sites/default/files/declarative-pipeline-refcard.pdf

// https://vetlugin.wordpress.com/2017/01/31/guide-jenkins-pipeline-merge-requests/

// KWA TOOD :
// - estimate deviation from base branch (if relevant)
// - separate stage for the javadoc:aggregate-jar build (in order to -T 1C the packaging)
// - fix the partial build

pipeline {
    agent {
        label 'slaves'
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
               // Maven : nothing to do, the settings.xml file is passed to maven by command arg & configured by env variables
               // Npm : we could have chosen "npm config" command, but, using a file, we keep the same principle as for maven
               // KWA Note : Awful outside docker...
               // sh "cp -f .ci/.npmrc ~/"
               // sh "rm -f ~/.m2/settings.xml"
               echo "Workspace location : ${env.WORKSPACE}"
               echo "Branch : ${env.GIT_BRANCH}"
           }
       }

        stage("Detecting changes for build") {
            steps {
                script {
                    // OMA : to get info from scm checkout
                    env.GIT_REV=checkout(scm).GIT_COMMIT
                    env.GIT_PRECEDENT_COMMIT=checkout(scm).GIT_PREVIOUS_SUCCESSFUL_COMMIT
                }
                sh "git --git-dir .git rev-parse HEAD > vitam_commit.txt"
                sh '''git diff --name-only ${GIT_REV} ${GIT_PRECEDENT_COMMIT} | grep -oE '^[^/]+' | sort | uniq > .changed_roots.txt'''
                // GIT_PREVIOUS_SUCCESSFUL_COMMIT
                script {
                    def changedRoots = readFile(".changed_roots.txt").tokenize('\n')
                    // KWA Caution bis : check if the file is empty before...
                    env.CHANGED_VITAM = changedRoots.contains("sources") || changedRoots.contains("doc")
                    env.CHANGED_VITAM_PRODUCT = changedRoots.contains("rpm") || changedRoots.contains("deb")
                    // KWA Caution : need to get check conditions twice

                    // init default deploy_goal.txt
                    writeFile file: 'deploy_goal.txt', text: "${env.DEPLOY_GOAL}"
                }
                // OMA: evaluate project version ; write directly through shell as I didn't find anything else
                sh "$MVN_BASE -q -f sources/pom.xml --non-recursive -Dexec.args='\${project.version}' -Dexec.executable=\"echo\" org.codehaus.mojo:exec-maven-plugin:1.3.1:exec > version_projet.txt"
                echo "Changed VITAM : ${env.CHANGED_VITAM}"
                echo "Changed VITAM : ${env.CHANGED_VITAM_PRODUCT}"
            }
        }

        // Override the default maven deploy target when on master (publish on nexus)
        stage("Computing maven target") {
            when {
                anyOf {
                    branch "develop*"
                    branch "master_*"
                    branch "master"
                }
            }
            environment {
                DEPLOY_GOAL = "deploy"
                MASTER_BRANCH = "true"
            }
            steps {
                script {
                    // overwrite file content with one more goal
                    writeFile file: 'deploy_goal.txt', text: "${env.DEPLOY_GOAL}"
                    writeFile file: 'master_branch.txt', text: "${env.MASTER_BRANCH}"
                 }
                echo "We are on master branch (${env.GIT_BRANCH}) ; deploy goal is \"${env.DEPLOY_GOAL}\""
            }
        }

        stage ("Execute unit tests") {
         // when {
        //     //     environment(name: 'CHANGED_VITAM', value: 'true')
        //     // }
            steps {
                dir('sources') {
                    sh '$MVN_COMMAND -f pom.xml clean test'
                }
            }
            post {
                always {
                    junit 'sources/**/target/surefire-reports/*.xml'
                }
            }
        }


        stage("Build packages") {
            // Separated for the -T 1C option (possible here, but not while executing the tests)
            // Caution : it force us to recompile and rebuild the jar packages, but it doesn't cost that much (KWA TODO: To be verified)
            // when {
            //     environment(name: 'CHANGED_VITAM', value: 'true')
            // }
            environment {
                DEPLOY_GOAL = readFile("deploy_goal.txt")
            }
            steps {
                parallel(
                    "Package VITAM solution" : {
                        dir('sources') {
                            sh '$MVN_COMMAND -f pom.xml -Dmaven.test.skip=true -DskipTests=true clean package rpm:attached-rpm jdeb:jdeb $DEPLOY_GOAL -Drevision=${BUILD_NUMBER}'
                        }
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

        stage("Publish packages") {
            // when {
            //     //environment(name: 'CHANGED_VITAM_PRODUCT', value: 'true')
            //     environment(name: 'MASTER_BRANCH', value: 'true')
            // }
            steps {
                parallel(
                    "Upload vitam-griffons packages": {
                        sshagent (credentials: ['jenkins_sftp_to_repository']) {
                            sh 'vitam-build.git/push_griffons_repo.sh griffons $SERVICE_REPO_SSHURL'
                        }
                    }
                )
            }
        }
        stage("Update symlink") {
            steps {
                sshagent (credentials: ['jenkins_sftp_to_repository']) {
                    sh 'vitam-build.git/push_symlink_repo.sh griffons $SERVICE_REPO_SSHURL'
                }
            }
        }
    }
}
