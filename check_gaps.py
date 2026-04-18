import os, glob, re, json
base = r'C:\PHASE2\Koha_Port'
gaps = []
# Check each module for GlobalExceptionHandler, ErrorResponse, WebMvcConfig
modules = [d for d in os.listdir(base) if d.startswith('koha-java-') and os.path.isdir(os.path.join(base, d))]
for mod in sorted(modules):
    mod_path = os.path.join(base, mod)
    jfiles = glob.glob(os.path.join(mod_path, 'src', 'main', 'java', '**', '*.java'), recursive=True)
    fnames = [os.path.basename(f) for f in jfiles]
    has_geh = 'GlobalExceptionHandler.java' in fnames
    has_err = any('ErrorResponse' in f for f in fnames)
    has_wmc = 'WebMvcConfig.java' in fnames
    has_todo = False
    for jf in jfiles:
        if 'Impl' in os.path.basename(jf):
            with open(jf, 'r', errors='ignore') as f:
                content = f.read()
                if '// TODO' in content or 'throw new UnsupportedOperationException' in content:
                    has_todo = True
                    break
    if not has_geh: gaps.append(f'{mod}: MISSING GlobalExceptionHandler')
    if not has_err: gaps.append(f'{mod}: MISSING ErrorResponse DTO')
    if not has_wmc and 'gateway' not in mod: gaps.append(f'{mod}: MISSING WebMvcConfig')
    if has_todo: gaps.append(f'{mod}: HAS TODO stubs in ServiceImpl')
with open(os.path.join(base, 'gap_details.json'), 'w') as f:
    json.dump(gaps, f, indent=2)
