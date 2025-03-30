def call(body) {
	def image_tag = '0.0.0'


	body.resloveStrategy = Closure.DELEGATE_FIRST
	body.delegate = pipelineParams
	body()


	if (!pipelineParams.AWS_REGIONS?.trim())
	    pipelineParams.AWS_REGIONS = ''
	if (!pipelineParams.AWS_REGIONS?.trim())
	    pipelineParams.AWS_REGIONS = 'us-east-1'


	pipeline {
	
	  agent any
		
	  environment {
		BRANCH = "${pipelineParms.BRANCH}"
		GIT_CREDENTIAL = "${pipelineParms.GIT_CREDENTIAL}"
		IMAGE_REPO = "${pipelineParms.IMAGE_REPO}"
		GITHUB_REPO = "${pipelineParms.GITHUB_REPO}"
	        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
	    }
	
	  stages {
	
	    stage('Checkout Source') {
	      steps {
	        git url:"${GITHUB_REPO}", branch:"${BRANCH}", credentialsId: "${GIT_CREDENTIAL}"
	      }
	    }
	
	      
	      stage("Build image") {
	            steps {
	                script {
	                    myapp = docker.build("${IMAGE_REPO}":"${env.BUILD_ID}")
	                }
	            }
	        }
	    
	         
	        stage('Login and Dcoker push') {
	          steps {
	            script{
	                  withDockerRegistry([ credentialsId: "dockerhub", url: "" ]){
	                  myapp.push("${env.BUILD_ID}") }
	                  }
			          }
	           }
	         
	    
	    
	
	    
	  }
	
	}
}
