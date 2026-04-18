import os, glob, json
base = r'C:\PHASE2\Koha_Port\koha-original'
# Key source directories and their migration targets
source_dirs = {
    'Koha/REST/V1': 'API Controllers (main migration target)',
    'Koha': 'Domain models',
    'C4': 'Core library',
    'acqui': 'Acquisitions Perl scripts',
    'admin': 'Admin Perl scripts',
    'circ': 'Circulation Perl scripts',
    'members': 'Patron/members Perl scripts',
    'serials': 'Serials Perl scripts',
    'catalogue': 'Catalogue Perl scripts',
    'opac': 'OPAC Perl scripts',
    'reports': 'Reports Perl scripts',
    'ill': 'ILL Perl scripts',
    'erm': 'ERM Perl scripts',
    'reserve': 'Holds Perl scripts',
    'tools': 'Tools Perl scripts',
    'xt': 'Extended tests (NOT business logic - test quality checks)',
    't': 'Unit tests (NOT business logic)',
}
report = {}
for d, desc in source_dirs.items():
    full = os.path.join(base, d)
    if os.path.isdir(full):
        pl_files = glob.glob(os.path.join(full, '**', '*.pl'), recursive=True)
        pm_files = glob.glob(os.path.join(full, '**', '*.pm'), recursive=True)
        t_files = glob.glob(os.path.join(full, '**', '*.t'), recursive=True)
        report[d] = {
            'description': desc,
            'pl_count': len(pl_files),
            'pm_count': len(pm_files),
            't_count': len(t_files),
            'pl_files': [os.path.relpath(f, full) for f in sorted(pl_files)[:10]],
            'pm_files': [os.path.relpath(f, full) for f in sorted(pm_files)[:10]],
        }
    else:
        report[d] = {'description': desc, 'exists': False}
with open(r'C:\PHASE2\Koha_Port\source_audit.json', 'w') as f:
    json.dump(report, f, indent=2)
