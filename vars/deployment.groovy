def call(body) {
	def pipelineParams = [:]
	def image_tag = '0.0.0'


	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = pipelineParams
	body()



	if (!pipelineParams.GIT_CREDENTIAL?.trim())
	    pipelineParams.GIT_CREDENTIAL = ''
	if (!pipelineParams.BRANCH?.trim())
 	    pipelineParams.BRANCH = ''
	if (!pipelineParams.IMAGE_REPO?.trim())
	    pipelineParams.IMAGE_REPO = ''
	if (!pipelineParams.GITHUB_REPO?.trim())
	    pipelineParams.GITHUB_REPO = ''
	


	pipeline {
	
	  agent any
		
	  environment {
		GIT_CREDENTIAL = "${pipelineParams.GIT_CREDENTIAL}"
		BRANCH = "${pipelineParams.BRANCH}"
		IMAGE_REPO = "${pipelineParams.IMAGE_REPO}"
		GITHUB_REPO = "${pipelineParams.GITHUB_REPO}"
	    DOCKERHUB_CREDENTIALS = credentials('dockerhub')
        CLUSTER_NAME = 'eksdemo1'
        AWS_ACCESS_KEY_ID = credentials('flask_access_key')
        AWS_SECRET_ACCESS_KEY = credentials('flask_secretkey')
        AWS_REGION = 'us-east-1'
	    }
	
	  stages {
	
	    stage('Checkout Source') {
	      steps {
	        git url: "${GITHUB_REPO}", branch: "${BRANCH}", credentialsId: "${GIT_CREDENTIAL}"
	      }
	    }

        stage('Connect to Eks') {
            steps {
                script {
                    sh """
                        export AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
                        export AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY
                        aws eks update-kubeconfig --region $AWS_REGION --name $CLUSTER_NAME
                    """
                }
            }
        }

        stage('Verify Connection') {
            steps {
                script {
                    sh """
                    kubectl get nodes
                    cd helm
                    helm install flaskapp . --values values.yaml

                    """

                }
            }
        }

      
	
	   
	   

  
	
	    
	  }
	
	}
}
