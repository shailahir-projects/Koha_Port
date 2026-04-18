import os
import glob
modules = glob.glob(r"C:\PHASE2\Koha_Port\koha-java-*")
config_template = """package {pkg}.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(true)
            .parameterName("format")
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML);
    }
}
"""
for mod in modules:
    # gateway modules don't use Spring WebMvc (they use WebFlux)
    if "gateway" in mod:
        continue
    # get base package
    # looking for main app class
    app_class = glob.glob(os.path.join(mod, "src", "main", "java", "**", "*Application.java"), recursive=True)
    if app_class:
        app_dir = os.path.dirname(app_class[0])
        config_dir = os.path.join(app_dir, "config")
        os.makedirs(config_dir, exist_ok=True)
        config_file = os.path.join(config_dir, "WebMvcConfig.java")
        # calculate package
        rel_path = os.path.relpath(app_dir, os.path.join(mod, "src", "main", "java"))
        pkg = rel_path.replace(os.sep, '.')
        if not os.path.exists(config_file):
            with open(config_file, 'w') as f:
                f.write(config_template.replace("{pkg}", pkg))
print("Injected WebMvcConfig.java")
