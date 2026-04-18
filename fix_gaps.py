import os, glob
base = r'C:\PHASE2\Koha_Port'
error_response_template = '''package {pkg}.dto;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class ErrorResponse {{
    private int status;
    private String error;
    private String message;
    private String timestamp;
}}
'''
geh_template = '''package {pkg}.controller;
import {pkg}.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
@RestControllerAdvice
public class GlobalExceptionHandler {{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {{
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }}
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {{
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }}
}}
'''
# Fix missing ErrorResponse in acquisitions, intranet-gateway, opac-gateway
for mod_name in ['koha-java-acquisitions', 'koha-java-intranet-gateway', 'koha-java-opac-gateway']:
    mod_path = os.path.join(base, mod_name)
    app_files = glob.glob(os.path.join(mod_path, 'src', 'main', 'java', '**', '*Application.java'), recursive=True)
    if app_files:
        app_dir = os.path.dirname(app_files[0])
        rel = os.path.relpath(app_dir, os.path.join(mod_path, 'src', 'main', 'java')).replace(os.sep, '.')
        dto_dir = os.path.join(app_dir, 'dto')
        os.makedirs(dto_dir, exist_ok=True)
        er_file = os.path.join(dto_dir, 'ErrorResponse.java')
        if not os.path.exists(er_file):
            with open(er_file, 'w') as f:
                f.write(error_response_template.format(pkg=rel))
# Fix missing GlobalExceptionHandler in search
for mod_name in ['koha-java-search']:
    mod_path = os.path.join(base, mod_name)
    app_files = glob.glob(os.path.join(mod_path, 'src', 'main', 'java', '**', '*Application.java'), recursive=True)
    if app_files:
        app_dir = os.path.dirname(app_files[0])
        rel = os.path.relpath(app_dir, os.path.join(mod_path, 'src', 'main', 'java')).replace(os.sep, '.')
        ctrl_dir = os.path.join(app_dir, 'controller')
        os.makedirs(ctrl_dir, exist_ok=True)
        geh_file = os.path.join(ctrl_dir, 'GlobalExceptionHandler.java')
        if not os.path.exists(geh_file):
            with open(geh_file, 'w') as f:
                f.write(geh_template.format(pkg=rel))
print('Fixed all gaps')
