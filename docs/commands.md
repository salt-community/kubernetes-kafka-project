## 1. Secrets - Create/Update

```bash
kubectl create secret generic order-service-secrets \
  --from-env-file=restaurant-order-service/.env \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create secret generic verifier-secrets \
  --from-env-file=kafka-verifier/.env \
  --dry-run=client -o yaml | kubectl apply -f -
```

## 2. Build Images

```bash
docker build -t verifier ./kafka-verifier
docker build -t order-service ./restaurant-order-service
```

## 3. Kubernetes Configuration Scripts

### Delete Existing Kubernetes Resources

```bash
kubectl delete -f k8s/broker.yml --ignore-not-found
kubectl delete -f k8s/order-service.yml --ignore-not-found
kubectl delete -f k8s/verifier.yaml --ignore-not-found
```

### Apply Kubernetes Configurations

```bash
kubectl apply -f k8s/broker.yml
kubectl apply -f k8s/order-service.yml
kubectl apply -f k8s/verifier.yaml
```

## 4. Dashboard

### Install Dashboard
```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
```

### Apply Kubernetes Configurations
```bash
kubectl apply -f k8s/dashboard-admin.yml
```
### Start Local Kubernetes API proxy
```bash
kubectl proxy
```
### Generate Token To Access Dashboard
```bash
kubectl -n kubernetes-dashboard create token admin-user
```

# [Go To Dashboard](http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy)