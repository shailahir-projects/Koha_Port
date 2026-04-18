import os
gw_dirs = [r"C:\PHASE2\Koha_Port\koha-java-intranet-gateway\src\main\resources", r"C:\PHASE2\Koha_Port\koha-java-opac-gateway\src\main\resources"]
gw_yaml = """server:
  port: {port}
spring:
  application:
    name: {name}
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
      routes:
        - id: catalog
          uri: http://localhost:8094
          predicates:
            - Path=/api/v1/biblios/**,/api/v1/items/**,/api/v1/authorities/**
        - id: auth
          uri: http://localhost:8095
          predicates:
            - Path=/api/v1/auth/**,/api/v1/oauth/**
        - id: patron
          uri: http://localhost:8082
          predicates:
            - Path=/api/v1/patrons/**
        # We assume Docker or Kubernetes DNS handles lb:// mapping
        # but locally we'll map to localhost for simplicity right now
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
"""
for path, (name, port) in zip(gw_dirs, [("koha-java-intranet-gateway", 8080), ("koha-java-opac-gateway", 8079)]):
    os.makedirs(path, exist_ok=True)
    with open(os.path.join(path, "application.yml"), 'w') as f:
        f.write(gw_yaml.format(port=port, name=name))
