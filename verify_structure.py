import json
with open(r'C:\PHASE2\Koha_Port\structure_audit.json') as f:
    data = json.load(f)
issues = []
for mod, info in data.items():
    if 'error' in info:
        issues.append(f'{mod}: {info["error"]}')
        continue
    if info.get('misplaced_files'):
        issues.append(f'{mod}: misplaced={info["misplaced_files"]}')
    if info.get('missing_subdirs'):
        issues.append(f'{mod}: missing_dirs={info["missing_subdirs"]}')
    if not info.get('has_ResourceNotFoundException'):
        issues.append(f'{mod}: no ResourceNotFoundException')
    if not info.get('has_BusinessRuleException'):
        issues.append(f'{mod}: no BusinessRuleException')
if not issues:
    print('ALL MODULES PASS STRUCTURE CHECK')
else:
    for i in issues:
        print(i)
