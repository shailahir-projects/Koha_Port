import os
import glob
import re
pom_files = glob.glob(r"C:\PHASE2\Koha_Port\*\pom.xml")
dependency_xml = """
        <dependency>
            <groupId>com.fasterxml.jackson.dataformat</groupId>
            <artifactId>jackson-dataformat-xml</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
"""
auth_deps_xml = """
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.6</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.6</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.6</version>
            <scope>runtime</scope>
        </dependency>
"""
for pom in pom_files:
    if "koha-original" in pom: continue
    if pom.endswith("Koha_Port\pom.xml"): continue # Skip root pom for inner deps if needed
    with open(pom, 'r') as f:
        content = f.read()
    # check if already added
    if "jackson-dataformat-xml" not in content:
        # inject before </dependencies>
        # find the last </dependencies>
        idx = content.rfind("</dependencies>")
        if idx != -1:
            new_content = content[:idx] + dependency_xml + content[idx:]
            if "koha-java-auth" in pom and "jjwt-api" not in new_content:
                idx2 = new_content.rfind("</dependencies>")
                new_content = new_content[:idx2] + auth_deps_xml + new_content[idx2:]
            # For gateway moduels
            if "gateway" in pom and "spring-boot-starter-web" in new_content:
                new_content = new_content.replace("<artifactId>spring-boot-starter-web</artifactId>", "<artifactId>spring-cloud-starter-gateway</artifactId>")
            with open(pom, 'w') as f:
                f.write(new_content)
print("Injected XML and other required dependencies into all pom.xml files.")
