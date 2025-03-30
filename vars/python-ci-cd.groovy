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
		AWS_REGIONS = "${pipelineParms.AWS_REGIONS}"
		GIT_CREDENTIAL = "${pipelineParms.GIT_CREDENTIAL}"
		IMAGE_REPO = "${pipelineParms.IMAGE_REPO}"
	        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
	    }
	
	  stages {
	
	    stage('Checkout Source') {
	      steps {
	        git url:'https://github.com/malamcsc/kubernetes_project.git', branch:'master', credentialsId: 'github'
	      }
	    }
	
	      stage('Checkout') {
	        steps { git branch: 'master', credentialsId: 'github', url: 'https://github.com/malamcsc/kubernetes_project.git'
	        }
	      }
	      stage("Build image") {
	            steps {
	                script {
	                    myapp = docker.build("malamcsc/kubernetes_project:${env.BUILD_ID}")
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
