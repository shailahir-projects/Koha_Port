import os, glob, re, json
base = r'C:\PHASE2\Koha_Port'
rest_dir = os.path.join(base, 'koha-original', 'Koha', 'REST', 'V1')
# Get all Perl REST controllers
perl_files = glob.glob(os.path.join(rest_dir, '**', '*.pm'), recursive=True)
perl_names = [os.path.relpath(f, rest_dir).replace('\\', '/') for f in sorted(perl_files)]
# Get all Java controllers
java_files = glob.glob(os.path.join(base, 'koha-java-*', 'src', 'main', 'java', '**', '*Controller.java'), recursive=True)
java_names = [os.path.basename(f).replace('Controller.java','').lower() for f in java_files]
java_full = [os.path.basename(f) for f in java_files]
# Check each Perl file against Java
gaps = []
covered = []
for pf in perl_names:
    # Derive expected Java name
    name_part = os.path.basename(pf).replace('.pm','').lower()
    dir_part = os.path.dirname(pf).lower().replace('/', '_').lstrip('_')
    # Check if any controller covers this
    found = any(name_part in jn.lower() or (dir_part and dir_part.split('_')[0] in jn.lower()) for jn in java_names)
    if found:
        covered.append(pf)
    else:
        gaps.append(pf)
result = {
    'total_perl_rest_files': len(perl_names),
    'covered': len(covered),
    'gaps': len(gaps),
    'gap_list': gaps,
    'all_perl_files': perl_names,
    'all_java_controllers': java_full
}
with open(os.path.join(base, 'rest_coverage.json'), 'w') as f:
    json.dump(result, f, indent=2)
