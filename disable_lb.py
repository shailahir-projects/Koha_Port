import os, glob
base = r'C:\PHASE2\Koha_Port'
modules = [d for d in os.listdir(base) if d.startswith('koha-java-') and d != 'koha-java-database' and os.path.isdir(os.path.join(base, d))]
for mod in sorted(modules):
    yml_path = os.path.join(base, mod, 'src', 'main', 'resources', 'application.yml')
    if os.path.exists(yml_path):
        with open(yml_path, 'r') as f:
            content = f.read()
        if 'liquibase' not in content:
            # Add liquibase disabled after spring: section
            if 'spring:' in content:
                content = content.replace('spring:', 'spring:\n  liquibase:\n    enabled: false', 1)
            else:
                content += '\nspring:\n  liquibase:\n    enabled: false\n'
            with open(yml_path, 'w') as f:
                f.write(content)
print('Disabled liquibase in all microservice application.yml files')
