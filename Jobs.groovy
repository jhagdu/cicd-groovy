job("1-Update Env") {
  description("Update Environment -- Pull Code -> Build Image -> Push to Docker Hub")

  scm {
    github("jhagdu/Continuous_Delivery","master")
  }

  triggers {
    githubPush()
  }

  steps {
    shell("sudo docker build -t jhagdu/centos-web:v\${BUILD_NUMBER} . \nsudo docker push jhagdu/centos-web:v\${BUILD_NUMBER}")
  }
}


job("2-RollOut Updates") {
  description("RollOut Updates by contacting to Kubernetes")

  parameters {
    label("K8s")
  }

  triggers {
    upstream("1-Update Env","SUCCESS")
  }

  steps {
    shell(readFileFromWorkspace("job2Script.sh"))
  }
}

deliveryPipelineView("CICD") {
  columns(1)
  enableManualTriggers(true)
  linkToConsoleLog(true)
  pipelineInstances(1)
  allowPipelineStart(true)
  pipelines {
    component("RollOut Update", "1-Update Env")
  }
}

