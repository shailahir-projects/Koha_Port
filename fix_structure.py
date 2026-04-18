import os, glob, shutil
base = r'C:\PHASE2\Koha_Port'
# Templates
rnf_template = '''package {pkg}.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {{
    public ResourceNotFoundException(String message) {{
        super(message);
    }}
    public ResourceNotFoundException(String resource, Long id) {{
        super(resource + " not found with id: " + id);
    }}
}}
'''
bre_template = '''package {pkg}.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessRuleException extends RuntimeException {{
    public BusinessRuleException(String message) {{
        super(message);
    }}
}}
'''
repo_template = '''package {pkg}.repository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
@RequiredArgsConstructor
public class SearchRepository {{
    private final JdbcTemplate jdbc;
}}
'''
modules = sorted([d for d in os.listdir(base) if d.startswith('koha-java-') and os.path.isdir(os.path.join(base, d))])
for mod in modules:
    mod_path = os.path.join(base, mod)
    app_files = glob.glob(os.path.join(mod_path, 'src', 'main', 'java', '**', '*Application.java'), recursive=True)
    if not app_files:
        continue
    base_pkg_dir = os.path.dirname(app_files[0])
    rel_pkg = os.path.relpath(base_pkg_dir, os.path.join(mod_path, 'src', 'main', 'java')).replace(os.sep, '.')
    # 1. Create exception/ dir with both classes if missing
    exc_dir = os.path.join(base_pkg_dir, 'exception')
    os.makedirs(exc_dir, exist_ok=True)
    rnf_file = os.path.join(exc_dir, 'ResourceNotFoundException.java')
    bre_file = os.path.join(exc_dir, 'BusinessRuleException.java')
    if not os.path.exists(rnf_file):
        with open(rnf_file, 'w') as f:
            f.write(rnf_template.format(pkg=rel_pkg))
    if not os.path.exists(bre_file):
        with open(bre_file, 'w') as f:
            f.write(bre_template.format(pkg=rel_pkg))
    # 2. Create repository/ if missing (search module)
    if mod == 'koha-java-search':
        repo_dir = os.path.join(base_pkg_dir, 'repository')
        os.makedirs(repo_dir, exist_ok=True)
        repo_file = os.path.join(repo_dir, 'SearchRepository.java')
        if not os.path.exists(repo_file):
            with open(repo_file, 'w') as f:
                f.write(repo_template.format(pkg=rel_pkg))
    # 3. Move misplaced CatalogController.java
    if mod == 'koha-java-catalog':
        misplaced = os.path.join(base_pkg_dir, 'CatalogController.java')
        target = os.path.join(base_pkg_dir, 'controller', 'CatalogController.java')
        if os.path.exists(misplaced):
            # Read, fix package, write to controller/
            with open(misplaced, 'r') as f:
                content = f.read()
            content = content.replace(
                'package com.shailahir.koha.catalog;',
                'package com.shailahir.koha.catalog.controller;'
            )
            os.makedirs(os.path.join(base_pkg_dir, 'controller'), exist_ok=True)
            if not os.path.exists(target):
                with open(target, 'w') as f:
                    f.write(content)
            os.remove(misplaced)
            print(f'  Moved CatalogController.java to controller/')
    # 4. Gateway config dirs
    if 'gateway' in mod:
        config_dir = os.path.join(base_pkg_dir, 'config')
        os.makedirs(config_dir, exist_ok=True)
print('Structure corrections complete.')
