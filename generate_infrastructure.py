import os
import glob
base_dir = r"C:\PHASE2\Koha_Port"
modules = glob.glob(os.path.join(base_dir, "koha-java-*"))
# 1. Generate Dockerfiles
dockerfile_content = """FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
"""
for mod in modules:
    with open(os.path.join(mod, "Dockerfile"), "w") as f:
        f.write(dockerfile_content)
print("Generated Dockerfiles.")
# 2. Generate application.yml
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
yaml_content = """server:
  port: {port}
spring:
  application:
    name: {name}
  datasource:
    url: \
    username: \
    password: \
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
"""
gateway_yaml_content = """server:
  port: {port}
spring:
  application:
    name: {name}
management:
  endpoints:
    web:
      exposure:
        include: health,info
"""
for mod_name, port in ports.items():
    path = os.path.join(base_dir, mod_name, "src", "main", "resources")
    os.makedirs(path, exist_ok=True)
    content = gateway_yaml_content if "gateway" in mod_name else yaml_content
    with open(os.path.join(path, "application.yml"), "w") as f:
        f.write(content.format(port=port, name=mod_name))
print("Generated application.yml files.")
# 3. Generate docker-compose.yml
compose_content = ["version: '3.8'", "services:", "  postgres:", "    image: postgres:15", "    environment:", "      POSTGRES_DB: koha", "      POSTGRES_USER: koha_user", "      POSTGRES_PASSWORD: koha_pass", "    ports:", "      - '5432:5432'"]
for mod_name, port in ports.items():
    compose_content.extend([
        f"  {mod_name}:",
        f"    build: ./{mod_name}",
        f"    ports:",
        f"      - '{port}:{port}'",
        "    environment:",
        "      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/koha",
        "      SPRING_DATASOURCE_USERNAME: koha_user",
        "      SPRING_DATASOURCE_PASSWORD: koha_pass",
        "    depends_on:",
        "      - postgres"
    ])
with open(os.path.join(base_dir, "docker-compose.yml"), "w") as f:
    f.write(chr(10).join(compose_content))
print("Generated docker-compose.yml.")
