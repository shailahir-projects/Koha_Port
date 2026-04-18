import os, glob, json
base = r'C:\PHASE2\Koha_Port'
result = {}
for mod in sorted(glob.glob(os.path.join(base,'koha-java-*'))):
    modname = os.path.basename(mod)
    jfiles = glob.glob(os.path.join(mod,'src','main','java','**','*.java'), recursive=True)
    controllers = [f for f in jfiles if 'Controller' in os.path.basename(f)]
    services = [f for f in jfiles if 'Service' in os.path.basename(f) and 'Impl' not in os.path.basename(f)]
    impls = [f for f in jfiles if 'Impl' in os.path.basename(f)]
    repos = [f for f in jfiles if 'Repository' in os.path.basename(f)]
    dtos = [f for f in jfiles if 'Dto' in os.path.basename(f) or 'Request' in os.path.basename(f) or 'Response' in os.path.basename(f)]
    has_dockerfile = os.path.exists(os.path.join(mod,'Dockerfile'))
    has_appyml = os.path.exists(os.path.join(mod,'src','main','resources','application.yml'))
    has_pom = os.path.exists(os.path.join(mod,'pom.xml'))
    pom_has_xml = False
    if has_pom:
        with open(os.path.join(mod,'pom.xml'),'r',errors='ignore') as f:
            pom_has_xml = 'jackson-dataformat-xml' in f.read()
    result[modname] = {
        'java_files': len(jfiles),
        'controllers': [os.path.basename(c) for c in controllers],
        'services': len(services),
        'impls': len(impls),
        'repos': len(repos),
        'dtos': len(dtos),
        'dockerfile': has_dockerfile,
        'app_yml': has_appyml,
        'xml_dep': pom_has_xml
    }
with open(os.path.join(base,'audit_result.json'),'w') as f:
    json.dump(result, f, indent=2)
