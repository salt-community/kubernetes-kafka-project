
## 1. Apply Metric Server
```bash
kubectl apply -f ./k8s/metrics-server.yml
kubectl get pods -n kube-system
```

### ✅ Test Which Are Top Pods (using most cpu)
```bash
kubectl top pods
```

## 2. Apply HPA For Order-Service
```bash
kubectl apply -f ./k8s/order-service-hpa.yml
kubectl get hpa
```

### ✅ Verify & Save Explicit Instance
```bash
kubectl get pods -l app=order-service
```

## 3. Start CPU Burner
```bash
kubectl exec order-service-**** -- sh -c "yes > /dev/null &"
```

## 4. Watch HPA in Realtime
```bash
kubectl get hpa order-service-hpa -w
```