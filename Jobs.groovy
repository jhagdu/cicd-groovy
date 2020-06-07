job("3-Update Env") {
  description("As Soon as we get new code in our master branch, This Job will Update our Production Environment. This Job will Pull code from master branch, Build our container image using Dockerfile and Push that image to Docker Hub")
  scm {
    github("https://github.com/jhagdu/Continuous_Delivery.git","master")
  } 
  triggers {
    githubPush()
  }
  steps {
    shell("sudo docker build -t jhagdu/centos-web:v${BUILD_NUMBER} .")
    shell("sudo docker push jhagdu/centos-web:v${BUILD_NUMBER}")
  }
}
job("4-RollOut Updates") {
  description("This Job will RollOut Updates in out production environment so that clients get Latest Updates. So This Job will contact to our Kubernetes and ask K8s Deployment to RollOut Updates without any downtime. Also this Job will Undo Updates if site is not working after Last Update")
  triggers {
    upstream("3-Update Env","SUCCESS")
  }
  steps {
    shell(readFileFromWorkspace("../3-Update Env/job4Script.sh"))
  }
}
