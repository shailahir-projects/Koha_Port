import os, glob, json
base = r'C:\PHASE2\Koha_Port'
# Exact Perl REST file -> Java module + controller name mapping
gap_to_module = {
    'AdditionalContents.pm':         ('koha-java-admin',       'AdditionalContentsController'),
    'AdvancedEditorMacro.pm':        ('koha-java-admin',       'AdvancedEditorMacroController'),
    'ArticleRequests.pm':            ('koha-java-circulation', 'ArticleRequestController'),
    'AuthorisedValueCategories.pm':  ('koha-java-admin',       'AuthorisedValueCategoryController'),
    'AuthorisedValues.pm':           ('koha-java-admin',       'AuthorisedValueController'),
    'Authorities.pm':                ('koha-java-catalog',     'AuthorityController'),  # exists - false negative
    'BackgroundJobs.pm':             ('koha-java-batch',       'BackgroundJobController'),
    'Biblios.pm':                    ('koha-java-catalog',     'BiblioController'),      # exists
    'Biblios/ItemGroups.pm':         ('koha-java-catalog',     'ItemGroupController'),   # exists
    'Biblios/ItemGroups/Items.pm':   ('koha-java-catalog',     'ItemGroupItemController'),
    'Bookings.pm':                   ('koha-java-circulation', 'BookingController'),
    'CSPReports.pm':                 ('koha-java-opac-gateway','CspReportController'),
    'CashRegisters/Cashups.pm':      ('koha-java-finance',     'CashupController'),
    'CashRegisters/Registers.pm':    ('koha-java-finance',     'CashRegisterController'),
    'Checkouts.pm':                  ('koha-java-circulation', 'CheckoutController'),
    'CirculationRules.pm':           ('koha-java-circulation', 'CirculationRuleController'),
    'Cities.pm':                     ('koha-java-admin',       'CityController'),
    'Config/File/Transports.pm':     ('koha-java-admin',       'FileTransportController'),
    'Config/SMTP/Servers.pm':        ('koha-java-admin',       'SmtpServerController'),
    'DeletedBiblios.pm':             ('koha-java-catalog',     'DeletedBiblioController'), # exists
    'ExtendedAttributeTypes.pm':     ('koha-java-admin',       'ExtendedAttributeTypeController'),
    'ImportBatchProfiles.pm':        ('koha-java-catalog',     'ImportBatchProfileController'),
    'ImportRecordMatches.pm':        ('koha-java-catalog',     'ImportRecordMatchController'),
    'ItemTypes.pm':                  ('koha-java-catalog',     'ItemTypeController'),    # exists
    'Items.pm':                      ('koha-java-catalog',     'ItemController'),        # exists
    'Libraries.pm':                  ('koha-java-admin',       'LibraryController'),
    'Lists.pm':                      ('koha-java-patron',      'ListController'),
    'OAuth.pm':                      ('koha-java-auth',        'OAuthController'),       # exists in AuthController
    'OAuth/Client.pm':               ('koha-java-auth',        'OAuthClientController'),
    'Patrons.pm':                    ('koha-java-patron',      'PatronController'),      # exists
    'Patrons/Attributes.pm':         ('koha-java-patron',      'PatronAttributeController'),
    'Patrons/Categories.pm':         ('koha-java-patron',      'PatronCategoryController'),
    'Patrons/Checkouts.pm':          ('koha-java-patron',      'PatronCheckoutController'),
    'Patrons/HoldGroups.pm':         ('koha-java-patron',      'PatronHoldGroupController'),
    'Patrons/Password.pm':           ('koha-java-patron',      'PatronPasswordController'),
    'Patrons/Password/Expiration.pm':('koha-java-patron',      'PatronPasswordExpirationController'),
    'Patrons/Recalls.pm':            ('koha-java-circulation', 'RecallController'),
    'Preservation.pm':               ('koha-java-admin',       'PreservationController'),
    'Preservation/Processings.pm':   ('koha-java-admin',       'PreservationProcessingController'),
    'Preservation/Trains.pm':        ('koha-java-admin',       'PreservationTrainController'),
    'Preservation/WaitingList.pm':   ('koha-java-admin',       'PreservationWaitingListController'),
    'Quotes.pm':                     ('koha-java-acquisitions','QuoteController'),
    'RecordSources.pm':              ('koha-java-catalog',     'RecordSourceController'), # exists
    'ReturnClaims.pm':               ('koha-java-circulation', 'ReturnClaimController'),
    'SIP2/Accounts.pm':              ('koha-java-admin',       'Sip2AccountController'),
    'SIP2/Institutions.pm':          ('koha-java-admin',       'Sip2InstitutionController'),
    'SIP2/SystemPreferenceOverrides.pm':('koha-java-admin',    'Sip2PreferenceController'),
    'Static.pm':                     ('koha-java-admin',       'StaticController'),
    'Status.pm':                     ('koha-java-admin',       'StatusController'),
    'StockRotation/Rotas.pm':        ('koha-java-circulation', 'RotaController'),
    'StockRotation/Stage.pm':        ('koha-java-circulation', 'RotaStageController'),
    'Suggestions.pm':                ('koha-java-acquisitions','SuggestionController'),
    'Tickets.pm':                    ('koha-java-admin',       'TicketController'),
    'TransferLimits.pm':             ('koha-java-admin',       'TransferLimitController'),
    'TwoFactorAuth.pm':              ('koha-java-auth',        'TwoFactorAuthController'),
}
# Already existing controllers (false negatives from fuzzy match) 
already_exist = {'Authorities.pm','Biblios.pm','Biblios/ItemGroups.pm','DeletedBiblios.pm',
                 'ItemTypes.pm','Items.pm','Patrons.pm','OAuth.pm','RecordSources.pm','ItemGroups.pm'}
with open(os.path.join(base, 'gap_mapping.json'), 'w') as f:
    json.dump({'gap_to_module': gap_to_module, 'already_exist': list(already_exist)}, f, indent=2)
print(f'True gaps to implement: {len([k for k in gap_to_module if k not in already_exist])}')
