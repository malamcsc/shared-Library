def call(body) {
	def pipelineParams = [:]
	def image_tag = '0.0.0'


	body.resloveStrategy = Closure.DELEGATE_FIRST
	body.delegate = pipelineParams
	body()



	if (!pipelineParams.GIT_CREDENTIAL?.trim())
	    pipelineParams.GIT_CREDENTIAL = ''
	if (!pipelineParams.IMAGE_REPO?.trim())
	    pipelineParams.IMAGE_REPO = ''
	if (!pipelineParams.GITHUB_REPO?.trim())
	    pipelineParams.GITHUB_REPO = ''
	


	pipeline {
	
	  agent any
		
	  environment {
		GIT_CREDENTIAL = "${pipelineParms.GIT_CREDENTIAL}"
		IMAGE_REPO = "${pipelineParms.IMAGE_REPO}"
		GITHUB_REPO = "${pipelineParms.GITHUB_REPO}"
	        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
	    }
	
	  stages {
	
	    stage('Checkout Source') {
	      steps {
	        git url:"${GITHUB_REPO}", branch:"main", credentialsId: "${GIT_CREDENTIAL}"
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
