package pe.edu.utp.pf_api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/v1")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> h = new HashSet<>();
        h.add(TestResource.class); // Clase de prueba
        h.add(pe.edu.utp.pf_api.controller.UsuarioController.class);
        h.add(pe.edu.utp.pf_api.controller.ConductorController.class);
        h.add(pe.edu.utp.pf_api.controller.TrabajadorController.class);
        h.add(pe.edu.utp.pf_api.controller.VehiculoController.class);
        h.add(pe.edu.utp.pf_api.controller.ServicioController.class);
        return h;
    }
}