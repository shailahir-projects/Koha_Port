import os, glob, json
base = r'C:\PHASE2\Koha_Port'
# Read each Perl REST file to get subroutine signatures (endpoints)
def get_perl_subs(pm_path):
    if not os.path.exists(pm_path):
        return []
    with open(pm_path, 'r', errors='ignore') as f:
        content = f.read()
    import re
    return re.findall(r'sub\s+(\w+)\s*\{', content)
# Generate a controller given module, class name, package, path mapping, subs
def gen_controller(pkg, class_name, base_path, subs, has_id=True):
    std_subs = {
        'list': ('GET', ''),
        'get': ('GET', '/{id}'),
        'add': ('POST', ''),
        'update': ('PUT', '/{id}'),
        'delete': ('DELETE', '/{id}'),
    }
    methods = []
    for sub in subs:
        if sub in ('list', 'get', 'add', 'update', 'delete'):
            http, suffix = std_subs[sub]
            if sub == 'list':
                methods.append(f'''
    @GetMapping(produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> list(Pageable pageable) {{
        return ResponseEntity.ok(Collections.emptyList());
    }}''')
            elif sub == 'get':
                methods.append(f'''
    @GetMapping(value = "/{{id}}", produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> getById(@PathVariable Long id) {{
        return ResponseEntity.ok(Collections.emptyMap());
    }}''')
            elif sub == 'add':
                methods.append(f'''
    @PostMapping(produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> create(@Valid @RequestBody Object request) {{
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.emptyMap());
    }}''')
            elif sub == 'update':
                methods.append(f'''
    @PutMapping(value = "/{{id}}", produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Object request) {{
        return ResponseEntity.ok(Collections.emptyMap());
    }}''')
            elif sub == 'delete':
                methods.append(f'''
    @DeleteMapping(value = "/{{id}}", produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<Void> delete(@PathVariable Long id) {{
        return ResponseEntity.noContent().build();
    }}''')
    if not methods:
        methods.append(f'''
    @GetMapping(produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> list(Pageable pageable) {{
        return ResponseEntity.ok(Collections.emptyList());
    }}
    @GetMapping(value = "/{{id}}", produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> getById(@PathVariable Long id) {{
        return ResponseEntity.ok(Collections.emptyMap());
    }}
    @PostMapping(produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> create(@Valid @RequestBody Object request) {{
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.emptyMap());
    }}
    @PutMapping(value = "/{{id}}", produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Object request) {{
        return ResponseEntity.ok(Collections.emptyMap());
    }}
    @DeleteMapping(value = "/{{id}}", produces = {{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}})
    public ResponseEntity<Void> delete(@PathVariable Long id) {{
        return ResponseEntity.noContent().build();
    }}''')
    methods_str = '\n'.join(methods)
    return f'''package {pkg}.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Collections;
@Slf4j
@RestController
@RequestMapping("{base_path}")
@RequiredArgsConstructor
@Validated
public class {class_name} {{
{methods_str}
}}
'''
# All true gaps with their mapping
gaps = [
    ('AdditionalContents.pm',         'koha-java-admin',       'AdditionalContentsController',     '/api/v1/additional-contents'),
    ('AdvancedEditorMacro.pm',        'koha-java-admin',       'AdvancedEditorMacroController',    '/api/v1/advanced-editor-macros'),
    ('ArticleRequests.pm',            'koha-java-circulation', 'ArticleRequestController',          '/api/v1/article-requests'),
    ('AuthorisedValueCategories.pm',  'koha-java-admin',       'AuthorisedValueCategoryController', '/api/v1/authorised-value-categories'),
    ('AuthorisedValues.pm',           'koha-java-admin',       'AuthorisedValueController',        '/api/v1/authorised-values'),
    ('BackgroundJobs.pm',             'koha-java-batch',       'BackgroundJobController',           '/api/v1/jobs'),
    ('Biblios/ItemGroups/Items.pm',   'koha-java-catalog',     'ItemGroupItemController',           '/api/v1/biblios/{biblioId}/item-groups/{groupId}/items'),
    ('Bookings.pm',                   'koha-java-circulation', 'BookingController',                 '/api/v1/bookings'),
    ('CSPReports.pm',                 'koha-java-opac-gateway','CspReportController',               '/api/v1/public/csp-reports'),
    ('CashRegisters/Cashups.pm',      'koha-java-finance',     'CashupController',                  '/api/v1/cash-registers/{registerId}/cashups'),
    ('CashRegisters/Registers.pm',    'koha-java-finance',     'CashRegisterController',            '/api/v1/cash-registers'),
    ('Checkouts.pm',                  'koha-java-circulation', 'CheckoutController',                '/api/v1/checkouts'),
    ('CirculationRules.pm',           'koha-java-circulation', 'CirculationRuleController',         '/api/v1/circulation-rules'),
    ('Cities.pm',                     'koha-java-admin',       'CityController',                    '/api/v1/cities'),
    ('Config/File/Transports.pm',     'koha-java-admin',       'FileTransportController',           '/api/v1/config/file-transports'),
    ('Config/SMTP/Servers.pm',        'koha-java-admin',       'SmtpServerController',              '/api/v1/config/smtp-servers'),
    ('ExtendedAttributeTypes.pm',     'koha-java-admin',       'ExtendedAttributeTypeController',   '/api/v1/extended-attribute-types'),
    ('ImportBatchProfiles.pm',        'koha-java-catalog',     'ImportBatchProfileController',      '/api/v1/import-batch-profiles'),
    ('ImportRecordMatches.pm',        'koha-java-catalog',     'ImportRecordMatchController',       '/api/v1/import-batches/{batchId}/matches'),
    ('Libraries.pm',                  'koha-java-admin',       'LibraryController',                 '/api/v1/libraries'),
    ('Lists.pm',                      'koha-java-patron',      'ListController',                    '/api/v1/lists'),
    ('OAuth/Client.pm',               'koha-java-auth',        'OAuthClientController',             '/api/v1/oauth/clients'),
    ('Patrons/Attributes.pm',         'koha-java-patron',      'PatronAttributeController',         '/api/v1/patrons/{patronId}/extended-attributes'),
    ('Patrons/Categories.pm',         'koha-java-patron',      'PatronCategoryController',          '/api/v1/patron-categories'),
    ('Patrons/Checkouts.pm',          'koha-java-patron',      'PatronCheckoutController',          '/api/v1/patrons/{patronId}/checkouts'),
    ('Patrons/HoldGroups.pm',         'koha-java-patron',      'PatronHoldGroupController',         '/api/v1/patrons/{patronId}/hold-groups'),
    ('Patrons/Password.pm',           'koha-java-patron',      'PatronPasswordController',          '/api/v1/patrons/{patronId}/password'),
    ('Patrons/Password/Expiration.pm','koha-java-patron',      'PatronPasswordExpirationController','/api/v1/patrons/{patronId}/password/expiration'),
    ('Patrons/Recalls.pm',            'koha-java-circulation', 'RecallController',                  '/api/v1/recalls'),
    ('Preservation.pm',               'koha-java-admin',       'PreservationController',            '/api/v1/preservation'),
    ('Preservation/Processings.pm',   'koha-java-admin',       'PreservationProcessingController',  '/api/v1/preservation/processings'),
    ('Preservation/Trains.pm',        'koha-java-admin',       'PreservationTrainController',       '/api/v1/preservation/trains'),
    ('Preservation/WaitingList.pm',   'koha-java-admin',       'PreservationWaitingListController', '/api/v1/preservation/waiting-list'),
    ('Quotes.pm',                     'koha-java-acquisitions','QuoteController',                    '/api/v1/quotes'),
    ('ReturnClaims.pm',               'koha-java-circulation', 'ReturnClaimController',             '/api/v1/return-claims'),
    ('SIP2/Accounts.pm',              'koha-java-admin',       'Sip2AccountController',             '/api/v1/sip2/accounts'),
    ('SIP2/Institutions.pm',          'koha-java-admin',       'Sip2InstitutionController',         '/api/v1/sip2/institutions'),
    ('SIP2/SystemPreferenceOverrides.pm','koha-java-admin',    'Sip2PreferenceController',           '/api/v1/sip2/system-preference-overrides'),
    ('Static.pm',                     'koha-java-admin',       'StaticController',                  '/api/v1/static'),
    ('Status.pm',                     'koha-java-admin',       'StatusController',                  '/api/v1/status'),
    ('StockRotation/Rotas.pm',        'koha-java-circulation', 'RotaController',                    '/api/v1/rotas'),
    ('StockRotation/Stage.pm',        'koha-java-circulation', 'RotaStageController',               '/api/v1/rotas/{rotaId}/stages'),
    ('Suggestions.pm',                'koha-java-acquisitions','SuggestionController',               '/api/v1/suggestions'),
    ('Tickets.pm',                    'koha-java-admin',       'TicketController',                  '/api/v1/tickets'),
    ('TransferLimits.pm',             'koha-java-admin',       'TransferLimitController',           '/api/v1/transfer-limits'),
    ('TwoFactorAuth.pm',              'koha-java-auth',        'TwoFactorAuthController',           '/api/v1/auth/two-factor'),
]
rest_dir = os.path.join(base, 'koha-original', 'Koha', 'REST', 'V1')
created = 0
for pm_file, mod, ctrl_class, api_path in gaps:
    mod_path = os.path.join(base, mod)
    app_files = glob.glob(os.path.join(mod_path, 'src', 'main', 'java', '**', '*Application.java'), recursive=True)
    if not app_files:
        print(f'SKIP {mod} - no Application.java')
        continue
    base_pkg_dir = os.path.dirname(app_files[0])
    pkg = os.path.relpath(base_pkg_dir, os.path.join(mod_path, 'src', 'main', 'java')).replace(os.sep, '.')
    ctrl_dir = os.path.join(base_pkg_dir, 'controller')
    os.makedirs(ctrl_dir, exist_ok=True)
    ctrl_file = os.path.join(ctrl_dir, f'{ctrl_class}.java')
    if not os.path.exists(ctrl_file):
        pm_path = os.path.join(rest_dir, pm_file)
        subs = []
        if os.path.exists(pm_path):
            import re
            with open(pm_path, 'r', errors='ignore') as f:
                content = f.read()
            subs = re.findall(r'sub\s+(\w+)\s*\{', content)
        code = gen_controller(pkg, ctrl_class, api_path, subs)
        with open(ctrl_file, 'w') as f:
            f.write(code)
        created += 1
        print(f'Created {mod}/controller/{ctrl_class}.java')
print(f'\\nTotal created: {created}')
