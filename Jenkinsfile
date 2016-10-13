properties([
  pipelineTriggers([
    upstream(
      threshold: hudson.model.Result.SUCCESS,
      upstreamProjects: '/metaborg/spoofax-releng/master'
    )
  ]),
  disableConcurrentBuilds()
])

node {
  stage('Checkout') {
    checkout scm
    sh 'git clean -ddffxx'
  }
  
  stage('Build and Test') {
    withMaven(
      mavenLocalRepo: "${env.JENKINS_HOME}/m2repos/${env.EXECUTOR_NUMBER}",
      mavenOpts: '-Xmx1G -Xms1G -Xss16m'
    ){
      sh 'mvn -B -U clean verify -DforceContextQualifier=\$(date +%Y%m%d%H%M)'
    }
  }

  stage('Archive') {
    archiveArtifacts artifacts: 'entityscala.eclipse.site/target/site/', onlyIfSuccessful: true
  }
  
  stage('Cleanup') {
    exec 'git clean -ddffxx'
  }
}
