properties([
  pipelineTriggers([
    upstream(
      threshold: hudson.model.Result.SUCCESS,
      upstreamProjects: '/metaborg/spoofax-releng/master'
    )
  ]),
  disableConcurrentBuilds()
])

node('spoofax-buildenv-jenkins') {
  // In Jenkins, under Tools, add a JDK Installation with:
  // - Name: JDK 11
  // - JAVA_HOME: /usr/lib/jvm/java-11-openjdk-amd64
  // - Install automatically: false
  // Ensure the JDK 11 is available in the Spoofax Docker image at the specified path.
  jdk = tool name: 'JDK 11'
  env.JAVA_HOME = "${jdk}"
  
  stage('Echo') {
    // Print important variables and versions for debugging purposes.
    sh 'env'
    sh 'bash --version'
    sh 'git --version'
    sh 'python3 --version'
    sh 'pip3 --version'
    sh "$JAVA_HOME/bin/java -version"
    sh "$JAVA_HOME/bin/javac -version"
    sh 'mvn --version'
  }
  
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
    sh 'git clean -ddffxx'
  }
}
