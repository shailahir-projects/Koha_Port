import os, glob, json
base = r'C:\PHASE2\Koha_Port'
modules = sorted([d for d in os.listdir(base) if d.startswith('koha-java-') and os.path.isdir(os.path.join(base, d))])
report = {}
for mod in modules:
    mod_path = os.path.join(base, mod)
    jfiles = glob.glob(os.path.join(mod_path, 'src', 'main', 'java', '**', '*.java'), recursive=True)
    # Get the base package path
    app_files = [f for f in jfiles if 'Application.java' in os.path.basename(f)]
    base_pkg_dir = os.path.dirname(app_files[0]) if app_files else None
    if not base_pkg_dir:
        report[mod] = {'error': 'NO Application.java found'}
        continue
    # Check for files at wrong level (should be in sub-packages)
    files_at_base = [os.path.basename(f) for f in jfiles if os.path.dirname(f) == base_pkg_dir and 'Application' not in os.path.basename(f)]
    # Check sub-directories exist
    expected_dirs = ['config', 'controller', 'dto', 'service', 'repository', 'exception']
    if 'gateway' in mod:
        expected_dirs = ['config', 'controller', 'dto']  # gateways are simpler
    existing_dirs = [d for d in os.listdir(base_pkg_dir) if os.path.isdir(os.path.join(base_pkg_dir, d))]
    missing_dirs = [d for d in expected_dirs if d not in existing_dirs]
    # Check for files that should be in controller/ but are at base level
    misplaced = []
    for f in files_at_base:
        if 'Controller' in f:
            misplaced.append(f'{f} -> should be in controller/')
        elif 'Dto' in f or 'Request' in f or 'Response' in f:
            misplaced.append(f'{f} -> should be in dto/')
        elif 'Service' in f:
            misplaced.append(f'{f} -> should be in service/')
        elif 'Repository' in f:
            misplaced.append(f'{f} -> should be in repository/')
        else:
            misplaced.append(f'{f} -> misplaced at base package')
    # Check exception classes
    exc_dir = os.path.join(base_pkg_dir, 'exception')
    has_rnf = os.path.exists(os.path.join(exc_dir, 'ResourceNotFoundException.java'))
    has_bre = os.path.exists(os.path.join(exc_dir, 'BusinessRuleException.java'))
    report[mod] = {
        'base_pkg': os.path.relpath(base_pkg_dir, os.path.join(mod_path, 'src', 'main', 'java')),
        'files_at_base_level': files_at_base,
        'existing_subdirs': existing_dirs,
        'missing_subdirs': missing_dirs,
        'misplaced_files': misplaced,
        'has_ResourceNotFoundException': has_rnf,
        'has_BusinessRuleException': has_bre
    }
with open(os.path.join(base, 'structure_audit.json'), 'w') as f:
    json.dump(report, f, indent=2)
