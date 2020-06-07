if ! kubectl get deployments | grep web-dep
then
  kubectl create deployment web-dep --image=jhagdu/centos-web:v${BUILD_NUMBER}
  kubectl scale deployment web-dep --replicas=3
  kubectl expose deployment/web-dep --port=80 --type=NodePort
else
  kubectl set image deploy web-dep centos-web=jhagdu/centos-web:v${BUILD_NUMBER}
  kubectl rollout status deploy web-dep

  nodeport=$(kubectl get svc -o jsonpath={.items[*].spec.ports[*].nodePort})
  status=$(curl -s -w "%{http_code}" -o /dev/nell http://192.168.99.100:$nodeport/webapp.html)
  if [ $status -ne 200 ]
  then
    kubectl rollout undo deploy web-dep
  fi
fi
