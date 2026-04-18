import os
base_dir = r"C:\PHASE2\Koha_Port"
k8s_dir = os.path.join(base_dir, "k8s")
os.makedirs(k8s_dir, exist_ok=True)
ports = {
    "koha-java-opac-gateway": 8079, "koha-java-intranet-gateway": 8080,
    "koha-java-acquisitions": 8081, "koha-java-patron": 8082,
    "koha-java-admin": 8083, "koha-java-circulation": 8084,
    "koha-java-holds": 8085, "koha-java-search": 8086,
    "koha-java-finance": 8087, "koha-java-reporting": 8088,
    "koha-java-ill": 8089, "koha-java-serials": 8090,
    "koha-java-notification": 8091, "koha-java-batch": 8092,
    "koha-java-erm": 8093, "koha-java-catalog": 8094,
    "koha-java-auth": 8095
}
# Namespace
with open(os.path.join(k8s_dir, "namespace.yaml"), "w") as f:
    f.write("apiVersion: v1\nkind: Namespace\nmetadata:\n  name: koha\n")
# Postgres
pg_dir = os.path.join(k8s_dir, "postgres")
os.makedirs(pg_dir, exist_ok=True)
pg_deploy = """apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: koha
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
        - name: postgres
          image: postgres:15
          env:
            - name: POSTGRES_DB
              value: "koha"
            - name: POSTGRES_USER
              value: "koha_user"
            - name: POSTGRES_PASSWORD
              value: "koha_pass"
          ports:
            - containerPort: 5432
"""
with open(os.path.join(pg_dir, "deployment.yaml"), "w") as f:
    f.write(pg_deploy)
pg_svc = """apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: koha
spec:
  ports:
    - port: 5432
  selector:
    app: postgres
"""
with open(os.path.join(pg_dir, "service.yaml"), "w") as f:
    f.write(pg_svc)
# Microservices
for name, port in ports.items():
    mod_dir = os.path.join(k8s_dir, name)
    os.makedirs(mod_dir, exist_ok=True)
    deployment = f"""apiVersion: apps/v1
kind: Deployment
metadata:
  name: {name}
  namespace: koha
spec:
  replicas: 2
  selector:
    matchLabels:
      app: {name}
  template:
    metadata:
      labels:
        app: {name}
    spec:
      containers:
        - name: {name}
          image: shailahir/{name}:latest
          imagePullPolicy: IfNotPresent
          envFrom:
            - configMapRef:
                name: {name}-config
          ports:
            - containerPort: 8080
          resources:
            limits:
              memory: "512Mi"
              cpu: "500m"
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 10
"""
    with open(os.path.join(mod_dir, "deployment.yaml"), "w") as f:
        f.write(deployment)
    svc = f"""apiVersion: v1
kind: Service
metadata:
  name: {name}
  namespace: koha
spec:
  ports:
    - port: {port}
      targetPort: 8080
  selector:
    app: {name}
"""
    with open(os.path.join(mod_dir, "service.yaml"), "w") as f:
        f.write(svc)
    cm = f"""apiVersion: v1
kind: ConfigMap
metadata:
  name: {name}-config
  namespace: koha
data:
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres:5432/koha"
  SPRING_DATASOURCE_USERNAME: "koha_user"
  SPRING_DATASOURCE_PASSWORD: "koha_pass"
"""
    with open(os.path.join(mod_dir, "configmap.yaml"), "w") as f:
        f.write(cm)
print("Generated Kubernetes manifests.")
