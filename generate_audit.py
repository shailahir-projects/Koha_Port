import os
import glob
import re
swagger_dir = r"C:\PHASE2\Koha_Port\koha-original\api\v1\swagger\paths"
java_dir = r"C:\PHASE2\Koha_Port"
swagger_files = glob.glob(os.path.join(swagger_dir, '*.yaml'))
java_controllers = glob.glob(os.path.join(java_dir, '**', '*Controller.java'), recursive=True)
report = ["# Migration Gap Report\n"]
report.append(f"Found {len(swagger_files)} Swagger YAML files.\n")
report.append(f"Found {len(java_controllers)} Spring Boot Controllers.\n")
with open('MIGRATION_GAP_REPORT.md', 'w') as f:
    f.writelines(report)
print('Report generated.')
