


### CPU Burner

```
kubectl get pods -l app=backend
```

Assume it said -> backend-85bf8685c5-26k9z

```
kubectl exec backend-85bf8685c5-26k9z -- sh -c "yes > /dev/null &"
```

### HPA
```
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
name: backend-hpa
spec:
scaleTargetRef:
apiVersion: apps/v1
kind: Deployment
name: backend
minReplicas: 1
maxReplicas: 5
metrics:
- type: Resource
resource:
name: cpu
target:
type: Utilization
averageUtilization: 50
behavior:
scaleDown:
stabilizationWindowSeconds: 60
```

```
kubectl apply -f backend-hpa.yaml
```

```
kubectl get hpa
```

```
kubectl get hpa backend-hpa -w
```

```
kubectl top pods
NAME                      CPU   MEM
backend-...               3m    230Mi
frontend-...              0m    18Mi
postgres-...              1m    63Mi
```

```
kubectl get hpa backend-hpa -w
kubectl get deploy backend -w
```
