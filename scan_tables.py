import os, glob, re, json
base = r'C:\PHASE2\Koha_Port'
tables = set()
# Scan all Repository files for table references in SQL
repo_files = glob.glob(os.path.join(base, 'koha-java-*', 'src', 'main', 'java', '**', '*.java'), recursive=True)
for f in repo_files:
    with open(f, 'r', errors='ignore') as fh:
        content = fh.read()
    # Find FROM/INTO/UPDATE/DELETE FROM table patterns
    for pattern in [r'FROM\s+(\w+)', r'INTO\s+(\w+)', r'UPDATE\s+(\w+)', r'DELETE\s+FROM\s+(\w+)', r'JOIN\s+(\w+)']:
        for match in re.finditer(pattern, content, re.IGNORECASE):
            t = match.group(1).lower()
            if t not in ('select','set','where','and','or','on','as','values','null','true','false','count','now','excluded','conflict'):
                tables.add(t)
with open(os.path.join(base, 'tables_found.json'), 'w') as f:
    json.dump(sorted(list(tables)), f, indent=2)
