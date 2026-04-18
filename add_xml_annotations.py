import os
import glob
import re
base_dir = r"C:\PHASE2\Koha_Port"
# 1. Add Produces to Controllers
controllers = glob.glob(os.path.join(base_dir, "koha-java-*", "src", "main", "java", "**", "*Controller.java"), recursive=True)
for ctrl in controllers:
    with open(ctrl, "r", encoding="utf-8") as f:
        content = f.read()
    if "produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}" not in content:
        # Add MediaType import if not present
        if "org.springframework.http.MediaType" not in content:
            content = content.replace("import org.springframework.web.bind.annotation.*;", "import org.springframework.web.bind.annotation.*;\nimport org.springframework.http.MediaType;")
        # We replace mapping annotations
        for mapping in ["@GetMapping", "@PostMapping", "@PutMapping", "@PatchMapping", "@DeleteMapping"]:
            # Find and replace but ensure we don't duplicate
            # Basic hack: just replace basic ones.
            content = re.sub(
                rf"{mapping}\((.*?)\)", 
                rf"{mapping}(\1, produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})", 
                content
            )
            # Remove any duplication if it happened
            content = content.replace("produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}", "produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}")
    with open(ctrl, "w", encoding="utf-8") as f:
        f.write(content)
# 2. Add JacksonXmlRootElement to Dtos
dtos = glob.glob(os.path.join(base_dir, "koha-java-*", "src", "main", "java", "**", "dto", "*.java"), recursive=True)
for dto in dtos:
    with open(dto, "r", encoding="utf-8") as f:
        content = f.read()
    if "@JacksonXmlRootElement" not in content and "class " in content:
        if "import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;" not in content:
            content = content.replace("package ", "package ") # wait, let's put it after package
            lines = content.split('\n')
            for i, line in enumerate(lines):
                if line.startswith("package "):
                    lines.insert(i+1, "\nimport com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;")
                    break
            content = '\n'.join(lines)
        content = content.replace("public class ", "@JacksonXmlRootElement\npublic class ")
    with open(dto, "w", encoding="utf-8") as f:
        f.write(content)
print("Added XML annotations.")
