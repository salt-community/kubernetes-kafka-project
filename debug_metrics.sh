#!/bin/bash
echo "=== Checking APIService ==="
kubectl get apiservice v1beta1.metrics.k8s.io -o wide

echo -e "\n=== Checking Service ==="
kubectl get service metrics-server -n kube-system -o wide

echo -e "\n=== Checking Endpoints ==="
kubectl get endpoints metrics-server -n kube-system

echo -e "\n=== Checking Pod Status ==="
kubectl get pods -n kube-system -l k8s-app=metrics-server -o wide

echo -e "\n=== Checking Pod Logs (Last 20 lines) ==="
kubectl logs -n kube-system -l k8s-app=metrics-server --tail=20
